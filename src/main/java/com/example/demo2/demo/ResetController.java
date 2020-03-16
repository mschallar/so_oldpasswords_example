package com.example.demo2.demo;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ResetController {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OldPasswordsService oldPasswordsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping("reset_password")
	public ResponseEntity<?> reset(@RequestBody PasswordResetDTO resetDTO) {
		
		Optional<User> findByLogin = this.userService.findByLogin(resetDTO.getName());
		
		if (!findByLogin.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		User user = findByLogin.get();
		Integer userId = user.getUserId();
		
		String encodedPassword = passwordEncoder.encode(resetDTO.getPassword());
		
		for (OldPasswords oldPasswords : oldPasswordsService.findByOwnerId(userId)) {
			
			if (passwordEncoder.matches(resetDTO.getPassword(), oldPasswords.getEncryptedPassword())) {
				// Information: Don't do that! Don't reveal that another user already has such a password!
				log.info("Password already used.");
				return new ResponseEntity<>("PASSWORD_ALREADY_USED", HttpStatus.BAD_REQUEST);
			}
			
		}
		
		OldPasswords oldPasswords = new OldPasswords();
		oldPasswords.setEncryptedPassword(passwordEncoder.encode(encodedPassword));
		oldPasswords.setPasswordOwnerId(userId);
		oldPasswordsService.save(oldPasswords);

		user.setEncryptedPassword(encodedPassword);

		user.setResetPasswordToken(null);
		userService.save(user);
		
		return ResponseEntity.ok().build();
		
	}

}
