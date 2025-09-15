package com.patomicroservicios.user_service.service;

import com.patomicroservicios.user_service.dto.request.RegisterUserRequest;
import com.patomicroservicios.user_service.exceptions.UserNotFoundException;
import com.patomicroservicios.user_service.model.User;
import com.patomicroservicios.user_service.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService{

    @Autowired
    IUserRepository userRepository;

    @Override
    public User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException(userId));
    }

    @Override
    public void createUser(String userId, RegisterUserRequest userDto) {
        User user= new User(userId, userDto.getFirstName(),userDto.getLastName(),userDto.getDni(), userDto.getEmail());
        userRepository.save(user);
    }
}
