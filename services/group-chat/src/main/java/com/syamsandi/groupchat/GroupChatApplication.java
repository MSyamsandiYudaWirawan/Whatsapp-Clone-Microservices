package com.syamsandi.groupchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.Async;

@SpringBootApplication
@EnableJpaAuditing
@Async
public class GroupChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(GroupChatApplication.class, args);
	}

}
