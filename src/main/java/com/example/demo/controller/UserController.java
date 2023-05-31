package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.service.UserService;
import com.example.demo.ui.UserRequestModel;
import com.example.demo.ui.UserResponseModel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
	private final ModelMapper modelMapper;
	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserResponseModel> createUser(@RequestBody UserRequestModel requestModel) {
		log.info("inside create user with request:: " + requestModel);
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		UserDto userDto = modelMapper.map(requestModel, UserDto.class);
		StringBuffer sb = new StringBuffer();
		sb.append(requestModel.getPassword());
		userDto.setEncryptedPassword(sb.reverse().toString());
		log.info("userDto :: " + userDto);
		UserResponseModel responseModel = userService.createUser(userDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);

	}

	@GetMapping
	public ResponseEntity<List<UserResponseModel>> getAllUsers() {
		log.info("inside getAllUsers()");
		List<UserResponseModel> resList = new ArrayList<UserResponseModel>();
		List<UserEntity> entities = userService.getAllUsers();
		for (UserEntity entity : entities) {
			resList.add(modelMapper.map(entity, UserResponseModel.class));
		}
		return ResponseEntity.status(HttpStatus.OK).body(resList);
	}
}
