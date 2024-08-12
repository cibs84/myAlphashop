package com.alphashop;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greetings")
@CrossOrigin(origins = "http://localhost:4200/")
public class GreetingsController {
	
	@GetMapping
	public String getGreetings() {
		return "\"Hello, this is a welcome greeting!\"";
	}
	
	@GetMapping("{username}")
	public String getGreetings(@PathVariable(value="username")  String usernamexxx) {
		if (usernamexxx.equals("Marco")) {
			throw new RuntimeException("User %s is disabled".formatted(usernamexxx));
		}
		return String.format("\"Hello %s, this is a welcome greeting!\"", usernamexxx);
	}
}
