package com.example.mingpark.controller;

import com.example.mingpark.dto.MemberSignupRequestDto;
import com.example.mingpark.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members/signup")
    public String signup(
            @Valid @ModelAttribute MemberSignupRequestDto request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "redirect:/signup.html?error=validation";
        }

        try {
            memberService.signup(request);
            return "redirect:/signup.html?success=true";
        } catch (IllegalArgumentException e) {
            return "redirect:/signup.html?error=duplicate";
        }
    }
}
