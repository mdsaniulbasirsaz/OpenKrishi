package com.openkrishi.OpenKrishi.domain.ngo.dtos;

import com.openkrishi.OpenKrishi.domain.ngo.entity.Member;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateMemberResponseDto {
    private UUID id;
    private UUID ngoId;
    private String name;
    private String address;
    private String phone;
    private String image;
    private Member.MemberDesignation memberDesignation;
}
