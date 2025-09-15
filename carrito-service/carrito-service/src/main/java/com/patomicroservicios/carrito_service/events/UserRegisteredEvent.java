package com.patomicroservicios.carrito_service.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisteredEvent {
    private String userId;

    @Override
    public String toString() {
        return "UserRegisteredEvent{userId='" + userId + "'}";
    }
}
