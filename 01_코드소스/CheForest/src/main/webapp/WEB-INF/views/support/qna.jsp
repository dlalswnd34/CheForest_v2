<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>도움말 & 문의 - CheForest</title>
    <link href="https://fonts.googleapis.com/css2?family=Gowun+Dodum:wght@400&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/common/common.css">
    <link rel="stylesheet" href="/css/qna.css">
    <!-- Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
</head>
<body>
<jsp:include page="/common/header.jsp"/>
    <!-- Q&A 페이지 컨텐츠 -->
    <div class="qna-page">
        <!-- 페이지 헤더 -->
        <section class="qna-header">
            <div class="container">
                <div class="qna-header-content">
                    <div class="qna-header-icon">
                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10"/>
                            <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/>
                            <path d="M12 17h.01"/>
                        </svg>
                        <h1>도움말 & 문의</h1>
                    </div>
                    <p class="qna-header-description">
                        CheForest 이용에 도움이 되는 정보를 찾아보세요
                    </p>
                    <div class="qna-header-features">
                        <div class="feature-item">
                            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                            </svg>
                            <span>업무시간 내 답변</span>
                        </div>
                        <div class="feature-item">
                            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M3 18v-6a9 9 0 0 1 18 0v6"/>
                                <path d="M21 19a2 2 0 0 1-2 2h-1a2 2 0 0 1-2-2v-3a2 2 0 0 1 2-2h3zM3 19a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2v-3a2 2 0 0 0-2-2H3z"/>
                            </svg>
                            <span>친절한 고객지원</span>
                        </div>
                    </div>
                </div>
            </div>
        </section>

<%--        <!-- 빠른 검색 -->--%>
<%--        <section class="qna-search">--%>
<%--            <div class="container">--%>
<%--                <div class="search-wrapper">--%>
<%--                    <div class="search-input-wrapper">--%>
<%--                        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">--%>
<%--                            <circle cx="11" cy="11" r="8"/>--%>
<%--                            <path d="m21 21-4.35-4.35"/>--%>
<%--                        </svg>--%>
<%--                        <input type="text" id="searchInput" placeholder="궁금한 내용을 검색해보세요..." class="search-input">--%>
<%--                    </div>--%>
<%--                </div>--%>
<%--            </div>--%>
<%--        </section>--%>

        <div class="container">
            <div class="qna-content">
                <!-- 사이드바 -->
                <div class="qna-sidebar">
                    <!-- CheForest 소개 -->
                    <div class="sidebar-card brand-card">
                        <div class="card-header">
                            <h3>
                                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z"/>
                                    <path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12"/>
                                </svg>
                                CheForest와 함께
                            </h3>
                        </div>
                        <div class="card-content">
                            <p class="brand-description">
                                요리의 숲에서 함께 성장해요! 씨앗부터 숲까지, 여러분의 요리 여정을 응원합니다.
                            </p>
                            <div class="brand-features">
                                <button class="brand-feature-btn" onclick="showPage('grade')">
                                    <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <polygon points="12,2 15.09,8.26 22,9.27 17,14.14 18.18,21.02 12,17.77 5.82,21.02 7,14.14 2,9.27 8.91,8.26"/>
                                    </svg>
                                    <span>등급 시스템</span>
                                </button>
                                <button class="brand-feature-btn" onclick="showPage('grade')">
                                    <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M9.937 15.5A2 2 0 0 0 8.5 14.063l-6.135-1.582a.5.5 0 0 1 0-.962L8.5 9.936A2 2 0 0 0 9.937 8.5l1.582-6.135a.5.5 0 0 1 .963 0L14.063 8.5A2 2 0 0 0 15.5 9.937l6.135 1.582a.5.5 0 0 1 0 .962L15.5 14.063a2 2 0 0 0-1.437 1.437l-1.582 6.135a.5.5 0 0 1-.963 0z"/>
                                    </svg>
                                    <span>성장 여정</span>
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- 연락처 정보 -->
                    <div class="sidebar-card">
                        <div class="card-header">
                            <h3>
                                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
                                </svg>
                                연락처
                            </h3>
                        </div>
                        <div class="card-content">
                            <div class="contact-item">
                                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                                    <polyline points="22,6 12,13 2,6"/>
                                </svg>
                                <span>CheForest3@gmail.com</span>
                            </div>
                            <div class="contact-item">
                                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
                                </svg>
                                <span>1588-1234</span>
                            </div>
                            <div class="contact-item">
                                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <circle cx="12" cy="12" r="10"/>
                                    <polyline points="12,6 12,12 16,14"/>
                                </svg>
                                <span>평일 09:00 - 18:00</span>
                            </div>
                        </div>
                    </div>

                    <!-- 커뮤니티 현황 -->
                    <div class="sidebar-card community-card">
                        <div class="card-header">
                            <h3>
                                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <polyline points="22,12 18,12 15,21 9,3 6,12 2,12"/>
                                </svg>
                                커뮤니티 현황
                            </h3>
                        </div>
                        <div class="card-content">
                            <div class="community-stats">
                                <div class="stat-item">
                                    <div class="stat-icon">
                                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                                            <circle cx="9" cy="7" r="4"/>
                                            <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                                            <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                                        </svg>
                                    </div>
                                    <div class="stat-number"><c:out value="${allMemberCount}" /></div>
                                    <div class="stat-label">총 회원수</div>
                                </div>
                                <div class="stat-item">
                                    <div class="stat-icon">
                                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
                                        </svg>
                                    </div>
                                    <div class="stat-number"><c:out value="${totalRecipeCount}" /></div>
                                    <div class="stat-label">레시피 수</div>
                                </div>
                            </div>
                            <div class="community-message">
                                💚 오늘도 맛있는 하루 되세요!
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 메인 컨텐츠 -->
                <div class="qna-main">
                    <!-- 탭 네비게이션 -->
                    <div class="tab-navigation">
                        <button class="tab-btn active" data-tab="faq">자주 묻는 질문</button>
                        <button class="tab-btn" data-tab="contact">문의하기</button>
                    </div>

                    <!-- FAQ 탭 -->
                    <div class="tab-content active" id="faq-tab">
                        <div class="main-card">
                            <div class="card-header">
                                <div class="faq-header">
                                    <h2>자주 묻는 질문 <span class="faq-count">(${inquiriesIsFaqCount})</span></h2>
                                    <div class="search-badge" id="searchBadge" style="display: none;">
                                        <span id="searchText"></span> 검색 결과
                                    </div>
                                </div>
                            </div>
                            <div class="card-content">
                                <div class="faq-list" id="faqList">
                                    <!-- FAQ 아이템들 -->
                                    <c:forEach var="faq" items="${inquiriesIsFaq}">
                                        <div class="faq-item"
                                             data-question="${faq.title}"
                                             data-answer="${faq.answerContent}"
                                             data-helpful="89"
                                             data-not-helpful="3">

                                            <div class="faq-question">
                                                <svg class="question-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                    <circle cx="12" cy="12" r="10"/>
                                                    <path d="M8 12l4 4 8-8"/>
                                                </svg>
                                                <span class="question-text">${faq.title}</span>
                                                <svg class="chevron-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                    <path d="M6 9l6 6 6-6"/>
                                                </svg>
                                            </div>

                                            <div class="faq-answer">
                                                <div class="answer-content">
                                                    <p>${faq.answerContent}</p>
                                                    <div class="answer-separator"></div>
<%--                                                    <div class="answer-actions">--%>
<%--                                                        <span class="helpful-text">이 답변이 도움이 되었나요?</span>--%>
<%--                                                        <div class="helpful-buttons">--%>
<%--                                                            <button class="helpful-btn helpful-yes">--%>
<%--                                                                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">--%>
<%--                                                                    <path d="M7 10v12"/>--%>
<%--                                                                    <path d="M15 5.88 14 10h5.83a2 2 0 0 1 1.92 2.56l-2.33 8A2 2 0 0 1 17.5 22H4a2 2 0 0 1-2-2v-8a2 2 0 0 1 2-2h2.76a2 2 0 0 0 1.79-1.11L12 2h3.73a2 2 0 0 1 1.92 2.56z"/>--%>
<%--                                                                </svg>--%>
<%--                                                                <span class="helpful-count">89</span>--%>
<%--                                                            </button>--%>
<%--                                                            <button class="helpful-btn helpful-no">--%>
<%--                                                                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">--%>
<%--                                                                    <path d="M17 14V2"/>--%>
<%--                                                                    <path d="M9 18.12 10 14H4.17a2 2 0 0 1-1.92-2.56l2.33-8A2 2 0 0 1 6.5 2H20a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2h-2.76a2 2 0 0 0-1.79 1.11L12 22h-3.73a2 2 0 0 1-1.92-2.56z"/>--%>
<%--                                                                </svg>--%>
<%--                                                                <span class="helpful-count">3</span>--%>
<%--                                                            </button>--%>
<%--                                                        </div>--%>
<%--                                                    </div>--%>

                                                </div>
                                            </div>

                                        </div>
                                    </c:forEach>



                                <!-- 검색 결과 없음 -->
                                <div class="no-results" id="noResults" style="display: none;">
                                    <div class="no-results-content">
                                        <svg class="no-results-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <circle cx="12" cy="12" r="10"/>
                                            <path d="M8 12l4 4 8-8"/>
                                        </svg>
                                        <h3>검색 결과가 없습니다</h3>
                                        <p>다른 키워드로 검색해보시거나 문의하기를 이용해주세요.</p>
                                        <button class="btn-reset" onclick="resetSearch()">검색 초기화</button>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
              </div>

                    <!-- 문의하기 탭 -->
                    <div class="tab-content" id="contact-tab">
                        <div class="main-card">
                            <div class="card-header">
                                <h2>
                                    <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                                        <polyline points="22,6 12,13 2,6"/>
                                    </svg>
                                    문의하기
                                </h2>
                                <p class="contact-description">
                                    궁금한 점이나 문제가 있으시면 언제든 문의해주세요. 업무시간 내에 답변드립니다.
                                </p>
                            </div>
                            <div class="card-content">
                                <form id="contactForm" class="contact-form">
                                    <div class="form-group">
                                        <label for="subject">제목 *</label>
                                        <input type="text" id="subject" name="subject" placeholder="문의 제목을 입력해주세요" required>
                                    </div>

                                    <div class="form-group">
                                        <label for="message">문의 내용 *</label>
                                        <textarea id="message" name="message" placeholder="문의 내용을 상세히 작성해주세요. 문제가 발생한 상황, 오류 메시지, 사용 환경 등을 포함해주시면 더 정확한 답변을 드릴 수 있습니다." rows="12" required></textarea>
                                    </div>

                                    <div class="form-notice">
                                        <div class="notice-content">
                                            <svg class="notice-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                <circle cx="12" cy="12" r="10"/>
                                                <path d="M8 12l4 4 8-8"/>
                                            </svg>
                                            <div class="notice-text">
                                                <p class="notice-title">문의 전 확인사항</p>
                                                <ul class="notice-list">
                                                    <li>• 자주 묻는 질문을 먼저 확인해보세요</li>
                                                    <li>• 기술 문제의 경우 사용 기기와 브라우저 정보를 포함해주세요</li>
                                                    <li>• 구체적이고 상세한 설명을 작성해주시면 더 정확한 답변을 받을 수 있습니다</li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>

                                    <button type="submit" class="btn-submit">
                                        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M22 2L11 13"/>
                                            <polygon points="22,2 15,22 11,13 2,9"/>
                                        </svg>
                                        문의 전송
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>

    <script src="/js/common/common.js"></script>
    <script src="/js/qna.js"></script>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>