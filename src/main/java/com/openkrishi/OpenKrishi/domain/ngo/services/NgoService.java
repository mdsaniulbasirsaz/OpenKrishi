package com.openkrishi.OpenKrishi.domain.ngo.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.AddressUpdateRequestDto;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.NgoUpdateRequestDto;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import com.openkrishi.OpenKrishi.domain.ngo.repository.NgoRepository;
import com.openkrishi.OpenKrishi.domain.user.entity.Address;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import com.openkrishi.OpenKrishi.domain.user.repository.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;


@Service
public class NgoService {

    private final NgoRepository ngoRepository;
    private final Cloudinary cloudinary;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public NgoService(NgoRepository ngoRepository, Cloudinary cloudinary, UserRepository userRepository, JwtService jwtService) {
        this.ngoRepository = ngoRepository;
        this.cloudinary = cloudinary;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }


    public Ngo updateNgo(NgoUpdateRequestDto requestDto, MultipartFile licencePdf, String authToken) throws IOException {
        String token = authToken.replace("Bearer ", "");
        if (!jwtService.validateToken(token)) {
            throw new SecurityException("Invalid or expired token");
        }

        UUID userId = jwtService.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ngo ngo = ngoRepository.findByUser_Id(userId)
                .orElseGet(() -> createNewNgo(user));

        // update managerName
        if (requestDto.getManagerName() != null) {
            ngo.setManagerName(requestDto.getManagerName());
        }

        // upload PDF to Cloudinary if present
        if (licencePdf != null && !licencePdf.isEmpty()) {
            String url = uploadLicencePdf(licencePdf);
            ngo.setLicenceUrl(url);
        }

        return ngoRepository.save(ngo);
    }

    private Ngo createNewNgo(User user) {
        Ngo ngo = new Ngo();
        ngo.setUser(user);
        ngo.setManagerName("Default Manager");
        return ngo;
    }

    private String uploadLicencePdf(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                Map.of("resource_type", "raw", "folder", "ngo_licences"));
        return (String) uploadResult.get("secure_url");
    }

    public String updateNgoAddress(UUID userId, AddressUpdateRequestDto requestDto) {
        Ngo ngo = ngoRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("NGO not found for user"));

        Address address = ngo.getAddress();
        if (address == null) {
            address = new Address();
        }

        if (requestDto.getStreet() != null) address.setStreet(requestDto.getStreet());
        if (requestDto.getHouseNo() != null) address.setHouseNo(requestDto.getHouseNo());
        if (requestDto.getState() != null) address.setState(requestDto.getState());
        if (requestDto.getCity() != null) address.setCity(requestDto.getCity());
        if (requestDto.getPostCode() != null) address.setPostCode(requestDto.getPostCode());
        if (requestDto.getVillage() != null) address.setVillage(requestDto.getVillage());

        ngo.setAddress(address);
        ngoRepository.save(ngo);

        return "Address updated successfully";
    }



}
