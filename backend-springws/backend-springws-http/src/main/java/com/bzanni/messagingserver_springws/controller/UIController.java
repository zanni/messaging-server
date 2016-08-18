package com.bzanni.messagingserver_springws.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.bzanni.messagingserver_springws.etcd.EtcdBinding;

@Controller
public class UIController {

	@Resource
	private EtcdBinding etcd;

	@GetMapping("/")
	public String welcome(Map<String, Object> model) {
		model.put("server_name", etcd.getCurrentName());
		return "index";
	}
}
