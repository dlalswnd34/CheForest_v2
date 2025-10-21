<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
  int boardId   = request.getParameter("boardId") != null ? Integer.parseInt(request.getParameter("boardId")) : 0;
  int memberIdx = session.getAttribute("memberIdx") == null ? -1 : (int) session.getAttribute("memberIdx");
%>


<!-- ✅ CSRF 토큰 (Spring Security에서 자동 주입) -->
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>

<!-- 좋아요 박스 -->
<div class="like-wrap">
  <button id="likeBtn">♡</button>
  <div id="likeCountText">좋아요 0개</div>
</div>

<!-- CSS -->
<link rel="stylesheet" href="/css/like.css"/>

<!-- 좋아요 기능 JS -->
<script src="/js/like.js"></script>

<script>
  // 🚀 JS 초기화
  initLikeButton({
    likeType: "BOARD",          // 레시피면 "RECIPE"
    boardId: <%= boardId %>,    // 게시글 ID
    recipeId: null,             // 레시피 페이지일 경우 값 세팅
    memberIdx: <%= memberIdx %> // 로그인 안 했으면 -1
  });
</script>
