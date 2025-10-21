<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <!-- jQuery CDN -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <meta charset="UTF-8">
    <title>셰프봇</title>
    <!-- CSS -->
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/chatbot.css"/>

    <!-- ✅ CSRF 토큰 (Spring Security에서 자동 주입) -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <!-- ✅ contextPath JS 변수로 주입 -->
    <script>
        var contextPath = "<%=request.getContextPath()%>";
    </script>
</head>
<body>
<!-- 챗봇 버튼 -->
<div id="chatbot-btn">AI</div>

<!-- 챗봇 창 -->
<div id="chatbot-window">
    <div class="chatbot-header">
        <div class="chatbot-header-left">
            <img src="<%=request.getContextPath()%>/images/bear-mascot.png" alt="셰프봇">
            <span>셰프봇</span>
        </div>
        <button id="chatbot-close-btn">✕</button>
    </div>
    <div id="chatbot-messages">
        <!-- 초기 봇 메시지 -->
        <div class="message bot-msg">
            <img src="<%=request.getContextPath()%>/images/bear-mascot.png" alt="셰프봇" class="bot-avatar">
            <div class="bubble">안녕하세요! 저는 셰프봇이에요 🍳</div>
        </div>
    </div>

    <!-- ✅ 토글 화살표 (기본 ▼) -->
    <div id="chatbot-toggle-btn">▼숨기기</div>

    <!-- 빠른 버튼 -->
    <div id="chatbot-quick-buttons">
        <button type="button" class="quick-btn" data-action="추천레시피">사이트레시피<br>랜덤추천</button>
        <button type="button" class="quick-btn" data-action="추천메뉴">유저레시피<br>랜덤추천</button>
        <button type="button" class="quick-btn" data-action="사이트TOP5레시피">사이트TOP5<br>레시피</button>
        <button type="button" class="quick-btn" data-action="유저TOP5레시피">유저TOP5<br>레시피</button>
        <button type="button" class="quick-btn" data-action="문의하기">문의하기</button>
        <button type="button" class="quick-btn" data-action="사이트 이용가이드">사이트<br>이용가이드</button>
    </div>

    <!-- 입력창 -->
    <div id="chatbot-input">
        <input type="text" id="chatbot-user-input" placeholder="메시지를 입력하세요">
        <button id="chatbot-send-btn">▶</button>
    </div>
</div>

<!-- ✅ JS 분리 -->
<script defer src="<%=request.getContextPath()%>/js/chatbot.js"></script>
</body>
</html>
