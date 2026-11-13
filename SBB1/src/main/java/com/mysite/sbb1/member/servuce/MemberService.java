package com.mysite.sbb1.member.servuce;

import com.mysite.sbb1.member.dto.MemberDto;
import com.mysite.sbb1.member.entity.Member;
import com.mysite.sbb1.member.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    public void createMember(@Valid MemberDto memberDto){
        Member member = new Member().builder()
                .username(memberDto.getUsername())
                .password(passwordEncoder.encode(memberDto.getPassword1()))
                .email(memberDto.getEmail())
                .gender(memberDto.getGender())
                .department(memberDto.getDepartment())
                .registration(memberDto.getRegistration())
                .build();
        memberRepository.save(member);
    }
}
