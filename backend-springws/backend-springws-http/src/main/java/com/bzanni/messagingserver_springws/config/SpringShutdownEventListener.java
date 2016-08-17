package com.bzanni.messagingserver_springws.config;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.bzanni.messagingserver_springws.etcd.EtcdBinding;

/**
 * Remove service conf poll to etcd cluster at application shutdown event
 * 
 * @author bertrand
 *
 */
@Component
public class SpringShutdownEventListener implements
		ApplicationListener<ContextClosedEvent> {

	private static final Logger LOGGER = LogManager
			.getLogger(SpringInitEventListener.class);

	@Resource
	private EtcdBinding etcdBinding;

	@Override
	public void onApplicationEvent(final ContextClosedEvent event) {
		try {
			etcdBinding.removeConf();
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
	}
}
