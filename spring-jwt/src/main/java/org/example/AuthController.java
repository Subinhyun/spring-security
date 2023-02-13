package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> singup(@RequestBody MemberRequestDto requestDto){
        return ResponseEntity.ok(authService.singup(requestDto));
    }
    //로그인
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody MemberRequestDto requestDto){
        return ResponseEntity.ok(authService.login(requestDto));
    }
    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reIssue(@RequestBody TokenRequestDto tokenRequestDto){
        return ResponseEntity.ok(authService.reIssue(tokenRequestDto));
    }

}
