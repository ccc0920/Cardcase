package com.cardcase.server.service;

import java.util.Map;

public interface JwtService {
    String createToken(Map<String, String> map);

    boolean checkToken(String token);
}
