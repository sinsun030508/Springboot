package com.mysite.sbb1.member.controller;


import com.mysite.sbb1.member.dto.MemberDto;
import com.mysite.sbb1.member.repository.MemberRepository;
import com.mysite.sbb1.member.servuce.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @GetMapping("/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginError", "아이디 혹은 비밀번호를 확인하세요.");
        return "member/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("============ LOGOUT ============");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signup(MemberDto memberDto) {
        return "member/signup";
    }




    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("memberDto") MemberDto memberDto,
                         BindingResult bindingResult) {
        log.info("====== memberDto={}", memberDto);

        if(bindingResult.hasErrors()) {
            return "member/signup";
        }

        if(!memberDto.getPassword1().equals(memberDto.getPassword2())){
            bindingResult.rejectValue("password2","passwordIncorrect","입력된 두 개의 패스워드가 일치하지 않습니다");
            return "member/signup";
        }

        memberService.createMember(memberDto);

        return "redirect:/";
    }

}
