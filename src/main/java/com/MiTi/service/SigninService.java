package com.MiTi.service;

import com.MiTi.dto.SigninDto;
import com.MiTi.entity.Signin;
import com.MiTi.repository.SigninRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SigninService {

    private final SigninRepository signinRepository;

    @Autowired
    public SigninService(SigninRepository signinRepository) {
        this.signinRepository = signinRepository;
    }

    @Transactional
    public Integer savePost(SigninDto signinDTO) {
        Signin signin = signinDTO.toEntity();
        Signin savedSignin = signinRepository.save(signin);
        return savedSignin.getUser_id();
    }
}
