package com.bzanni.messagingserver_springws.config;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
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
public class SpringInitEventListener implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

	private static final Logger LOGGER = LogManager.getLogger(SpringInitEventListener.class);

	@Value("${messagingserver.webapp.host}")
	private String webappHost;

	@Resource
	private EtcdBinding etcdBinding;

	@Override
	public void onApplicationEvent(final EmbeddedServletContainerInitializedEvent event) {

		try {
			etcdBinding.initHttpScheduledConf(webappHost,
					Integer.toString(event.getEmbeddedServletContainer().getPort()));

			etcdBinding.initWaitForChange();

		} catch (IOException e) {
			LOGGER.error(e);
		}

	}

}