package com.openkrishi.OpenKrishi.domain.user.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import com.openkrishi.OpenKrishi.domain.ngo.repository.NgoRepository;
import com.openkrishi.OpenKrishi.domain.user.dto.ProfileUpdateRequestDto;
import com.openkrishi.OpenKrishi.domain.user.dto.UserAddressDto;
import com.openkrishi.OpenKrishi.domain.user.dto.UserProfileResponseDto;
import com.openkrishi.OpenKrishi.domain.user.entity.Address;
import com.openkrishi.OpenKrishi.domain.user.entity.Profile;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import com.openkrishi.OpenKrishi.domain.user.repository.UserProfileRepository;
import com.openkrishi.OpenKrishi.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class ProfileService {

    private final UserProfileRepository userProfileRepository;
    private final Cloudinary cloudinary;
    private final JwtService jwtService;
    private final NgoRepository ngoRepository;
    private final UserRepository userRepository;

    public ProfileService(UserProfileRepository userProfileRepository, Cloudinary cloudinary, JwtService jwtService, NgoRepository ngoRepository, UserRepository userRepository){
        this.userProfileRepository = userProfileRepository;
        this.cloudinary = cloudinary;
        this.jwtService = jwtService;
        this.ngoRepository = ngoRepository;
        this.userRepository = userRepository;
    }


    //-----------Get Profile----------------
    public UserProfileResponseDto getUserProfile(UUID userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        UserProfileResponseDto dto = new UserProfileResponseDto();
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());

        Profile profile = user.getProfile();
        if(profile !=null)
        {
            dto.setImage(profile.getImage());
            dto.setDob(profile.getDob());
        }

        if (user.getRole() == User.Role.NGO) {
            Ngo ngo = user.getNgo();
            if(ngo != null) {
                dto.setLicenceUrl(ngo.getLicenceUrl());
                dto.setManagerName(ngo.getManagerName());
                if(ngo.getAddress() != null) {
                    Address address = ngo.getAddress();
                    UserAddressDto userAddressDto = new UserAddressDto();
                    userAddressDto.setAddressId(address.getAddressId());
                    userAddressDto.setStreet(address.getStreet());
                    userAddressDto.setCity(address.getCity());
                    userAddressDto.setHouseNo(address.getHouseNo());
                    userAddressDto.setState(address.getState());
                    userAddressDto.setVillage(address.getVillage());
                    userAddressDto.setPostCode(address.getPostCode());
                    dto.setAddress(userAddressDto);
                }
            }
        }

        return dto;

    }

    public void updateProfile(UUID userId, ProfileUpdateRequestDto profileUpdateRequestDto, MultipartFile imageFile) throws IOException {
        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch profile or create new
        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
            user.setProfile(profile);
        }

        // Upload image to Cloudinary
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = uploadProfileImage(imageFile);
            profile.setImage(imageUrl);
        }

        // Update DOB
        if (profileUpdateRequestDto != null && profileUpdateRequestDto.getDob() != null) {
            profile.setDob(profileUpdateRequestDto.getDob());
        }

        // Save profile
        userProfileRepository.save(profile);
    }

    private String uploadProfileImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "profile_images",
                "overwrite", true,
                "resource_type", "image"
        ));
        return (String) uploadResult.get("secure_url"); // secure_url ব্যবহার করুন
    }


}
