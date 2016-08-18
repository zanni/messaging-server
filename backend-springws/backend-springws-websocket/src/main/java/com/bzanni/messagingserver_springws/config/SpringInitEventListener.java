package com.bzanni.messagingserver_springws.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.bzanni.messagingserver_springws.etcd.EtcdBinding;

/**
 * Init service conf poll to etcd cluster at application initialized event
 * 
 * @author bertrand
 *
 */
@Component
public class SpringInitEventListener implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

	@Value("${messagingserver.webapp.host}")
	private String webappHost;

	@Resource
	private EtcdBinding etcdBinding;

	@Override
	public void onApplicationEvent(final EmbeddedServletContainerInitializedEvent event) {
		String port = Integer.toString(event.getEmbeddedServletContainer().getPort());
		String name = webappHost.replace("\\.", "") + "-" + port;
		etcdBinding.initWebsocketScheduledConf(name, webappHost, port);

	}

}