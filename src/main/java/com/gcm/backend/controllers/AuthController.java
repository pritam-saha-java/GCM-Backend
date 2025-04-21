package com.gcm.backend.controllers;

import java.util.*;
import java.util.stream.Collectors;

import com.gcm.backend.entity.MessageEntity;
import com.gcm.backend.repository.MessageRepository;
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

import com.gcm.backend.entity.ERole;
import com.gcm.backend.entity.Role;
import com.gcm.backend.entity.User;
import com.gcm.backend.payload.request.LoginRequest;
import com.gcm.backend.payload.request.SignupRequest;
import com.gcm.backend.payload.response.JwtResponse;
import com.gcm.backend.payload.response.MessageResponse;
import com.gcm.backend.repository.RoleRepository;
import com.gcm.backend.repository.UserRepository;
import com.gcm.backend.security.jwt.JwtUtils;
import com.gcm.backend.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*")
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

  @Autowired
  MessageRepository messageRepository;

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

    // ✅ Create new user
    User user = new User();
    user.setUsername(signUpRequest.getUsername());
    user.setEmail(signUpRequest.getEmail());
    user.setPassword(encoder.encode(signUpRequest.getPassword()));
    user.setRawPassword(signUpRequest.getPassword());
    user.setPaymentPassword(encoder.encode(signUpRequest.getPaymentPassword()));
    user.setRawPaymentPassword(signUpRequest.getPaymentPassword());
    user.setPhone(signUpRequest.getPhone());
    user.setCountryCode(signUpRequest.getCountryCode());
    user.setReferralCode(signUpRequest.getReferralCode()); // This is the code user used to join
    user.setBalance(10.00); // 🎁 Welcome bonus
    user.setUserReferralCode(generateSixDigitNumber()); // 👤 This user's own code

    Set<Role> roles = new HashSet<>();
    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    roles.add(userRole);
    user.setRoles(roles);

    userRepository.save(user);

    // 📨 Welcome message
    MessageEntity welcomeMsg = new MessageEntity();
    welcomeMsg.setUserName(user.getUsername());
    welcomeMsg.setMessage("10 USD credited in your account as welcome bonus.");
    messageRepository.save(welcomeMsg);

    // 💸 Referral Commission Distribution (Level 1 to 3)
    distributeRegistrationBonus(user, signUpRequest.getReferralCode());

    return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
  }

  public void distributeRegistrationBonus(User newUser, String refCode) {
    double[] bonuses = {3.0, 2.0, 1.0}; // Level 1 to 3 commission
    for (int level = 0; level < bonuses.length && refCode != null && !refCode.isEmpty(); level++) {
      Optional<User> referrerOpt = userRepository.findByUserReferralCode(refCode);
      if (referrerOpt.isEmpty()) break;

      User referrer = referrerOpt.get();

      // 💰 Add balance
      referrer.setBalance(referrer.getBalance() + bonuses[level]);
      userRepository.save(referrer);

      // 📩 Notification message
      MessageEntity msg = new MessageEntity();
      msg.setUserName(referrer.getUsername());
      msg.setMessage(String.format("%.2f USD credited in your account as level %d referral bonus from %s",
              bonuses[level], level + 1, newUser.getUsername()));
      messageRepository.save(msg);

      // ⬆ Move up to next level
      refCode = referrer.getReferralCode();
    }
  }


  public static String generateSixDigitNumber() {
    Random random = new Random();
    int i = 100000 + random.nextInt(900000);
    return Integer.toString(i);
  }

}
