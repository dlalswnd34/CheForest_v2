<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>아이디 찾기 - CheForest</title>
  <link rel="stylesheet" href="/css/common/common.css">
  <link rel="stylesheet" href="/css/auth/findId.css">
  <%-- CSRF 토큰 정보를 meta 태그에 추가 --%>
  <meta name="_csrf" content="${_csrf.token}">
  <meta name="_csrf_header" content="${_csrf.headerName}">
</head>
<body>

<main class="find-id-main">
  <div class="find-id-container">

    <!-- 뒤로가기 버튼 -->
    <button class="btn-back" id="btn-back">
      <span class="back-icon">←</span>
      로그인으로 돌아가기
    </button>

    <!-- 아이디 찾기 카드 -->
    <div class="find-id-card">

      <!-- 카드 헤더 -->
      <div class="card-header">
        <h1 class="card-title">아이디 찾기</h1>
        <p class="card-description">
          회원가입 시 사용한 이메일 주소를 입력해주세요
        </p>
      </div>

      <!-- 카드 콘텐츠 -->
      <div class="card-content">

        <!-- 이메일 인증 단계 -->
        <div class="verification-step" id="verification-step">
          <form class="verification-form" id="verification-form">

            <!-- 이메일 입력 -->
            <div class="form-group">
              <label for="email" class="form-label">이메일</label>
              <div class="unified-input-container">
                <div class="input-section">
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                    <polyline points="22,6 12,13 2,6"/>
                  </svg>
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

        <!-- 아이디 찾기 완료 단계 (처음에는 숨김) -->
        <div class="result-step" id="result-step" style="display: none;">
          <div class="result-content">

            <!-- 성공 아이콘 -->
            <div class="success-icon">
              <span class="check-icon">✓</span>
            </div>

            <!-- 결과 메시지 -->
            <div class="result-message">
              <h3 class="result-title">아이디 찾기 완료</h3>
              <p class="result-description">
                입력하신 이메일로 가입된 아이디입니다
              </p>
            </div>

            <!-- 찾은 아이디 표시 -->
            <div class="found-id-container">
              <div class="found-id-box">
                <span class="user-icon">👤</span>
                <span class="found-id-text" id="found-id-text">사용자아이디</span>
              </div>
            </div>

            <!-- 액션 버튼들 -->
            <div class="result-actions">
              <button class="btn-primary" id="btn-go-login">
                로그인하기
              </button>
              <button class="btn-secondary" id="btn-find-password">
                비밀번호 찾기
              </button>
            </div>

          </div>
        </div>

      </div>

      <!-- 카드 푸터 (이메일 인증 단계에서만 표시) -->
      <div class="card-footer" id="card-footer">
        <div class="footer-divider"></div>

        <div class="footer-content">
          <div class="footer-section">
            <p class="footer-text">비밀번호를 잊으셨나요?</p>
            <button class="btn-link" id="btn-footer-find-password">
              비밀번호 찾기
            </button>
          </div>

          <div class="footer-section">
            <p class="footer-text">이미 아이디를 아시나요?</p>
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
<script src="/js/auth/findId.js"></script>
</body>
</html>
