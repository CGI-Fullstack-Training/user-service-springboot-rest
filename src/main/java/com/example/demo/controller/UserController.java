package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.exception.MyCustomException;
import com.example.demo.service.UserService;
import com.example.demo.ui.ErrorModel;
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

	@ExceptionHandler(value = NumberFormatException.class)
	public ResponseEntity<ErrorModel> handleException(NumberFormatException e) {
		ErrorModel errorModel = new ErrorModel(HttpStatus.BAD_REQUEST.value(), e.getMessage(),
				System.currentTimeMillis());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorModel);
	}

	@ExceptionHandler(value = MyCustomException.class)
	public ResponseEntity<ErrorModel> handleIdNotFoundException(MyCustomException e) {
		ErrorModel errorModel = new ErrorModel(HttpStatus.NOT_FOUND.value(), e.getMessage(),
				System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorModel);

	}

	@ExceptionHandler(value = IllegalArgumentException.class)
	public ResponseEntity<ErrorModel> handleIllegalArgumentException(IllegalArgumentException e) {
		log.info("hello ::::  " + e.toString());
		ErrorModel errorModel = new ErrorModel(HttpStatus.BAD_REQUEST.value(), "Illegal argument passed",
				System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorModel);

	}

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

	@GetMapping("/{id}")
	public ResponseEntity<?> getuserById(@PathVariable("id") int id) throws NumberFormatException, MyCustomException {
		UserResponseModel response = userService.getuserById(id);
		if (response == null) {
			throw new MyCustomException("User With id " + id + " nt found");
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(userService.getuserById(id));
		}

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUserById(@PathVariable("id") int id) {
		return ResponseEntity.ok(userService.deleteUserById(id));

	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponseModel> updateUserById(@PathVariable("id") int id,
			@RequestBody UserRequestModel userRequestModel) {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.updateUserById(id, userRequestModel));
	}

	@DeleteMapping
	public ResponseEntity<?> deleteAllUsers() {
		return ResponseEntity.ok(userService.deleteAllUsers());
	}

	@GetMapping("/findByUserId/{userId}")
	public ResponseEntity<UserResponseModel> findByUserId(@PathVariable("userId") String userId) {
		return ResponseEntity.ok(userService.findByUserId(userId));
	}

	@GetMapping("/findByEmail/{email}")
	public ResponseEntity<UserResponseModel> findByEmail(@PathVariable("email") String email)
			throws IllegalArgumentException {
		return ResponseEntity.ok(userService.findByEmail(email));
	}

}
