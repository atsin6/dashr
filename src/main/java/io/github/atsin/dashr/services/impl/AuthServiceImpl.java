package io.github.atsin.dashr.services.impl;

import io.github.atsin.dashr.dto.DriverDto;
import io.github.atsin.dashr.dto.SignupDto;
import io.github.atsin.dashr.dto.UserDto;
import io.github.atsin.dashr.entities.Driver;
import io.github.atsin.dashr.entities.User;
import io.github.atsin.dashr.entities.enums.Role;
import io.github.atsin.dashr.exceptions.ResourceNotFoundException;
import io.github.atsin.dashr.exceptions.RuntimeConflictException;
import io.github.atsin.dashr.repositories.UserRepository;
import io.github.atsin.dashr.security.JWTService;
import io.github.atsin.dashr.services.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RiderService riderService;
    private final WalletService walletService;
    private final DriverService driverService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    //Login Method Requires Spring Security
    @Override
    public String[] login(String email, String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);


        return new String[] {accessToken, refreshToken};

    }

    @Override
    @Transactional
    public UserDto signUp(SignupDto signupDto) {

        User user = userRepository.findByEmail(signupDto.getEmail()).orElse(null);
        if(user!=null) {
            throw new RuntimeConflictException("User already exists"+signupDto.getEmail());
        }

        User mappedUser = modelMapper.map(signupDto, User.class);
        mappedUser.setRoles(Set.of(Role.RIDER));
        mappedUser.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        User savedUser = userRepository.save(mappedUser);

        ///Creating user related entities
        riderService.createNewRider(savedUser);
        walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public DriverDto onboardNewDriver(Long userId, String vehicleId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + userId + " not found")
        );

        if(user.getRoles().contains(Role.DRIVER)) {
            throw new RuntimeConflictException("User with id " + userId + " is already a driver.");
        }

        Driver newDriver = Driver.builder()
                .user(user)
                .rating(0.00)
                .vehicleId(vehicleId)
                .available(Boolean.TRUE)
                .build();

        user.getRoles().add(Role.DRIVER);
        userRepository.save(user);

        Driver savedDriver = driverService.createNewDriver(newDriver);
        return modelMapper.map(savedDriver, DriverDto.class);
    }

    @Override
    public String refreshToken(String refreshToken) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + userId + " not found")
        );

        return jwtService.generateRefreshToken(user);
    }
}
