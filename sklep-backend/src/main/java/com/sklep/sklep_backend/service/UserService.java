package com.sklep.sklep_backend.service;

import com.sklep.sklep_backend.dto.ReqRes;


public interface UserService{
    ReqRes register(ReqRes registrationRequest);
    ReqRes login(ReqRes loginRequest);
    ReqRes refreshToken(ReqRes refreshTokenRequest);
}
