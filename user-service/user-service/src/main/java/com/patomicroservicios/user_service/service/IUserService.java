package com.patomicroservicios.user_service.service;

import com.patomicroservicios.user_service.dto.request.RegisterUserRequest;
import com.patomicroservicios.user_service.model.User;

public interface IUserService {
    User getUser(String userId);
    void createUser(String userId,RegisterUserRequest userDto);
}
