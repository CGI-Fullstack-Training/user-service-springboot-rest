package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.repo.UserRepo;
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
		log.info("userdto:: {}  ",userDto);
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
		UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
		userEntity.setUserId(UUID.randomUUID().toString());
		log.info("userEntity :: {} ",userEntity);
		userRepository.save(userEntity);
		UserResponseModel responseModel=modelMapper.map(userEntity, UserResponseModel.class);
		log.info("user saved successufully details are :: {} ", responseModel);
		return responseModel;
	}


	@Override
	public List<UserEntity> getAllUsers() {
		return userRepository.findAll();
	}

}
