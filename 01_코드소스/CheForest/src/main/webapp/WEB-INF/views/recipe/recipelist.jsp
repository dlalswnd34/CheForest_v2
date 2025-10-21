<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>

<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CheForest 레시피</title>
    <link rel="stylesheet" href="/css/common/common.css">
    <link rel="stylesheet" href="/css/recipe/recipe.css">
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
</head>
<jsp:include page="/common/header.jsp"/>
<body>
<div class="min-h-screen bg-white">
    <!-- 페이지 헤더 -->
    <section class="bg-gradient-to-r from-pink-500 to-orange-500 text-white py-12">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div class="text-center">
                <div class="flex items-center justify-center mb-4">
                    <i data-lucide="book-open" class="h-10 w-10 mr-3"></i>
                    <h1 class="text-4xl">CheForest 레시피</h1>
                </div>
                <p class="text-lg opacity-90 max-w-2xl mx-auto">
                    요리의 즐거움을 함께하세요! 다양한 카테고리의 검증된 레시피들을 만나보세요.
                </p>
            </div>
        </div>
    </section>

    <!-- 검색 및 필터 섹션 -->
    <section class="py-8" style="background-color: rgba(156, 163, 175, 0.1);">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <form action="<c:url value='/recipe/list'/>" method="GET" class="flex flex-col lg:flex-row gap-6 items-center">
                <!-- 현재 카테고리 필터를 유지하기 위한 숨김 필드 -->
                <input type="hidden" name="categoryKr" value="${categoryKr}"/>
                <!-- 검색 시 페이지를 0으로 초기화 -->
                <input type="hidden" name="page" value="0"/>

                <%--검색관련 div--%>
                <div class="flex items-center gap-2 flex-grow">
                    <!-- 정렬 옵션: name="searchType" 및 선택 상태 유지 -->
                    <select id="boardSortSelect" name="searchType" class="border border-gray-300 rounded-lg px-3 py-3 text-sm bg-white">
                        <!-- Model에서 받은 searchType 값으로 선택 상태 유지 -->
                        <option value="title" ${searchType == 'title' || empty searchType ? 'selected' : ''}>제목</option>
                        <option value="ingredient" ${searchType == 'ingredient' ? 'selected' : ''}>재료</option>
                    </select>

                    <!-- 검색바 -->
                    <div class="relative flex-1 max-w-md">
                        <i data-lucide="search" class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500 h-4 w-4"></i>
                        <!-- 검색 후 input 필드를 비우기 위해 value 속성 제거됨 -->
                        <input
                                type="text"
                                name="searchKeyword"
                                placeholder="레시피, 재료, 요리법 검색..."
                                class="recipe-search-input pl-10 pr-4 py-3 w-full border-2 border-gray-200 focus:border-orange-500 rounded-lg bg-white"
                        />
                    </div>

                    <button type="submit" class="bg-orange-500 hover:bg-orange-600 text-white px-4 py-4 rounded-lg text-sm transition-colors">
                        <i data-lucide="search" class="h-4 w-4"></i>
                    </button>
                </div>
            </form>
        </div>
    </section>

    <!-- 카테고리 + 레시피 목록 -->
    <section class="py-8">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div class="flex flex-col lg:flex-row gap-8">
                <!-- 사이드바 - 카테고리 -->
                <aside class="lg:w-64">
                    <div class="bg-white border border-gray-200 rounded-lg p-6 sticky top-24">
                        <h3 class="font-semibold mb-4 flex items-center">
                            <i data-lucide="chef-hat" class="h-5 w-5 mr-2 text-orange-500"></i>
                            카테고리
                        </h3>

                        <!-- ✅ JS 렌더링 전용 -->
                        <div class="space-y-2" id="recipeCategoryButtons">
                            <!-- recipe.js에서 렌더링됨 -->
                            <div class="text-gray-400 text-sm text-center py-2">로딩 중...</div>

                        </div>
                    </div>
                </aside>


                <!-- 메인 -->
                <main class="flex-1">

                    <!-- searchKeyword가 비어있을 때(검색 중이 아닐 때)만 인기 레시피를 표시 -->
                    <c:if test="${empty searchKeyword}">
                        <!-- 인기 레시피 -->
                        <div class="mb-12">
                            <h3 class="text-xl flex items-center mb-6">
                                <i data-lucide="trending-up" class="w-6 h-6 mr-3 text-red-500"></i>
                                인기 레시피 <span class="ml-2 px-3 py-1 bg-red-100 text-red-700 text-sm rounded-full">TOP 3</span>
                            </h3>
                            <div id="popularGrid" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                <c:forEach var="recipe" items="${best3Recipes}" varStatus="loop">
                                    <!-- ✅ JS 카운팅용: 카테고리 키 계산 -->
                                    <c:set var="catKey" value="" />
                                    <c:choose>
                                        <c:when test="${recipe.categoryKr eq '한식'}"><c:set var="catKey" value="korean"/></c:when>
                                        <c:when test="${recipe.categoryKr eq '양식'}"><c:set var="catKey" value="western"/></c:when>
                                        <c:when test="${recipe.categoryKr eq '중식'}"><c:set var="catKey" value="chinese"/></c:when>
                                        <c:when test="${recipe.categoryKr eq '일식'}"><c:set var="catKey" value="japanese"/></c:when>
                                        <c:when test="${recipe.categoryKr eq '디저트'}"><c:set var="catKey" value="dessert"/></c:when>
                                        <c:otherwise><c:set var="catKey" value="etc"/></c:otherwise>
                                    </c:choose>

                                    <!-- ✅ 카드에 data-category/data-title/data-description 부여 -->
                                    <div class="popular-recipe-card bg-white border border-gray-200 rounded-lg overflow-hidden shadow-lg"
                                         data-category="${catKey}"
                                         data-title="${fn:escapeXml(recipe.titleKr)}"
                                         data-description="${fn:escapeXml(fn:substring(recipe.instructionKr,0,120))}"
                                         onclick="location.href='<c:url value='/recipe/view'><c:param name='recipeId' value='${recipe.recipeId}'/></c:url>'">
                                        <div class="relative">
                                            <div class="absolute top-3 left-3 z-10">
                                                <div class="rank-badge w-8 h-8 text-white rounded-full flex items-center justify-center text-sm">
                                                        ${loop.index + 1}
                                                </div>
                                            </div>
                                            <img src="${empty recipe.thumbnail ? '/images/default_thumbnail.png' : recipe.thumbnail}"
                                                 alt="${recipe.titleKr}"
                                                 class="recipe-card-image w-full h-56 object-cover"
                                                 onerror="this.onerror=null; this.src='/images/default_thumbnail.png';"/>
                                            <div class="absolute top-3 right-3 flex flex-col space-y-2">
                                                <span class="category-badge ${recipe.categoryKr eq '한식' ? 'korean' :
                                                                         recipe.categoryKr eq '일식' ? 'japanese' :
                                                                         recipe.categoryKr eq '중식' ? 'chinese' :
                                                                         recipe.categoryKr eq '양식' ? 'western' :
                                                                         recipe.categoryKr eq '디저트' ? 'dessert' : ''}">
                                                        ${recipe.categoryKr}
                                                </span>
                                                <span class="category-badge hot-badge">HOT</span>
                                            </div>
                                        </div>
                                        <div class="p-4">
                                            <h3 class="recipe-card-title text-lg mb-2 line-clamp-1">${recipe.titleKr}</h3>
                                            <p class="text-sm text-gray-500 mb-3 line-clamp-2">${fn:substring(recipe.instructionKr,0,80)}...</p>

                                            <div class="flex items-center justify-between text-sm mb-2">
                                                <div class="flex items-center space-x-1 text-gray-500">
                                                    <i data-lucide="clock" class="h-4 w-4"></i><span>${recipe.cookTime}</span>
                                                </div>
                                                <span class="difficulty-badge
                                                ${recipe.difficulty == 'Easy' ? 'easy' :
                                                  recipe.difficulty == 'Normal' ? 'normal' :
                                                  recipe.difficulty == 'Hard' ? 'hard' : ''}">
                                                        ${recipe.difficulty}
                                                </span>
                                            </div>

                                            <hr class="my-2">

                                            <div class="flex items-center space-x-4 text-sm text-gray-500">
                                                <div class="flex items-center space-x-1">
                                                    <i data-lucide="eye" class="h-4 w-4"></i><span>${recipe.viewCount}</span>
                                                </div>
                                                <div class="flex items-center space-x-1 text-red-500">
                                                    <i data-lucide="heart" class="h-4 w-4"></i><span>${recipe.likeCount}</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>

                    <!-- 전체 레시피 -->
                    <div>
                        <h3 class="text-xl flex items-center mb-6">
                            <i data-lucide="list" class="w-6 h-6 mr-3 text-orange-500"></i>
                            <%-- 👇 [수정됨] JSTL을 사용하여 제목을 동적으로 변경 --%>
                            <c:choose>
                                <%-- URL에 categoryKr 파라미터가 있을 경우 --%>
                                <c:when test="${not empty categoryKr}">
                                    <c:out value="${categoryKr}" /> 레시피
                                </c:when>
                                <%-- categoryKr 파라미터가 없을 경우 (전체 보기) --%>
                                <c:otherwise>
                                    전체 레시피
                                </c:otherwise>
                            </c:choose>
                            <span class="ml-2 px-3 py-1 bg-gray-100 text-gray-700 text-sm rounded-full">${totalCount}개</span>
                        </h3>
                        <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
                            <c:forEach var="recipe" items="${recipeList}">
                                <div class="recipe-card bg-white border border-gray-200 rounded-lg overflow-hidden shadow-lg"
                                     onclick="location.href='/recipe/view?recipeId=${recipe.recipeId}'">
                                    <div class="relative">
                                        <img src="${empty recipe.thumbnail ? '/images/default_recipe.jpg' : recipe.thumbnail}"
                                             alt="${recipe.titleKr}"
                                             class="recipe-card-image w-full h-56 object-cover"
                                             onerror="this.src='/images/default_recipe.jpg'"/>
                                        <div class="absolute top-3 right-3">
                                            <span class="category-badge ${recipe.categoryKr eq '한식' ? 'korean' :
                                                                         recipe.categoryKr eq '일식' ? 'japanese' :
                                                                         recipe.categoryKr eq '중식' ? 'chinese' :
                                                                         recipe.categoryKr eq '양식' ? 'western' :
                                                                         recipe.categoryKr eq '디저트' ? 'dessert' : ''}">
                                                    ${recipe.categoryKr}
                                            </span>
                                        </div>
                                    </div>
                                    <div class="p-4">
                                        <h3 class="recipe-card-title text-lg mb-2 line-clamp-1">${recipe.titleKr}</h3>
                                        <p class="text-sm text-gray-500 mb-3 line-clamp-2">${fn:substring(recipe.instructionKr,0,80)}...</p>

                                        <div class="flex items-center justify-between text-sm mb-2">
                                            <div class="flex items-center space-x-1 text-gray-500">
                                                <i data-lucide="clock" class="h-4 w-4"></i><span>${recipe.cookTime}</span>
                                            </div>
                                            <span class="difficulty-badge
                                                ${recipe.difficulty == 'Easy' ? 'easy' :
                                                  recipe.difficulty == 'Normal' ? 'normal' :
                                                  recipe.difficulty == 'Hard' ? 'hard' : ''}">
                                                    ${recipe.difficulty}
                                            </span>
                                        </div>

                                        <hr class="my-2">

                                        <div class="flex items-center space-x-4 text-sm text-gray-500">
                                            <div class="flex items-center space-x-1">
                                                <i data-lucide="eye" class="h-4 w-4"></i><span>${recipe.viewCount}</span>
                                            </div>
                                            <div class="flex items-center space-x-1 text-red-500">
                                                <i data-lucide="heart" class="h-4 w-4"></i><span>${recipe.likeCount}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <!-- ✅ 페이징 (10개 블록, fmt:formatNumber 적용) -->
                        <div class="mt-8 flex justify-center">
                            <c:if test="${totalPages > 1}">
                                <nav class="flex space-x-2">

                                    <!-- ✅ 안전 가드 -->
                                    <c:set var="totalPagesSafe" value="${empty totalPages ? 0 : totalPages}" />
                                    <c:set var="currentPageSafe" value="${empty currentPage ? 0 : currentPage}" />
                                    <c:set var="sizeSafe" value="${empty param.size ? 9 : param.size}" />

                                    <!-- ✅ 블록 계산 (0-base) -->
                                    <c:set var="blockSize" value="10"/>
                                    <c:set var="blockStart" value="${currentPageSafe - (currentPageSafe mod blockSize)}"/>
                                    <c:set var="blockEnd" value="${blockStart + blockSize - 1}"/>
                                    <c:if test="${blockEnd >= totalPagesSafe - 1}">
                                        <c:set var="blockEnd" value="${totalPagesSafe - 1}"/>
                                    </c:if>

                                    <c:if test="${totalPagesSafe > 1}">
                                        <nav class="flex space-x-2">
                                            <!-- ◀ 이전 블록 (10칸 점프) -->
                                            <c:set var="prevBlockPage" value="${blockStart - blockSize}"/>
                                            <c:if test="${prevBlockPage < 0}">
                                                <c:set var="prevBlockPage" value="0"/>
                                            </c:if>
                                            <c:url var="prevBlockUrl" value="/recipe/list">
                                                <c:param name="page" value="${prevBlockPage}"/>
                                                <c:param name="size" value="${sizeSafe}"/>
                                                <c:param name="categoryKr" value="${categoryKr}"/>
                                                <c:param name="searchKeyword" value="${searchKeyword}"/>
                                            </c:url>
                                            <a href="${prevBlockUrl}" class="px-3 py-1 border rounded ${blockStart==0?'pointer-events-none opacity-50':''}">«</a>

                                            <!-- 현재 블록 1~10 -->
                                            <c:forEach var="i" begin="${blockStart}" end="${blockEnd}">
                                                <c:url var="pageUrl" value="/recipe/list">
                                                    <c:param name="page" value="${i}"/>
                                                    <c:param name="size" value="${sizeSafe}"/>
                                                    <c:param name="categoryKr" value="${categoryKr}"/>
                                                    <c:param name="searchKeyword" value="${searchKeyword}"/>
                                                </c:url>
                                                <a href="${pageUrl}"
                                                   class="px-3 py-1 border rounded
                                                         ${currentPageSafe == i ? 'bg-orange-500 text-white border-orange-500' : 'bg-white text-gray-700'}">
                                                        ${i + 1}
                                                </a>
                                            </c:forEach>

                                            <!-- » 다음 블록 (10칸 점프) -->
                                            <c:set var="nextBlockPage" value="${blockStart + blockSize}"/>
                                            <c:if test="${nextBlockPage > totalPagesSafe - 1}">
                                                <c:set var="nextBlockPage" value="${totalPagesSafe - 1}"/>
                                            </c:if>
                                            <c:url var="nextBlockUrl" value="/recipe/list">
                                                <c:param name="page" value="${nextBlockPage}"/>
                                                <c:param name="size" value="${sizeSafe}"/>
                                                <c:param name="categoryKr" value="${categoryKr}"/>
                                                <c:param name="searchKeyword" value="${searchKeyword}"/>
                                            </c:url>
                                            <a href="${nextBlockUrl}" class="px-3 py-1 border rounded ${(blockStart + blockSize) > (totalPagesSafe - 1) ? 'pointer-events-none opacity-50':''}">»</a>
                                        </nav>
                                    </c:if>

                                </nav>
                            </c:if>
                        </div>
                    </div>
                </main>
            </div>
        </div>
    </section>
</div>

<!-- ===== 챗봇 UI + JS 연결 (푸터 위로 이동) ===== -->
<jsp:include page="/WEB-INF/views/chat/chatbot.jsp" />

<script>
    document.addEventListener('DOMContentLoaded', function() {
        lucide.createIcons();
    });
</script>
<script src="/js/common/common.js"></script>
<script src="/js/recipe.js"></script>

<jsp:include page="/common/footer.jsp"/>
</body>
</html>
