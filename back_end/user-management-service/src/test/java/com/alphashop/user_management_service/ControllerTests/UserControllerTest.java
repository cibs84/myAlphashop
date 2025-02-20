package com.alphashop.user_management_service.ControllerTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.alphashop.user_management_service.models.User;
import com.alphashop.user_management_service.repositories.UserRepository;

import test.BaseSpringIT;

@TestMethodOrder(OrderAnnotation.class)
public class UserControllerTest extends BaseSpringIT {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	String JsonData = """
			{
			    "userId": "mario",
			    "password": "pass1234",
			    "active": true,
			    "roles": [
			            "USER"
			        ]
			}
			""";
	
	@Test
	@Order(1)
	public void testUserCreate1() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.post("/api/users/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(print());
	}

	@Test
	@Order(2)
	public void testUserFindUserId() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/find/userid/mario")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				  
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userId").value("mario"))
				.andExpect(jsonPath("$.password").exists())
				.andExpect(jsonPath("$.active").exists())
				.andExpect(jsonPath("$.active").value(true))
				  
				.andExpect(jsonPath("$.roles[0]").exists())
				.andExpect(jsonPath("$.roles[0]").value("USER")) 
				.andDo(print());
		
		String hashedPasswordInDb = userRepository.findByUserId("mario").get().getPassword();
		
		assertThat(passwordEncoder.matches("pass1234", hashedPasswordInDb))
				.isEqualTo(true);
	}
	
	String JsonData2 = """
			{
			    "userId": "admin",
			    "password": "pass1234",
			    "active": true,
			    "roles": [
			            "USER",
			            "ADMIN"
			        ]
			}
			""";
	
	@Test
	@Order(3)
	public void testUserCreate2() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.post("/api/users/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData2)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(print());
	}
	
	String JsonDataUsers = """
			[
			    {
			        "userId": "mario",
			        "password": "pass1234",
			        "active": true,
			        "roles": [
			            "USER"
			        ]
			    },
			    {
			        "userId": "admin",
			        "password": "pass1234",
			        "active": true,
			        "roles": [
			            "USER"
			        ]
			    }
			]
			""";;
	
	@Test
	@Order(4)
	public void testUserFindAll() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/find/all")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pagination.totalPagesArray", hasSize(2)))
				.andExpect(jsonPath("$.itemList", hasSize(2)))
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				 //UTENTE 1
				.andExpect(jsonPath("$.itemList[0].id").exists())
				.andExpect(jsonPath("$.itemList[0].userId").exists())
				.andExpect(jsonPath("$.itemList[0].userId").value("mario"))
				.andExpect(jsonPath("$.itemList[0].password").exists())
				.andExpect(jsonPath("$.itemList[0].active").exists())
				.andExpect(jsonPath("$.itemList[0].active").value(true))
				.andExpect(jsonPath("$.itemList[0].roles[0]").exists())
				.andExpect(jsonPath("$.itemList[0].roles[0]").value("USER")) 
				 //UTENTE 2
				.andExpect(jsonPath("$.itemList[1].id").exists())
				.andExpect(jsonPath("$.itemList[1].userId").exists())
				.andExpect(jsonPath("$.itemList[1].userId").value("admin"))
				.andExpect(jsonPath("$.itemList[1].password").exists())
				.andExpect(jsonPath("$.itemList[1].active").exists())
				.andExpect(jsonPath("$.itemList[1].active").value(true))
				.andExpect(jsonPath("$.itemList[1].roles[0]").exists())
				.andExpect(jsonPath("$.itemList[1].roles[0]").value("USER")) 
				.andExpect(jsonPath("$.itemList[1].roles[1]").exists())
				.andExpect(jsonPath("$.itemList[1].roles[1]").value("ADMIN")) 
				.andReturn();
		
		String hashedPasswordInDb = userRepository.findByUserId("mario").get().getPassword();
		
		assertThat(passwordEncoder.matches("pass1234", hashedPasswordInDb))
				.isEqualTo(true);
	}
	
	String JsonDataUpdated = """
			{
			    "userId": "admin",
			    "password": "pass1234",
			    "active": true,
			    "roles": [
			            "USER",
			            "ADMIN"
			        ]
			}
			""";
	
	@Test
	@Order(5)
	public void testUsersUpdate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/users/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonDataUpdated)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print());
		
		Optional<User> user = userRepository.findByUserId("admin");
		assertThat(user.get().getRoles().size()).isEqualTo(2);
	}
	
	@Test
	@Order(6)
	//@Disabled //<-- Enable to preserve user in mongodb
	public void testDelUtente1() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/delete/mario")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("200 OK"))
				.andExpect(jsonPath("$.message").value("Deleting user 'mario' performed successfully"))
				.andDo(print());
	}
	
	@Test
	@Order(7)
	//@Disabled //<-- Enable to preserve user in mongodb
	public void testDelUtente2() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/delete/admin")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("200 OK"))
				.andExpect(jsonPath("$.message").value("Deleting user 'admin' performed successfully"
						+ ""))
				.andDo(print());
	}
	
}


