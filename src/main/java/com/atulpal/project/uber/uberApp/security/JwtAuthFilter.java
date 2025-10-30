package com.atulpal.project.uber.uberApp.security;


import com.atulpal.project.uber.uberApp.entities.User;
import com.atulpal.project.uber.uberApp.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;


    //It is used to pass the exception from 1 context to another context
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            final String requestTokenHeader = request.getHeader("Authorization");

            //If requestTokenHeader is null or not starts with "Bearer " then do nothing
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
                filterChain.doFilter(request, response);
                return;
            }

            //If requestTokenHeader is not null and starts with "Bearer " then
            //extract the token without the "Bearer "
            String token = requestTokenHeader.substring(7);
            //Validating and Extracting UserId from the token using JwtService
            Long userId = jwtService.getUserIdFromToken(token);



            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //validate the userId with the database user
                User user = userService.getUserById(userId);

                //putting the user into spring security context holder
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationToken.setDetails(
                        // this will set some more user info. like IP Address and other user related info. in authenticationToken
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);
        }catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }
}
