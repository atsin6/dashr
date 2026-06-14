package io.github.atsin.dashr.services.impl;

import io.github.atsin.dashr.dto.SignupDto;
import io.github.atsin.dashr.dto.UserDto;
import io.github.atsin.dashr.entities.User;
import io.github.atsin.dashr.entities.enums.Role;
import io.github.atsin.dashr.repositories.UserRepository;
import io.github.atsin.dashr.security.JWTService;
import io.github.atsin.dashr.services.DriverService;
import io.github.atsin.dashr.services.RiderService;
import io.github.atsin.dashr.services.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RiderService riderService;

    @Mock
    private WalletService walletService;

    @Mock
    private DriverService driverService;

    @Spy
    private ModelMapper modelMapper;

    @Spy
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private SignupDto signupDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setPassword("password");
        user.setRoles(Set.of(Role.RIDER));
    }

    @Test
    public void testLogin_whenSuccess(){
        //Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access_token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh_token");

        //Act
        String[] tokens = authService.login("email", "password");

        //Assert
        assertThat(tokens).hasSize(2);
        assertThat(tokens[0]).isEqualTo("access_token");
        assertThat(tokens[1]).isEqualTo("refresh_token");

    }

    @Test
    void testSignup_whenSuccess(){
        //Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        //Act
        signupDto = new SignupDto();
        signupDto.setEmail("test@gmail.com");
        signupDto.setPassword("password");
        UserDto userDto = authService.signUp(signupDto);

        //Assert
        assertThat(userDto).isNotNull();
        assertThat(userDto.getEmail()).isEqualTo(signupDto.getEmail());

        verify(riderService).createNewRider(any(User.class));
        verify(walletService).createNewWallet(any(User.class));

    }
}