package com.bzanni.messagingserver_springws.config;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.bzanni.messagingserver_springws.etcd.EtcdBinding;

/**
 * Init service conf poll to etcd cluster at application initialized event
 * 
 * Init listener on websocket active node add/delete events
 * 
 * @author bertrand
 *
 */
@Component
public class SpringInitEventListener implements
		ApplicationListener<EmbeddedServletContainerInitializedEvent> {

	private static final Logger LOGGER = LogManager
			.getLogger(SpringInitEventListener.class);

	@Resource
	private EtcdBinding etcdBinding;

	@Override
	public void onApplicationEvent(
			final EmbeddedServletContainerInitializedEvent event) {

		final String host;
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			host = localHost.getHostAddress();

			etcdBinding.initHttpScheduledConf(host, Integer.toString(event
					.getEmbeddedServletContainer().getPort()));

			etcdBinding.initWaitForChange();

		} catch (UnknownHostException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}

	}

}