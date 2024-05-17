package com.blaiseCoder.userAuthApp.config;

import com.blaiseCoder.userAuthApp.entities.Role;
import com.blaiseCoder.userAuthApp.filters.JwtAuthenticationFilter;
import com.blaiseCoder.userAuthApp.repositories.UserRepository;
import com.blaiseCoder.userAuthApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//for creating beans configurations
@RequiredArgsConstructor
public class SecurityConfig {
 private final UserRepository userRepository;
 private final UserService userService;
 private  final JwtAuthenticationFilter jwtAuthenticationFilter;
 @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
     httpSecurity.csrf(AbstractHttpConfigurer::disable)
             .authorizeHttpRequests(
                     request->request.requestMatchers("/api/v1/auth/**").permitAll().requestMatchers("/api/v1/admin").hasAnyAuthority(Role.ADMIN.name()).anyRequest().authenticated()


             )
             .sessionManagement(manager->manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
             .authenticationProvider(authenticationProvider()).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

 return httpSecurity.build();


 }
 @Bean
 public AuthenticationProvider  authenticationProvider(){
  DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
  authenticationProvider.setUserDetailsService(userService.userDetailsService());
  authenticationProvider.setPasswordEncoder(passwordEncoder());
return authenticationProvider;
 }
 @Bean
 public PasswordEncoder passwordEncoder(){
  return new BCryptPasswordEncoder();
 }
  @Bean
 public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
  return config.getAuthenticationManager();
  }
}