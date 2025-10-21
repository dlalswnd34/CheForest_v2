<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${recipe.titleKr}" default="레시피 상세보기"/> - CheForest</title>
  <link rel="stylesheet" href="/css/common/common.css">
  <link rel="stylesheet" href="/css/recipe/recipeview.css">
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
  <link href="https://fonts.googleapis.com/css2?family=Gowun+Dodum:wght@400&display=swap" rel="stylesheet">
  <style>
    .whitespace-pre-line { white-space: pre-line; }
    .empty-box { padding:16px; border:1px dashed #e5e7eb; border-radius:8px; color:#6b7280; background:#fafafa; }
    .lang-en { display: none; }
    .toggle-btn {
      background-color: #f97316;
      color: white;
      font-weight: bold;
      padding: 4px 12px;
      border-radius: 6px;
      font-size: 0.875rem;
      margin-bottom: 12px;
      transition: background-color 0.2s;
      cursor: pointer;
      border: none;
    }
    .toggle-btn:hover { background-color: #d66214; }
  </style>

  <!-- ✅ 로그인 상태에 따라 memberIdx 내려주기 -->
  <sec:authorize access="isAuthenticated()">
    <sec:authentication property="principal" var="loginUser"/>
    <script>
      const memberIdx = "${loginUser.memberIdx}";
      console.log("로그인 사용자 memberIdx:", memberIdx);
    </script>
  </sec:authorize>
  <sec:authorize access="!isAuthenticated()">
    <script>
      const memberIdx = null;
      console.log("비로그인 상태");
    </script>
  </sec:authorize>
</head>
<body>
<jsp:include page="/common/header.jsp"/>

<div class="detail-container">

  <div>
    <div class="nav-content">
      <button class="back-btn" id="backBtn" onclick="location.href='/recipe/list'">
        <svg class="back-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path d="m15 18-6-6 6-6"/>
        </svg>
        <span>레시피 목록으로</span>
      </button>
    </div>
  </div>

  <c:choose>
    <c:when test="${recipe != null}">
      <div class="detail-content">

        <div class="post-header">
          <div class="post-meta">
            <c:set var="cat" value="${fn:trim(recipe.categoryKr)}" />
            <c:choose>
              <c:when test="${cat eq '한식'}"><c:set var="catClass" value="korean"/></c:when>
              <c:when test="${cat eq '양식'}"><c:set var="catClass" value="western"/></c:when>
              <c:when test="${cat eq '중식'}"><c:set var="catClass" value="chinese"/></c:when>
              <c:when test="${cat eq '일식'}"><c:set var="catClass" value="japanese"/></c:when>
              <c:when test="${cat eq '디저트'}"><c:set var="catClass" value="dessert"/></c:when>
              <c:otherwise><c:set var="catClass" value=""/></c:otherwise>
            </c:choose>

            <span class="category-badge ${catClass}">
              <c:out value="${cat}" default="기타"/>
            </span>

            <div class="author-section">

              <button id="toggle-lang-btn" data-current-lang="kr" class="toggle-btn" type="button">
                English로 보기
              </button>

              <h1 class="post-title">
                <span class="lang-kr"><c:out value="${recipe.titleKr}" default="제목없음"/></span>
                <span class="lang-en"><c:out value="${recipe.titleEn}" default="No Title"/></span>
              </h1>

              <!-- ✅ 좋아요 / 조회수 (원래 버튼 복원) -->
              <div class="action-buttons">
                <button class="action-btn like-btn" id="likeBtn" type="button">
                  <svg class="heart-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M19.5 12.5 12 20l-7.5-7.5a5 5 0 0 1 7.5-7 5 5 0 0 1 7.5 7Z"/>
                  </svg>
                  <span class="count" id="likeCountText"><c:out value="${recipe.likeCount}" default="0"/></span>
                </button>

                <div class="action-btn view-btn" title="조회수">
                  <svg class="eye-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"/>
                    <circle cx="12" cy="12" r="3"/>
                  </svg>
                  <span class="count"><c:out value="${recipe.viewCount}" default="0"/></span>
                </div>
              </div>
            </div>
          </div>

          <div class="post-info-grid">
            <div class="info-item flex flex-col items-center justify-center">
              <i data-lucide="clock" class="h-6 w-6 text-orange-500 mb-2"></i>
              <p class="info-label">조리시간</p>
              <p class="info-value">
                <c:out value="${recipe.cookTime}" default="-" />
              </p>
            </div>

            <div class="info-item flex flex-col items-center justify-center">
              <i data-lucide="chef-hat" class="h-6 w-6 text-orange-500 mb-2"></i>
              <p class="info-label">난이도</p>
              <span class="difficulty-badge">
                <c:out value="${recipe.difficulty}" default="-" />
              </span>
            </div>
          </div>
        </div>

        <div class="post-image">
          <img src="<c:out value='${recipe.thumbnail}' default='/images/default_thumbnail.png'/>"
               alt="레시피 이미지" class="main-image"
               onerror="this.src='/images/default_thumbnail.png'">
        </div>

        <div class="recipe-content">
          <!-- 재료 -->
          <div class="recipe-section">
            <div class="section-header">
              <h2 class="text-xl font-bold text-gray-800 border-b-2 pb-1 mb-3">재료</h2>
            </div>
            <div class="ingredients-container">
              <div class="lang-kr">
                <c:choose>
                  <c:when test="${not empty recipe.ingredientDisplayList}">
                    <div class="ingredient-grid">
                      <c:forEach var="item" items="${recipe.ingredientDisplayList}">
                        <div class="ingredient-item">• <span class="ingredient-name"><c:out value="${item}"/></span></div>
                      </c:forEach>
                    </div>
                  </c:when>
                  <c:otherwise><div class="empty-box">등록된 재료가 없습니다.</div></c:otherwise>
                </c:choose>
              </div>

              <div class="lang-en">
                <c:choose>
                  <c:when test="${not empty recipe.ingredientEnDisplayList}">
                    <div class="ingredient-grid">
                      <c:forEach var="item" items="${recipe.ingredientEnDisplayList}">
                        <div class="ingredient-item">• <span class="ingredient-name"><c:out value="${item}"/></span></div>
                      </c:forEach>
                    </div>
                  </c:when>
                  <c:otherwise><div class="empty-box">No ingredients registered.</div></c:otherwise>
                </c:choose>
              </div>
            </div>
          </div>

          <!-- 조리법 -->
          <div class="recipe-section">
            <div class="section-header">
              <h2 class="text-xl font-bold text-gray-800 border-b-2 pb-1 mb-3">조리법</h2>
            </div>
            <div class="cooking-steps">
              <div class="lang-kr">
                <c:choose>
                  <c:when test="${not empty recipe.instructionSteps}">
                    <c:forEach var="step" items="${recipe.instructionSteps}" varStatus="st">
                      <c:if test="${not empty step}">
                        <div class="step-item">
                          <span class="step-number">${st.index + 1}.</span>
                          <span class="step-desc"><c:out value="${step}"/></span>
                        </div>
                      </c:if>
                    </c:forEach>
                  </c:when>
                  <c:otherwise><div class="empty-box">등록된 조리법이 없습니다.</div></c:otherwise>
                </c:choose>
              </div>

              <div class="lang-en">
                <c:choose>
                  <c:when test="${not empty recipe.instructionEnSteps}">
                    <c:forEach var="step" items="${recipe.instructionEnSteps}" varStatus="st">
                      <c:if test="${not empty step}">
                        <div class="step-item">
                          <span class="step-number">${st.index + 1}.</span>
                          <span class="step-desc"><c:out value="${step}"/></span>
                        </div>
                      </c:if>
                    </c:forEach>
                  </c:when>
                  <c:otherwise><div class="empty-box">No instructions registered.</div></c:otherwise>
                </c:choose>
              </div>
            </div>
          </div>
        </div>
      </div>
    </c:when>

    <c:otherwise>
      <div class="detail-content">
        <div class="empty-box">존재하지 않거나 삭제된 레시피입니다.</div>
        <div style="margin-top:16px;">
          <button class="back-btn" onclick="location.href='/recipe/list'">목록으로</button>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
</div>

<!-- 언어 토글 -->
<script>
  document.addEventListener('DOMContentLoaded', function () {
    const toggleButton = document.getElementById('toggle-lang-btn');
    const krElements = document.querySelectorAll('.lang-kr');
    const enElements = document.querySelectorAll('.lang-en');

    if (toggleButton) {
      toggleButton.addEventListener('click', function() {
        const currentLang = this.getAttribute('data-current-lang');
        let newLang = '';
        if (currentLang === 'kr') { newLang = 'en'; this.innerText = '한국어로 보기'; }
        else { newLang = 'kr'; this.innerText = 'English로 보기'; }
        krElements.forEach(el => el.style.display = newLang === 'kr' ? '' : 'none');
        enElements.forEach(el => el.style.display = newLang === 'en' ? 'block' : 'none');
        this.setAttribute('data-current-lang', newLang);
      });
    }
  });
</script>

<!-- jQuery + like.js -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="/js/like.js"></script>
<script>
  document.addEventListener('DOMContentLoaded', function () {
    lucide.createIcons();
    initLikeButton({
      likeType: "RECIPE",
      boardId: null,
      recipeId: "${recipe.recipeId}",
      memberIdx: memberIdx
    });
  });
</script>

<script src="/js/recipe/recipeview.js"></script>
<script src="/js/common/common.js"></script>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>
