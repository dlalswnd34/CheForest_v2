<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CheForest 관리자</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Gowun+Dodum:wght@400&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/admin/common.css">
    <link rel="stylesheet" href="/css/admin/admin.css">
    <link rel="stylesheet" href="/css/admin/adminAdd.css">
    <script src="https://cdn.jsdelivr.net/npm/lucide@latest/dist/umd/lucide.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script> <!-- jQuery 사용 -->
    <%-- CSRF 토큰 정보를 meta 태그에 추가 --%>
    <meta name="_csrf" content="${_csrf.token}">
    <meta name="_csrf_header" content="${_csrf.headerName}">
</head>
<body>
<div class="admin-dashboard">
    <!-- 왼쪽 사이드바 -->
    <aside class="sidebar">
        <!-- 로고 섹션 -->
        <div class="sidebar-header">
            <div class="logo">
                <div class="logo-icon">
                    <i data-lucide="shield" class="icon"></i>
                </div>
                <div class="logo-text">
                <a href="/"><h2>CheForest</h2></a>
                    <span>관리자</span>
                </div>
            </div>
        </div>

        <!-- 네비게이션 메뉴 -->
        <nav class="sidebar-nav">
            <ul class="nav-list">
                <li class="nav-item">
                    <a href="#" class="nav-link active" data-tab="dashboard">
                        <i data-lucide="bar-chart-3" class="nav-icon"></i>
                        <span>대시보드</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link" data-tab="users">
                        <i data-lucide="users" class="nav-icon"></i>
                        <span>회원 관리</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link" data-tab="recipes">
                        <i data-lucide="chef-hat" class="nav-icon"></i>
                        <span>레시피 관리</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link" data-tab="posts">
                        <i data-lucide="message-square" class="nav-icon"></i>
                        <span>게시글 관리</span>
                    </a>
                </li>
<%--                <li class="nav-item">--%>
<%--                    <a href="#" class="nav-link" data-tab="events">--%>
<%--                        <i data-lucide="calendar" class="nav-icon"></i>--%>
<%--                        <span>이벤트 관리</span>--%>
<%--                    </a>--%>
<%--                </li>--%>
                <li class="nav-item">
                    <a href="#" class="nav-link" data-tab="inquiries">
                        <i data-lucide="help-circle" class="nav-icon"></i>
                        <span>문의사항</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link" data-tab="settings">
                        <i data-lucide="settings" class="nav-icon"></i>
                        <span>설정</span>
                    </a>
                </li>
            </ul>
        </nav>
    </aside>

    <!-- 메인 콘텐츠 영역 -->
    <main class="main-content">
        <!-- 헤더 -->
        <header class="content-header">
            <div class="header-left">
                <h1 class="page-title">대시보드</h1>
                <p class="page-subtitle">CheForest 관리자 시스템</p>
            </div>
            <div class="header-right">
                <div class="current-time">
                    <div class="date-line">
                        <i data-lucide="clock" class="time-icon"></i>
                        <span id="date-text">2025년 01월 01일</span>
                    </div>
                    <div class="time-line" id="time-text">00시 00분 00초</div>
                </div>
            </div>
        </header>

        <!-- 대시보드 탭 -->
        <div id="dashboard-tab" class="tab-content active">
            <!-- 통계 카드 섹션 -->
            <section class="stats-section">
                <div class="stats-grid">
                    <div class="stat-card card-orange">
                        <div class="stat-content">
                            <div class="stat-info">
                                <p class="stat-label">총 가입자 수</p>
                                <h3 class="stat-number">${allMemberCount}</h3>
                                <p class="stat-labelColor1">금일 신규 가입자 ${todayMemberCount}↑</p>

                            </div>
                            <div class="stat-icon iconSize1">
                                <i data-lucide="users" class="icon" ></i>
                            </div>
                        </div>
                    </div>
                    <div class="stat-card card-green">
                        <div class="stat-content">
                            <div class="stat-info">
                                <p class="stat-label">현재 접속자</p>
                                <h3 class="stat-number"><span id="activeUsers">0</span></h3>
                                <p class="stat-labelColor1">인증 접속자 <span class="loggedInUsers">0</span></p>

                            </div>
                            <div class="stat-icon iconSize1">
                                <i data-lucide="activity" class="icon"></i>
                            </div>
                        </div>
                    </div>
                    <div class="stat-card card-blue">
                        <div class="stat-content">
                            <div class="stat-info">
                                <p class="stat-label">금일 누적 방문자</p>
                                <h3 class="stat-number stat-number2"><span id="totalVisitorsToday">0</span></h3>
                                <p class="stat-labelColor1">인증 방문자 <span id="totalVisitMemberToday">0</span></p>
                                <p class="stat-labelColor1">최대 동접자수 <span id="peakConcurrentUsers">0</span></p>
                            </div>
                            <div class="stat-icon iconSize1">
                                <i data-lucide="eye" class="icon"></i>
                            </div>
                        </div>
                    </div>
                    <div class="stat-card card-pink">
                        <div class="stat-content">
                            <div class="stat-info">
                                <p class="stat-label">오늘 게시글</p>
                                <h3 class="stat-number">${countTodayNewBoard.boardCount}</h3>
                                <p class="stat-labelColor1">댓글 ${countTodayNewBoard.reviewCount}</p>
                            </div>
                            <div class="stat-icon iconSize1">
                                <i data-lucide="file-text" class="icon"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- 차트 섹션 -->
            <section class="charts-section">
                <div class="charts-grid">
                    <!-- 회원 상태 분포 -->
                    <div class="chart-card">
                        <div class="chart-header">
                            <h3 class="chart-title">
                                <i data-lucide="users" class="title-icon"></i>
                                회원 상태 분포
                            </h3>
                        </div>
                        <div class="chart-body">
                            <div class="chart-container">
                                <canvas id="memberStatusChart"></canvas>
                                <div class="chart-center">
                                    <div class="center-label">활성화율</div>
                                    <div class="center-value">${accountStatusCounts.activePercent}%</div>
                                </div>
                            </div>
                            <div class="chart-legend">
                                <div class="legend-item">
                                    <div class="legend-dot orange"></div>
                                    <div class="legend-text">
                                        <span class="legend-label">활동계정</span>
                                        <span class="legend-value">${accountStatusCounts.activeCount} (${accountStatusCounts.activePercent}%)</span>
                                    </div>
                                </div>
                                <div class="legend-item">
                                    <div class="legend-dot gray-orange"></div>
                                    <div class="legend-text">
                                        <span class="legend-label">휴면계정</span>
                                        <span class="legend-value">${accountStatusCounts.dormantCount} (${accountStatusCounts.dormantPercent}%)</span>
                                    </div>
                                </div>
                                <div class="legend-item">
                                    <div class="legend-dot pink"></div>
                                    <div class="legend-text">
                                        <span class="legend-label">제재계정</span>
                                        <span class="legend-value">${accountStatusCounts.suspendedCount} (${accountStatusCounts.suspendedPercent}%)</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 월별 활동 현황 -->
                    <div class="chart-card">
                        <div class="chart-header">
                            <h3 class="chart-title">
                                <i data-lucide="bar-chart" class="title-icon"></i>
                                월별 활동 현황
                            </h3>
                        </div>
                        <div class="chart-body">
                            <div class="chart-container">
                                <canvas id="monthlyActivityChart"></canvas>
                            </div>
                            <div class="chart-legend horizontal">
                                <div class="legend-item">
                                    <div class="legend-dot orange"></div>
                                    <span class="legend-label">게시글</span>
                                </div>
                                <div class="legend-item">
                                    <div class="legend-dot pink"></div>
                                    <span class="legend-label">가입자수</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- 하단 정보 섹션 -->
            <section class="info-section">
                <div class="info-grid">
                    <!-- 최근 게시물 -->
                    <div class="info-card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <i data-lucide="message-square" class="title-icon"></i>
                                최근 게시물
                            </h3>
                        </div>
                        <div class="card-body">
                            <div class="post-list">

                                <c:forEach var="post" items="${recentPosts}">
                                    <div class="post-item viewDetailsBt" data-board-id="${post.boardId}" onclick="PostManager.viewDetails(${post.boardId})">
                                        <div class="post-content">
                                            <h4 class="post-title">${post.title}</h4>
                                            <div class="post-meta">
                                                <span class="post-category">${post.category}</span>
                                                <span class="post-time"> ${post.insertTime}</span>
                                            </div>
                                            <div class="post-stats">
                                            <span class="stat-item">
                                                <i data-lucide="heart" class="stat-icon"></i>
                                                ${post.likeCount}
                                            </span>
                                                                            <span class="stat-item">
                                                <i data-lucide="eye" class="stat-icon"></i>
                                                ${post.viewCount}
                                            </span>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                </c:forEach>

                            </div>
                        </div>
                    </div>

                    <!-- 문의사항 -->
                    <div class="info-card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <i data-lucide="help-circle" class="title-icon"></i>
                                미처리 문의사항
                                <span class="inquiry-badge"><span class="inquiriesPendingCount"></span></span>
                            </h3>
                        </div>
                        <div class="card-body">
                            <div class="inquiry-list" id="inquiry-list">
                                불러오는 중...
                                <!-- JavaScript로 동적 생성 -->
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>

        <!-- 회원 관리 탭 -->
        <div id="users-tab" class="tab-content">
            <!-- 회원 통계 카드 -->
            <section class="stats-section">
                <div class="stats-grid">
                    <div class="stat-card card-blue">
                        <div class="stat-content">
                            <div class="stat-info">
                                <h3 class="stat-number">${allMemberCount}</h3>
                                <p class="stat-label">전체 회원</p>
                            </div>
                            <div class="iconSize2 stat-labelColor1">
                                <i data-lucide="users" class="icon"></i>
                            </div>
                        </div>
                    </div>
                    <div class="stat-card card-green">
                        <div class="stat-content">
                            <div class="stat-info">
                                <h3 class="stat-number"><span class="loggedInUsers">0</span></h3>
                                <p class="stat-label">현재 인증 접속자</p>
                            </div>
                            <div class="iconSize2 stat-labelColor1">
                                <i data-lucide="activity" class="icon"></i>
                            </div>
                        </div>
                    </div>
                    <div class="stat-card card-orange">
                        <div class="stat-content">
                            <div class="stat-info">
                                <h3 class="stat-number">${accountStatusCounts.activeCount}</h3>
                                <p class="stat-label">활성 회원</p>
                            </div>
                            <div class="iconSize2 stat-labelColor1">
                                <i data-lucide="user-check" class="icon"></i>
                            </div>
                        </div>
                    </div>
                    <div class="stat-card card-red">
                        <div class="stat-content">
                            <div class="stat-info">
                                <h3 class="stat-number">${accountStatusCounts.suspendedCount}</h3>
                                <p class="stat-label">제재 회원</p>
                            </div>
                            <div class="iconSize2 stat-labelColor1">
                                <i data-lucide="ban" class="icon"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- 회원 관리 기능 -->
            <div id="user-management-content">
                <section class="management-section">
                    <div class="management-card">
                        <div class="management-header">
                            <div class="header-left">
                                <h3 class="management-title">
                                    <i data-lucide="users" class="title-icon"></i>
                                    회원 관리
                                </h3>
                            </div>
                            <div class="header-controls">
                                <div class="search-box">
                                    <i data-lucide="search" class="search-icon"></i>
                                    <input type="text" placeholder="회원 이름 또는 관리번호 검색..." class="search-input" id="user-search">
                                </div>

<%--                                <select class="filter-select" id="user-sort">--%>
<%--                                    <option value="joinDate">가입일순</option>--%>
<%--                                    <option value="lastLogin">최근 로그인</option>--%>
<%--                                    <option value="name">이름순</option>--%>
<%--                                    <option value="posts">게시글순</option>--%>
<%--                                </select>--%>
<%--                                <button class="sort-btn" id="user-sort-order">--%>
<%--                                    <i data-lucide="sort-desc" class="btn-icon"></i>--%>
<%--                                </button>--%>

                            </div>

                        </div>

                        <div class="management-tabs">
                            <div class="tab-list">
                                <button class="tab-btn active" data-user-tab="all">
                                    <i data-lucide="users" class="tab-icon"></i>
                                    <span>전체 회원 </span>
                                </button>
                                <button class="tab-btn" data-user-tab="online">
                                    <i data-lucide="activity" class="tab-icon"></i>
                                    <span>현재 접속 </span>
                                </button>
                                <button class="tab-btn" data-user-tab="suspended">
                                    <i data-lucide="ban" class="tab-icon"></i>
                                    <span>제재 회원 </span>
                                </button>
                            </div>
                        </div>
                        <div class="table-container" id="table-container">
                <!-- JavaScript로 동적 생성 -->
                        </div>
                    </div>
                </section>
            </div>
        </div>

        <!-- 레시피 관리 탭 -->
        <div id="recipes-tab" class="tab-content">
            <section class="management-section">
                <div class="management-card">
                    <div class="management-header">
                        <div class="header-left">
                            <h3 class="management-title">
                                <i data-lucide="chef-hat" class="title-icon"></i>
                                레시피 & 식재료 관리
                            </h3>
                        </div>
                        <div class="header-controls">
                            <div class="search-box">
                                <i data-lucide="search" class="search-icon"></i>
                                <input type="text" placeholder="레시피 검색..." class="search-input" id="recipe-search">
                            </div>
                            <select class="filter-select" id="recipe-searchType">
                                <option value="title">요리명</option>
                                <option value="ingredient">재료</option>
                            </select>
                            <select class="filter-select" id="recipe-category">
                                <option value="">전체 카테고리</option>
                                <option value="한식">한식</option>
                                <option value="양식">양식</option>
                                <option value="중식">중식</option>
                                <option value="일식">일식</option>
                                <option value="디저트">디저트</option>
                            </select>
<%--                            <button class="sort-btn" id="recipe-sort-order">--%>
<%--                                <i data-lucide="sort-desc" class="btn-icon"></i>--%>
<%--                            </button>--%>
                        </div>
                    </div>

                    <!-- 레시피 탭 -->
                    <div class="management-tabs">
                        <div class="tab-list">
<%--                            <button class="tab-btn active" data-recipe-tab="recipes">--%>
<%--                                <i data-lucide="chef-hat" class="tab-icon"></i>--%>
<%--                                <span>레시피 관리 (5)</span>--%>
<%--                            </button>--%>
<%--                            <button class="tab-btn" data-recipe-tab="ingredients">--%>
<%--                                <i data-lucide="leaf" class="tab-icon"></i>--%>
<%--                                <span>계절 식재료 (5)</span>--%>
<%--                            </button>--%>
                        </div>
                    </div>

                    <!-- 레시피 통계 카드 -->
<%--                    <div class="stats-grid-small" id="recipe-stats">--%>
<%--                        <div class="stat-card card-blue">--%>
<%--                            <div class="stat-content">--%>
<%--                                <div class="stat-info">--%>
<%--                                    <h3 class="stat-number">5</h3>--%>
<%--                                    <p class="stat-label">전체 레시피</p>--%>
<%--                                </div>--%>
<%--                                <div class="iconSize2 stat-labelColor1">--%>
<%--                                    <i data-lucide="chef-hat" class="icon"></i>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                        <div class="stat-card card-green">--%>
<%--                            <div class="stat-content">--%>
<%--                                <div class="stat-info">--%>
<%--                                    <h3 class="stat-number">3</h3>--%>
<%--                                    <p class="stat-label">공개</p>--%>
<%--                                </div>--%>
<%--                                <div class="iconSize2 stat-labelColor1">--%>
<%--                                    <i data-lucide="check-circle" class="icon"></i>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                        <div class="stat-card card-gray">--%>
<%--                            <div class="stat-content">--%>
<%--                                <div class="stat-info">--%>
<%--                                    <h3 class="stat-number">1</h3>--%>
<%--                                    <p class="stat-label">임시저장</p>--%>
<%--                                </div>--%>
<%--                                <div class="iconSize2 stat-labelColor1">--%>
<%--                                    <i data-lucide="edit-3" class="icon"></i>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                        <div class="stat-card card-red">--%>
<%--                            <div class="stat-content">--%>
<%--                                <div class="stat-info">--%>
<%--                                    <h3 class="stat-number">1</h3>--%>
<%--                                    <p class="stat-label">비공개</p>--%>
<%--                                </div>--%>
<%--                                <div class="iconSize2 stat-labelColor1">--%>
<%--                                    <i data-lucide="eye-off" class="icon"></i>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                    </div>--%>

                    <!-- 레시피 그리드 -->
                    <div class="recipe-grid" id="recipes-grid">
                        <!-- JavaScript로 동적 생성 -->
                    </div>
                </div>
            </section>
        </div>

        <!-- 게시글 관리 탭 -->
        <div id="posts-tab" class="tab-content">
            <section class="management-section">
                <div class="management-card">
                    <div class="management-header">
                        <div class="header-left">
                            <h3 class="management-title">
                                <i data-lucide="message-square" class="title-icon"></i>
                                게시물 관리
                            </h3>
                        </div>
                        <div class="header-controls">
                            <div class="search-box">
                                <i data-lucide="search" class="search-icon"></i>
                                <input type="text" placeholder="게시물 검색..." class="search-input" id="post-search">
                            </div>
                            <select class="filter-select" id="post-searchType">
                                <option value="title">요리명</option>
                                <option value="ingredient">재료</option>
                            </select>
                            <select class="filter-select" id="post-category">
                                <option value="">전체 카테고리</option>
                                <option value="한식">한식</option>
                                <option value="양식">양식</option>
                                <option value="중식">중식</option>
                                <option value="일식">일식</option>
                                <option value="디저트">디저트</option>
                            </select>
<%--                            <select class="filter-select" id="post-status">--%>
<%--                                <option value="all">전체 상태</option>--%>
<%--                                <option value="published">공개</option>--%>
<%--                                <option value="private">비공개</option>--%>
<%--                                <option value="reported">신고됨</option>--%>
<%--                            </select>--%>
<%--                            <select class="filter-select" id="post-sort">--%>
<%--                                <option value="views">조회수순</option>--%>
<%--                                <option value="likes">좋아요순</option>--%>
<%--                                <option value="comments">댓글수순</option>--%>
<%--                                <option value="title">제목순</option>--%>
<%--                            </select>--%>
<%--                            <button class="sort-btn" id="post-sort-order">--%>
<%--                                <i data-lucide="sort-desc" class="btn-icon"></i>--%>
<%--                            </button>--%>
                        </div>
                    </div>

                    <!-- 게시글/댓글 탭 -->
<%--                    <div class="management-tabs">--%>
<%--                        <div class="tab-list">--%>
<%--                            <button class="tab-btn active" data-post-tab="posts">--%>
<%--                                <i data-lucide="book-open" class="tab-icon"></i>--%>
<%--                                <span>게시글 관리 (6)</span>--%>
<%--                            </button>--%>
<%--                            <button class="tab-btn" data-post-tab="comments">--%>
<%--                                <i data-lucide="message-square" class="tab-icon"></i>--%>
<%--                                <span>댓글 관리 (8)</span>--%>
<%--                            </button>--%>
<%--                        </div>--%>
<%--                    </div>--%>

                    <!-- 게시글 통계 카드 -->
<%--                    <div class="stats-grid-small" id="post-stats">--%>
<%--                        <div class="stat-card card-blue">--%>
<%--                            <div class="stat-content">--%>
<%--                                <div class="stat-info">--%>
<%--                                    <h3 class="stat-number">6</h3>--%>
<%--                                    <p class="stat-label">전체 게시글</p>--%>
<%--                                </div>--%>
<%--                                <div class="iconSize2 stat-labelColor1">--%>
<%--                                    <i data-lucide="book-open" class="icon"></i>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                        <div class="stat-card card-green">--%>
<%--                            <div class="stat-content">--%>
<%--                                <div class="stat-info">--%>
<%--                                    <h3 class="stat-number">5</h3>--%>
<%--                                    <p class="stat-label">공개</p>--%>
<%--                                </div>--%>
<%--                                <div class="iconSize2 stat-labelColor1">--%>
<%--                                    <i data-lucide="check-circle" class="icon"></i>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                        <div class="stat-card card-red">--%>
<%--                            <div class="stat-content">--%>
<%--                                <div class="stat-info">--%>
<%--                                    <h3 class="stat-number">1</h3>--%>
<%--                                    <p class="stat-label">신고</p>--%>
<%--                                </div>--%>
<%--                                <div class="iconSize2 stat-labelColor1">--%>
<%--                                    <i data-lucide="flag" class="icon"></i>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                        <div class="stat-card card-orange">--%>
<%--                            <div class="stat-content">--%>
<%--                                <div class="stat-info">--%>
<%--                                    <h3 class="stat-number">2</h3>--%>
<%--                                    <p class="stat-label">인기글</p>--%>
<%--                                </div>--%>
<%--                                <div class="iconSize2 stat-labelColor1">--%>
<%--                                    <i data-lucide="star" class="icon"></i>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                    </div>--%>

                    <!-- 게시글 리스트 -->
                    <div class="post-management-list" id="posts-management-list">
                        <!-- JavaScript로 동적 생성 -->
                    </div>
                </div>
            </section>
        </div>

<%--        <!-- 이벤트 관리 탭 -->--%>
<%--        <div id="events-tab" class="tab-content">--%>
<%--            <section class="management-section">--%>
<%--                <div class="management-card">--%>
<%--                    <div class="management-header">--%>
<%--                        <div class="header-left">--%>
<%--                            <h3 class="management-title">--%>
<%--                                <i data-lucide="trophy" class="title-icon"></i>--%>
<%--                                이벤트 관리--%>
<%--                            </h3>--%>
<%--                        </div>--%>
<%--                        <div class="header-controls">--%>
<%--                            <div class="search-box">--%>
<%--                                <i data-lucide="search" class="search-icon"></i>--%>
<%--                                <input type="text" placeholder="이벤트명, 설명, 태그 검색..." class="search-input" id="event-search">--%>
<%--                            </div>--%>
<%--                            <select class="filter-select" id="event-category">--%>
<%--                                <option value="all">전체 카테고리</option>--%>
<%--                                <option value="공모전">공모전</option>--%>
<%--                                <option value="테스트">테스트</option>--%>
<%--                                <option value="이벤트">이벤트</option>--%>
<%--                                <option value="워크샵">워크샵</option>--%>
<%--                            </select>--%>
<%--                            <select class="filter-select" id="event-status">--%>
<%--                                <option value="all">전체 상태</option>--%>
<%--                                <option value="active">진행중</option>--%>
<%--                                <option value="draft">임시저장</option>--%>
<%--                                <option value="paused">일시정지</option>--%>
<%--                                <option value="ended">종료</option>--%>
<%--                                <option value="cancelled">취소</option>--%>
<%--                            </select>--%>
<%--                            <button class="add-btn" onclick="AdminAllTabs.openEventForm()">--%>
<%--                                <i data-lucide="plus" class="btn-icon"></i>--%>
<%--                                새 이벤트--%>
<%--                            </button>--%>
<%--                        </div>--%>
<%--                    </div>--%>

<%--                    <!-- 이벤트 통계 카드 -->--%>
<%--                    <div class="stats-grid-small">--%>
<%--&lt;%&ndash;                    <div class="stats-grid-large">&ndash;%&gt;--%>
<%--                        <div class="stat-card card-blue">--%>
<%--                            <div class="stat-content">--%>
<%--                                <div class="stat-info">--%>
<%--                                    <h3 class="stat-number">4</h3>--%>
<%--                                    <p class="stat-label">전체 이벤트</p>--%>
<%--                                </div>--%>
<%--                                <div class="iconSize2 stat-labelColor1">--%>
<%--                                    <i data-lucide="calendar" class="icon"></i>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                        <div class="stat-card card-yellow">--%>
<%--                            <div class="stat-content">--%>
<%--                                <div class="stat-info">--%>
<%--                                    <h3 class="stat-number">2</h3>--%>
<%--                                    <p class="stat-label">진행중</p>--%>
<%--                                </div>--%>
<%--                                <div class="iconSize2 stat-labelColor1">--%>
<%--                                    <i data-lucide="play" class="icon"></i>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                        <div class="stat-card card-gray">--%>
<%--                            <div class="stat-content">--%>
<%--                                <div class="stat-info">--%>
<%--                                    <h3 class="stat-number">1</h3>--%>
<%--                                    <p class="stat-label">비공개</p>--%>
<%--                                </div>--%>
<%--                                <div class="iconSize2 stat-labelColor1">--%>
<%--                                    <i data-lucide="edit-3" class="icon"></i>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                        <div class="stat-card card-green">--%>
<%--                            <div class="stat-content">--%>
<%--                                <div class="stat-info">--%>
<%--                                    <h3 class="stat-number">1</h3>--%>
<%--                                    <p class="stat-label">종료</p>--%>
<%--                                </div>--%>
<%--                                <div class="iconSize2 stat-labelColor1">--%>
<%--                                    <i data-lucide="check-circle" class="icon"></i>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--&lt;%&ndash;                        <div class="stat-card card-orange">&ndash;%&gt;--%>
<%--&lt;%&ndash;                            <div class="stat-content">&ndash;%&gt;--%>
<%--&lt;%&ndash;                                <div class="stat-info">&ndash;%&gt;--%>
<%--&lt;%&ndash;                                    <h3 class="stat-number">5,949</h3>&ndash;%&gt;--%>
<%--&lt;%&ndash;                                    <p class="stat-label">총 참가자</p>&ndash;%&gt;--%>
<%--&lt;%&ndash;                                    <p class="stat-sub">진행중: 1,703명</p>&ndash;%&gt;--%>
<%--&lt;%&ndash;                                </div>&ndash;%&gt;--%>
<%--&lt;%&ndash;                                <div class="iconSize2 stat-labelColor1">&ndash;%&gt;--%>
<%--&lt;%&ndash;                                    <i data-lucide="users" class="icon"></i>&ndash;%&gt;--%>
<%--&lt;%&ndash;                                </div>&ndash;%&gt;--%>
<%--&lt;%&ndash;                            </div>&ndash;%&gt;--%>
<%--&lt;%&ndash;                        </div>&ndash;%&gt;--%>
<%--&lt;%&ndash;                        <div class="stat-card card-purple">&ndash;%&gt;--%>
<%--&lt;%&ndash;                            <div class="stat-content">&ndash;%&gt;--%>
<%--&lt;%&ndash;                                <div class="stat-info">&ndash;%&gt;--%>
<%--&lt;%&ndash;                                    <h3 class="stat-number">575만원</h3>&ndash;%&gt;--%>
<%--&lt;%&ndash;                                    <p class="stat-label">총 예산</p>&ndash;%&gt;--%>
<%--&lt;%&ndash;                                    <p class="stat-sub">진행중: 320만원</p>&ndash;%&gt;--%>
<%--&lt;%&ndash;                                </div>&ndash;%&gt;--%>
<%--&lt;%&ndash;                                <div class="iconSize2 stat-labelColor1">&ndash;%&gt;--%>
<%--&lt;%&ndash;                                    <i data-lucide="trending-up" class="icon"></i>&ndash;%&gt;--%>
<%--&lt;%&ndash;                                </div>&ndash;%&gt;--%>
<%--&lt;%&ndash;                            </div>&ndash;%&gt;--%>
<%--&lt;%&ndash;                        </div>&ndash;%&gt;--%>
<%--                    </div>--%>

<%--                    <!-- 이벤트 그리드 -->--%>
<%--                    <div class="events-grid" id="events-grid">--%>
<%--                        <!-- JavaScript로 동적 생성 -->--%>
<%--                    </div>--%>
<%--                </div>--%>
<%--            </section>--%>
<%--        </div>--%>

        <!-- 문의사항 탭 -->
        <div id="inquiries-tab" class="tab-content">
            <section class="management-section">
                <div class="management-card">
                    <div class="management-header">
                        <div class="header-left">
                            <h3 class="management-title">
                                <i data-lucide="help-circle" class="title-icon"></i>
                                문의사항 관리
                            </h3>
                        </div>
                        <div class="header-controls">
                            <div class="search-box">
                                <i data-lucide="search" class="search-icon"></i>
                                <input type="text" placeholder="제목, 내용 검색..." class="search-input" id="inquiry-search">
                            </div>
                            <select class="filter-select" id="inquiry-status">
                                <option value="all">전체 상태</option>
                                <option value="대기중">대기중</option>
                                <option value="답변완료">답변완료</option>
                            </select>
                        </div>
                    </div>

                    <!-- 문의사항 통계 카드 -->
                    <div class="stats-grid-small">
                        <div class="stat-card card-blue">
                            <div class="stat-content">
                                <div class="stat-info">
                                    <h3 class="stat-number"><span class="inquiriesAllCount">0</span></h3>
                                    <p class="stat-label">전체 문의</p>
                                </div>
                                <div class="iconSize2 stat-labelColor1">
                                    <i data-lucide="help-circle" class="icon"></i>
                                </div>
                            </div>
                        </div>
                        <div class="stat-card card-yellow">
                            <div class="stat-content">
                                <div class="stat-info">
                                    <h3 class="stat-number"><span class="inquiriesPendingCount">0</span></h3>
                                    <p class="stat-label">대기중</p>
                                </div>
                                <div class="iconSize2 stat-labelColor1">
                                    <i data-lucide="clock" class="icon"></i>
                                </div>
                            </div>
                        </div>
                        <div class="stat-card card-green">
                            <div class="stat-content">
                                <div class="stat-info">
                                    <h3 class="stat-number"><span class="inquiriesAnsweredCount">0</span></h3>
                                    <p class="stat-label">답변완료</p>
                                </div>
                                <div class="iconSize2 stat-labelColor1">
                                    <i data-lucide="check-circle-2" class="icon"></i>
                                </div>
                            </div>
                        </div>
                        <div class="stat-card card-purple">
                            <div class="stat-content">
                                <div class="stat-info">
                                    <h3 class="stat-number"><span class="inquiriesTodayCount">0</span></h3>
                                    <p class="stat-label">오늘 접수</p>
                                </div>
                                <div class="iconSize2 stat-labelColor1">
                                    <i data-lucide="calendar" class="icon"></i>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 문의사항 리스트 -->
                    <div class="inquiry-management-list" id="inquiries-management-list">
                        <!-- JavaScript로 동적 생성 -->
                    </div>
                </div>
            </section>
        </div>

        <!-- 설정 탭 -->
        <div id="settings-tab" class="tab-content">
            <!-- DB 관리 섹션 -->
            <section class="settings-section">
                <div class="management-card">
                    <div class="management-header">
                        <div class="header-left">
                            <h3 class="management-title">
                                <i data-lucide="database" class="title-icon"></i>
                                데이터베이스 관리
                            </h3>
                        </div>
                    </div>

                    <!-- 시스템 상태 -->
                    <div class="stats-grid-small">
                        <div class="stat-card card-green">
                            <div class="stat-content">
                                <div class="stat-info">
                                    <p class="stat-label">서버 상태</p>
                                    <h3 class="stat-number-small">정상</h3>
                                </div>
                                <div class="stat-icon">
                                    <i data-lucide="server" class="icon"></i>
                                </div>
                            </div>
                        </div>
                        <div class="stat-card card-blue">
                            <div class="stat-content">
                                <div class="stat-info">
                                    <p class="stat-label">마지막 동기화</p>
                                    <h3 class="stat-number-small"><span id="last-sync-text">-</span></h3>
                                </div>
                                <div class="stat-icon">
                                    <i data-lucide="clock" class="icon"></i>
                                </div>
                            </div>
                        </div>
                        <div class="stat-card card-purple">
                            <div class="stat-content">
                                <div class="stat-info">
                                    <p class="stat-label">활성 연결</p>
                                    <h3 class="stat-number-small">4개</h3>
                                </div>
                                <div class="stat-icon">
                                    <i data-lucide="activity" class="icon"></i>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- DB 관리 버튼들 -->
                    <div class="db-actions">
                        <div class="db-action-card">
                            <div class="action-icon">
                                <i data-lucide="refresh-cw" class="icon"></i>
                            </div>
                            <div class="action-content">
                                <h4 class="action-title">통합검색동기화</h4>
                                <p class="action-desc">전체 데이터의 검색 인덱스를 다시 생성하고 동기화합니다</p>
                                <div class="progress-bar" id="db-sync-progress" style="display: none;">
                                    <div class="progress-fill"></div>
                                    <span class="progress-text">0%</span>
                                </div>
                            </div>
                            <button class="action-btn gradient-btn" onclick="AdminAllTabs.startDbSync()">
                                <i data-lucide="refresh-cw" class="btn-icon"></i>
                                동기화 시작
                            </button>
                        </div>

                        <div class="db-action-card">
                            <div class="action-icon blue">
                                <i data-lucide="download" class="icon"></i>
                            </div>
                            <div class="action-content">
                                <h4 class="action-title">API데이터 불러오기</h4>
                                <p class="action-desc">외부 API에서 최신 데이터를 불러와 업데이트합니다</p>
                                <div class="progress-bar" id="api-sync-progress" style="display: none;">
                                    <div class="progress-fill blue"></div>
                                    <span class="progress-text">0%</span>
                                </div>
                                <div id="api-sub-buttons" style="display:none; margin-top:10px;">
                                    <button class="sub-btn blue-btn" onclick="runApi('datako')">DataKO</button>
                                    <button class="sub-btn blue-btn" onclick="runApi('spoonacular')">Spoonacular</button>
                                    <button class="sub-btn blue-btn" onclick="runApi('wf')">TheMealDB</button>
                                    <button class="sub-btn red-btn" onclick="stopAll()">중단</button>
                                </div>
                            </div>
                            <button class="action-btn blue-btn" id="main-api-btn" onclick="toggleApiButtons()">
                                <i data-lucide="download" class="btn-icon"></i>
                                데이터 불러오기
                            </button>
                        </div>
                    </div>

<%--                    <!-- 시스템 모니터링 -->--%>
<%--                    <div class="system-monitoring">--%>
<%--                        <h4 class="monitoring-title">시스템 모니터링</h4>--%>
<%--                        <div class="monitoring-grid">--%>
<%--                            <div class="monitoring-item">--%>
<%--                                <div class="monitoring-label">--%>
<%--                                    <i data-lucide="cpu" class="monitoring-icon"></i>--%>
<%--                                    <span>CPU 사용률</span>--%>
<%--                                </div>--%>
<%--                                <div class="monitoring-progress">--%>
<%--                                    <div class="progress-bar">--%>
<%--                                        <div class="progress-fill" style="width: 45%"></div>--%>
<%--                                    </div>--%>
<%--                                    <span class="monitoring-value">45%</span>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                            <div class="monitoring-item">--%>
<%--                                <div class="monitoring-label">--%>
<%--                                    <i data-lucide="hard-drive" class="monitoring-icon"></i>--%>
<%--                                    <span>메모리 사용률</span>--%>
<%--                                </div>--%>
<%--                                <div class="monitoring-progress">--%>
<%--                                    <div class="progress-bar">--%>
<%--                                        <div class="progress-fill" style="width: 62%"></div>--%>
<%--                                    </div>--%>
<%--                                    <span class="monitoring-value">62%</span>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                            <div class="monitoring-item">--%>
<%--                                <div class="monitoring-label">--%>
<%--                                    <i data-lucide="database" class="monitoring-icon"></i>--%>
<%--                                    <span>디스크 사용률</span>--%>
<%--                                </div>--%>
<%--                                <div class="monitoring-progress">--%>
<%--                                    <div class="progress-bar">--%>
<%--                                        <div class="progress-fill" style="width: 78%"></div>--%>
<%--                                    </div>--%>
<%--                                    <span class="monitoring-value">78%</span>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                    </div>--%>
                </div>

<%--                <!-- 시스템 설정 -->--%>
<%--                <div class="management-card">--%>
<%--                    <div class="management-header">--%>
<%--                        <div class="header-left">--%>
<%--                            <h3 class="management-title">--%>
<%--                                <i data-lucide="settings" class="title-icon"></i>--%>
<%--                                시스템 설정--%>
<%--                            </h3>--%>
<%--                        </div>--%>
<%--                    </div>--%>

<%--                    <!-- 설정 탭들 -->--%>
<%--                    <div class="settings-tabs">--%>
<%--                        <div class="tab-list">--%>
<%--                            <button class="tab-btn active" data-settings-tab="site">--%>
<%--                                <i data-lucide="globe" class="tab-icon"></i>--%>
<%--                                <span>사이트 설정</span>--%>
<%--                            </button>--%>
<%--                            <button class="tab-btn" data-settings-tab="security">--%>
<%--                                <i data-lucide="shield" class="tab-icon"></i>--%>
<%--                                <span>보안 설정</span>--%>
<%--                            </button>--%>
<%--                            <button class="tab-btn" data-settings-tab="recipe">--%>
<%--                                <i data-lucide="file-text" class="tab-icon"></i>--%>
<%--                                <span>레시피 설정</span>--%>
<%--                            </button>--%>
<%--                            <button class="tab-btn" data-settings-tab="grade">--%>
<%--                                <i data-lucide="award" class="tab-icon"></i>--%>
<%--                                <span>등급 설정</span>--%>
<%--                            </button>--%>
<%--                            <button class="tab-btn" data-settings-tab="notification">--%>
<%--                                <i data-lucide="bell" class="tab-icon"></i>--%>
<%--                                <span>알림 설정</span>--%>
<%--                            </button>--%>
<%--                        </div>--%>
<%--                    </div>--%>

<%--                    <!-- 사이트 설정 탭 -->--%>
<%--                    <div id="site-settings" class="settings-tab-content active">--%>
<%--                        <div class="settings-grid">--%>
<%--                            <div class="settings-card">--%>
<%--                                <div class="settings-header">--%>
<%--                                    <h4 class="settings-title">--%>
<%--                                        <i data-lucide="globe" class="settings-icon"></i>--%>
<%--                                        기본 정보--%>
<%--                                    </h4>--%>
<%--                                </div>--%>
<%--                                <div class="settings-body">--%>
<%--                                    <div class="setting-item">--%>
<%--                                        <label>사이트 이름</label>--%>
<%--                                        <input type="text" value="CheForest" class="setting-input">--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item">--%>
<%--                                        <label>사이트 슬로건</label>--%>
<%--                                        <input type="text" value="당신만의 특별한 레시피를 발견하세요" class="setting-input">--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item">--%>
<%--                                        <label>관리자 이메일</label>--%>
<%--                                        <input type="email" value="admin@cheforest.com" class="setting-input">--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>

<%--                            <div class="settings-card">--%>
<%--                                <div class="settings-header">--%>
<%--                                    <h4 class="settings-title">--%>
<%--                                        <i data-lucide="palette" class="settings-icon"></i>--%>
<%--                                        디자인 설정--%>
<%--                                    </h4>--%>
<%--                                </div>--%>
<%--                                <div class="settings-body">--%>
<%--                                    <div class="setting-item">--%>
<%--                                        <label>메인 컬러</label>--%>
<%--                                        <div class="color-display">--%>
<%--                                            <div class="color-box"></div>--%>
<%--                                            <span>핑크-오렌지 그라데이션</span>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item">--%>
<%--                                        <label>로고 업로드</label>--%>
<%--                                        <div class="upload-area">--%>
<%--                                            <i data-lucide="upload" class="upload-icon"></i>--%>
<%--                                            <span>클릭하여 로고 이미지 업로드</span>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item toggle">--%>
<%--                                        <label>다크 모드 지원</label>--%>
<%--                                        <div class="toggle-switch">--%>
<%--                                            <input type="checkbox" id="dark-mode">--%>
<%--                                            <span class="slider"></span>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                    </div>--%>

<%--                    <!-- 보안 설정 탭 -->--%>
<%--                    <div id="security-settings" class="settings-tab-content">--%>
<%--                        <div class="settings-grid">--%>
<%--                            <div class="settings-card">--%>
<%--                                <div class="settings-header">--%>
<%--                                    <h4 class="settings-title">--%>
<%--                                        <i data-lucide="shield" class="settings-icon"></i>--%>
<%--                                        인증 보안--%>
<%--                                    </h4>--%>
<%--                                </div>--%>
<%--                                <div class="settings-body">--%>
<%--                                    <div class="setting-item toggle">--%>
<%--                                        <div>--%>
<%--                                            <label>2단계 인증 (2FA)</label>--%>
<%--                                            <p class="setting-desc">관리자 계정의 보안을 강화합니다</p>--%>
<%--                                        </div>--%>
<%--                                        <div class="toggle-switch">--%>
<%--                                            <input type="checkbox" id="two-fa">--%>
<%--                                            <span class="slider"></span>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item">--%>
<%--                                        <label>세션 타임아웃 (분)</label>--%>
<%--                                        <input type="number" value="30" class="setting-input">--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item">--%>
<%--                                        <label>최대 로그인 시도 횟수</label>--%>
<%--                                        <input type="number" value="5" class="setting-input">--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>

<%--                            <div class="settings-card">--%>
<%--                                <div class="settings-header">--%>
<%--                                    <h4 class="settings-title">--%>
<%--                                        <i data-lucide="lock" class="settings-icon"></i>--%>
<%--                                        비밀번호 정책--%>
<%--                                    </h4>--%>
<%--                                </div>--%>
<%--                                <div class="settings-body">--%>
<%--                                    <div class="setting-item">--%>
<%--                                        <label>최소 비밀번호 길이</label>--%>
<%--                                        <input type="number" value="8" class="setting-input">--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item toggle">--%>
<%--                                        <label>대문자 포함 필수</label>--%>
<%--                                        <div class="toggle-switch">--%>
<%--                                            <input type="checkbox" id="uppercase" checked>--%>
<%--                                            <span class="slider"></span>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item toggle">--%>
<%--                                        <label>숫자 포함 필수</label>--%>
<%--                                        <div class="toggle-switch">--%>
<%--                                            <input type="checkbox" id="numbers" checked>--%>
<%--                                            <span class="slider"></span>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                    </div>--%>

<%--                    <!-- 레시피 설정 탭 -->--%>
<%--                    <div id="recipe-settings" class="settings-tab-content">--%>
<%--                        <div class="settings-grid">--%>
<%--                            <div class="settings-card">--%>
<%--                                <div class="settings-header">--%>
<%--                                    <h4 class="settings-title">--%>
<%--                                        <i data-lucide="file-text" class="settings-icon"></i>--%>
<%--                                        게시 정책--%>
<%--                                    </h4>--%>
<%--                                </div>--%>
<%--                                <div class="settings-body">--%>
<%--                                    <div class="setting-item toggle">--%>
<%--                                        <div>--%>
<%--                                            <label>레시피 사전 승인</label>--%>
<%--                                            <p class="setting-desc">새 레시피 게시 전 관리자 승인 필요</p>--%>
<%--                                        </div>--%>
<%--                                        <div class="toggle-switch">--%>
<%--                                            <input type="checkbox" id="recipe-approval">--%>
<%--                                            <span class="slider"></span>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item">--%>
<%--                                        <label>최대 이미지 개수</label>--%>
<%--                                        <input type="number" value="10" class="setting-input">--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item">--%>
<%--                                        <label>최대 이미지 크기 (MB)</label>--%>
<%--                                        <input type="number" value="5" class="setting-input">--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>

<%--                            <div class="settings-card">--%>
<%--                                <div class="settings-header">--%>
<%--                                    <h4 class="settings-title">--%>
<%--                                        <i data-lucide="tag" class="settings-icon"></i>--%>
<%--                                        카테고리 관리--%>
<%--                                    </h4>--%>
<%--                                </div>--%>
<%--                                <div class="settings-body">--%>
<%--                                    <div class="category-list">--%>
<%--                                        <div class="category-item">--%>
<%--                                            <span>한식</span>--%>
<%--                                            <div class="toggle-switch">--%>
<%--                                                <input type="checkbox" id="category-korean" checked>--%>
<%--                                                <span class="slider"></span>--%>
<%--                                            </div>--%>
<%--                                        </div>--%>
<%--                                        <div class="category-item">--%>
<%--                                            <span>양식</span>--%>
<%--                                            <div class="toggle-switch">--%>
<%--                                                <input type="checkbox" id="category-western" checked>--%>
<%--                                                <span class="slider"></span>--%>
<%--                                            </div>--%>
<%--                                        </div>--%>
<%--                                        <div class="category-item">--%>
<%--                                            <span>중식</span>--%>
<%--                                            <div class="toggle-switch">--%>
<%--                                                <input type="checkbox" id="category-chinese" checked>--%>
<%--                                                <span class="slider"></span>--%>
<%--                                            </div>--%>
<%--                                        </div>--%>
<%--                                        <div class="category-item">--%>
<%--                                            <span>일식</span>--%>
<%--                                            <div class="toggle-switch">--%>
<%--                                                <input type="checkbox" id="category-japanese" checked>--%>
<%--                                                <span class="slider"></span>--%>
<%--                                            </div>--%>
<%--                                        </div>--%>
<%--                                        <div class="category-item">--%>
<%--                                            <span>디저트</span>--%>
<%--                                            <div class="toggle-switch">--%>
<%--                                                <input type="checkbox" id="category-dessert" checked>--%>
<%--                                                <span class="slider"></span>--%>
<%--                                            </div>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                    </div>--%>

<%--                    <!-- 등급 설정 탭 -->--%>
<%--                    <div id="grade-settings" class="settings-tab-content">--%>
<%--                        <div class="grade-system">--%>
<%--                            <h4 class="grade-title">--%>
<%--                                <i data-lucide="award" class="title-icon"></i>--%>
<%--                                회원 등급 시스템--%>
<%--                            </h4>--%>
<%--                            <div class="grade-grid">--%>
<%--                                <div class="grade-card grade-씨앗">--%>
<%--                                    <div class="grade-badge">씨앗</div>--%>
<%--                                    <div class="grade-requirements">--%>
<%--                                        <div>게시글: 0개 이상</div>--%>
<%--                                        <div>좋아요: 0개 이상</div>--%>
<%--                                        <div>팔로워: 0명 이상</div>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                                <div class="grade-card grade-뿌리">--%>
<%--                                    <div class="grade-badge">뿌리</div>--%>
<%--                                    <div class="grade-requirements">--%>
<%--                                        <div>게시글: 5개 이상</div>--%>
<%--                                        <div>좋아요: 20개 이상</div>--%>
<%--                                        <div>팔로워: 5명 이상</div>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                                <div class="grade-card grade-새싹">--%>
<%--                                    <div class="grade-badge">새싹</div>--%>
<%--                                    <div class="grade-requirements">--%>
<%--                                        <div>게시글: 15개 이상</div>--%>
<%--                                        <div>좋아요: 50개 이상</div>--%>
<%--                                        <div>팔로워: 15명 이상</div>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                                <div class="grade-card grade-나무">--%>
<%--                                    <div class="grade-badge">나무</div>--%>
<%--                                    <div class="grade-requirements">--%>
<%--                                        <div>게시글: 30개 이상</div>--%>
<%--                                        <div>좋아요: 100개 이상</div>--%>
<%--                                        <div>팔로워: 30명 이상</div>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                                <div class="grade-card grade-숲">--%>
<%--                                    <div class="grade-badge">숲</div>--%>
<%--                                    <div class="grade-requirements">--%>
<%--                                        <div>게시글: 50개 이상</div>--%>
<%--                                        <div>좋아요: 200개 이상</div>--%>
<%--                                        <div>팔로워: 50명 이상</div>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                    </div>--%>

<%--                    <!-- 알림 설정 탭 -->--%>
<%--                    <div id="notification-settings" class="settings-tab-content">--%>
<%--                        <div class="settings-grid">--%>
<%--                            <div class="settings-card">--%>
<%--                                <div class="settings-header">--%>
<%--                                    <h4 class="settings-title">--%>
<%--                                        <i data-lucide="bell" class="settings-icon"></i>--%>
<%--                                        이메일 알림--%>
<%--                                    </h4>--%>
<%--                                </div>--%>
<%--                                <div class="settings-body">--%>
<%--                                    <div class="setting-item toggle">--%>
<%--                                        <label>새 회원 가입 알림</label>--%>
<%--                                        <div class="toggle-switch">--%>
<%--                                            <input type="checkbox" id="new-member-alert" checked>--%>
<%--                                            <span class="slider"></span>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item toggle">--%>
<%--                                        <label>새 레시피 등록 알림</label>--%>
<%--                                        <div class="toggle-switch">--%>
<%--                                            <input type="checkbox" id="new-recipe-alert" checked>--%>
<%--                                            <span class="slider"></span>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item toggle">--%>
<%--                                        <label>문의사항 접수 알림</label>--%>
<%--                                        <div class="toggle-switch">--%>
<%--                                            <input type="checkbox" id="inquiry-alert" checked>--%>
<%--                                            <span class="slider"></span>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item">--%>
<%--                                        <label>알림 수신 이메일</label>--%>
<%--                                        <input type="email" value="admin@cheforest.com" class="setting-input">--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>

<%--                            <div class="settings-card">--%>
<%--                                <div class="settings-header">--%>
<%--                                    <h4 class="settings-title">--%>
<%--                                        <i data-lucide="message-square" class="settings-icon"></i>--%>
<%--                                        시스템 알림--%>
<%--                                    </h4>--%>
<%--                                </div>--%>
<%--                                <div class="settings-body">--%>
<%--                                    <div class="setting-item toggle">--%>
<%--                                        <label>시스템 점검 알림</label>--%>
<%--                                        <div class="toggle-switch">--%>
<%--                                            <input type="checkbox" id="maintenance-alert" checked>--%>
<%--                                            <span class="slider"></span>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item toggle">--%>
<%--                                        <label>보안 경고 알림</label>--%>
<%--                                        <div class="toggle-switch">--%>
<%--                                            <input type="checkbox" id="security-alert" checked>--%>
<%--                                            <span class="slider"></span>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                    <div class="setting-item">--%>
<%--                                        <label>CPU 사용률 경고 임계값 (%)</label>--%>
<%--                                        <input type="number" value="80" class="setting-input">--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                </div>--%>

            </section>
        </div>
    </main>
</div>

<!-- 모달들 -->
<!-- 이벤트 생성/수정 모달 -->
<div id="event-modal" class="modal" style="display: none;">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">
                <i data-lucide="trophy" class="title-icon"></i>
                새 이벤트 만들기
            </h3>
            <button class="modal-close" onclick="AdminAllTabs.closeEventForm()">
                <i data-lucide="x" class="close-icon"></i>
            </button>
        </div>
        <div class="modal-body">
            <form id="event-form" class="event-form">
                <div class="form-section">
                    <h4 class="section-title">기본 정보</h4>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>이벤트 제목</label>
                            <input type="text" name="title" placeholder="매력적인 이벤트 제목을 입력하세요" required>
                        </div>
                        <div class="form-group full-width">
                            <label>이벤트 설명</label>
                            <textarea name="description" placeholder="이벤트에 대한 자세한 설명을 입력하세요" rows="3"></textarea>
                        </div>
                        <div class="form-group">
                            <label>카테고리</label>
                            <select name="category">
                                <option value="공모전">공모전</option>
                                <option value="테스트">테스트</option>
                                <option value="이벤트">이벤트</option>
                                <option value="워크샵">워크샵</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>예산 (원)</label>
                            <input type="number" name="budget" placeholder="0">
                        </div>
                    </div>
                </div>

                <div class="form-section">
                    <h4 class="section-title">일정 정보</h4>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>시작일</label>
                            <input type="date" name="startDate" required>
                        </div>
                        <div class="form-group">
                            <label>종료일</label>
                            <input type="date" name="endDate" required>
                        </div>
                        <div class="form-group">
                            <label>시간</label>
                            <input type="text" name="time" placeholder="14:00 - 17:00">
                        </div>
                        <div class="form-group">
                            <label>장소</label>
                            <input type="text" name="location" placeholder="온라인 또는 오프라인 장소">
                        </div>
                    </div>
                </div>

                <div class="form-section">
                    <h4 class="section-title">참가자 및 혜택</h4>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>최대 참가자 수</label>
                            <input type="number" name="maxParticipants" placeholder="0 (무제한)">
                        </div>
                        <div class="form-group">
                            <label>상품/혜택</label>
                            <input type="text" name="prize" placeholder="참가자 혜택을 입력하세요">
                        </div>
                        <div class="form-group full-width">
                            <label>태그 (쉼표로 구분)</label>
                            <input type="text" name="tags" placeholder="레시피, 공모전, 상금">
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn-secondary" onclick="AdminAllTabs.closeEventForm()">취소</button>
            <button type="submit" class="btn-primary" onclick="AdminAllTabs.saveEvent()">
                <i data-lucide="save" class="btn-icon"></i>
                만들기
            </button>
        </div>
    </div>
</div>

<!-- 문의사항 답변 모달 -->
<div id="inquiry-answer-modal" class="modal" style="display: none;">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">
                <i data-lucide="reply" class="title-icon"></i>
                문의 답변 작성
            </h3>
            <button class="modal-close" onclick="AdminAllTabs.closeInquiryAnswer()">
                <i data-lucide="x" class="close-icon"></i>
            </button>
        </div>
        <div class="modal-body">
            <div id="original-inquiry" class="original-inquiry">
                <!-- 원본 문의 내용이 여기에 표시됨 -->
            </div>
            <div class="answer-form">
                <label>답변 내용</label>
                <textarea id="answer-content" placeholder="친절하고 정확한 답변을 작성해주세요..." rows="8"></textarea>
                <div class="answer-guide">
                    <div class="guide-header">
                        <i data-lucide="info" class="guide-icon"></i>
                        답변 작성 가이드
                    </div>
                    <ul class="guide-list">
                        <li>정중하고 친근한 언어를 사용해주세요</li>
                        <li>구체적이고 실용적인 해결방법을 제시해주세요</li>
                        <li>추가 문의가 필요한 경우 연락처를 안내해주세요</li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn-secondary" onclick="AdminAllTabs.closeInquiryAnswer()">취소</button>
            <button type="submit" class="btn-primary" onclick="AdminAllTabs.submitAnswer()">
                <i data-lucide="send" class="btn-icon"></i>
                답변 전송
            </button>
        </div>
    </div>
</div>

<!-- Tailwind CSS -->
<script src="https://cdn.tailwindcss.com"></script>
<!-- Lucide Icons -->
<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
<script src="/js/admin/common.js"></script>
<script src="/js/admin/admin.js"></script>
<script src="/js/admin/adminDashboard.js"></script>
<script>
    // Lucide 아이콘 초기화
    lucide.createIcons();

    // 관리자 페이지 초기화
    AdminAllTabs.initialize();

    // Java -> JS 데이터 변환
    const accountStatusCounts = {
        activeCount: ${accountStatusCounts.activeCount},
        dormantCount: ${accountStatusCounts.dormantCount},
        suspendedCount: ${accountStatusCounts.suspendedCount}
    };
    const monthlyData = ${monthlyActivityData};

    CustomAdmin.initializeCharts(accountStatusCounts,monthlyData);
</script>
</body>
</html>