package com.openkrishi.OpenKrishi.domain.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import com.openkrishi.OpenKrishi.domain.admin.entity.Profile;
import com.openkrishi.OpenKrishi.domain.admin.repository.ProfileRepository;


@RestController
@RequestMapping("v1/admin")
public class AdminController {

    @Autowired
    private ProfileRepository profileRepository;

    @GetMapping("/profiles")
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    @GetMapping("/success")
    public String Success() {
        return "Swagger Setup Success";
    }
    
}
