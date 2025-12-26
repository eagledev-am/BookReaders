package com.eagledev.bookreaders.config;

import com.eagledev.bookreaders.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepo repo;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(
                     config -> config.requestMatchers(
                             "/api/auth/**" ,
                              "/api-docs/**"  ,
                              "/swagger-ui/**"
                     )
                             .permitAll()
                             .anyRequest()
                             .permitAll()
                    )
                    .sessionManagement(sessionConfig ->
                            sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authenticationProvider(authenticationProvider())
                    .logout(
                            httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer.logoutUrl("/api/logout")
                                    .clearAuthentication(true)
                                    .invalidateHttpSession(true)
                                    .logoutSuccessHandler((req,res,auth) -> SecurityContextHolder.clearContext())
                    );

            return http.build();
    }


    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider(userDetailsService());
        daoProvider.setPasswordEncoder(passwordEncoder());
        return daoProvider;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(){
        return email -> {
            return repo.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("message : user not found!"));
        };
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration){
        return authenticationConfiguration.getAuthenticationManager();
    }

}
