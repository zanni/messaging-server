package com.bzanni.messagingserver.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 
 * HTTP request logger implemented as spring chained filter
 * 
 * @author bzanni
 *
 */
@Component
public class HttpRequestLoggingFilter implements Filter {

	private static final Logger LOGGER = LogManager
			.getLogger(HttpRequestLoggingFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;

		chain.doFilter(request, response);
		
		LOGGER.debug(req.getRemoteHost() + " - " + req.getMethod() + " - "
				+ res.getStatus() + " - " + req.getRequestURI());

		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
