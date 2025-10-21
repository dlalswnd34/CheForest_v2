<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>비밀번호 찾기 - CheForest</title>
  <link rel="stylesheet" href="/css/common/common.css">
  <link rel="stylesheet" href="/css/auth/findPassword.css">
  <%-- CSRF 토큰 정보를 meta 태그에 추가 --%>
  <meta name="_csrf" content="${_csrf.token}">
  <meta name="_csrf_header" content="${_csrf.headerName}">
</head>
<body>
<main class="find-password-main">
  <div class="find-password-container">

    <!-- 뒤로가기 버튼 -->
    <button class="btn-back" id="btn-back">
      <span class="back-icon">←</span>
      로그인으로 돌아가기
    </button>

    <!-- 비밀번호 찾기 카드 -->
    <div class="find-password-card">

      <!-- 카드 헤더 -->
      <div class="card-header">
        <h1 class="card-title">비밀번호 찾기</h1>
        <p class="card-description">
          아이디와 가입 시 사용한 이메일 주소를 입력해주세요
        </p>
      </div>

      <!-- 카드 콘텐츠 -->
      <div class="card-content">

        <!-- 1단계: 이메일 인증 -->
        <div class="verification-step" id="verification-step">
          <form class="verification-form" id="verification-form">

            <!-- 아이디 입력 -->
            <div class="form-group">
              <label for="user-id" class="form-label">아이디</label>
              <div class="single-input-container">
                <input
                        type="text"
                        id="user-id"
                        class="form-input"
                        placeholder="가입하신 아이디를 입력하세요"
                        autocomplete="username"
                        required
                >
              </div>
            </div>

            <!-- 이메일 입력 -->
            <div class="form-group">
              <label for="email" class="form-label">이메일</label>
              <div class="unified-input-container">
                <div class="input-section">
                  <input
                          type="email"
                          id="email"
                          class="form-input"
                          placeholder="회원가입 시 사용한 이메일"
                          autocomplete="email"
                          required
                  >
                </div>
                <button type="button" class="btn-send-code" id="btn-send-code">
                  <span class="btn-text">인증발송</span>
                </button>
              </div>
            </div>

            <!-- 인증번호 입력 (처음에는 숨김) -->
            <div class="form-group" id="code-group" style="display: none;">
              <label for="email-code" class="form-label">인증번호</label>
              <div class="input-with-button">
                <input
                        type="text"
                        id="email-code"
                        class="form-input"
                        placeholder="인증번호 6자리를 입력하세요"
                        maxlength="6"
                        autocomplete="off"
                >
                <button type="button" class="btn-verify-code" id="btn-verify-code">
                  인증확인
                </button>
              </div>
            </div>

          </form>
        </div>

        <!-- 2단계: 새 비밀번호 입력 (처음에는 숨김) -->
        <div class="reset-step" id="reset-step" style="display: none;">
          <div class="reset-section">
            <h3 class="reset-title">🔒 새 비밀번호 설정</h3>
            <form id="reset-form" class="reset-form">
              <div class="form-group input-icon-wrapper">
                <label for="new-password" class="form-label">새 비밀번호</label>
                <input type="password" id="new-password" class="form-input"
                       placeholder="새 비밀번호를 입력하세요" required minlength="8">
              </div>
              <div class="form-group input-icon-wrapper">
                <label for="confirm-password" class="form-label">비밀번호 확인</label>
                <input type="password" id="confirm-password" class="form-input"
                       placeholder="비밀번호를 다시 입력하세요" required minlength="8">
              </div>
              <button type="submit" class="btn-primary full-width">비밀번호 변경</button>
            </form>
          </div>
        </div>
      </div>

      <!-- 카드 푸터 -->
      <div class="card-footer" id="card-footer">
        <div class="footer-divider"></div>

        <div class="footer-content">
          <div class="footer-section">
            <p class="footer-text">아이디가 기억나지 않으시나요?</p>
            <button class="btn-link" id="btn-footer-find-id">
              아이디 찾기
            </button>
          </div>

          <div class="footer-section">
            <p class="footer-text">아이디와 비밀번호를 모두 기억하시나요?</p>
            <button class="btn-link" id="btn-footer-login">
              로그인하기
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<!-- 🔔 알림 모달 -->
<div id="alertModal" class="modal" style="display:none;">
  <div class="modal-content">
    <span class="close-btn" id="alertModalClose">&times;</span>
    <p id="alertModalMessage"></p>
  </div>
</div>

<script src="/js/common.js"></script>
<script src="/js/auth/findPassword.js"></script>
</body>
</html>
