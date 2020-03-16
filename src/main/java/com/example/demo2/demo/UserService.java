package com.example.demo2.demo;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public Optional<User> findByLogin(String name) {
		return userRepository.findByName(name);
	}

	public void save(User user) {
		userRepository.save(user);
	}

}
