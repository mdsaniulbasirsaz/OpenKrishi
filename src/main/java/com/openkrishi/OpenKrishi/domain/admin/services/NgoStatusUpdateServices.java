package com.openkrishi.OpenKrishi.domain.admin.services;

import com.openkrishi.OpenKrishi.domain.user.entity.User;
import com.openkrishi.OpenKrishi.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NgoStatusUpdateServices {

    private final UserRepository userRepository;


    @Autowired
    public NgoStatusUpdateServices(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    // Ngo Status by Admin
    @Transactional
    public boolean activeNgoByEmail(String email)
    {
        Optional<User> userOptional = userRepository.findByEmailAndRole(email, User.Role.NGO);

        if(userOptional.isPresent())
        {
            User ngoUser = userOptional.get();
            ngoUser.setStatus(User.Status.ACTIVE);
            userRepository.save(ngoUser);
            return true;
        }
        return false;
    }
}
