package com.openkrishi.OpenKrishi.domain.farmer.services;


import com.openkrishi.OpenKrishi.domain.farmer.dtos.FarmerCreateRequestDto;
import com.openkrishi.OpenKrishi.domain.farmer.dtos.FarmerResponseDto;
import com.openkrishi.OpenKrishi.domain.farmer.entity.Farmer;
import com.openkrishi.OpenKrishi.domain.farmer.repostitory.FarmerRepository;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Member;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import com.openkrishi.OpenKrishi.domain.ngo.repository.MemberRepository;
import com.openkrishi.OpenKrishi.domain.ngo.repository.NgoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FarmerService {


    private final FarmerRepository farmerRepository;
    private final MemberRepository memberRepository;
    private final NgoRepository ngoRepository;


    public FarmerService(
            FarmerRepository farmerRepository,
            MemberRepository memberRepository,
            NgoRepository ngoRepository
    ) {
        this.farmerRepository = farmerRepository;
        this.memberRepository = memberRepository;
        this.ngoRepository = ngoRepository;
    }

    //---------------------Create Farmer---------------------
    public FarmerResponseDto createFarmer(UUID userId, FarmerCreateRequestDto farmerCreateRequestDto)
    {
        // Find Member
        Ngo ngo = ngoRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("NGO not found with User Id " + userId));



        // Create Farmer
        Farmer farmer = new Farmer();
        farmer.setFarmerName(farmerCreateRequestDto.getFarmerName());
        farmer.setPhone(farmerCreateRequestDto.getPhone());
        farmer.setLatitude(farmerCreateRequestDto.getLatitude());
        farmer.setLongitude(farmerCreateRequestDto.getLongitude());
        farmer.setNgo(ngo);

        Farmer savedFarmer = farmerRepository.save(farmer);

        return mapToDto(savedFarmer);
    }
    //---------------------DTO Mapping---------------------
    private FarmerResponseDto mapToDto(Farmer farmer) {
        FarmerResponseDto responseDto = new FarmerResponseDto();
        responseDto.setId(farmer.getId());
        responseDto.setFarmerName(farmer.getFarmerName());
        responseDto.setPhone(farmer.getPhone());
        responseDto.setLatitude(farmer.getLatitude());
        responseDto.setLongitude(farmer.getLongitude());
        responseDto.setNgoId(farmer.getNgo().getNgoId());
        return responseDto;
    }


    //--------- Get All Farmer-------------
    public List<FarmerResponseDto> getAllFarmers(){
        List<Farmer> farmers = farmerRepository.findAll();

        return  farmers.stream().map(this::mapToDto).collect(Collectors.toList());
    }
}
