package com.openkrishi.OpenKrishi.domain.ngo.dtos;

import com.openkrishi.OpenKrishi.domain.ngo.entity.Member;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class MemberResponseDto {
    private UUID id;
    private String name;
    private String phone;
    private String address;
    private String image;
    private Member.MemberDesignation memberDesignation;
    private UUID ngoId;
}
