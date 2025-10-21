<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>등급 시스템 - CheForest</title>
  <link rel="stylesheet" href="/css/common/common.css">
  <link rel="stylesheet" href="/css/grade.css">
  <!-- Tailwind CSS -->
  <script src="https://cdn.tailwindcss.com"></script>
  <!-- Lucide Icons -->
  <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
</head>
<body>
<!-- 헤더 부분은 제외 -->
<jsp:include page="/common/header.jsp"/>

<main class="grade-main">

  <!-- 페이지 헤더 -->
  <section class="grade-hero">
    <div class="hero-container">
      <div class="hero-content">
        <div class="hero-title-wrapper">
          <span class="hero-icon">🏆</span>
          <h1 class="hero-title">CheForest 등급 시스템</h1>
        </div>
        <p class="hero-description">
          포인트를 쌓아 성장하는 5단계 등급 시스템
        </p>
        <div class="hero-features">
          <div class="feature-item">
            <span class="feature-icon">✨</span>
            <span>씨앗(0점) ~ 숲(4000점+)</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🏆</span>
            <span>포인트로 자동 승급</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">⏰</span>
            <span>하루 최대 500점</span>
          </div>
        </div>
      </div>
    </div>
  </section>

  <!-- 메인 콘텐츠 -->
  <div class="grade-container">
    <div class="grade-layout">
      <sec:authorize access="isAuthenticated()">
        <sec:authentication property="principal" var="userDetails"/>
        <c:set var="m" value="${userDetails.member}"/>
      </sec:authorize>

      <!-- ✅ 로그인 여부 -->
      <c:set var="isLoggedIn" value="${not empty m}"/>
      <%-- 오늘 포인트 안전값 (미지정 시 0) --%>
      <c:set var="tp" value="${empty todayPoints ? 0 : todayPoints}"/>
      <!-- 오늘 남은 포인트(0 하한) -->
      <c:set var="remain" value="${500 - tp}"/>
      <c:if test="${remain < 0}"><c:set var="remain" value="0"/></c:if>

        <!-- 사이드바 - 내 등급 현황 -->
        <aside class="grade-sidebar">

          <c:choose>
            <%-- 비회원 --%>
            <c:when test="${empty m}">
              <div class="current-grade-card">
                <h2 class="grade-name">내 등급 정보</h2>
                <p class="grade-description">로그인 후 이용 가능합니다.</p>
                <a href="/auth/login" class="btn-login">로그인 하러가기</a>
              </div>
            </c:when>

            <%-- 회원 --%>
            <c:otherwise>
              <!-- 현재 등급 카드 -->
              <div class="current-grade-card">
                <div class="grade-icon-wrapper">
                  <c:choose>
                    <c:when test="${m.grade eq '씨앗'}"><div class="grade-icon seed-grade">⚡</div></c:when>
                    <c:when test="${m.grade eq '뿌리'}"><div class="grade-icon root-grade">⚓</div></c:when>
                    <c:when test="${m.grade eq '새싹'}"><div class="grade-icon sprout-grade">🌱</div></c:when>
                    <c:when test="${m.grade eq '나무'}"><div class="grade-icon tree-grade">🌲</div></c:when>
                    <c:when test="${m.grade eq '숲'}"><div class="grade-icon forest-grade">🌳</div></c:when>
                    <c:otherwise><div class="grade-icon">🌟</div></c:otherwise>
                  </c:choose>
                </div>
                <h2 class="grade-name">${m.grade}</h2>
                <p class="grade-description">나의 현재 등급</p>

                <div class="points-display">
                  <div class="total-points">${m.point}</div>
                  <div class="points-label">총 포인트</div>

                  <div class="points-grid">
                    <div class="point-item">
                      <div class="point-value">${tp}</div>
                      <div class="point-label">오늘 획득량</div>
                    </div>
                    <div class="point-item">
                      <div class="point-value"><c:out value="${remain}"/></div>
                      <div class="point-label">남은 획득량</div>
                    </div>
                      <c:choose>
                          <c:when test="${myRank <= 3}">
                              <div class="text-yellow-500 font-bold mt-4">🥇 상위 ${myRank}위 달성!</div>
                          </c:when>
                          <c:otherwise>
                              <div class="text-gray-600 mt-4">나의 현재 순위: <span class="font-bold text-orange-600">${myRank}위</span></div>
                          </c:otherwise>
                      </c:choose>
                  </div>
                </div>

                <div class="grade-range">
                    ${m.grade} 등급:
                  <c:choose>
                    <c:when test="${m.grade eq '씨앗'}">0 ~ 999점</c:when>
                    <c:when test="${m.grade eq '뿌리'}">1000 ~ 1999점</c:when>
                    <c:when test="${m.grade eq '새싹'}">2000 ~ 2999점</c:when>
                    <c:when test="${m.grade eq '나무'}">3000 ~ 3999점</c:when>
                    <c:otherwise>4000+점</c:otherwise>
                  </c:choose>
                </div>

                <div class="join-date">
                  가입일: <c:out value="${joinDate}" default="-" />
                </div>
              </div>

              <!-- 이모티콘 현황 -->
              <div class="emoji-status-card">
                <div class="card-header">
                  <span class="card-icon">🎭</span>
                  <h3>이모티콘 현황</h3>
                </div>
                <div class="card-content">
                  <div class="emoji-count">
                    <c:set var="emojiCount" value="0"/>
                    <c:choose>
                      <c:when test="${m.grade eq '씨앗'}"><c:set var="emojiCount" value="4"/></c:when>
                      <c:when test="${m.grade eq '뿌리'}"><c:set var="emojiCount" value="8"/></c:when>
                      <c:when test="${m.grade eq '새싹'}"><c:set var="emojiCount" value="12"/></c:when>
                      <c:when test="${m.grade eq '나무'}"><c:set var="emojiCount" value="16"/></c:when>
                      <c:when test="${m.grade eq '숲'}"><c:set var="emojiCount" value="20"/></c:when>
                    </c:choose>

                    <div class="emoji-total">${emojiCount}개</div>
                    <div class="emoji-label">사용 가능한 이모티콘</div>
                  </div>

                  <div class="emoji-progress">
                    <div class="emoji-grade-item
                 <c:choose><c:when test='${emojiCount >= 4}'>unlocked</c:when><c:otherwise>locked</c:otherwise></c:choose>">
                      <div class="emoji-grade-info"><span class="emoji-grade-icon">⚡</span><span>씨앗</span></div>
                      <div class="emoji-count-info">+4개
                        <c:choose><c:when test='${emojiCount >= 4}'>✓</c:when><c:otherwise>&#128274;</c:otherwise></c:choose>
                      </div>
                    </div>
                    <div class="emoji-grade-item
                 <c:choose><c:when test='${emojiCount >= 8}'>unlocked</c:when><c:otherwise>locked</c:otherwise></c:choose>">
                      <div class="emoji-grade-info"><span class="emoji-grade-icon">⚓</span><span>뿌리</span></div>
                      <div class="emoji-count-info">+4개
                        <c:choose><c:when test='${emojiCount >= 8}'>✓</c:when><c:otherwise>&#128274;</c:otherwise></c:choose>
                      </div>
                    </div>
                    <div class="emoji-grade-item
                 <c:choose><c:when test='${emojiCount >= 12}'>unlocked</c:when><c:otherwise>locked</c:otherwise></c:choose>">
                      <div class="emoji-grade-info"><span class="emoji-grade-icon">🌱</span><span>새싹</span></div>
                      <div class="emoji-count-info">+4개
                        <c:choose><c:when test='${emojiCount >= 12}'>✓</c:when><c:otherwise>&#128274;</c:otherwise></c:choose>
                      </div>
                    </div>
                    <div class="emoji-grade-item
                 <c:choose><c:when test='${emojiCount >= 16}'>unlocked</c:when><c:otherwise>locked</c:otherwise></c:choose>">
                      <div class="emoji-grade-info"><span class="emoji-grade-icon">🌲</span><span>나무</span></div>
                      <div class="emoji-count-info">+4개
                        <c:choose><c:when test='${emojiCount >= 16}'>✓</c:when><c:otherwise>&#128274;</c:otherwise></c:choose>
                      </div>
                    </div>
                    <div class="emoji-grade-item
                 <c:choose><c:when test='${emojiCount >= 20}'>unlocked</c:when><c:otherwise>locked</c:otherwise></c:choose>">
                      <div class="emoji-grade-info"><span class="emoji-grade-icon">🌳</span><span>숲</span></div>
                      <div class="emoji-count-info">+4개
                        <c:choose><c:when test='${emojiCount >= 20}'>✓</c:when><c:otherwise>&#128274;</c:otherwise></c:choose>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 현재 등급 혜택 -->
              <div class="current-benefits-card">
                <div class="card-header">
                  <span class="card-icon">🎁</span>
                  <h3>현재 등급 혜택</h3>
                </div>
                <div class="card-content">
                  <div class="benefit-item">
                    <span class="benefit-icon">⭐</span>
                    <span>🎭 이모티콘 ${emojiCount}개 사용 가능</span>
                  </div>
                </div>
              </div>
            </c:otherwise>
          </c:choose>
        </aside>

        <!-- 메인 콘텐츠 - 전체 등급 시스템 -->
      <main class="grade-content">

        <!-- p: 로그인 시 사용자 포인트, 비로그인 시 0 -->
        <c:set var="p" value="${empty m ? 0 : m.point}"/>
        <!-- 등급 시스템 안내 -->
        <div class="grade-intro">
          <h2 class="intro-title">
            <span class="intro-icon">👑</span>
            등급 시스템 안내
          </h2>
          <p class="intro-description">
            포인트를 쌓아 등급을 올리세요! 게시글 작성 시 100포인트(3개/일), 댓글 작성 시 10포인트(20개/일)를 획득합니다.
            하루 최대 500포인트까지 획득 가능하며, 등급이 올라가면 채팅 이모티콘이 4개씩 해금됩니다.
          </p>
        </div>

        <!-- 등급 목록 -->
        <div class="grades-list">

          <!-- 씨앗 등급 -->
          <div class="grade-item
               <c:choose>
                 <c:when test='${isLoggedIn and p >= 0 and p < 1000}'> current</c:when>
                 <c:when test='${isLoggedIn and p >= 1000}'> achieved</c:when>
               </c:choose>">
            <div class="grade-header">
              <div class="grade-info">
                <div class="grade-icon-wrapper">
                  <div class="grade-icon seed-grade">⚡</div>
                </div>
                <div class="grade-text">
                  <h3 class="grade-title">
                    씨앗 (Seed)
                    <c:choose>
                      <c:when test='${isLoggedIn and p >= 0 and p < 1000}'>
                        <span class="grade-badge current-badge">현재 등급</span>
                      </c:when>
                      <c:when test='${isLoggedIn and p >= 1000}'>
                        <span class="grade-badge achieved-badge">달성</span>
                      </c:when>
                    </c:choose>
                  </h3>
                  <p class="grade-desc">요리를 시작하는 단계</p>
                </div>
              </div>
            </div>
            <div class="grade-details">
              <div class="upgrade-conditions">
                <h4 class="detail-title">
                  <span class="detail-icon">🏅</span>
                  승급 조건
                </h4>
                <div class="condition-box">
                  <div class="points-requirement">
                    <div class="points-range">0 ~ 999</div>
                    <div class="points-unit">포인트</div>
                  </div>
                  <div class="earning-methods">
                    <div class="method-item">
                      <span class="method-icon">✏️</span>
                      <span>게시글: +100포인트 (3개/일)</span>
                    </div>
                    <div class="method-item">
                      <span class="method-icon">💬</span>
                      <span>댓글: +10포인트 (20개/일)</span>
                    </div>
                    <div class="method-item full-width">
                      <span class="method-icon">⏰</span>
                      <span>하루 최대 500포인트 제한</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="grade-benefits">
                <h4 class="detail-title">
                  <span class="detail-icon">🎁</span>
                  등급 혜택
                </h4>
                <div class="benefits-list">
                  <div class="benefit-item">
                    <span class="benefit-icon">⭐</span>
                    <span>기본 채팅 기능 이용</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 뿌리 등급 -->
          <div class="grade-item
               <c:choose>
                 <c:when test='${isLoggedIn and p >= 1000 and p < 2000}'> current</c:when>
                 <c:when test='${isLoggedIn and p >= 2000}'> achieved</c:when>
               </c:choose>">
            <div class="grade-header">
              <div class="grade-info">
                <div class="grade-icon-wrapper">
                  <div class="grade-icon root-grade">⚓</div>
                </div>
                <div class="grade-text">
                  <h3 class="grade-title">
                    뿌리 (Root)
                    <c:choose>
                      <c:when test='${isLoggedIn and p >= 1000 and p < 2000}'>
                        <span class="grade-badge current-badge">현재 등급</span>
                      </c:when>
                      <c:when test='${isLoggedIn and p >= 2000}'>
                        <span class="grade-badge achieved-badge">달성</span>
                      </c:when>
                    </c:choose>
                  </h3>
                  <p class="grade-desc">요리 기초를 다지는 단계</p>
                </div>
              </div>
            </div>
            <div class="grade-details">
              <div class="upgrade-conditions">
                <h4 class="detail-title">
                  <span class="detail-icon">🏅</span>
                  승급 조건
                </h4>
                <div class="condition-box">
                  <div class="points-requirement">
                    <div class="points-range">1,000 ~ 1,999</div>
                    <div class="points-unit">포인트</div>
                  </div>
                  <div class="earning-methods">
                    <div class="method-item">
                      <span class="method-icon">✏️</span>
                      <span>게시글: +100포인트 (3개/일)</span>
                    </div>
                    <div class="method-item">
                      <span class="method-icon">💬</span>
                      <span>댓글: +10포인트 (20개/일)</span>
                    </div>
                    <div class="method-item full-width">
                      <span class="method-icon">⏰</span>
                      <span>하루 최대 500포인트 제한</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="grade-benefits">
                <h4 class="detail-title">
                  <span class="detail-icon">🎁</span>
                  등급 혜택
                </h4>
                <div class="benefits-list">
                  <div class="benefit-item">
                    <span class="benefit-icon">⭐</span>
                    <span>🎭 이모티콘 +4개 해금 (총 8개 사용 가능)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 새싹 등급 -->
          <div class="grade-item
               <c:choose>
                 <c:when test='${isLoggedIn and p >= 2000 and p < 3000}'> current</c:when>
                 <c:when test='${isLoggedIn and p >= 3000}'> achieved</c:when>
               </c:choose>">
            <div class="grade-header">
              <div class="grade-info">
                <div class="grade-icon-wrapper">
                  <div class="grade-icon sprout-grade">🌱</div>
                </div>
                <div class="grade-text">
                  <h3 class="grade-title">
                    새싹 (Sprout)
                    <c:choose>
                      <c:when test='${isLoggedIn and p >= 2000 and p < 3000}'>
                        <span class="grade-badge current-badge">현재 등급</span>
                      </c:when>
                      <c:when test='${isLoggedIn and p >= 3000}'>
                        <span class="grade-badge achieved-badge">달성</span>
                      </c:when>
                    </c:choose>
                  </h3>
                  <p class="grade-desc">요리 실력이 성장하는 단계</p>
                </div>
              </div>
            </div>
            <div class="grade-details">
              <div class="upgrade-conditions">
                <h4 class="detail-title">
                  <span class="detail-icon">🏅</span>
                  승급 조건
                </h4>
                <div class="condition-box">
                  <div class="points-requirement">
                    <div class="points-range">2,000 ~ 2,999</div>
                    <div class="points-unit">포인트</div>
                  </div>
                  <div class="earning-methods">
                    <div class="method-item">
                      <span class="method-icon">✏️</span>
                      <span>게시글: +100포인트 (3개/일)</span>
                    </div>
                    <div class="method-item">
                      <span class="method-icon">💬</span>
                      <span>댓글: +10포인트 (20개/일)</span>
                    </div>
                    <div class="method-item full-width">
                      <span class="method-icon">⏰</span>
                      <span>하루 최대 500포인트 제한</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="grade-benefits">
                <h4 class="detail-title">
                  <span class="detail-icon">🎁</span>
                  등급 혜택
                </h4>
                <div class="benefits-list">
                  <div class="benefit-item">
                    <span class="benefit-icon">⭐</span>
                    <span>🎭 이모티콘 +4개 해금 (총 12개 사용 가능)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 나무 등급 -->
          <div class="grade-item
               <c:choose>
                 <c:when test='${isLoggedIn and p >= 3000 and p < 4000}'> current</c:when>
                 <c:when test='${isLoggedIn and p >= 4000}'> achieved</c:when>
               </c:choose>">
            <div class="grade-header">
              <div class="grade-info">
                <div class="grade-icon-wrapper">
                  <div class="grade-icon tree-grade">🌲</div>
                </div>
                <div class="grade-text">
                  <h3 class="grade-title">
                    나무 (Tree)
                    <c:choose>
                      <c:when test='${isLoggedIn and p >= 3000 and p < 4000}'>
                        <span class="grade-badge current-badge">현재 등급</span>
                      </c:when>
                      <c:when test='${isLoggedIn and p >= 4000}'>
                        <span class="grade-badge achieved-badge">달성</span>
                      </c:when>
                    </c:choose>
                  </h3>
                  <p class="grade-desc">탄탄한 요리 실력을 갖춘 단계</p>
                </div>
              </div>
            </div>
            <div class="grade-details">
              <div class="upgrade-conditions">
                <h4 class="detail-title">
                  <span class="detail-icon">🏅</span>
                  승급 조건
                </h4>
                <div class="condition-box">
                  <div class="points-requirement">
                    <div class="points-range">3,000 ~ 3,999</div>
                    <div class="points-unit">포인트</div>
                  </div>
                  <div class="earning-methods">
                    <div class="method-item">
                      <span class="method-icon">✏️</span>
                      <span>게시글: +100포인트 (3개/일)</span>
                    </div>
                    <div class="method-item">
                      <span class="method-icon">💬</span>
                      <span>댓글: +10포인트 (20개/일)</span>
                    </div>
                    <div class="method-item full-width">
                      <span class="method-icon">⏰</span>
                      <span>하루 최대 500포인트 제한</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="grade-benefits">
                <h4 class="detail-title">
                  <span class="detail-icon">🎁</span>
                  등급 혜택
                </h4>
                <div class="benefits-list">
                  <div class="benefit-item">
                    <span class="benefit-icon">⭐</span>
                    <span>🎭 이모티콘 +4개 해금 (총 16개 사용 가능)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 숲 등급 -->
          <div class="grade-item
               <c:choose>
                 <c:when test='${isLoggedIn and p >= 4000}'> current</c:when>
               </c:choose>">
            <div class="grade-header">
              <div class="grade-info">
                <div class="grade-icon-wrapper">
                  <div class="grade-icon forest-grade">🌳</div>
                </div>
                <div class="grade-text">
                  <h3 class="grade-title">
                    숲 (Forest)
                    <c:if test='${isLoggedIn and p >= 4000}'>
                      <span class="grade-badge current-badge">현재 등급</span>
                    </c:if>
                  </h3>
                  <p class="grade-desc">요리 마스터 최고 등급</p>
                </div>
              </div>
            </div>
            <div class="grade-details">
              <div class="upgrade-conditions">
                <h4 class="detail-title">
                  <span class="detail-icon">🏅</span>
                  승급 조건
                </h4>
                <div class="condition-box">
                  <div class="points-requirement">
                    <div class="points-range">4,000+</div>
                    <div class="points-unit">포인트</div>
                  </div>
                  <div class="earning-methods">
                    <div class="method-item">
                      <span class="method-icon">✏️</span>
                      <span>게시글: +100포인트 (3개/일)</span>
                    </div>
                    <div class="method-item">
                      <span class="method-icon">💬</span>
                      <span>댓글: +10포인트 (20개/일)</span>
                    </div>
                    <div class="method-item full-width">
                      <span class="method-icon">⏰</span>
                      <span>하루 최대 500포인트 제한</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="grade-benefits">
                <h4 class="detail-title">
                  <span class="detail-icon">🎁</span>
                  등급 혜택
                </h4>
                <div class="benefits-list">
                  <div class="benefit-item">
                    <span class="benefit-icon">⭐</span>
                    <span>🎭 이모티콘 +4개 해금 (총 20개 사용 가능)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>

        <!-- 포인트 획득 가이드 -->
        <div class="points-guide">
          <div class="guide-header">
            <span class="guide-icon">✨</span>
            <h3>포인트 획득 가이드</h3>
          </div>
          <div class="guide-content">
            <div class="guide-columns">
              <div class="guide-column">
                <h4 class="guide-title">
                  <span class="guide-method-icon">✏️</span>
                  게시글 작성으로 포인트 획득
                </h4>
                <ul class="guide-list">
                  <li class="guide-item">
                    <span class="guide-bullet">⭐</span>
                    레시피 게시글: 100포인트
                  </li>
                  <li class="guide-item">
                    <span class="guide-bullet">⭐</span>
                    고품질 사진과 상세한 설명 필수
                  </li>
                  <li class="guide-item">
                    <span class="guide-bullet">⭐</span>
                    하루 3개까지 작성 가능 (300포인트)
                  </li>
                </ul>
              </div>
              <div class="guide-column">
                <h4 class="guide-title">
                  <span class="guide-method-icon">💬</span>
                  댓글 작성으로 포인트 획득
                </h4>
                <ul class="guide-list">
                  <li class="guide-item">
                    <span class="guide-bullet">⭐</span>
                    댓글 1개: 10포인트
                  </li>
                  <li class="guide-item">
                    <span class="guide-bullet">⭐</span>
                    하루 20개까지만 포인트 적용
                  </li>
                  <li class="guide-item">
                    <span class="guide-bullet">⭐</span>
                    의미있는 댓글 작성 권장
                  </li>
                </ul>
              </div>
            </div>

            <div class="limit-system">
              <h4 class="limit-title">
                <span class="limit-icon">⏰</span>
                일일 포인트 제한 시스템
              </h4>
              <div class="limit-cards">
                <div class="limit-card daily">
                  <div class="limit-label">하루 최대</div>
                  <div class="limit-value">500포인트</div>
                </div>
                <div class="limit-card posts">
                  <div class="limit-label">게시글 제한</div>
                  <div class="limit-value">3개/일</div>
                </div>
                <div class="limit-card comments">
                  <div class="limit-label">댓글 제한</div>
                  <div class="limit-value">20개/일</div>
                </div>
                <div class="limit-card reset">
                  <div class="limit-label">리셋 시간</div>
                  <div class="limit-value">매일 00:00</div>
                </div>
              </div>
            </div>
          </div>
        </div>

      </main>

    </div>
  </div>

</main>
<script>
  // Lucide 아이콘 초기화
  lucide.createIcons();
</script>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>
