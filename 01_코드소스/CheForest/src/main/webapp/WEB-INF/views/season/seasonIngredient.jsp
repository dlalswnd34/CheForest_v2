<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>제철 식재료 - CheForest</title>
    <link rel="stylesheet" href="/css/common/common.css">
    <link rel="stylesheet" href="/css/seasonIngredient.css">
    <link href="https://fonts.googleapis.com/css2?family=Gowun+Dodum:wght@400&display=swap" rel="stylesheet">
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
    <script>document.addEventListener('DOMContentLoaded', () => lucide.createIcons());</script>
</head>
<body>
<jsp:include page="/common/header.jsp"/>

<div class="ingredients-container">
    <div class="ingredients-content">
        <!-- 헤더 섹션  -->
        <div class="page-header">
            <div class="header-main">
                <span class="season-icon">🌸</span>
                <h1 class="page-title">봄철 식재료</h1>
            </div>
            <p class="page-description">제철 식재료로 더 맛있고 건강한 요리를 만들어보세요</p>

        </div>

        <!-- 계절 선택 탭 -->
        <div class="season-tabs">
            <div class="tabs-wrapper">
                <div class="tab-list">
                    <button class="tab-button active" id="springTab" data-season="spring"
                            onclick="switchSeasonTab('spring'); return false;">
                        <span class="tab-icon">🌸</span><span class="tab-text">봄</span>
                    </button>
                    <button class="tab-button" id="summerTab" data-season="summer"
                            onclick="switchSeasonTab('summer'); return false;">
                        <span class="tab-icon">☀️</span><span class="tab-text">여름</span>
                    </button>
                    <button class="tab-button" id="autumnTab" data-season="autumn"
                            onclick="switchSeasonTab('autumn'); return false;">
                        <span class="tab-icon">🍂</span><span class="tab-text">가을</span>
                    </button>
                    <button class="tab-button" id="winterTab" data-season="winter"
                            onclick="switchSeasonTab('winter'); return false;">
                        <span class="tab-icon">☃️️</span><span class="tab-text">겨울</span>
                    </button>
                </div>
            </div>
        </div>

        <!-- 식재료 그리드: 봄 (기본 표시) -->
        <div class="ingredients-grid" data-season-panel="spring">
            <c:forEach var="item" items="${springIngredients}">
                <div class="ingredient-card">
                    <div class="card-image">
                        <img src="${item.imageUrl}" alt="${item.name}" class="ingredient-img">
                        <div class="image-overlay"></div>
                        <div class="season-badge">
              <span class="badge-text">
                <c:choose>
                    <c:when test="${item.seasons eq 'spring'}">봄</c:when>
                    <c:when test="${item.seasons eq 'summer'}">여름</c:when>
                    <c:when test="${item.seasons eq 'autumn'}">가을</c:when>
                    <c:when test="${item.seasons eq 'winter'}">겨울</c:when>
                </c:choose>
              </span>
                        </div>
                    </div>

                    <div class="card-header">
                        <div class="ingredient-title-line">
                            <h3 class="ingredient-name">${item.name}</h3>
                            <div class="status-dot"></div>
                        </div>
                        <p class="ingredient-description">${item.description}</p>
                    </div>

                    <div class="card-content">
                        <div class="peak-season">
                            <svg class="clock-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <circle cx="12" cy="12" r="10"/>
                                <polyline points="12,6 12,12 16,14"/>
                            </svg>
                            <span class="peak-text">제철: ${item.seasonDetail}</span>
                        </div>

                        <div class="benefits-section">
                            <h4 class="section-title">✨ 주요 효능</h4>
                            <div class="benefits-tags">${item.effects}</div>
                        </div>

                        <c:url var="recUrl" value="/season/recommend">
                            <c:param name="keyword" value="${item.name}"/>
                        </c:url>
                        <a class="recipe-btn" href="${recUrl}">🍽 레시피 추천</a>

                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- 식재료 그리드: 여름 -->
        <div class="ingredients-grid" data-season-panel="summer" hidden>
            <c:forEach var="item" items="${summerIngredients}">
                <div class="ingredient-card">
                    <div class="card-image">
                        <img src="${item.imageUrl}" alt="${item.name}" class="ingredient-img">
                        <div class="image-overlay"></div>
                        <div class="season-badge">
              <span class="badge-text">
                <c:choose>
                    <c:when test="${item.seasons eq 'spring'}">봄</c:when>
                    <c:when test="${item.seasons eq 'summer'}">여름</c:when>
                    <c:when test="${item.seasons eq 'autumn'}">가을</c:when>
                    <c:when test="${item.seasons eq 'winter'}">겨울</c:when>
                </c:choose>
              </span>
                        </div>
                    </div>

                    <div class="card-header">
                        <div class="ingredient-title-line">
                            <h3 class="ingredient-name">${item.name}</h3>
                            <div class="status-dot"></div>
                        </div>
                        <p class="ingredient-description">${item.description}</p>
                    </div>

                    <div class="card-content">
                        <div class="peak-season">
                            <svg class="clock-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <circle cx="12" cy="12" r="10"/>
                                <polyline points="12,6 12,12 16,14"/>
                            </svg>
                            <span class="peak-text">제철: ${item.seasonDetail}</span>
                        </div>

                        <div class="benefits-section">
                            <h4 class="section-title">✨ 주요 효능</h4>
                            <div class="benefits-tags">${item.effects}</div>
                        </div>

                        <c:url var="recUrl" value="/season/recommend">
                            <c:param name="keyword" value="${item.name}"/>
                        </c:url>
                        <a class="recipe-btn" href="${recUrl}">🍽 레시피 추천</a>

                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- 식재료 그리드: 가을 -->
        <div class="ingredients-grid" data-season-panel="autumn" hidden>
            <c:forEach var="item" items="${autumnIngredients}">
                <div class="ingredient-card">
                    <div class="card-image">
                        <img src="${item.imageUrl}" alt="${item.name}" class="ingredient-img">
                        <div class="image-overlay"></div>
                        <div class="season-badge">
              <span class="badge-text">
                <c:choose>
                    <c:when test="${item.seasons eq 'spring'}">봄</c:when>
                    <c:when test="${item.seasons eq 'summer'}">여름</c:when>
                    <c:when test="${item.seasons eq 'autumn'}">가을</c:when>
                    <c:when test="${item.seasons eq 'winter'}">겨울</c:when>
                </c:choose>
              </span>
                        </div>
                    </div>

                    <div class="card-header">
                        <div class="ingredient-title-line">
                            <h3 class="ingredient-name">${item.name}</h3>
                            <div class="status-dot"></div>
                        </div>
                        <p class="ingredient-description">${item.description}</p>
                    </div>

                    <div class="card-content">
                        <div class="peak-season">
                            <svg class="clock-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <circle cx="12" cy="12" r="10"/>
                                <polyline points="12,6 12,12 16,14"/>
                            </svg>
                            <span class="peak-text">제철: ${item.seasonDetail}</span>
                        </div>

                        <div class="benefits-section">
                            <h4 class="section-title">✨ 주요 효능</h4>
                            <div class="benefits-tags">${item.effects}</div>
                        </div>

                        <c:url var="recUrl" value="/season/recommend">
                            <c:param name="keyword" value="${item.name}"/>
                        </c:url>
                        <a class="recipe-btn" href="${recUrl}">🍽 레시피 추천</a>

                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- 식재료 그리드: 겨울 -->
        <div class="ingredients-grid" data-season-panel="winter" hidden>
            <c:forEach var="item" items="${winterIngredients}">
                <div class="ingredient-card">
                    <div class="card-image">
                        <img src="${item.imageUrl}" alt="${item.name}" class="ingredient-img">
                        <div class="image-overlay"></div>
                        <div class="season-badge">
              <span class="badge-text">
                <c:choose>
                    <c:when test="${item.seasons eq 'spring'}">봄</c:when>
                    <c:when test="${item.seasons eq 'summer'}">여름</c:when>
                    <c:when test="${item.seasons eq 'autumn'}">가을</c:when>
                    <c:when test="${item.seasons eq 'winter'}">겨울</c:when>
                </c:choose>
              </span>
                        </div>
                    </div>

                    <div class="card-header">
                        <div class="ingredient-title-line">
                            <h3 class="ingredient-name">${item.name}</h3>
                            <div class="status-dot"></div>
                        </div>
                        <p class="ingredient-description">${item.description}</p>
                    </div>

                    <div class="card-content">
                        <div class="peak-season">
                            <svg class="clock-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <circle cx="12" cy="12" r="10"/>
                                <polyline points="12,6 12,12 16,14"/>
                            </svg>
                            <span class="peak-text">제철: ${item.seasonDetail}</span>
                        </div>

                        <div class="benefits-section">
                            <h4 class="section-title">✨ 주요 효능</h4>
                            <div class="benefits-tags">${item.effects}</div>
                        </div>

                        <c:url var="recUrl" value="/season/recommend">
                            <c:param name="keyword" value="${item.name}"/>
                        </c:url>
                        <a class="recipe-btn" href="${recUrl}">🍽 레시피 추천</a>

                    </div>
                </div>
            </c:forEach>
        </div>
        <%--   하단 식재료 가이드 섹션     --%>
        <!-- === 봄철 식재료 가이드 === -->
        <section class="seasonal-guide" data-season-panel="spring" hidden>
            <div class="guide-card">
                <div class="guide-header">
                    <div class="guide-icon-wrapper"><span class="guide-main-icon">🌸</span></div>
                    <div>
                        <h2 class="guide-title">봄철 식재료 가이드</h2>
                        <p class="guide-subtitle">전문가가 추천하는 활용법</p>
                    </div>
                </div>
                <div class="guide-content">
                    <div class="guide-grid">
                        <div class="guide-item">
                            <h3 class="guide-item-title">📦 보관법</h3>
                            <p class="guide-item-text">
                                봄나물은 한철에 많이 나오므로 바로 세척해 냉장 보관하세요.<br>
                                향이 쉽게 날아가므로 2~3일 내 섭취하는 것이 좋습니다.
                            </p>
                        </div>
                        <div class="guide-item">
                            <h3 class="guide-item-title">🍳 조리법</h3>
                            <p class="guide-item-text">
                                살짝 데치거나 무침으로 활용하면 제철 향이 살아납니다.<br>
                                마늘·참기름을 곁들이면 영양과 맛을 높일 수 있습니다.
                            </p>
                        </div>
                        <div class="guide-item">
                            <h3 class="guide-item-title">💡 팁</h3>
                            <p class="guide-item-text">
                                어린잎 채소는 샐러드로 활용, 뿌리채소는 국거리로 쓰면 좋습니다.
                            </p>
                        </div>
                    </div>
                    <div class="special-info">
                        <h3 class="special-title">⭐ 봄철 특별 정보</h3>
                        <p class="special-text">
                            봄철 식재료는 해독작용과 피로 회복에 도움을 주며, 섬유질과 비타민이 풍부합니다.
                        </p>
                    </div>
                </div>
            </div>
        </section>

        <!-- === 여름철 식재료 가이드 === -->
        <section class="seasonal-guide" data-season-panel="summer" hidden>
            <div class="guide-card">
                <div class="guide-header">
                    <div class="guide-icon-wrapper"><span class="guide-main-icon">☀️</span></div>
                    <div>
                        <h2 class="guide-title">여름철 식재료 가이드</h2>
                        <p class="guide-subtitle">전문가가 추천하는 활용법</p>
                    </div>
                </div>
                <div class="guide-content">
                    <div class="guide-grid">
                        <div class="guide-item">
                            <h3 class="guide-item-title">📦 보관법</h3>
                            <p class="guide-item-text">
                                여름 과일은 수분이 많아 냉장 보관이 필수입니다.<br>
                                오이는 신문지로 감싸 냉장실에 두면 신선도가 오래갑니다.
                            </p>
                        </div>
                        <div class="guide-item">
                            <h3 class="guide-item-title">🍳 조리법</h3>
                            <p class="guide-item-text">
                                수분 많은 채소는 생으로 샐러드, 냉국으로 활용하세요.<br>
                                짧은 조리로 영양소 손실을 최소화하는 것이 좋습니다.
                            </p>
                        </div>
                        <div class="guide-item">
                            <h3 class="guide-item-title">💡 팁</h3>
                            <p class="guide-item-text">
                                제철 과일을 얼려 스무디나 빙수로 활용하면 무더위를 이길 수 있습니다.
                            </p>
                        </div>
                    </div>
                    <div class="special-info">
                        <h3 class="special-title">⭐ 여름철 특별 정보</h3>
                        <p class="special-text">
                            여름 식재료는 전해질과 수분 보충에 효과적이며, 비타민 C·칼륨이 풍부해 땀 손실을 보완합니다.
                        </p>
                    </div>
                </div>
            </div>
        </section>

        <!-- === 가을철 식재료 가이드 === -->
        <section class="seasonal-guide" data-season-panel="autumn" hidden>
            <div class="guide-card">
                <div class="guide-header">
                    <div class="guide-icon-wrapper"><span class="guide-main-icon">🍂</span></div>
                    <div>
                        <h2 class="guide-title">가을철 식재료 가이드</h2>
                        <p class="guide-subtitle">전문가가 추천하는 활용법</p>
                    </div>
                </div>
                <div class="guide-content">
                    <div class="guide-grid">
                        <div class="guide-item">
                            <h3 class="guide-item-title">📦 보관법</h3>
                            <p class="guide-item-text">
                                무·배추 같은 뿌리채소는 서늘한 곳에 보관하세요.<br>
                                사과·배는 신문지에 싸서 냉장 보관하면 당도가 오래 유지됩니다.
                            </p>
                        </div>
                        <div class="guide-item">
                            <h3 class="guide-item-title">🍳 조리법</h3>
                            <p class="guide-item-text">
                                구이나 조림으로 조리하면 깊은 맛을 냅니다.<br>
                                국물요리로 활용하면 속을 따뜻하게 합니다.
                            </p>
                        </div>
                        <div class="guide-item">
                            <h3 class="guide-item-title">💡 팁</h3>
                            <p class="guide-item-text">
                                수확철 과일은 저장성이 좋아 겨울철까지 먹을 수 있습니다.
                            </p>
                        </div>
                    </div>
                    <div class="special-info">
                        <h3 class="special-title">⭐ 가을철 특별 정보</h3>
                        <p class="special-text">
                            가을 식재료는 면역력 강화와 영양 보충에 좋아 환절기 건강 관리에 효과적입니다.
                        </p>
                    </div>
                </div>
            </div>
        </section>

        <!-- === 겨울철 식재료 가이드 === -->
        <section class="seasonal-guide" data-season-panel="winter" hidden>
            <div class="guide-card">
                <div class="guide-header">
                    <div class="guide-icon-wrapper"><span class="guide-main-icon">⛄</span></div>
                    <div>
                        <h2 class="guide-title">겨울철 식재료 가이드</h2>
                        <p class="guide-subtitle">전문가가 추천하는 활용법</p>
                    </div>
                </div>
                <div class="guide-content">
                    <div class="guide-grid">
                        <div class="guide-item">
                            <h3 class="guide-item-title">📦 보관법</h3>
                            <p class="guide-item-text">
                                김장채소는 김치로 저장, 감귤은 통풍 잘되는 곳에서 상온 보관 가능합니다.
                            </p>
                        </div>
                        <div class="guide-item">
                            <h3 class="guide-item-title">🍳 조리법</h3>
                            <p class="guide-item-text">
                                국·탕, 구이에 적합하며 찜 요리로도 활용하면 좋습니다.<br>
                                뿌리채소는 오랜 조리에도 영양소가 잘 유지됩니다.
                            </p>
                        </div>
                        <div class="guide-item">
                            <h3 class="guide-item-title">💡 팁</h3>
                            <p class="guide-item-text">
                                제철 어패류와 함께 섭취하면 단백질·오메가3를 보충할 수 있습니다.
                            </p>
                        </div>
                    </div>
                    <div class="special-info">
                        <h3 class="special-title">⭐ 겨울철 특별 정보</h3>
                        <p class="special-text">
                            겨울 식재료는 면역력 강화에 도움되며, 따뜻한 국물요리로 체온 유지에 효과적입니다.
                        </p>
                    </div>
                </div>
            </div>
        </section>


    </div>
</div>

<script src="/js/common/common.js"></script>
<script src="/js/seasonIngredient.js"></script>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>
