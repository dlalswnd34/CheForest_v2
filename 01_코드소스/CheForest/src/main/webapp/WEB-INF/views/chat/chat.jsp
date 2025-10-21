<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>chat</title>
  <link rel="stylesheet" href="/css/chat.css">
</head>
<body>

<!-- 스티키 버튼 -->
<button id="chatBtn">채팅</button>

  <div class="chat-container">
    <header class="chat-header">
      <p><b>와글와글 요리이야기</b></p>
      <button id="chat-close-btn" class="chat-close-btn">x</button>
    </header>

    <div id="chatBox" class="chatBox">
    </div>

    <footer class="chat-input-area">
      <button id="emoji-toggle-btn" class="emoji-toggle-btn">😀</button>
      <input type="text" id="msgInput" placeholder="메시지를 입력하세요..." onkeydown="handleKey(event)">
      <button id="chat-send-btn" class="chat-send-btn" onClick="handleClick(event)">▶</button>
    </footer>

  </div>

<!-- 이모티콘 버튼 영역 -->
<div id="emojiBox" class="emoji-panel" style="display: none;">
</div>

<%--++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++--%>

<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>

<%-- 🌟 sec:authorize를 사용하여 로그인 상태에 따라 안전하게 변수 선언 🌟 --%>
<sec:authorize access="isAuthenticated()">
  <sec:authentication property="principal.member.memberIdx" var="currentMemberIdx" />
  <script>
    const IS_LOGGED_IN = true;
    const MY_ID = Number("${currentMemberIdx}");
  </script>
</sec:authorize>

<sec:authorize access="isAnonymous()">
  <script>
    const IS_LOGGED_IN = false;
    const MY_ID = 0; // 로그인하지 않은 경우 ID는 0
  </script>
</sec:authorize>

<script>
  // 예: /프로젝트명
  const CONTEXT_PATH = "<%= request.getContextPath() %>";
</script>

  <script src="/js/chat.js"></script>

</body>
</html>