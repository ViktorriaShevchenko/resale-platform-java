package com.vshevchenko.resaleplatform.service;

import org.springframework.web.multipart.MultipartFile;
import com.vshevchenko.resaleplatform.dto.NewPassword;
import com.vshevchenko.resaleplatform.dto.UpdateUser;
import com.vshevchenko.resaleplatform.dto.User;

public interface UserService {

    User getUser(String email);
    UpdateUser updateUser(String email, UpdateUser updateUser);
    void updatePassword(String email, NewPassword newPassword);
    void updateUserImage(String email, MultipartFile image);
}
