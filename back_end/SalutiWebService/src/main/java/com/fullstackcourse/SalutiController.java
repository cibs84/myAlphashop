package com.fullstackcourse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saluti")
@CrossOrigin(origins = "http://localhost:4200/")
public class SalutiController {
	
	@GetMapping
	public String getSaluti() {
		return "\"Ciao, questo è un saluto di benvenuto!\"";
	}
	
	@GetMapping("{username}")
	public String getSaluti(@PathVariable(value="username")  String usernamexxx) {
		if (usernamexxx.equals("Marco")) {
			throw new RuntimeException("L'utente %s è disabilitato".formatted(usernamexxx));
		}
		return String.format("\"Ciao %s, questo è un saluto di benvenuto!\"", usernamexxx);
	}
}
