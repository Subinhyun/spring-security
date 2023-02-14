package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.jwt.JwtTokenProvider;
import org.example.model.*;
import org.example.repository.RefreshTokenRepository;
import org.example.repository.UserRepository;
import org.example.service.PrincipalDetails;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final RefreshTokenRepository refreshTokenRepository;
//
//    private final RefreshToken refreshToken;


    @PostMapping("/signup")
    public UserRequestDto register (@RequestBody UserRequestDto requestDto) {
        userService.register(requestDto);
        return requestDto;
    }

    @PostMapping("/login")
    public Token login(@RequestBody LoginRequestDto requestDto) {
        User member = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 회원입니다."));
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(member.getPassword(), requestDto.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        Token token = jwtTokenProvider.getToken(member.getUsername());
        userService.login(member, token);
        return token;
    }

    @GetMapping("/userinfo")
    @ResponseBody
    public String getUserInfo(@AuthenticationPrincipal PrincipalDetails userDetails){
        if(userDetails != null){ //
            System.out.println("로그인 된 상태입니다.");
            return userDetails.getUser().getUsername();
        }
        return "확인 불가";
    }

}
