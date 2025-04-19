package com.gcm.backend.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.gcm.backend.models.ERole;
import com.gcm.backend.models.Role;
import com.gcm.backend.models.User;
import com.gcm.backend.payload.request.LoginRequest;
import com.gcm.backend.payload.request.SignupRequest;
import com.gcm.backend.payload.response.JwtResponse;
import com.gcm.backend.payload.response.MessageResponse;
import com.gcm.backend.repository.RoleRepository;
import com.gcm.backend.repository.UserRepository;
import com.gcm.backend.security.jwt.JwtUtils;
import com.gcm.backend.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @RequestMapping(path = "/signin", method = RequestMethod.POST)
  public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles));
  }

  @RequestMapping(path = "/signup", method = RequestMethod.POST)
  public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Email is already in use!"));
    }

    User user = new User(
            signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()),
            signUpRequest.getPassword(),
            encoder.encode(signUpRequest.getPaymentPassword()),
            signUpRequest.getPaymentPassword(),
            signUpRequest.getPhone(),
            signUpRequest.getCountryCode(),
            signUpRequest.getReferralCode()
    );

    Set<Role> roles = new HashSet<>();
    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    roles.add(userRole);

    user.setRoles(roles);
    userRepository.save(user);
    return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);

  }
}
