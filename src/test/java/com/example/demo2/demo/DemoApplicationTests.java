package com.example.demo2.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { DemoApplication.class, DemoApplicationTests.ConfigForTests.class } )
public class DemoApplicationTests {

	@Configuration
	@WebAppConfiguration
	static class ConfigForTests {
		
		@Bean
		public PasswordEncoder passwordEncoder() {
			// Use only for tests
			return NoOpPasswordEncoder.getInstance();
		}
		
	}
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	private MockMvc mockMvc;
	
	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void test() throws Exception {
		insertUser();
		
		/*
		 * First call, ok
		 */
		PasswordResetDTO firstResetDto = new PasswordResetDTO();
		firstResetDto.setName("testuser123");
		firstResetDto.setPassword("mypassword123");
		
		mockMvc
			.perform(
					post("/reset_password")
					.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
					.content(new ObjectMapper().writeValueAsString(firstResetDto))) //
			.andDo(print())//
			.andExpect(status().isOk());//
		
		/*
		 * Second call, another password provided
		 */
		PasswordResetDTO wrongPasswordResetDto = new PasswordResetDTO();
		wrongPasswordResetDto.setName("testuser123");
		wrongPasswordResetDto.setPassword("mypassword123");
		
		mockMvc
			.perform(
					post("/reset_password")
					.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
					.content(new ObjectMapper().writeValueAsString(wrongPasswordResetDto))) //
			.andDo(print())//
			.andExpect(status().isBadRequest());//
		
		/*
		 * Third call, password already used
		 */
		PasswordResetDTO passwordResettetDto = new PasswordResetDTO();
		passwordResettetDto.setName("testuser123");
		passwordResettetDto.setPassword("That's not my password");
		
		mockMvc
			.perform(
					post("/reset_password")
					.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
					.content(new ObjectMapper().writeValueAsString(passwordResettetDto))) //
			.andDo(print())//
			.andExpect(status().isOk());//
	}

	private void insertUser() {
		User user = new User();
		user.setEncryptedPassword("mypassword123");
		user.setName("testuser123");
		user.setUserId(1);
		userService.save(user);
	}

}
