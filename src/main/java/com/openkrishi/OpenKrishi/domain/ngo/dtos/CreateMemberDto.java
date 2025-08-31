package com.openkrishi.OpenKrishi.domain.ngo.dtos;


import com.openkrishi.OpenKrishi.domain.ngo.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMemberDto {

    private String name;
    private String address;
    private String phone;
    private String image;
    private Member.MemberDesignation memberDesignation;
}
