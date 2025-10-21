<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입 - CheForest</title>
    <link rel="stylesheet" href="/css/auth/signup.css">

    <!-- ✅ CSRF 토큰 (AJAX용) -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Gowun+Dodum:wght@400&display=swap" rel="stylesheet">
    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
</head>
<body>
<div class="signup-container">
    <div class="signup-wrapper">
        <!-- 뒤로가기 버튼 -->
        <button class="back-button" onclick="showPage('home')">
            <i data-lucide="arrow-left"></i>
            홈으로 돌아가기
        </button>

        <!-- 회원가입 카드 -->
        <div class="signup-card">
            <!-- 헤더 -->
            <div class="signup-header">
                <h1 class="brand-title">CheForest</h1>
                <p class="brand-subtitle">요리의 숲으로 함께 떠나볼까요?</p>
            </div>

            <!-- 회원가입 폼 -->
            <div class="signup-content">
                <form id="signupForm" class="signup-form">

                    <!-- 아이디 -->
                    <div class="form-group">
                        <label for="userId" class="form-label">아이디</label>
                        <div class="input-container flex-1">
                            <i data-lucide="user" class="input-icon"></i>
                            <input
                                    type="text"
                                    id="userId"
                                    name="userId"
                                    placeholder="아이디를 입력하세요"
                                    class="form-input"
                                    required
                                    oninput="clearError('userId')"
                            >
                        </div>
                        <div class="success-message" id="userIdSuccess" style="display: none;">
                            <i data-lucide="check-circle"></i>
                            <span>사용 가능한 아이디입니다.</span>
                        </div>
                        <div class="field-error" id="userIdError">
                            <i data-lucide="alert-circle"></i>
                            <span></span>
                        </div>
                    </div>

                    <!-- 이메일 -->
                    <div class="form-group">
                        <label for="email" class="form-label">이메일</label>
                        <div class="input-row">
                            <div class="input-container flex-1">
                                <i data-lucide="mail" class="input-icon"></i>
                                <input
                                        type="email"
                                        id="email"
                                        name="email"
                                        placeholder="인증받을 이메일을 입력하세요"
                                        class="form-input"
                                        required
                                        oninput="clearError('email')"
                                >
                            </div>
                            <button type="button" id="sendEmailBtn" class="verify-button" disabled>
                                인증발송
                            </button>
                        </div>
                        <div class="field-error" id="emailError">
                            <i data-lucide="alert-circle"></i>
                            <span></span>
                        </div>
                        <!-- 인증번호 발송 안내 문구 -->
                        <div class="success-message" id="emailSentMessage" style="display: none;">
                            <i data-lucide="check-circle"></i>
                            <span>인증번호가 발송되었습니다. 메일을 확인해주세요.</span>
                        </div>
                    </div>

                    <!-- 이메일 인증번호 -->
                    <div class="form-group verification-section" id="emailVerificationSection" style="display: none;">
                        <div class="verification-container">
                            <label for="emailCode" class="form-label">인증번호</label>
                            <div class="input-row">
                                <input
                                        type="text"
                                        id="emailCode"
                                        name="emailCode"
                                        placeholder="인증번호 6자리를 입력하세요"
                                        class="form-input flex-1"
                                        maxlength="6"
                                        oninput="clearError('emailCode')"
                                >
                                <button type="button" id="verifyEmailBtn" class="verify-button">
                                    인증확인
                                </button>
                            </div>
                            <div class="success-message" id="emailSuccess" style="display: none;">
                                <i data-lucide="check-circle"></i>
                                <span>이메일 인증이 완료되었습니다.</span>
                            </div>
                            <div class="field-error" id="emailCodeError">
                                <i data-lucide="alert-circle"></i>
                                <span></span>
                            </div>
                        </div>
                    </div>

                    <!-- 비밀번호 -->
                    <div class="form-group">
                        <label for="password" class="form-label">비밀번호</label>
                        <div class="input-container">
                            <i data-lucide="lock" class="input-icon"></i>
                            <input
                                    type="password"
                                    id="password"
                                    name="password"
                                    placeholder="비밀번호를 입력하세요"
                                    class="form-input"
                                    required
                            >
                        </div>
                        <div class="help-text">
                            * 10~20자, 영문, 숫자, 특수문자 포함
                        </div>
                        <div class="field-error" id="passwordError">
                            <i data-lucide="alert-circle"></i>
                            <span></span>
                        </div>
                    </div>

                    <!-- 비밀번호 확인 -->
                    <div class="form-group">
                        <label for="confirmPassword" class="form-label">비밀번호 확인</label>
                        <div class="input-container">
                            <i data-lucide="lock" class="input-icon"></i>
                            <input
                                    type="password"
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    placeholder="비밀번호를 다시 입력하세요"
                                    class="form-input"
                                    required
                            >
                        </div>
                        <div class="password-match" id="passwordMatch"></div>
                        <div class="field-error" id="confirmPasswordError">
                            <i data-lucide="alert-circle"></i>
                            <span></span>
                        </div>
                    </div>

                    <!-- 닉네임 -->
                    <div class="form-group">
                        <label for="nickname" class="form-label">닉네임</label>
                        <div class="input-container flex-1">
                            <i data-lucide="user" class="input-icon"></i>
                            <input
                                    type="text"
                                    id="nickname"
                                    name="nickname"
                                    placeholder="사용할 닉네임을 입력하세요"
                                    class="form-input"
                                    required
                                    oninput="clearError('nickname')"
                            >
                        </div>
                        <div class="success-message" id="nicknameSuccess" style="display: none;">
                            <i data-lucide="check-circle"></i>
                            <span>사용 가능한 닉네임입니다.</span>
                        </div>
                        <div class="field-error" id="nicknameError">
                            <i data-lucide="alert-circle"></i>
                            <span></span>
                        </div>
                    </div>

                    <!-- 회원가입 버튼 -->
                    <button type="submit" id="signupBtn" class="signup-button">
                        <i data-lucide="user-plus"></i>
                        회원가입
                    </button>
                </form>
            </div>

            <!-- 푸터 -->
            <div class="signup-footer">
                <div class="separator"></div>
                <div class="footer-content">
                    <p class="footer-text">이미 계정이 있으시나요?</p>
                    <button class="login-link" onclick="showPage('login')">
                        로그인하기
                    </button>
                </div>
            </div>
        </div>

        <!-- 등급 안내 링크 -->
        <div class="grade-link">
            <button onclick="showPage('grade')">
                CheForest 등급 시스템 알아보기 →
            </button>
        </div>
    </div>
</div>

<!-- ✅ 토스트 -->
<div id="toast" class="toast" style="display:none;">
    <span id="toastMessage"></span>
</div>

<!-- ✅ 성공 모달 -->
<div id="successModal" class="modal" style="display:none;">
    <div class="modal-content">
        <h2 id="modalTitle"></h2>
        <p id="modalMessage"></p>
        <button id="modalOkBtn">확인</button>
    </div>
</div>

<script src="/js/auth/signup.js"></script>
<script src="/js/common/common.js"></script>
<script>
    lucide.createIcons();
</script>
</body>
</html>
