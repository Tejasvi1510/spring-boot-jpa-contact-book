package spring.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import spring.smart.dao.ContactRepository;
import spring.smart.dao.UserRepository;
import spring.smart.entities.User;
import spring.smart.entities.contact;

@RestController
public class SearchController {
     
	@Autowired
	UserRepository userRepository;
	@Autowired
	ContactRepository contactRepository;
	
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal)
	{
		System.out.println("output of query"+query);
		User user=userRepository.getUserByEmail(principal.getName());
		List<contact> contacts=contactRepository.findByNameContainingAndUser(query, user);
	    System.out.println(contacts);
		return ResponseEntity.ok(contacts);
	}
}
