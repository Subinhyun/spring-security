package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.jwt.JwtTokenProvider;
import org.example.model.*;
import org.example.repository.LogoutAccessTokenRepository;
import org.example.repository.UserRepository;
import org.example.service.PrincipalDetails;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final LogoutAccessTokenRepository logoutAccessTokenRepository;


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
    @Transactional
    public String getUserInfo(@AuthenticationPrincipal PrincipalDetails userDetails, HttpServletRequest request){
        String accessToken = jwtTokenProvider.resolveAccessToken(request);

        if (logoutAccessTokenRepository.existsByUsername(userDetails.getUsername())) {
            if (logoutAccessTokenRepository.existsByAccessToken(accessToken)) {
                throw new IllegalArgumentException("로그아웃 된 상태입니다.");
            }
            logoutAccessTokenRepository.deleteByUsername(userDetails.getUser().getUsername());
        }

        if (userDetails != null){
            System.out.println("로그인 된 상태입니다.");
            return userDetails.getUser().getUsername();
        }

        return "확인 불가";
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@AuthenticationPrincipal PrincipalDetails userDetails, HttpServletRequest request) {

        String accessToken = jwtTokenProvider.resolveAccessToken(request);
//        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        if (userDetails == null) {
            throw new IllegalArgumentException("로그아웃 된 상태입니다.");
        }

        userService.logout(authentication, accessToken);
        return "로그아웃";
    }

}
