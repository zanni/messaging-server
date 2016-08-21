package com.bzanni.messagingserver_springws.etcd;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import mousio.client.promises.ResponsePromise.IsSimplePromiseResponseHandler;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;

/**
 * etcd bindings helper:
 * 
 * - initWebsocketScheduledConf/initWebsocketScheduledConf:
 * 
 * init conf polling to etcd
 * 
 * - initWaitForChange:
 * 
 * init watch on ws conf on etcd
 * 
 * @author bertrand
 *
 */
@Component
@Configuration
public class EtcdBinding {

	private static final Logger LOGGER = LogManager.getLogger(EtcdBinding.class);

	private static final String APP_NAME = "messagingserver_springws";

	private static final String WS_SERVICE_NAME = "ws_service";

	private static final int WS_SERVICE_TTL_SECOND = 10;

	private static final String HTTP_SERVICE_NAME = "http_service";

	private static final int HTTP_SERVICE_TTL_SECOND = 10;

	@Value("#{'${messagingserver.etcd}'.split(',')}")
	private List<String> etcdNode;

	private EtcdResponsePromise<EtcdKeysResponse> promise = null;

	private EtcdClient etcd = null;

	private Map<String, String> activeWebsocketNode;

	private String currentKey;

	private String currentValue;

	private String currentName;

	@PostConstruct
	public void init() {

		activeWebsocketNode = new ConcurrentHashMap<String, String>();

		LOGGER.info("Etcd nodes: " + etcdNode.toString());

		// init etcd client
		List<URI> list = new ArrayList<URI>();
		for (String node : etcdNode) {
			list.add(URI.create(node));
		}
		etcd = new EtcdClient(list.toArray(new URI[list.size()]));

		LOGGER.info("Successfully connected to etcd (cluster: " + etcd.version().getCluster() + ", server: "
				+ etcd.version().getServer() + ")");

		try {
			// create etcd dir if missing
			etcd.putDir(APP_NAME + "/" + WS_SERVICE_NAME).send().getNow();

			// init active nodes
			EtcdKeysResponse etcdKeysResponse = etcd.getDir(APP_NAME + "/" + WS_SERVICE_NAME).recursive().send().get();
			etcdKeysResponse.node.nodes.forEach(action -> {
				LOGGER.debug("Init active node: " + action.key);
				String key = action.key;
				// remove etcd prefix
				key = key.replace("/" + EtcdBinding.APP_NAME + "/" + EtcdBinding.WS_SERVICE_NAME + "/", "");
				activeWebsocketNode.put(key, action.value);
			});
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (EtcdException e) {
			LOGGER.error(e);
		} catch (EtcdAuthenticationException e) {
			LOGGER.error(e);
		} catch (TimeoutException e) {
			LOGGER.error(e);
		}

	}

	/**
	 * remove service conf from etcd
	 * 
	 * @throws IOException
	 * @throws EtcdException
	 * @throws EtcdAuthenticationException
	 * @throws TimeoutException
	 */
	public void removeConf() throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException {
		if (etcd != null) {
			etcd.delete(currentKey).send().get();
		}
	}

	/**
	 * init polling service http conf to etcd
	 * 
	 * @param serviceType
	 * @param host
	 * @param port
	 * @param ttl
	 */
	public void initHttpScheduledConf(final String name, final String host, final String port) {
		this.initScheduledConf(HTTP_SERVICE_NAME, name, host, port, HTTP_SERVICE_TTL_SECOND);
	}

	/**
	 * init polling service websocket conf to etcd
	 * 
	 * @param serviceType
	 * @param host
	 * @param port
	 * @param ttl
	 */
	public void initWebsocketScheduledConf(final String name, final String host, final String port) {
		this.initScheduledConf(WS_SERVICE_NAME, name, host, port, WS_SERVICE_TTL_SECOND);
	}

	/**
	 * init watcher on websocket active node etcd conf
	 * 
	 * @throws IOException
	 */
	public void initWaitForChange() throws IOException {
		if (etcd != null) {
			// Wait for next change on foo
			promise = etcd.getDir(APP_NAME + "/" + WS_SERVICE_NAME).recursive().waitForChange().send();
			IsSimplePromiseResponseHandler<EtcdKeysResponse> callback = (listener) -> {

				try {
					EtcdKeysResponse etcdKeysResponse = listener.get();

					String key = etcdKeysResponse.node.key;
					// remove etcd prefix
					key = key.replace("/" + EtcdBinding.APP_NAME + "/" + EtcdBinding.WS_SERVICE_NAME + "/", "");
					String value = etcdKeysResponse.node.value;
					// key += value;
					if (activeWebsocketNode.get(key) == null && value != null) {
						LOGGER.debug("Add active node: " + key);
						activeWebsocketNode.put(key, value);
					} else if (value == null) {
						LOGGER.debug("Del active node: " + key);
						activeWebsocketNode.remove(key);
					}

					this.initWaitForChange();

				} catch (IOException e) {
					LOGGER.error(e);
				} catch (EtcdException e) {
					LOGGER.error(e);
				} catch (EtcdAuthenticationException e) {
					LOGGER.error(e);
				} catch (TimeoutException e) {
					LOGGER.error(e);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			};
			promise.addListener(callback);
		}
	}

	/**
	 * get active websocket node list
	 * 
	 * @return
	 */
	public Map<String, String> getActiveWebsocketNode() {
		return new HashMap<String, String>(this.activeWebsocketNode);
	}

	private void putConf(String serviceType, String name, String host, String port, int ttl)
			throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException {
		if (etcd != null) {
			currentKey = APP_NAME + "/" + serviceType + "/" + name;
			currentValue = host + ":" + port;
			LOGGER.info("Init self conf node: " + currentKey);

			etcd.put(currentKey, currentValue).ttl(ttl)

					.send().get();
		}
	}

	private void refreshConf() throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException {
		if (etcd != null) {
			// etcd.refresh(currentKey, WS_SERVICE_TTL_SECOND).send().get();
			etcd.put(currentKey, currentValue).ttl(WS_SERVICE_TTL_SECOND).send().get();
		}
	}

	private void initScheduledConf(final String serviceType, String name, final String host, final String port,
			final int ttl) {
		setCurrentName(name);
		EtcdBinding conf = this;
		try {
			conf.putConf(serviceType, name, host, port, ttl);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (EtcdException e) {
			LOGGER.error(e);
		} catch (EtcdAuthenticationException e) {
			LOGGER.error(e);
		} catch (TimeoutException e) {
			LOGGER.error(e);
		}

		if (etcd != null) {
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {

					try {

						conf.refreshConf();

					} catch (IOException e) {
						LOGGER.error(e);
					} catch (EtcdException e) {
						LOGGER.error(e);
					} catch (EtcdAuthenticationException e) {
						LOGGER.error(e);
					} catch (TimeoutException e) {
						LOGGER.error(e);
					}

				}

			}, new Date(), (WS_SERVICE_TTL_SECOND - 1) * 1000);
		}
	}

	public String getCurrentName() {
		return currentName;
	}

	private void setCurrentName(String currentName) {
		this.currentName = currentName;
	}

}
