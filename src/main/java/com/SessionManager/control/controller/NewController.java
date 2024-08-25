package com.SessionManager.control.controller;

import com.SessionManager.control.Exception.ApiException;
import com.SessionManager.control.dto.AppResponse;
import com.SessionManager.control.dto.ChangePasswordRequest;
import com.SessionManager.control.entity.User;
import com.SessionManager.control.repository.UserRepository;
import com.SessionManager.control.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NewController {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/get")
    public String getStuff(){
        return "hello chuks";
    }


    @Transactional
    @PostMapping("/change")
    public AppResponse<String> changePassword(@RequestBody ChangePasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ApiException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Invalidate all sessions after password change
        userSessionRepository.deleteByUserEmail(user.getEmail());

        return new AppResponse<>(0, "Password changed successfully. All sessions logged out.");
    }
}
