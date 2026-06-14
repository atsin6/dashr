package io.github.atsin.dashr.controllers;

import io.github.atsin.dashr.TestContainerConfiguration;
import io.github.atsin.dashr.dto.OnboardDriverDto;
import io.github.atsin.dashr.dto.SignupDto;
import io.github.atsin.dashr.entities.User;
import io.github.atsin.dashr.entities.enums.Role;
import io.github.atsin.dashr.repositories.RiderRepository;
import io.github.atsin.dashr.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "100000")
@Import(TestContainerConfiguration.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RiderRepository riderRepository;

    private User user;

    @BeforeEach
    void setUpEach() {
        user = new User();
        user.setId(1L);
        user.setEmail("Prabhakar@gmail.com");
        user.setPassword("password");
        user.setRoles(Set.of(Role.RIDER));
        user.setName("Prabhakar");
    }

    @Test
    void testSignUp_success() {
        SignupDto signupDto = new SignupDto();
        signupDto.setEmail("Prabhakar@gmail.com");
        signupDto.setPassword("password");
        signupDto.setName("Prabhakar");
        webTestClient.post()
                .uri("/auth/signup")
                .bodyValue(signupDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.email").isEqualTo(signupDto.getEmail())
                .jsonPath("$.data.name").isEqualTo(signupDto.getName());
    }

//    @Test
//    @WithUserDetails()
//    void onBoardNewDriver() {
//        if(!userRepository.existsById(1L)) {
//            userRepository.save(user);
//        }
//
//        OnboardDriverDto onboardDriverDto = new OnboardDriverDto();
//        onboardDriverDto.setVehicleId("UP21HASS");
//
//        webTestClient.post()
//                .uri("/auth/onboardDriver/{}", onboardDriverDto.getVehicleId())
//                .bodyValue(onboardDriverDto)
//                .exchange()
//                .expectStatus().isCreated();
//    }
}