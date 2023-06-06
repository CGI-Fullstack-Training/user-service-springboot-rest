package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.repo.UserRepo;
import com.example.demo.ui.UserRequestModel;
import com.example.demo.ui.UserResponseModel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private final ModelMapper modelMapper;
	private final UserRepo userRepository;

	@Override
	public UserResponseModel createUser(UserDto userDto) {
		log.info("userdto:: {}  ", userDto);
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
		UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
		userEntity.setUserId(UUID.randomUUID().toString());
		log.info("userEntity :: {} ", userEntity);
		userRepository.save(userEntity);
		UserResponseModel responseModel = modelMapper.map(userEntity, UserResponseModel.class);
		log.info("user saved successufully details are :: {} ", responseModel);
		return responseModel;
	}

	@Override
	public List<UserEntity> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public UserResponseModel getuserById(int id) {
		UserResponseModel responseModel = null;
		Optional<UserEntity> userEntity = userRepository.findById(id);
		if (userEntity.isPresent()) {
			responseModel = modelMapper.map(userEntity, UserResponseModel.class);
		}
		return responseModel;
	}

	@Override
	public String deleteUserById(int id) {
		Optional<UserEntity> userEntityFromDb = userRepository.findById(id);
		if (userEntityFromDb.isPresent()) {
			userRepository.deleteById(id);
			return "User with Id " + id + " deleted successfully";
		}
		return "User with Id " + id + " not found";

	}

	@Override
	public UserResponseModel updateUserById(int id, UserRequestModel requestModel) {
		UserResponseModel responseModel = null;
		Optional<UserEntity> userEntityFromDb = userRepository.findById(id);
		if (userEntityFromDb.isPresent()) {
			UserEntity entity = userEntityFromDb.get();
			entity.setFirstName(requestModel.getFirstName());
			entity.setLastName(requestModel.getLastName());
			entity.setEmail(requestModel.getEmail());
			entity.setPassword(requestModel.getPassword());
			UserEntity user = userRepository.save(entity);
			responseModel = modelMapper.map(user, UserResponseModel.class);
		}
		return responseModel;
	}

	@Override
	public String deleteAllUsers() {
		List<UserEntity> usersList = userRepository.findAll();
		if (usersList.isEmpty()) {
			return "users not exist";
		} else {
			userRepository.deleteAll();
			return "Deleted all users";
		}
	}

	@Override
	public UserResponseModel findByUserId(String userId) {

		UserEntity entity = userRepository.findByUserId(userId);
		return modelMapper.map(entity, UserResponseModel.class);
	}

	@Override
	public UserResponseModel findByEmail(String email) {
		UserEntity entity = userRepository.findByEmail(email);
		return modelMapper.map(entity, UserResponseModel.class);
	}

}
