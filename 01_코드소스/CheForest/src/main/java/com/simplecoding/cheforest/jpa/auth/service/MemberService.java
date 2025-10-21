package com.simplecoding.cheforest.jpa.auth.service;

import com.simplecoding.cheforest.jpa.auth.dto.MemberAdminDto;
import com.simplecoding.cheforest.jpa.auth.dto.MemberSignupDto;
import com.simplecoding.cheforest.jpa.auth.dto.MemberDetailDto;
import com.simplecoding.cheforest.jpa.auth.dto.MemberUpdateDto;
import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.repository.MemberRepository;
import com.simplecoding.cheforest.jpa.common.MapStruct;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final MapStruct mapStruct;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${NAVER_CLIENT_ID}")
    private String naverClientId;

    @Value("${NAVER_CLIENT_SECRET}")
    private String naverClientSecret;

    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;

    @Value("${KAKAO_CLIENT_SECRET}")
    private String kakaoClientSecret;

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;


    // ================= 회원가입 =================
    public void register(MemberSignupDto dto, String verifiedEmail) {
        // 중복검사
        if (memberRepository.existsByLoginId(dto.getLoginId())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        if (memberRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        // 이메일 인증 확인
        if (verifiedEmail == null || !verifiedEmail.equals(dto.getEmail())) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        // 비밀번호 암호화
        String encodedPw = passwordEncoder.encode(dto.getPassword());

        // 엔티티 변환 및 저장
        Member member = mapStruct.toEntity(dto);
        member.setPassword(encodedPw);
        member.setRole(Member.Role.USER);
        member.setPoint(0L);
        member.setGrade("씨앗");

        memberRepository.save(member);
    }

    // ================= 회원정보 수정 =================
    @Transactional
    public Member update(MemberUpdateDto dto, Long memberIdx) {

        // 1) 현재 사용자를 ID로 조회합니다. 없으면 예외를 발생시킵니다.
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 2) 닉네임 중복 검사 로직 추가
        //    변경하려는 닉네임이 현재 내 닉네임과 다를 경우에만 중복 검사를 수행합니다.
        if (!member.getNickname().equals(dto.getNickname())) {
            if (memberRepository.existsByNickname(dto.getNickname())) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
        }

        // 3) DTO의 값으로 회원 정보(Entity)를 업데이트합니다.
        member.setNickname(dto.getNickname());

        if (dto.getProfile() != null) {
            member.setProfile(dto.getProfile());
        }

        // 4) @Transactional에 의해 메서드가 종료될 때 변경된 내용이 DB에 자동으로 반영됩니다.
        return member;
    }

    // ================= 회원 상세 조회 =================
    @Transactional(readOnly = true)
    public MemberDetailDto findById(Long memberIdx) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        return mapStruct.toDetailDto(member);
    }

    // 🌟🌟🌟 STOMP 채팅 검증을 위해 추가된 메서드 🌟🌟🌟
    /**
     * 로그인 ID(Principal.getName()에서 가져온)를 사용하여 Member 엔티티를 조회합니다.
     * @param loginId 사용자 ID
     * @return Member 엔티티
     * @throws IllegalArgumentException 해당 ID의 사용자가 없을 경우
     */
    @Transactional(readOnly = true)
    public Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다: " + loginId));
    }
    // 🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟


    // ================= 중복검사 =================
    @Transactional(readOnly = true)
    public boolean existsByLoginId(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // ADMIN 통계용 작성한 게시글,댓글수 추가한 전체 회원정보(페이지네이션)
    public Page<MemberAdminDto> adminAllMember(String keyword,Pageable pageable) {
        return memberRepository.findAllWithBoardCounts(keyword, pageable);
    }
    // ADMIN 통계용 작성한 게시글,댓글수 추가한 제재당한 회원정보(페이지네이션)
    public Page<MemberAdminDto> adminSuspendedMember(String keyword,Pageable pageable) {
        return memberRepository.findSuspendedWithBoardCounts(keyword, pageable);
    }

    //    회원 탈퇴
    @Transactional
    public void withdraw(Long memberIdx, String accessToken) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        // DB 마스킹 처리
        member.setLoginId("deleted_" + member.getMemberIdx());
        member.setNickname("탈퇴한 회원_" + member.getMemberIdx());
        member.setEmail("deleted");
        member.setRole(Member.Role.LEFT);

        if (member.getProvider() != null
                && !"deleted".equalsIgnoreCase(member.getProvider())) {
            // 진짜 소셜 회원일 때만 처리
            member.setProvider("deleted");
            member.setSocialId("deleted");
            member.setPassword("SOCIAL_ACCOUNT");
        } else {
            // 일반 회원
            member.setPassword("deleted");
        }

        memberRepository.save(member);

        // 관리자 강제 삭제면 소셜 unlink 건너뜀
        if (accessToken == null) {
            log.info("관리자 강제 삭제 요청 → 소셜 unlink 건너뜀 (memberIdx={})", memberIdx);
            return;
        }

        // 소셜 unlink 호출
        if ("KAKAO".equalsIgnoreCase(member.getProvider())) {
            unlinkKakao(accessToken);
        } else if ("GOOGLE".equalsIgnoreCase(member.getProvider())) {
            unlinkGoogle(accessToken);
        } else if ("NAVER".equalsIgnoreCase(member.getProvider())) {
            unlinkNaver(accessToken);
        }
    }

    private void unlinkKakao(String accessToken) {
        String url = "https://kapi.kakao.com/v1/user/unlink";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> res = restTemplate.postForEntity(url, entity, String.class);
            log.info("카카오 unlink 응답: {}", res.getBody());
        } catch (Exception e) {
            log.error("카카오 unlink 실패", e);
        }
    }

    private void unlinkGoogle(String accessToken) {
        String url = "https://accounts.google.com/o/oauth2/revoke?token=" + accessToken;
        try {
            ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
            log.info("구글 unlink 응답: {}", res.getBody());
        } catch (Exception e) {
            log.error("구글 unlink 실패", e);
        }
    }

    private void unlinkNaver(String accessToken) {
        String url = "https://nid.naver.com/oauth2.0/token?grant_type=delete" +
                "&client_id=" + naverClientId +
                "&client_secret=" + naverClientSecret +
                "&access_token=" + accessToken +
                "&service_provider=NAVER";
        try {
            ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
            log.info("네이버 unlink 응답: {}", res.getBody());
        } catch (Exception e) {
            log.error("네이버 unlink 실패", e);
        }
    }


    // ================= 회원가입: 이메일 인증번호 발송 =================
    public void sendSignupVerificationCode(String email, HttpSession session) {
        // 1️⃣ 중복 이메일 방지
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 2️⃣ 인증번호 발송
        String code = emailService.sendAuthCode(email);
        long expireAt = System.currentTimeMillis() + (5 * 60 * 1000); // 5분 후 만료

        // 3️⃣ 세션에 저장
        session.setAttribute("signupAuthCode", code);
        session.setAttribute("signupAuthEmail", email);
        session.setAttribute("signupAuthExpireAt", expireAt);

        log.info("✅ 이메일 인증번호 발송 완료: {}, 만료 시각 = {}", email, expireAt);
    }

    // ================= 회원가입: 인증번호 검증 =================
    public void verifySignupAuthCode(String email, String inputCode, HttpSession session) {
        String savedCode = (String) session.getAttribute("signupAuthCode");
        String savedEmail = (String) session.getAttribute("signupAuthEmail");
        Long expireAt = (Long) session.getAttribute("signupAuthExpireAt");

        if (savedCode == null || savedEmail == null || expireAt == null) {
            throw new IllegalArgumentException("인증번호를 먼저 요청해주세요.");
        }

        if (!savedEmail.equals(email)) {
            throw new IllegalArgumentException("인증 요청한 이메일이 다릅니다.");
        }

        if (System.currentTimeMillis() > expireAt) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다. 다시 요청해주세요.");
        }

        if (!savedCode.equals(inputCode)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        // ✅ 인증 성공 → 이메일을 ‘검증 완료’ 상태로 표시
        session.setAttribute("verifiedSignupEmail", email);

        // ✅ 임시 세션값 정리
        session.removeAttribute("signupAuthCode");
        session.removeAttribute("signupAuthEmail");
        session.removeAttribute("signupAuthExpireAt");

        log.info("✅ 이메일 인증 성공: {}", email);
    }

    // ================= 아이디 찾기: 인증번호 발송 =================
    public void sendFindIdCode(String email, HttpSession session) {
        // 1️⃣ 이메일 존재 여부 확인
        if (!memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("❌ 가입된 계정을 찾을 수 없습니다.");
        }

        // 2️⃣ 인증번호 발송
        String code = emailService.sendAuthCode(email);
        long expireAt = System.currentTimeMillis() + (5 * 60 * 1000); // 5분 후 만료

        // 3️⃣ 세션 저장
        session.setAttribute("findIdAuthCode", code);
        session.setAttribute("findIdEmail", email);
        session.setAttribute("findIdAuthExpireAt", expireAt);

        log.info("✅ 아이디 찾기 인증번호 발송 완료: {}, 만료시각={}", email, expireAt);
    }

    // ================= 아이디 찾기: 인증번호 검증 =================
    public String verifyFindIdCode(String email, String inputCode, HttpSession session) {
        String savedCode = (String) session.getAttribute("findIdAuthCode");
        String savedEmail = (String) session.getAttribute("findIdEmail");
        Long expireAt = (Long) session.getAttribute("findIdAuthExpireAt");

        if (savedCode == null || savedEmail == null || expireAt == null) {
            throw new IllegalArgumentException("인증번호를 먼저 요청해주세요.");
        }

        if (!savedEmail.equals(email)) {
            throw new IllegalArgumentException("인증 요청한 이메일이 다릅니다.");
        }

        if (System.currentTimeMillis() > expireAt) {
            // 만료 시 세션 초기화
            session.removeAttribute("findIdAuthCode");
            session.removeAttribute("findIdEmail");
            session.removeAttribute("findIdAuthExpireAt");
            throw new IllegalArgumentException("인증번호가 만료되었습니다. 다시 요청해주세요.");
        }

        if (!savedCode.equals(inputCode)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        // ✅ 인증 성공 시 아이디 조회
        String loginId = memberRepository.findIdByEmail(email);
        if (loginId == null) {
            throw new IllegalArgumentException("해당 이메일로 가입된 아이디가 없습니다.");
        }

        // ✅ 세션 정리
        session.removeAttribute("findIdAuthCode");
        session.removeAttribute("findIdEmail");
        session.removeAttribute("findIdAuthExpireAt");

        log.info("✅ 아이디 찾기 인증 성공: {}", email);
        return loginId;
    }

    // ================= 비밀번호 찾기: 인증번호 발송 =================
    public void sendPasswordResetCode(String loginId, String email, HttpSession session) {
        // 1️⃣ 회원 존재 여부 확인
        Member member = memberRepository.findByLoginIdAndEmail(loginId, email)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 이메일이 일치하지 않습니다."));

        // 2️⃣ 인증번호 발송
        String code = emailService.sendAuthCode(email);
        long expireAt = System.currentTimeMillis() + (5 * 60 * 1000); // 5분 후 만료

        // 3️⃣ 세션에 저장
        session.setAttribute("pwResetCode", code);
        session.setAttribute("pwResetEmail", email);
        session.setAttribute("pwResetMemberId", member.getMemberIdx());
        session.setAttribute("pwResetExpireAt", expireAt);

        log.info("✅ 비밀번호 찾기 인증번호 발송 완료: {}, 만료시각={}", email, expireAt);
    }

    // ================= 비밀번호 찾기: 인증번호 검증 =================
    public void verifyPasswordResetCode(String inputCode, HttpSession session) {
        String savedCode = (String) session.getAttribute("pwResetCode");
        String email = (String) session.getAttribute("pwResetEmail");
        Long expireAt = (Long) session.getAttribute("pwResetExpireAt");

        // 1️⃣ 세션 값 유효성 검사
        if (savedCode == null || email == null || expireAt == null) {
            throw new IllegalArgumentException("인증번호를 먼저 요청해주세요.");
        }

        // 2️⃣ 만료 확인
        if (System.currentTimeMillis() > expireAt) {
            // 세션 값 제거
            session.removeAttribute("pwResetCode");
            session.removeAttribute("pwResetEmail");
            session.removeAttribute("pwResetExpireAt");
            throw new IllegalArgumentException("인증번호가 만료되었습니다. 다시 요청해주세요.");
        }

        // 3️⃣ 인증번호 비교
        if (!savedCode.equals(inputCode)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        // ✅ 성공 시: 인증 완료 상태로 표시
        session.setAttribute("verifiedPwResetEmail", email);

        // ✅ 임시 세션값 정리
        session.removeAttribute("pwResetCode");
        session.removeAttribute("pwResetExpireAt");

        log.info("✅ 비밀번호 찾기 이메일 인증 성공: {}", email);
    }

    // ================= 비밀번호 찾기: 비밀번호 재설정 =================
    public void resetPassword(String newPassword, HttpSession session) {
        Long memberIdx = (Long) session.getAttribute("pwResetMemberId");
        if (memberIdx == null) {
            throw new IllegalArgumentException("비밀번호 재설정 권한이 없습니다.");
        }

        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        member.setPassword(passwordEncoder.encode(newPassword));

        // 비밀번호 재설정 완료 후 세션에서 제거
        session.removeAttribute("pwResetMemberId");
    }

    // ================= 마지막 로그인 시간 갱신 =================
    @Transactional
    public void updateLastLoginTime(String loginId) {
        memberRepository.findByLoginId(loginId)
                .ifPresent(member -> {
                    // 최신 LocalDateTime을 구식 Date로 변환합니다.
                    Date date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
                    // 변환된 Date 값을 set 합니다.
                    member.setLastLoginTime(date);
                });
    }

    //  회원 제재하기(admin 용)
    public void applySuspension(Long memberIdx) {
//        Member member = memberRepository.findById(memberIdx)
//                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
//        member.setSuspension("정지");
//        memberRepository.save(member);
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        if (member.getSuspension() == null) {
            member.setSuspension("정지");
        } else {
            member.setSuspension(null);
        }

        memberRepository.save(member);
    }

//    마이페이지 비밀번호 변경
    @Transactional
    public void changePassword(Long memberIdx, String currentPassword, String newPassword) {
        // 1. 회원 조회
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 2. 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 3. 새 비밀번호 검증 (프론트 규칙 동일 적용)
        String pwPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9])(?!.*\\s).{10,20}$";
        if (newPassword == null || !newPassword.matches(pwPattern)) {
            throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자를 포함해 10~20자로 입력하세요.");
        }

        // 3-1 새 비밀번호가 기존과 같은지 확인
        if (passwordEncoder.matches(newPassword, member.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 기존 비밀번호와 달라야 합니다.");
        }

        // 4. 비밀번호 변경
        String encodedPassword = passwordEncoder.encode(newPassword);
        member.setPassword(encodedPassword);

        // 5. update_time 갱신 (BaseTimeEntity가 있다면 자동 갱신됨)
        memberRepository.save(member);

        log.info("✅ 비밀번호 변경 완료 - memberIdx={}, loginId={}", member.getMemberIdx(), member.getLoginId());
    }
}

