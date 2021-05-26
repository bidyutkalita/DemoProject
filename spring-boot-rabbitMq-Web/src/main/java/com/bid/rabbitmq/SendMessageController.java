package com.bid.rabbitmq;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// @RequestMapping("/rest/")
public class SendMessageController {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private Receiver receiver;

	@GetMapping(path = "/rest/test", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String test() {
		try {
			System.out.println("Sending message...");
			rabbitTemplate.convertAndSend(SpringBootRabbitMqApplication.topicExchangeName, "foo.bar.baz",
					"Hello from RabbitMQ!");
			receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

}
