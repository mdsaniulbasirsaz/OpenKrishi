package com.openkrishi.OpenKrishi.domain.ngo.services;


import com.openkrishi.OpenKrishi.domain.ngo.entity.Member;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import com.openkrishi.OpenKrishi.domain.ngo.repository.MemberRepository;
import com.openkrishi.OpenKrishi.domain.ngo.repository.NgoRepository;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import com.openkrishi.OpenKrishi.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private  final NgoRepository ngoRepository;

    private final UserRepository userRepository;

    public MemberService(MemberRepository memberRepository, NgoRepository ngoRepository, UserRepository userRepository)
    {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.ngoRepository = ngoRepository;
    }



    //---------------Ngo Member Create---------------
    @Transactional
    public Member createMember(String email, UUID ngoId, Member.MemberDesignation designation)
    {
        //Find User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + email));


        // Find NGO
        Ngo ngo = ngoRepository.findByUser_Id(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("Ngo not found with id: " + ngoId));

        // Check Ngo Status
        if(ngo.getUser().getStatus() != User.Status.ACTIVE)
        {
            throw  new IllegalStateException("Ngo is not Active. Cannot add Member.");
        }

        //Check if user is already a member
        if(memberRepository.existsByUserEmail(email)){
            throw new IllegalStateException("User is already a member");
        }

        // Create and save member
        Member member = new Member();
        member.setUser(user);
        member.setNgo(ngo);
        member.setMemberDesignation(designation);

        return memberRepository.save(member);
    }
}
