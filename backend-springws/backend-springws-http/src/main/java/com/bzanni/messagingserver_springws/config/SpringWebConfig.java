package com.bzanni.messagingserver_springws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.script.ScriptTemplateConfigurer;
import org.springframework.web.servlet.view.script.ScriptTemplateViewResolver;

@EnableWebMvc
@Configuration
@ComponentScan({ "com.bzanni.messagingserver.controller" })
public class SpringWebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public ScriptTemplateConfigurer configurer() {
        ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();

        //1. Nashorn jdk8 script engine.
        configurer.setEngineName("nashorn");

        //2. Add mustache.min.js and custom render.js to Nashorn
        configurer.setScripts("/static/js/mustache.min.js", "/static/js/render.js");

        //3. Ask Nashorn to run this function "render()"
        configurer.setRenderFunction("render");
        return configurer;
    }

    //Define where is Mustache template, in classpath level.
  	// If view "hello" is returned, Mustache temple will be '/static/templates/hello.html'
    @Bean
    public ViewResolver viewResolver() {
        ScriptTemplateViewResolver viewResolver = new ScriptTemplateViewResolver();
        viewResolver.setPrefix("/static/templates/");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

	//add static resources like js or css
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}

}