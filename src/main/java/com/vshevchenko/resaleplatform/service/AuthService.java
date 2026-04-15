package com.vshevchenko.resaleplatform.service;

import com.vshevchenko.resaleplatform.dto.Register;

public interface AuthService {
    boolean login(String userName, String password);

    boolean register(Register register);
}
