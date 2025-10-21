<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CheForest 통합 검색</title>
    <link rel="stylesheet" href="/css/search.css">
    <link href="https://fonts.googleapis.com/css2?family=Gowun+Dodum:wght@400&display=swap" rel="stylesheet">
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
</head>
<body>
<jsp:include page="/common/header.jsp"/>

<div class="min-h-screen">

    <!-- ===== 공통 변수 ===== -->
    <c:set var="activeTab" value="${empty param.type ? 'all' : param.type}"/>
    <c:set var="sizeSafe" value="${empty param.size ? 12 : param.size}"/>
    <c:set var="recipeCountSafe" value="${empty recipeCount ? 0 : recipeCount}"/>
    <c:set var="boardCountSafe"  value="${empty boardCount  ? 0 : boardCount}"/>
    <c:set var="totalCount" value="${recipeCountSafe + boardCountSafe}"/>

    <c:set var="totalPagesSafe"  value="${empty pages.totalPages ? 0 : pages.totalPages}" />
    <c:set var="currentPageSafe" value="${empty pages.number ? 0 : pages.number}" />

    <!-- 탭 URL 미리 생성 -->
    <c:url var="tabAllUrl" value="/search">
        <c:param name="totalKeyword" value="${param.totalKeyword}"/>
        <c:param name="type" value="all"/>
        <c:param name="page" value="0"/>
        <c:param name="size" value="${sizeSafe}"/>
    </c:url>
    <c:url var="tabRecipeUrl" value="/search">
        <c:param name="totalKeyword" value="${param.totalKeyword}"/>
        <c:param name="type" value="recipe"/>
        <c:param name="page" value="0"/>
        <c:param name="size" value="${sizeSafe}"/>
    </c:url>
    <c:url var="tabBoardUrl" value="/search">
        <c:param name="totalKeyword" value="${param.totalKeyword}"/>
        <c:param name="type" value="board"/>
        <c:param name="page" value="0"/>
        <c:param name="size" value="${sizeSafe}"/>
    </c:url>

    <!-- 검색 헤더 -->
    <section class="search-header">
        <div class="container">
            <div class="header-content">
                <div class="header-title">
                    <i data-lucide="search" class="header-icon"></i>
                    <h1>CheForest 통합 검색</h1>
                </div>
                <p class="header-subtitle">레시피와 게시글을 한 번에 검색하세요</p>
            </div>
        </div>
    </section>

    <!-- 검색 결과 -->
    <section class="search-results">
        <div class="container">

            <!-- 검색 결과 요약 -->
            <div class="search-summary">
                '<span class="search-term"><c:out value="${totalKeyword}"/></span>' 검색 결과
                <span class="search-term">
          <c:choose>
              <c:when test="${activeTab == 'recipe'}">${recipeCountSafe}</c:when>
              <c:when test="${activeTab == 'board'}">${boardCountSafe}</c:when>
              <c:otherwise>${totalCount}</c:otherwise>
          </c:choose>
        </span>건
                <c:if test="${totalCount > 0}">
                    (<span style="color:#f97316;">제목</span> 또는 <span style="color:#10b981;">재료</span> 일치)
                </c:if>
                <%--                <span class="ml-2 text-gray-500">전체: ${totalCount}건</span>--%>
            </div>

            <!-- 탭 -->
            <div class="tabs-container">
                <div class="tabs-list">
                    <a class="tab ${activeTab=='all' ? 'active' : ''}"    href="${tabAllUrl}">전체 (<c:out value="${totalCount}"/>)</a>
                    <a class="tab ${activeTab=='recipe' ? 'active' : ''}" href="${tabRecipeUrl}">레시피 (<c:out value="${recipeCountSafe}"/>)</a>
                    <a class="tab ${activeTab=='board' ? 'active' : ''}"  href="${tabBoardUrl}">게시글 (<c:out value="${boardCountSafe}"/>)</a>
                </div>

                <!-- 검색 결과 없음 -->
                <c:if test="${empty searches}">
                    <div id="noResults" class="no-results">
                        <i data-lucide="search" class="no-results-icon"></i>
                        <h3>검색 결과가 없습니다</h3>
                        <p>다른 검색어를 시도하거나 필터를 조정해보세요.</p>
                    </div>
                </c:if>

                <!-- 검색 결과 있음 -->
                <c:if test="${not empty searches}">
                    <div id="resultsGrid" class="results-grid">
                        <c:forEach var="item" items="${searches}">
                            <c:choose>
                                <c:when test="${item.type eq 'board'}">
                                    <c:url var="detailUrl" value="/board/view">
                                        <c:param name="boardId" value="${fn:replace(item.id, 'board-', '')}"/>
                                    </c:url>
                                </c:when>
                                <c:when test="${item.type eq 'recipe'}">
                                    <c:url var="detailUrl" value="/recipe/view">
                                        <c:param name="recipeId" value="${fn:replace(item.id, 'recipe-', '')}"/>
                                    </c:url>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="detailUrl" value="#"/>
                                </c:otherwise>
                            </c:choose>
                            <a class="result-card" href="${detailUrl}">
                                <div class="card-image-container">
                                    <c:choose>
                                        <c:when test="${empty item.thumbnail}">
                                            <img src="/images/no-image.png" alt="빈이미지" class="card-image">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${item.thumbnail}" alt="${fn:escapeXml(item.title)}" class="card-image">
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="card-badges">
                <span class="badge ${item.type == 'recipe' ? 'badge-recipe' : 'badge-board'}">
                        ${item.type == 'recipe' ? '레시피' : '게시글'}
                </span>
                                        <span class="badge badge-category"><c:out value="${item.category}"/></span>
                                    </div>
                                </div>

                                <div class="card-content">
                                    <h3 class="card-title"><c:out value="${item.title}"/></h3>
                                    <hr class="my-2"/>

                                    <div class="flex items-center justify-between text-sm mb-2 text-gray-600">
                                        <div class="flex items-center space-x-1">
                                            <i data-lucide="clock" class="h-4 w-4"></i>
                                            <span><c:out value="${item.cooktime}"/></span>
                                        </div>
                                        <span class="difficulty-badge
                    ${item.difficulty == 'Easy' ? 'easy' :
                      item.difficulty == 'Normal' ? 'normal' :
                      item.difficulty == 'Hard' ? 'hard' : ''}">
                    <c:out value="${item.difficulty}"/>
                </span>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                    </div>

                    <!-- 페이지네이션: 1페이지여도 노출 (0건일 때만 숨김) -->
                    <div class="mt-8 flex justify-center">
                        <c:if test="${pages.totalElements > 0}">
                            <nav class="flex space-x-2">
                                <c:set var="blockSize"  value="10"/>
                                <c:set var="blockStart" value="${currentPageSafe - (currentPageSafe mod blockSize)}"/>
                                <c:set var="blockEnd"   value="${blockStart + blockSize - 1}"/>
                                <c:if test="${blockEnd >= totalPagesSafe - 1}">
                                    <c:set var="blockEnd" value="${totalPagesSafe - 1}"/>
                                </c:if>

                                <c:set var="prevBlockPage" value="${blockStart - blockSize}"/>
                                <c:if test="${prevBlockPage < 0}">
                                    <c:set var="prevBlockPage" value="0"/>
                                </c:if>

                                <!-- prev URL -->
                                <c:url var="prevBlockUrl" value="/search">
                                    <c:param name="page" value="${prevBlockPage}"/>
                                    <c:param name="size" value="${sizeSafe}"/>
                                    <c:param name="totalKeyword" value="${param.totalKeyword}"/>
                                    <c:param name="type" value="${activeTab}"/>
                                </c:url>

                                <a href="${prevBlockUrl}" class="px-3 py-1 border rounded ${blockStart==0?'pointer-events-none opacity-50':''}">«</a>

                                <!-- page URLs -->
                                <c:forEach var="i" begin="${blockStart}" end="${blockEnd}">
                                    <c:url var="pageUrl" value="/search">
                                        <c:param name="page" value="${i}"/>
                                        <c:param name="size" value="${sizeSafe}"/>
                                        <c:param name="totalKeyword" value="${param.totalKeyword}"/>
                                        <c:param name="type" value="${activeTab}"/>
                                    </c:url>
                                    <a href="${pageUrl}"
                                       class="px-3 py-1 border rounded
                        ${currentPageSafe == i ? 'bg-orange-500 text-white border-orange-500' : 'bg-white text-gray-700'}">
                                            ${i + 1}
                                    </a>
                                </c:forEach>

                                <!-- next URL -->
                                <c:set var="nextBlockPage" value="${blockStart + blockSize}"/>
                                <c:if test="${nextBlockPage > totalPagesSafe - 1}">
                                    <c:set var="nextBlockPage" value="${totalPagesSafe - 1}"/>
                                </c:if>

                                <c:url var="nextBlockUrl" value="/search">
                                    <c:param name="page" value="${nextBlockPage}"/>
                                    <c:param name="size" value="${sizeSafe}"/>
                                    <c:param name="totalKeyword" value="${param.totalKeyword}"/>
                                    <c:param name="type" value="${activeTab}"/>
                                </c:url>

                                <a href="${nextBlockUrl}" class="px-3 py-1 border rounded ${(blockStart + blockSize) > (totalPagesSafe - 1) ? 'pointer-events-none opacity-50':''}">»</a>
                            </nav>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </div>
    </section>
</div>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        lucide.createIcons();
    });
</script>
</body>
</html>
