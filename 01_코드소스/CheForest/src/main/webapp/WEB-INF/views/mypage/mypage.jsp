<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>마이페이지 - CheForest</title>
    <link rel="stylesheet" href="/css/common/common.css">
    <link rel="stylesheet" href="/css/mypage.css">
    <!-- Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
    <%-- CSRF 토큰 정보를 meta 태그에 추가 --%>
    <meta name="_csrf" content="${_csrf.token}">
    <meta name="_csrf_header" content="${_csrf.headerName}">
</head>
<body data-social="${me.member.provider != null}">
<jsp:include page="/common/header.jsp"/>
<main class="mypage-main" data-member-idx="${currentMemberIdx}">
    <sec:authentication var="me" property="principal"/>

    <%-- JSTL: URL 파라미터를 확인하여 현재 활성화할 탭을 결정합니다. 기본값은 'profile'입니다. --%>
    <c:set var="activeTab" value="${empty param.tab ? 'profile' : param.tab}" />

    <%-- ✅ 프로필 경로 지정 (사이드바 방식 동일) --%>
    <c:set var="profileUrl"
           value="${(not empty me.member and not empty me.member.profile) ? me.member.profile : '/images/default_profile.png'}"/>
    <section class="mypage-header">
        <div class="container">
            <div class="profile-info">
                <div class="profile-avatar" style="position: relative; display: inline-block;">
                    <img
                            id="profile-image"
                            src="<c:out value='${profileUrl}'/>"
                            alt="프로필 이미지"
                            class="profile-image"
                            onerror="this.src='/images/default_profile.png'"
                    />
                    <!-- 수정 버튼 -->
                    <label for="profile-upload-input" class="btn-profile-edit">
                        프로필 수정
                    </label>
                    <input type="file" id="profile-upload-input" accept="image/*" style="display:none;">
                </div>
                <div class="profile-details">
                    <input type="hidden" id="provider" value="${me.member.provider}" />
                    <h1 class="profile-title"><c:out value="${me.member.nickname}"/>님의 마이페이지</h1>
                    <div class="profile-meta">
            <span class="level-badge">
              <span class="user-grade grade-${currentLevel}"><c:out value="${currentLevel}"/></span>
            </span>
                        <span class="join-date">가입일 : <c:out value="${joinDate}" default="-" /></span>
                    </div>
                    <div class="level-progress">
            <span class="progress-label">
              <c:if test="${nextLevel != null}">다음 등급까지</c:if>
              <c:if test="${nextLevel == null}">최고 등급 달성!</c:if>
            </span>
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: ${progressPercentage}%"></div>
                        </div>
                        <span class="progress-text">${progressPercentage}%</span>
                    </div>
                </div>
            </div>
        </div>
    </section>



    <section class="stats-section">
        <div class="container">
            <div class="stats-grid">
                <%-- 작성한 레시피 --%>
                <div class="stat-card stat-recipes">
                    <div class="stat-icon">👨‍🍳</div>
                    <div class="stat-number">
                        <fmt:formatNumber value="${empty myPostsTotalCount ? 0 : myPostsTotalCount}" />
                    </div>
                    <div class="stat-label">작성한 레시피</div>
                </div>
                <%-- 작성한 댓글 --%>
                <div class="stat-card stat-comments">
                    <div class="stat-icon">💬</div>
                    <div class="stat-number">
                        <fmt:formatNumber value="${empty myCommentsTotalCount ? 0 : myCommentsTotalCount}" />
                    </div>
                    <div class="stat-label">작성한 댓글</div>
                </div>

                <%-- 받은 좋아요 --%>
                <div class="stat-card stat-likes">
                    <div class="stat-icon">❤️</div>
                    <div class="stat-number">
                        <fmt:formatNumber value="${empty receivedLikesTotalCount ? 0 : receivedLikesTotalCount}" />
                    </div>
                    <div class="stat-label">받은 좋아요</div>
                </div>
                <div class="stat-card stat-views">
                    <div class="stat-icon">🔍</div>
                    <div class="stat-number"><fmt:formatNumber value="${myPostsTotalViewCount}" type="number"/></div>
                    <div class="stat-label">총 조회수</div>
                </div>
            </div>
        </div>
    </section>

    <section class="content-section">
        <div class="container">
            <div class="content-layout">
                <aside class="sidebar">
                    <div class="sidebar-content">
                        <h3 class="sidebar-title"><span class="sidebar-icon">👤</span>마이메뉴</h3>
                        <div class="menu-list">
                            <%-- JSTL: activeTab에 따라 active 클래스 적용 --%>
                            <button class="menu-item ${activeTab == 'profile' ? 'active' : ''}" data-tab="profile"><span class="menu-icon">👤</span><span class="menu-text">프로필</span></button>
                            <button class="menu-item ${activeTab == 'recipes' ? 'active' : ''}" data-tab="recipes"><span class="menu-icon">👨‍🍳</span><span class="menu-text">내 레시피</span></button>
                            <button class="menu-item ${activeTab == 'comments' ? 'active' : ''}" data-tab="comments"><span class="menu-icon">💬</span><span class="menu-text">내 댓글</span></button>
                            <button class="menu-item ${activeTab == 'liked' ? 'active' : ''}" data-tab="liked"><span class="menu-icon">❤️</span><span class="menu-text">좋아요</span></button>
                            <button class="menu-item ${activeTab == 'inquiries' ? 'active' : ''}" data-tab="inquiries"><span class="menu-icon">❓</span><span class="menu-text">문의 내역</span></button>
                            <button class="menu-item ${activeTab == 'settings' ? 'active' : ''}" data-tab="settings"><span class="menu-icon">⚙️</span><span class="menu-text">설정</span></button>
                        </div>
                    </div>
                </aside>

                <section class="main-content">

                    <%-- JSTL: activeTab에 따라 active 클래스 적용 --%>
                    <div class="tab-content ${activeTab == 'profile' ? 'active' : ''}" id="tab-profile">
                        <div class="content-grid">
                            <div class="info-card">
                                <div class="card-header">
                                    <h3 class="card-title"><span class="title-icon">🏆</span>등급 현황</h3>
                                </div>
                                <div class="card-content">
                                    <div class="level-status">
                                        <div class="current-level">
                                            <h4>현재 등급</h4>
                                            <div class="level-display">
                                                <div class="level-emoji">
                                                    <c:choose>
                                                        <c:when test="${currentLevel == '씨앗'}">&#x26A1;</c:when>
                                                        <c:when test="${currentLevel == '뿌리'}">&#x2693;</c:when>
                                                        <c:when test="${currentLevel == '새싹'}">&#x1F331;</c:when>
                                                        <c:when test="${currentLevel == '나무'}">&#x1F332;</c:when>
                                                        <c:when test="${currentLevel == '숲'}">&#x1F333;</c:when>
                                                        <c:otherwise>⭐</c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="level-name"><c:out value="${currentLevel}"/></div>
                                                <div class="level-desc">현재 <fmt:formatNumber value="${userPoints}" /> 포인트</div>
                                            </div>
                                        </div>

                                        <%-- 최고 등급이 아닐 경우에만 '다음 등급' 섹션을 표시 --%>
                                        <c:if test="${nextLevel != null}">
                                            <div class="next-level">
                                                <h4>다음 등급</h4>
                                                <div class="level-display">
                                                    <div class="level-emoji">
                                                        <c:choose>
                                                            <c:when test="${nextLevel == '뿌리'}">&#x2693;</c:when>
                                                            <c:when test="${nextLevel == '새싹'}">&#x1F331;</c:when>
                                                            <c:when test="${nextLevel == '나무'}">&#x1F332;</c:when>
                                                            <c:when test="${nextLevel == '숲'}">&#x1F333;</c:when>
                                                            <c:otherwise>🚀</c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div class="level-name"><c:out value="${nextLevel}"/></div>
                                                    <div class="level-desc"><fmt:formatNumber value="${pointsNeeded}" /> 포인트 더 필요</div>
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>

                            <div class="info-card">
                                <div class="card-header">
                                    <h3 class="card-title"><span class="title-icon">📈</span>활동 현황</h3>
                                </div>
                                <div class="card-content">
                                    <div class="activity-list">
                                        <div class="activity-item">
                                            <span class="activity-label">금주 레시피 작성</span>
                                            <span class="activity-badge"><c:out value="${totalRecipes != null ? totalRecipes : 0}"/>개</span>
                                        </div>
                                        <div class="activity-item">
                                            <span class="activity-label">금주 댓글 작성</span>
                                            <span class="activity-badge"><c:out value="${totalComments != null ? totalComments : 0}"/>개</span>
                                        </div>
                                        <div class="activity-item">
                                            <span class="activity-label">금주 받은 좋아요 수</span>
                                            <span class="activity-badge"><c:out value="${totalLikes != null ? totalLikes : 0}"/>회</span>
                                        </div>
                                        <div class="activity-item">
                                            <span class="activity-label">금주 포인트 획득 량</span>
                                            <span class="activity-badge"><c:out value="${weeklyPoints != null ? weeklyPoints : 0}"/>개</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <%-- ===================== 내 레시피 (AJAX) ===================== --%>
                    <div class="tab-content ${activeTab == 'recipes' ? 'active' : ''}" id="tab-recipes">
                        <div class="tab-header">
                            <h2 class="tab-title">작성한 레시피 (<c:out value="${myPostsTotalCount}" default="0"/>개)</h2>

                            <a href="/board/add" class="btn-primary" id="btn-create-recipe">
                                <span class="btn-icon">👨‍🍳</span> 새 레시피 작성
                            </a>
                        </div>

                        <%-- 🔹 AJAX로 렌더링될 영역 --%>
                        <div class="recipe-list" id="my-posts-list-container"></div>
                        <div class="pagination-container" id="my-posts-pagination"></div>
                    </div>

                    <%-- ===================== 내 댓글 (AJAX) ===================== --%>
                    <div class="tab-content ${activeTab == 'comments' ? 'active' : ''}" id="tab-comments">
                        <h2 class="tab-title">
                            작성한 댓글 (<fmt:formatNumber value="${empty myCommentsTotalCount ? 0 : myCommentsTotalCount}" />개)
                        </h2>

                        <%-- 🔹 AJAX로 렌더링될 영역 --%>
                        <div class="comment-list" id="my-comments-list-container"></div>
                        <div class="pagination-container" id="my-comments-pagination"></div>
                    </div>

                    <%-- ===================== 좋아요 (AJAX) ===================== --%>
                    <div class="tab-content ${activeTab == 'liked' ? 'active' : ''}" id="tab-liked">
                        <div class="mypage-like-head">
                            <h2 class="tab-title">좋아요한 레시피</h2>

                            <div class="mypage-like-tabs" role="tablist">
                                <button type="button"
                                        class="mypage-like-tabbtn is-active"
                                        data-like-tab="admin"
                                        aria-controls="mypage-like-pane-admin"
                                        aria-selected="true">
                                    <i data-lucide="chef-hat" class="h-5 w-5 text-orange-500"></i>
                                    <span class="brand-gradient">CheForest</span>
                                    <span>레시피</span>
                                </button>
                                <button type="button"
                                        class="mypage-like-tabbtn"
                                        data-like-tab="user"
                                        aria-controls="mypage-like-pane-user"
                                        aria-selected="false">
                                    🧑‍🍳 사용자 레시피
                                </button>
                            </div>
                        </div>

                        <%-- 🔹 CheForest 레시피 --%>
                        <div id="mypage-like-pane-admin" class="mypage-like-pane is-active" role="tabpanel">
                            <div class="mypage-like-list" id="liked-recipes-list-container"></div>
                            <div class="pagination-container" id="liked-recipes-pagination"></div>
                        </div>

                        <%-- 🔹 사용자 레시피 --%>
                        <div id="mypage-like-pane-user" class="mypage-like-pane" role="tabpanel">
                            <div class="mypage-like-list" id="liked-posts-list-container"></div>
                            <div class="pagination-container" id="liked-posts-pagination"></div>
                        </div>
                    </div>

                    <%-- ===================== 문의 내역 (기존 AJAX 유지) ===================== --%>
                    <div class="tab-content ${activeTab == 'inquiries' ? 'active' : ''}" id="tab-inquiries">
                        <div class="tab-header">
                            <h2 class="text-xl font-bold">문의 내역 (<span id="inquiry-count">...</span>개)</h2>
                            <button class="btn btn-primary" onclick="goToInquiry()">새 문의하기</button>
                        </div>

                        <div id="inquiry-filters" style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                            <div>
                                <label for="inquiry-status" style="margin-right:5px;">상태:</label>
                                <select id="inquiry-status" class="filter-select">
                                    <option value="all" selected>전체</option>
                                    <option value="대기중">답변대기</option>
                                    <option value="답변완료">답변완료</option>
                                </select>
                            </div>
                            <div>
                                <label for="inquiry-sort" style="margin-right:5px;">정렬:</label>
                                <select id="inquiry-sort" class="filter-select">
                                    <option value="desc" selected>최신순</option>
                                    <option value="asc">오래된순</option>
                                </select>
                            </div>
                        </div>
                        <div class="inquiries-list" id="inquiries-list-container">
                            <p class="loading-message">문의 내역을 불러오는 중...</p>
                        </div>

                        <div class="pagination-container" id="inquiries-pagination"></div>
                    </div>

                    <%-- ===================== 설정 ===================== --%>
                    <div class="tab-content ${activeTab == 'settings' ? 'active' : ''}" id="tab-settings">
                        <div class="settings-grid">
                            <div class="settings-card">
                                <form id="profile-update-form" action="/auth/update" method="post">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                                    <div class="card-header">
                                        <h3 class="card-title"><span class="title-icon">👤</span>계정 정보</h3>
                                    </div>
                                    <div class="card-content">
                                        <div class="form-grid">
                                            <!-- 닉네임 -->
                                            <div class="form-group">
                                                <label for="nickname">닉네임</label>
                                                <input type="text" id="nickname" name="nickname"
                                                       value="<c:out value='${me.member.nickname}'/>"
                                                       autocomplete="off" />

                                                <!-- 안내문 / 상태메시지 -->
                                                <div id="nicknameHelp" class="help-text" style="color:#6b7280; font-size:0.85rem;">
                                                    * 닉네임은 2자 이상 입력하세요.
                                                </div>
                                                <div id="nicknameSuccess" class="success-message" style="display:none; color:#16a34a; font-size:0.85rem;">
                                                    사용 가능한 닉네임입니다.
                                                </div>
                                                <div id="nicknameError" class="field-error" style="display:none; color:#dc2626; font-size:0.85rem;">
                                                    <span></span>
                                                </div>
                                            </div>
                                            <!-- 이메일 (읽기 전용) -->
                                            <div class="form-group">
                                                <label for="email">이메일</label>
                                                <input type="email"
                                                       value="<c:out value='${me.member.email}'/>" disabled>
                                            </div>
                                        </div>
                                        <button type="submit" class="btn-primary">프로필 업데이트</button>
                                    </div>
                                </form>
                            </div>

                            <c:choose>
                                <c:when test="${not empty me.member.provider}">
                                    <!-- 소셜 로그인 사용자 -->
                                    <div class="settings-card">
                                        <div class="card-header">
                                            <h3 class="card-title"><span class="title-icon">🔒</span>비밀번호 변경</h3>
                                        </div>
                                        <div class="card-content">
                                            <p style="color: #888;">소셜 로그인 계정은 비밀번호 변경이 불가능합니다.</p>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="settings-card">
                                        <div class="card-header">
                                            <h3 class="card-title"><span class="title-icon">🔒</span>비밀번호 변경</h3>
                                        </div>
                                        <div class="card-content">
                                            <form id="password-change-form">
                                                <div class="form-group">
                                                    <label for="current-password">현재 비밀번호</label>
                                                    <input type="password" id="current-password" name="current-password" required>
                                                </div>
                                                <!-- 새 비밀번호 -->
                                                <div class="form-group">
                                                    <label for="new-password">새 비밀번호</label>
                                                    <input type="password" id="new-password" name="new-password" required>
                                                    <div id="pwHelp" class="help-text" style="color:#6b7280; font-size:0.85rem;">
                                                        * 10~20자, 영문, 숫자, 특수문자 포함
                                                    </div>
                                                    <div id="pwSuccess" class="success-text" style="display:none; color:#16a34a; font-size:0.85rem;">
                                                        사용 가능한 비밀번호 형식입니다.
                                                    </div>
                                                    <div id="pwError" class="error-text" style="display:none; color:#dc2626; font-size:0.85rem;">
                                                        형식이 맞지 않습니다. (10~20자, 영문/숫자/특수문자 포함)
                                                    </div>
                                                </div>

                                                <!-- 새 비밀번호 확인 -->
                                                <div class="form-group">
                                                    <label for="confirm-password">새 비밀번호 확인</label>
                                                    <input type="password" id="confirm-password" name="confirm-password" required>
                                                    <div id="cpError" class="error-text" style="display:none; color:#dc2626; font-size:0.85rem;">
                                                        비밀번호가 일치하지 않습니다.
                                                    </div>
                                                </div>
                                                <div id="password-message-area" class="form-group" style="min-height: 24px;"></div>
                                                <button type="submit" class="btn-primary">비밀번호 변경</button>
                                            </form>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <div class="settings-card danger-card">
                                <div class="card-header">
                                    <h3 class="card-title"><span class="title-icon">🛡️</span>계정 관리</h3>
                                </div>
                                <div class="card-content">
                                    <div class="danger-zone">
                                        <h4 class="danger-title">계정 삭제</h4>
                                        <p class="danger-text">계정을 삭제하면 모든 데이터가 영구적으로 삭제되며 복구할 수 없습니다.</p>
                                        <form action="/member/withdraw" method="post" onsubmit="return confirm('정말 계정을 삭제하시겠습니까? 모든 데이터는 복구할 수 없습니다.');">
                                            <%-- Spring Security의 CSRF 토큰을 포함해야 합니다. --%>
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                            <button type="submit" class="btn-danger">계정 삭제하기</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </section>
            </div>
        </div>
    </section>
</main>

<!-- 문의 수정 모달 -->
<div id="edit-inquiry-modal" class="modal hidden">
    <div class="modal-content">
        <h3>문의 수정</h3>
        <form id="edit-inquiry-form">
            <input type="hidden" id="edit-inquiry-id" />
            <div class="form-group">
                <label for="edit-inquiry-title">제목</label>
                <input type="text" id="edit-inquiry-title" required />
            </div>
            <div class="form-group">
                <label for="edit-inquiry-content">내용</label>
                <textarea id="edit-inquiry-content" rows="5" required></textarea>
            </div>
            <div class="modal-actions">
                <button type="button" id="cancel-edit" class="btn-outline">취소</button>
                <button type="submit" class="btn-primary">수정하기</button>
            </div>
        </form>
    </div>
</div>
<jsp:include page="/common/footer.jsp"/>
<script>
    const currentMemberIdx = '<c:out value="${me.member.memberIdx}"/>';
</script>
<script src="/js/common/common.js"></script>
<script src="/js/mypages.js"></script>
</body>
</html>
