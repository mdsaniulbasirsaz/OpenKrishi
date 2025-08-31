package com.openkrishi.OpenKrishi.domain.ngo.services;

import com.cloudinary.Cloudinary;
import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.*;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Member;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import com.openkrishi.OpenKrishi.domain.ngo.repository.MemberRepository;
import com.openkrishi.OpenKrishi.domain.ngo.repository.NgoRepository;
import com.openkrishi.OpenKrishi.domain.user.entity.Address;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import com.openkrishi.OpenKrishi.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class NgoService {

    private final NgoRepository ngoRepository;
    private final Cloudinary cloudinary;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;


    public NgoService(MemberRepository memberRepository, NgoRepository ngoRepository, Cloudinary cloudinary, UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.ngoRepository = ngoRepository;
        this.cloudinary = cloudinary;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
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

    //--------------Ngo Create With Address------------
    public User createNgoWithAddress(
            NgoCreateWithAddressDto ngoCreateWithAddressDto
    ) throws IOException{
        User user = new User();
        user.setFullName(ngoCreateWithAddressDto.getFullName());
        user.setEmail(ngoCreateWithAddressDto.getEmail());
        user.setPassword(passwordEncoder.encode(ngoCreateWithAddressDto.getPassword()));
        user.setPhone(ngoCreateWithAddressDto.getPhone());
        user.setStatus(User.Status.INACTIVE);
        user.setRole(User.Role.NGO);
        user.setLatitude(ngoCreateWithAddressDto.getLatitude());
        user.setLongitude(ngoCreateWithAddressDto.getLongitude());


        Ngo ngo = new Ngo();
        ngo.setManagerName(ngoCreateWithAddressDto.getManagerName());
        ngo.setUser(user);
        user.setNgo(ngo);


        if(ngoCreateWithAddressDto.getLicenceUrl() !=null && !ngoCreateWithAddressDto.getLicenceUrl().isEmpty())
        {
            String base64Data = ngoCreateWithAddressDto.getLicenceUrl().split(",")[1];
            byte[] fileBytes = Base64.getDecoder().decode(base64Data);

            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    fileBytes,
                    Map.of("resource_type", "raw", "folder", "ngo_licences")
            );
            ngo.setLicenceUrl((String) uploadResult.get("secure_url"));
        }

        if (ngoCreateWithAddressDto.getAddress() !=null)
        {
            AddressUpdateRequestDto ad = ngoCreateWithAddressDto.getAddress();
            Address address = new Address();
            address.setStreet(ad.getStreet());
            address.setHouseNo(ad.getHouseNo());
            address.setState(ad.getState());
            address.setCity(ad.getCity());
            address.setPostCode(ad.getPostCode());
            address.setVillage(ad.getVillage());

            ngo.setAddress(address);

        }
        return userRepository.save(user);
    }


    // ---------------All Ngo List----------------
    public List<NgoResponseDto> FindAllNgos(){
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == User.Role.NGO && user.getNgo() !=null)
                .map(user -> {
                    Ngo ngo = user.getNgo();
                    Address address = ngo.getAddress();


                    return new NgoResponseDto(
                            user.getId(),
                            user.getFullName(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getLatitude(),
                            user.getLongitude(),
                            ngo.getManagerName(),
                            ngo.getLicenceUrl(),
                            address.getStreet(),
                            address.getHouseNo(),
                            address.getCity(),
                            address.getState(),
                            address.getPostCode(),
                            address.getVillage()
                    );
                })
                .collect(Collectors.toList());
    }


    //------------NGO MEMBER CREATE____________
    public CreateMemberResponseDto createMember(UUID userId, CreateMemberDto requestDto) {
        // Find NGO using userId from JWT
        Ngo ngo = ngoRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("NGO not found for this user"));

        // Check if NGO is active
        if (ngo.getUser().getStatus() != User.Status.ACTIVE) {
            throw new IllegalStateException("NGO is not active. Cannot create member.");
        }

        // Create Member entity
        Member member = new Member();
        member.setNgo(ngo);
        member.setName(requestDto.getName());
        member.setAddress(requestDto.getAddress());
        member.setPhone(requestDto.getPhone());
        member.setMemberDesignation(requestDto.getMemberDesignation());

        // Image upload (optional)
        if (requestDto.getImage() != null && !requestDto.getImage().isEmpty()) {
            try {
                String base64Data = requestDto.getImage().split(",")[1];
                byte[] fileBytes = java.util.Base64.getDecoder().decode(base64Data);
                Map<?, ?> uploadResult = cloudinary.uploader().upload(
                        fileBytes,
                        Map.of("resource_type", "image", "folder", "member_images")
                );
                member.setImage((String) uploadResult.get("secure_url"));
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload member image", e);
            }
        }

        Member savedMember = memberRepository.save(member);

        // Map to response DTO
        CreateMemberResponseDto responseDto = new CreateMemberResponseDto();
        responseDto.setId(savedMember.getId());
        responseDto.setNgoId(savedMember.getNgo().getNgoId());
        responseDto.setName(savedMember.getName());
        responseDto.setAddress(savedMember.getAddress());
        responseDto.setPhone(savedMember.getPhone());
        responseDto.setImage(savedMember.getImage());
        responseDto.setMemberDesignation(savedMember.getMemberDesignation());
        return responseDto;
    }


    // ------- Member List by NgoId------------
    public List<MemberResponseDto> getAllMembers(UUID userId) {
        // Find NGO from userId in JWT
        Ngo ngo = ngoRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("NGO not found for this user"));

        List<Member> members = memberRepository.findAllByNgo(ngo);

        return members.stream().map(member -> {
            MemberResponseDto dto = new MemberResponseDto();
            dto.setId(member.getId());
            dto.setName(member.getName());
            dto.setPhone(member.getPhone());
            dto.setAddress(member.getAddress());
            dto.setImage(member.getImage());
            dto.setMemberDesignation(member.getMemberDesignation());
            dto.setNgoId(member.getNgo().getNgoId());
            return dto;
        }).collect(Collectors.toList());
    }




}
