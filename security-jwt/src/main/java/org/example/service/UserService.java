package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.*;
import org.example.repository.RefreshTokenRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshToken refreshToken;

    public boolean checkSignupValueCondition(UserRequestDto requestDto){
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        String passwordCheck = requestDto.getPasswordCheck();

        boolean checkValueCondition = true;
        String pattern = "^[a-zA-Z0-9]*$";
        if( !(Pattern.matches(pattern,username) && username.length()>=4 && username.length()<=12) ){
            System.out.println("닉네임이 잘못되었습니다");
            checkValueCondition=false;
        }
        else if( !(Pattern.matches(pattern,password) && password.length()>=4 && password.length()<=32) ){
            System.out.println("비밀번호가 잘못되었습니다");
            checkValueCondition=false;
        }
        else if( !password.equals(passwordCheck) ){
            System.out.println("비밀번호 확인과 일치하지 않습니다");
            checkValueCondition=false;
        }
        return checkValueCondition;
    }

    public UserRequestDto register(UserRequestDto requestDto) {
        if(!checkSignupValueCondition(requestDto)){
            throw new IllegalArgumentException("회원가입 정보가 정확하지 않습니다.");
        };

        Optional<User> found = userRepository.findByUsername(requestDto.getUsername());
        if(found.isPresent()){
            throw new IllegalArgumentException("중복된 사용자 id가 존재합니다.");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword())); ;
        User user = new User(requestDto);
        userRepository.save(user);
        return requestDto;
    }


    public void login(User member, Token token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.id = member.getUsername();
        refreshToken.value = token.getRefreshToken();
        if (refreshTokenRepository.existsByValue(member.getUsername())) {
            refreshTokenRepository.deleteById(member.getUsername());
        }
        refreshTokenRepository.save(refreshToken);
    }

}
