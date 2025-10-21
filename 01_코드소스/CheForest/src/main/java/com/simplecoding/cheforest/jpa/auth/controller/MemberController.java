package com.simplecoding.cheforest.jpa.auth.controller;

import com.simplecoding.cheforest.jpa.auth.dto.MemberDetailDto;
import com.simplecoding.cheforest.jpa.auth.dto.MemberSignupDto;
import com.simplecoding.cheforest.jpa.auth.dto.MemberUpdateDto;
import com.simplecoding.cheforest.jpa.auth.dto.UserInfoDto;
import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.repository.MemberRepository;
import com.simplecoding.cheforest.jpa.auth.security.AuthUser;
import com.simplecoding.cheforest.jpa.auth.security.CustomOAuth2User;
import com.simplecoding.cheforest.jpa.auth.service.MemberService;
import com.simplecoding.cheforest.jpa.auth.service.EmailService;
import com.simplecoding.cheforest.jpa.auth.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;
    private final MemberRepository memberRepository;

    // ================= 로그인 페이지 =================
    @GetMapping("/auth/login")
    public String loginView() {
        return "auth/login";
    }

    // ================= 회원가입 페이지 =================
    @GetMapping("/auth/register")
    public String registerView(Model model) {
        model.addAttribute("memberSignupDto", new MemberSignupDto());
        return "auth/signup";
    }

    // ================= 회원가입 처리 =================
    @PostMapping("/auth/register/addition")
    @ResponseBody
    public ResponseEntity<String> register(
            @Valid @ModelAttribute MemberSignupDto dto,
            BindingResult bindingResult,
            HttpSession session) {

        // 입력값 검증
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("❌ 입력값을 확인해주세요.");
        }

        try {
            // 세션에서 인증 완료된 이메일 가져오기
            String verifiedEmail = (String) session.getAttribute("verifiedSignupEmail");

            if (verifiedEmail == null || !verifiedEmail.equals(dto.getEmail())) {
                return ResponseEntity.badRequest().body("❌ 이메일 인증이 완료되지 않았습니다.");
            }

            // 회원 등록
            memberService.register(dto, verifiedEmail);

            // 세션 정리
            session.removeAttribute("verifiedSignupEmail");

            return ResponseEntity.ok("OK");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ 회원가입 처리 중 오류가 발생했습니다.");
        }
    }

    // ================= 회원정보 수정 처리 =================
    @PostMapping("/auth/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> update(@Valid @ModelAttribute MemberUpdateDto dto,
                                                      BindingResult bindingResult,
                                                      @AuthenticationPrincipal AuthUser user) {
        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("success", false);
            response.put("message", "❌ 닉네임은 필수입니다.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Member updatedMember = memberService.update(dto, user.getMember().getMemberIdx());

            // SecurityContext 갱신
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();

            Authentication newAuth;
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails newPrincipal = new CustomUserDetails(updatedMember);
                newAuth = new UsernamePasswordAuthenticationToken(newPrincipal, authentication.getCredentials(), newPrincipal.getAuthorities());
            } else if (principal instanceof CustomOAuth2User oauth2User) {
                CustomOAuth2User newPrincipal = new CustomOAuth2User(updatedMember, oauth2User.getAttributes());
                newAuth = new UsernamePasswordAuthenticationToken(newPrincipal, null, newPrincipal.getAuthorities());
            } else {
                throw new IllegalStateException("지원되지 않는 인증 타입입니다.");
            }

            SecurityContextHolder.getContext().setAuthentication(newAuth);

            response.put("success", true);
            response.put("message", "회원정보가 수정되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "❌ " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

//    회원탈퇴
@PostMapping("/member/withdraw")
public String withdraw(@AuthenticationPrincipal AuthUser user,
                       HttpServletRequest request,
                       RedirectAttributes ra) {

    Member member = user.getMember();
    Long memberIdx = member.getMemberIdx();
    String accessToken = (String) request.getSession().getAttribute("accessToken");

    memberService.withdraw(memberIdx, accessToken);

    SecurityContextHolder.clearContext();
    request.getSession().invalidate();

    ra.addFlashAttribute("msg", "회원 탈퇴가 완료되었습니다. CheForest를 이용해주셔서 감사합니다.");
    return "redirect:/";
}

    // ================= 회원 상세 조회 =================
    @GetMapping("/auth/detail/{id}")
    public String detail(@PathVariable("id") Long memberIdx, Model model) {
        MemberDetailDto detail = memberService.findById(memberIdx);
        model.addAttribute("member", detail);
        return "auth/detail";
    }

    // ================= Ajax: 중복체크 & 이메일 =================
    @GetMapping("/auth/check-id")
    @ResponseBody
    public boolean checkId(@RequestParam String loginId) {
        return !memberService.existsByLoginId(loginId);
    }

    @GetMapping("/auth/check-nickname")
    @ResponseBody
    public boolean checkNickname(@RequestParam String nickname) {
        return !memberService.existsByNickname(nickname);
    }

    // ================= 회원가입: 이메일 인증번호 발송 =================
    @PostMapping("/auth/send-email-code")
    @ResponseBody
    public ResponseEntity<String> sendEmailCode(@RequestParam String email, HttpSession session) {
        try {
            memberService.sendSignupVerificationCode(email, session);
            return ResponseEntity.ok("OK");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("❌ " + e.getMessage());
        } catch (Exception e) {
            log.error("회원가입 이메일 인증 발송 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ 인증번호 발송 중 오류가 발생했습니다.");
        }
    }

    // ================= 회원가입: 인증번호 확인 =================
    @PostMapping("/auth/verify-email-code")
    @ResponseBody
    public ResponseEntity<String> verifyEmailCode(
            @RequestParam String email,
            @RequestParam String code,
            HttpSession session) {
        try {
            memberService.verifySignupAuthCode(email, code, session);
            return ResponseEntity.ok("OK");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    // ================= 아이디 찾기 페이지 =================
    @GetMapping("/auth/find-id")
    public String findIdView() {
        return "auth/findId";
    }

    // ================= 아이디 찾기: 인증번호 발송 =================
    @PostMapping("/auth/find-id/send-code")
    @ResponseBody
    public String sendFindIdCode(@RequestParam String email, HttpSession session) {
        try {
            memberService.sendFindIdCode(email, session);
            return "OK";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            log.error("아이디 찾기 이메일 발송 오류: {}", e.getMessage());
            return "❌ 이메일 전송 중 오류가 발생했습니다.";
        }
    }

    // ================= 아이디 찾기: 인증번호 확인 =================
    @PostMapping("/auth/find-id/verify-code")
    @ResponseBody
    public String verifyFindIdCode(@RequestParam String email,
                                   @RequestParam String code,
                                   HttpSession session) {
        try {
            String loginId = memberService.verifyFindIdCode(email, code, session);
            return loginId;
        } catch (IllegalArgumentException e) {
            return "❌ " + e.getMessage();
        } catch (Exception e) {
            log.error("아이디 찾기 인증 오류: {}", e.getMessage());
            return "❌ 서버 오류가 발생했습니다.";
        }
    }

    // ================= 비밀번호 찾기 페이지 =================
    @GetMapping("/auth/find-password")
    public String findPasswordView() {
        return "auth/findPassword";
    }

    // ================= 비밀번호 찾기: 인증번호 발송 =================
    @PostMapping("/auth/find-password/send-code")
    @ResponseBody
    public String sendPasswordResetCode(@RequestParam String loginId,
                                        @RequestParam String email,
                                        HttpSession session) {
        try {
            memberService.sendPasswordResetCode(loginId, email, session);
            return "OK";
        } catch (IllegalArgumentException e) {
            return "❌ " + e.getMessage();
        }
    }

    // ================= 비밀번호 찾기: 인증번호 검증 =================
    @PostMapping("/auth/find-password/verify-code")
    @ResponseBody
    public String verifyPasswordResetCode(@RequestParam String code,
                                          HttpSession session) {
        try {
            memberService.verifyPasswordResetCode(code, session);
            return "OK";
        } catch (IllegalArgumentException e) {
            return "❌ " + e.getMessage();
        }
    }

    // ================= 비밀번호 찾기: 비밀번호 재설정 =================
    @PostMapping("/auth/find-password/reset")
    @ResponseBody
    public String resetPassword(@RequestParam String newPassword,
                                HttpSession session) {
        try {
            memberService.resetPassword(newPassword, session);
            return "OK";
        } catch (IllegalArgumentException e) {
            return "❌ " + e.getMessage();
        }
    }

//    마이페이지 비밀번호 변경
@PostMapping("/auth/change-password")
@ResponseBody
public ResponseEntity<String> changePassword(
        @RequestBody Map<String, String> request,
        @AuthenticationPrincipal CustomUserDetails user) {

    // 1) 로그인 체크
    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("로그인 후 이용 가능합니다.");
    }

    // 2) 소셜 로그인 계정 차단
    Member member = user.getMember();
    if (member.getProvider() != null && !member.getProvider().isBlank()) {
        return ResponseEntity.badRequest()
                .body("소셜 로그인 계정은 비밀번호 변경이 불가능합니다.");
    }

    String currentPassword = request.get("currentPassword");
    String newPassword     = request.get("newPassword");

    try {
        memberService.changePassword(member.getMemberIdx(), currentPassword, newPassword);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body("❌ " + e.getMessage());
    } catch (Exception e) {
        log.error("비밀번호 변경 중 오류 발생: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("❌ 서버 오류가 발생했습니다.");
    }
}

    // 소셜 로그인 시 중복 닉네임 수정
    @PostMapping("/auth/nickname/update")
    public String updateSocialNickname(@RequestParam String nickname,
                                       RedirectAttributes ra) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 로그인 체크
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            ra.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        // 보안 및 로직 구분을 위한 방어 코드(일반회원이오면 막아주는 역활)
        if (!(auth.getPrincipal() instanceof CustomOAuth2User oauth2User)) {
            ra.addFlashAttribute("msg", "소셜 로그인 사용자만 변경 가능합니다.");
            return "redirect:/";
        }

        Member member = oauth2User.getMember();

        // 닉네임 유효성 검사
        if (nickname == null || nickname.trim().isEmpty()) {
            ra.addFlashAttribute("msg", "닉네임은 필수입니다.");
            return "redirect:/";
        }
        if (memberRepository.existsByNickname(nickname)) {
            ra.addFlashAttribute("msg", "이미 사용 중인 닉네임입니다.");
            return "redirect:/";
        }

        // DB 저장
        member.setNickname(nickname);
        memberRepository.save(member);

        // SecurityContext 갱신
        CustomOAuth2User updatedUser =
                new CustomOAuth2User(member, oauth2User.getAttributes());

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                updatedUser, null, updatedUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        ra.addFlashAttribute("msg", "닉네임이 변경되었습니다.");
        return "redirect:/";
    }

    // ================= Ajax: 이모티콘 사용 정보 조회 (새로 추가) =================
    /**
     * 현재 로그인된 사용자의 닉네임, 등급, 최대 이모티콘 개수 등 정보를 JSON으로 반환
     * @param user 현재 로그인된 사용자의 Principal (CustomUserDetails)
     * @return UserInfoDto (JSON 응답)
     */
    @GetMapping("/api/user/info")
    @ResponseBody // JSON 형태로 응답하기 위해 필수
    public ResponseEntity<UserInfoDto> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails user,
            @AuthenticationPrincipal CustomOAuth2User oauth2User) {

        // 1. Principal에서 Member 객체 추출
        Member member = null;
        if (user != null) {
            // 일반 로그인 사용자
            member = user.getMember();
        } else if (oauth2User != null) {
            // 소셜 로그인 사용자
            member = oauth2User.getMember();
        }

        // 2. 로그인되지 않은 경우 처리
        if (member == null) {
            // HTTP 401 Unauthorized 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 3. DTO로 변환하여 반환
        UserInfoDto userInfoDto = UserInfoDto.from(member);
        return ResponseEntity.ok(userInfoDto);
    }

//    로그인된 사용자 정보를 반환 목적(소셜+기존회원) QNA
    @GetMapping("/auth/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal(expression = "member") Member member) {
        if (member == null) {
            return ResponseEntity.status(401).body(Map.of("authenticated", false));
        }

        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "memberIdx", member.getMemberIdx(),
                "nickname", member.getNickname(),
                "email", member.getEmail(),
                "grade", member.getGrade(),
                "profile", member.getProfile(),
                "role", member.getRole().name()
        ));
    }
}
