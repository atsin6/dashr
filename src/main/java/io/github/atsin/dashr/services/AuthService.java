package io.github.atsin.dashr.services;

import io.github.atsin.dashr.dto.DriverDto;
import io.github.atsin.dashr.dto.SignupDto;
import io.github.atsin.dashr.dto.UserDto;

public interface AuthService {
    String[] login(String email, String password);
    UserDto signUp(SignupDto signupDto);
    DriverDto onboardNewDriver(Long userId, String vehicleId);

    String refreshToken(String refreshToken);
}
