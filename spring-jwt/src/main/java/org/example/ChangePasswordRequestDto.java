package org.example;

public class ChangePasswordRequestDto {
    private Member member;
    public String getEmail() {
        return member.getEmail();
    }

    public String getExPassword() {
        return member.getPassword();
    }

    public String getNewPassword() {
        return null;
    }
}
