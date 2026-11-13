package com.mysite.sbb1.member.repository;

import com.mysite.sbb1.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>  {

}
