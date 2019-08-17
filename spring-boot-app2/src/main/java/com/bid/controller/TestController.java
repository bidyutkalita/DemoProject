package com.bid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bid.dao.StudentRepository;
import com.bid.model.Student;

@RestController
public class TestController {
	@Autowired
	StudentRepository studentRepository;
	
	
	@PostMapping(path="/add") // Map ONLY POST Requests
	public String addNewUser (@RequestParam String name	, @RequestParam String email) {
		Student n = new Student();
		n.setName(name);
		n.setEmail(email);
		studentRepository.save(n);
		return "Saved";
	}

	@GetMapping(path="/all")
	public Iterable<Student> getAllUsers() {
		// This returns a JSON or XML with the users
		return studentRepository.findAll();
	}

}
