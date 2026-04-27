package com.cardcase.server.service;

public interface UserService {
    boolean sendEmail(String to, String subject, String text, byte[] imageBytes);

    byte[] vcodeToImage(String vcode);

    String insertUser(String email, String pwd);

    int verifyPassword(String email, String password);

    int getIdByEmail(String email);

    void setPwdByEmail(String email, String password);

    String getCodeByEmail(String email);

    String setCodeByEmail(String email);

    int setTypeByEmail(String email);
}
