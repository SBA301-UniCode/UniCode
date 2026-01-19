package com.example.unicode.service;

import com.example.unicode.entity.Users;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;

import java.text.ParseException;

public interface TokenService {
    String generateToken(Users user) throws JOSEException;
    boolean validateToken(String token) throws JOSEException, ParseException;
}
