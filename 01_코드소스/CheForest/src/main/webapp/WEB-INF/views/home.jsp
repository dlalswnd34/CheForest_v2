<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CheForest - 요리 레시피 공유 사이트</title>
    <link rel="stylesheet" href="/css/common/common.css">
    <link rel="stylesheet" href="/css/home.css">
    <!-- Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
</head>
<body>
<jsp:include page="/common/header.jsp"/>

    <main>
    <!-- 메인 배너 섹션 -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div id="bannerSlider" class="relative w-full h-96 md:h-[500px] lg:h-[600px] overflow-hidden rounded-xl group">
            <!-- 슬라이드 컨테이너 -->
            <div class="relative w-full h-full">
                <!-- 슬라이드 1 - 한식 -->
                <div class="slide absolute inset-0 slide-transition opacity-100" data-slide="0">
                    <img src="https://images.unsplash.com/photo-1693429308125-3be7b105ad56?w=1200&h=600&fit=crop&auto=format"
                        alt="한식 요리"
                        class="w-full h-full object-cover">
                    <!-- 텍스트 가독성을 위한 미세한 어두운 오버레이 -->
                    <div class="absolute inset-0 bg-black/20"></div>
                    <div class="absolute inset-0 flex items-center justify-center text-center text-white">
                        <div class="max-w-4xl px-6">
                            <span class="inline-block px-4 py-2 bg-white/90 text-gray-800 rounded-full text-sm mb-6 backdrop-blur-sm">한식</span>
                            <h2 class="text-3xl md:text-5xl lg:text-6xl mb-6 drop-shadow-lg">전통 한식의 깊은 맛</h2>
                            <p class="text-lg md:text-xl lg:text-2xl drop-shadow-md">집에서 만드는 정통 한국 요리</p>
                        </div>
                    </div>
                </div>

                <!-- 슬라이드 2 - 양식 -->
                <div class="slide absolute inset-0 slide-transition opacity-0" data-slide="1">
                    <img src="https://images.unsplash.com/photo-1662197480393-2a82030b7b83?w=1200&h=600&fit=crop&auto=format"
                        alt="양식 요리"
                        class="w-full h-full object-cover">
                    <div class="absolute inset-0 bg-black/20"></div>
                    <div class="absolute inset-0 flex items-center justify-center text-center text-white">
                        <div class="max-w-4xl px-6">
                            <span class="inline-block px-4 py-2 bg-white/90 text-gray-800 rounded-full text-sm mb-6 backdrop-blur-sm">양식</span>
                            <h2 class="text-3xl md:text-5xl lg:text-6xl mb-6 drop-shadow-lg">세계의 맛, 서양 요리</h2>
                            <p class="text-lg md:text-xl lg:text-2xl drop-shadow-md">집에서 즐기는 이탈리안 & 프렌치</p>
                        </div>
                    </div>
                </div>

                <!-- 슬라이드 3 - 중식 -->
                <div class="slide absolute inset-0 slide-transition opacity-0" data-slide="2">
                    <img src="https://images.unsplash.com/photo-1667474667223-bf26ff0db2a4?w=1200&h=600&fit=crop&auto=format"
                        alt="중식 요리"
                        class="w-full h-full object-cover">
                    <div class="absolute inset-0 bg-black/20"></div>
                    <div class="absolute inset-0 flex items-center justify-center text-center text-white">
                        <div class="max-w-4xl px-6">
                            <span class="inline-block px-4 py-2 bg-white/90 text-gray-800 rounded-full text-sm mb-6 backdrop-blur-sm">중식</span>
                            <h2 class="text-3xl md:text-5xl lg:text-6xl mb-6 drop-shadow-lg">진짜 중식의 깊은 맛</h2>
                            <p class="text-lg md:text-xl lg:text-2xl drop-shadow-md">집에서 만드는 authentic 중국 요리</p>
                        </div>
                    </div>
                </div>

                <!-- 슬라이드 4 - 일식 -->
                <div class="slide absolute inset-0 slide-transition opacity-0" data-slide="3">
                    <img src="https://images.unsplash.com/photo-1712725213572-443fe866a69a?w=1200&h=600&fit=crop&auto=format"
                        alt="일식 요리"
                        class="w-full h-full object-cover">
                    <div class="absolute inset-0 bg-black/20"></div>
                    <div class="absolute inset-0 flex items-center justify-center text-center text-white">
                        <div class="max-w-4xl px-6">
                            <span class="inline-block px-4 py-2 bg-white/90 text-gray-800 rounded-full text-sm mb-6 backdrop-blur-sm">일식</span>
                            <h2 class="text-3xl md:text-5xl lg:text-6xl mb-6 drop-shadow-lg">정교한 일본 요리의 예술</h2>
                            <p class="text-lg md:text-xl lg:text-2xl drop-shadow-md">섬세함이 담긴 일식 레시피</p>
                        </div>
                    </div>
                </div>

                <!-- 슬라이드 5 - 디저트 -->
                <div class="slide absolute inset-0 slide-transition opacity-0" data-slide="4">
                    <img src="https://images.unsplash.com/photo-1644158776192-2d24ce35da1d?w=1200&h=600&fit=crop&auto=format"
                        alt="디저트"
                        class="w-full h-full object-cover">
                    <div class="absolute inset-0 bg-black/20"></div>
                    <div class="absolute inset-0 flex items-center justify-center text-center text-white">
                        <div class="max-w-4xl px-6">
                            <span class="inline-block px-4 py-2 bg-white/90 text-gray-800 rounded-full text-sm mb-6 backdrop-blur-sm">디저트</span>
                            <h2 class="text-3xl md:text-5xl lg:text-6xl mb-6 drop-shadow-lg">달콤한 마무리</h2>
                            <p class="text-lg md:text-xl lg:text-2xl drop-shadow-md">특별한 날을 위한 홈메이드 디저트</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 네비게이션 화살표 -->
            <button
                onclick="previousSlide()"
                class="absolute left-4 top-1/2 transform -translate-y-1/2 bg-white/20 hover:bg-white/30 text-white p-3 rounded-full transition-all duration-200 opacity-0 group-hover:opacity-100 backdrop-blur-sm"
                aria-label="이전 슬라이드"
            >
                <i data-lucide="chevron-left" class="w-6 h-6"></i>
            </button>

            <button
                onclick="nextSlide()"
                class="absolute right-4 top-1/2 transform -translate-y-1/2 bg-white/20 hover:bg-white/30 text-white p-3 rounded-full transition-all duration-200 opacity-0 group-hover:opacity-100 backdrop-blur-sm"
                aria-label="다음 슬라이드"
            >
                <i data-lucide="chevron-right" class="w-6 h-6"></i>
            </button>

            <!-- 도트 인디케이터 -->
            <div class="absolute bottom-6 left-1/2 transform -translate-x-1/2 flex space-x-3">
                <button onclick="goToSlide(0)" class="slide-dot w-3 h-3 rounded-full bg-white scale-125 transition-all duration-200" aria-label="1번째 슬라이드로 이동"></button>
                <button onclick="goToSlide(1)" class="slide-dot w-3 h-3 rounded-full bg-white/50 hover:bg-white/75 transition-all duration-200" aria-label="2번째 슬라이드로 이동"></button>
                <button onclick="goToSlide(2)" class="slide-dot w-3 h-3 rounded-full bg-white/50 hover:bg-white/75 transition-all duration-200" aria-label="3번째 슬라이드로 이동"></button>
                <button onclick="goToSlide(3)" class="slide-dot w-3 h-3 rounded-full bg-white/50 hover:bg-white/75 transition-all duration-200" aria-label="4번째 슬라이드로 이동"></button>
                <button onclick="goToSlide(4)" class="slide-dot w-3 h-3 rounded-full bg-white/50 hover:bg-white/75 transition-all duration-200" aria-label="5번째 슬라이드로 이동"></button>
            </div>
        </div>
    </section>

        <!-- 인기 레시피 섹션 -->
        <section id="popular" class="py-16 bg-gray-50/30">
            <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div class="text-center mb-12">
                    <div class="flex items-center justify-center mb-4">
                        <i data-lucide="trending-up" class="h-8 w-8 text-orange-500 mr-3"></i>
                        <h2 class="text-4xl font-black brand-gradient">인기 레시피</h2>
                    </div>
                    <p class="text-gray-600 max-w-2xl mx-auto">
                        요리사들이 가장 사랑하는 레시피를 만나보세요. 좋아요와 조회수가 높은 검증된 맛있는 요리들입니다.
                    </p>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                    <c:forEach var="recipe" items="${popularRecipes}">
                        <div class="recipe-card bg-white rounded-xl shadow-lg overflow-hidden cursor-pointer"
                             onclick="location.href='/recipe/view?recipeId=${recipe.id}'">
                            <div class="relative">
                                <img src="<c:out value='${recipe.thumbnail}'/>"
                                     alt="<c:out value='${recipe.title}'/>"
                                     class="recipe-image w-full h-48 object-cover"
                                     onerror="handleImageError(this)">
                                <div class="absolute top-3 left-3">
                                  <span class="bg-red-100 text-red-800 px-3 py-1 rounded-full text-sm font-medium">
                                    <c:out value="${recipe.categoryName}"/>
                                  </span>
                                </div>
                            </div>

                            <div class="p-4">
                                <h3 class="text-lg mb-2 hover:text-orange-500 transition-colors">
                                    <c:out value="${recipe.title}"/>
                                </h3>
                                <p class="text-sm text-gray-600 mb-3">
                                    by <c:out value="${recipe.writerNickname}"/>
                                </p>

                                <div class="flex items-center justify-between text-sm text-gray-500 mb-3">
                                    <div class="flex items-center space-x-1">
                                        <i data-lucide="clock" class="w-4 h-4"></i>
                                        <span><c:out value="${recipe.cookTime}"/></span>
                                    </div>
                                    <span class="text-xs bg-gray-100 px-2 py-1 rounded">
                                        <c:out value="${recipe.difficulty}"/>
                                      </span>
                                </div>

                                <div class="flex items-center justify-between pt-3 border-t border-gray-200">
                                    <div class="flex items-center space-x-3 text-sm">
                                        <div class="flex items-center space-x-1 text-gray-500">
                                            <i data-lucide="eye" class="w-4 h-4"></i>
                                            <span><fmt:formatNumber value="${recipe.viewCount}" type="number"/></span>
                                        </div>
                                        <div class="flex items-center space-x-1 text-red-500">
                                            <i data-lucide="heart" class="w-4 h-4"></i>
                                            <span><fmt:formatNumber value="${recipe.likeCount}" type="number"/></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <div class="text-center mt-10">
                    <button id="moreRecipesBtn" class="btn-orange text-white px-8 py-3 rounded-lg transition-all duration-200">
                        더 많은 레시피 보기
                    </button>
                </div>
            </div>
        </section>

        <!-- 카테고리별 탐색 섹션 -->
        <section class="py-16">
            <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div class="text-center mb-12">
                    <div class="flex items-center justify-center mb-4">
                        <i data-lucide="compass" class="h-8 w-8 text-orange-500 mr-3"></i>
                        <h2 class="text-4xl font-black brand-gradient">카테고리별 탐색</h2>
                    </div>
                    <p class="text-gray-600 max-w-4xl mx-auto">
                        한식부터 디저트까지, 원하는 요리 스타일을 선택하고 탐색해보세요.
                    </p>
                </div>


            <!-- 카테고리 선택 -->
            <div class="flex flex-wrap justify-center gap-4 mb-8">
                <button class="category-btn active flex items-center space-x-3 px-6 py-3 rounded-xl transition-all duration-300 font-semibold border bg-gradient-to-r from-pink-500 to-orange-500 text-white shadow-2xl shadow-pink-500/30 border-transparent transform scale-105 hover:shadow-2xl hover:shadow-pink-500/40" onclick="switchCategory('korean')">
                    <span class="text-2xl">🥢</span>
                    <span class="whitespace-nowrap">한식</span>
                </button>
                <button class="category-btn flex items-center space-x-3 px-6 py-3 rounded-xl transition-all duration-300 font-semibold border bg-white text-gray-700 shadow-lg shadow-gray-200/50 border-gray-100 hover:shadow-2xl hover:shadow-orange-500/30 hover:bg-gradient-to-r hover:from-pink-500 hover:to-orange-500 hover:text-white hover:transform hover:scale-105 hover:border-transparent" onclick="switchCategory('western')">
                    <span class="text-2xl">🍝</span>
                    <span class="whitespace-nowrap">양식</span>
                </button>
                <button class="category-btn flex items-center space-x-3 px-6 py-3 rounded-xl transition-all duration-300 font-semibold border bg-white text-gray-700 shadow-lg shadow-gray-200/50 border-gray-100 hover:shadow-2xl hover:shadow-orange-500/30 hover:bg-gradient-to-r hover:from-pink-500 hover:to-orange-500 hover:text-white hover:transform hover:scale-105 hover:border-transparent" onclick="switchCategory('chinese')">
                    <span class="text-2xl">🥟</span>
                    <span class="whitespace-nowrap">중식</span>
                </button>
                <button class="category-btn flex items-center space-x-3 px-6 py-3 rounded-xl transition-all duration-300 font-semibold border bg-white text-gray-700 shadow-lg shadow-gray-200/50 border-gray-100 hover:shadow-2xl hover:shadow-orange-500/30 hover:bg-gradient-to-r hover:from-pink-500 hover:to-orange-500 hover:text-white hover:transform hover:scale-105 hover:border-transparent" onclick="switchCategory('japanese')">
                    <span class="text-2xl">🍣</span>
                    <span class="whitespace-nowrap">일식</span>
                </button>
                <button class="category-btn flex items-center space-x-3 px-6 py-3 rounded-xl transition-all duration-300 font-semibold border bg-white text-gray-700 shadow-lg shadow-gray-200/50 border-gray-100 hover:shadow-2xl hover:shadow-orange-500/30 hover:bg-gradient-to-r hover:from-pink-500 hover:to-orange-500 hover:text-white hover:transform hover:scale-105 hover:border-transparent" onclick="switchCategory('dessert')">
                    <span class="text-2xl">🧁</span>
                    <span class="whitespace-nowrap">디저트</span>
                </button>
            </div>
                <!-- 선택된 카테고리의 탭 콘텐츠 -->
                <div class="max-w-4xl mx-auto">
                    <div class="w-full">
                        <!-- 탭 네비게이션 -->
                        <div class="grid w-full grid-cols-2 mb-8 bg-gray-100 rounded-lg p-1">
                            <button id="recipesTabBtn" class="tab-btn active flex items-center justify-center space-x-2 py-2 px-4 rounded-md bg-white shadow-sm font-medium text-gray-900">
                                <i data-lucide="chef-hat" class="w-4 h-4"></i>
                                <span>CheForest 레시피</span>
                            </button>
                            <button id="communityTabBtn" class="tab-btn flex items-center justify-center space-x-2 py-2 px-4 rounded-md font-medium text-gray-600 hover:text-gray-900">
                                <i data-lucide="users" class="w-4 h-4"></i>
                                <span>사용자 레시피</span>
                            </button>
                        </div>

                        <!-- CheForest 레시피 탭 -->
                        <div id="recipesTab" class="tab-content">
                            <div class="bg-white border border-gray-200 rounded-lg">
                                <div class="p-6">
                                    <div class="flex items-center justify-between mb-6">
                                        <h3 class="text-xl" id="categoryTitle">
                                            카테고리명 자리 CheForest 레시피
                                        </h3>
                                        <button class="view-all-btn text-orange-500 flex items-center space-x-1 transition-all duration-200"
                                                data-type="cheforest" data-category="${activeCategory}">
                                            <span>전체보기</span>
                                            <i data-lucide="arrow-right" class="w-4 h-4"></i>
                                        </button>
                                    </div>

                                    <div class="space-y-4" id="recipesList">
                                        <!-- 한식 -->
                                        <div id="recipes-korean" class="category-pane">
                                            <c:forEach var="r" items="${categoryRecipes['한식']}" varStatus="st">
                                                <div class="flex items-center justify-between p-4 bg-gray-50/30 rounded-lg hover:bg-gray-50/50 transition-colors cursor-pointer">
                                                    <div class="flex-1">
                                                        <h4 class="font-medium mb-1"><c:out value="${r.title}"/></h4>
                                                        <p class="text-sm text-gray-600">by <c:out value="${r.writerNickname}"/></p>
                                                    </div>
                                                    <div class="flex items-center space-x-4 text-sm">
                                                        <div class="flex items-center space-x-1">
                                                            <i data-lucide="heart" class="w-4 h-4 fill-red-400 text-red-400"></i>
                                                            <span><c:out value="${r.likeCount}"/></span>
                                                            <span class="text-gray-500">(조회수 <c:out value="${r.viewCount}"/>)</span>
                                                        </div>
                                                        <span class="bg-gray-100 text-gray-600 px-2 py-1 rounded text-xs">${st.index + 1}위</span>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <!-- 양식 -->
                                        <div id="recipes-western" class="category-pane hidden">
                                            <c:forEach var="r" items="${categoryRecipes['양식']}" varStatus="st">
                                                <div class="flex items-center justify-between p-4 bg-gray-50/30 rounded-lg hover:bg-gray-50/50 transition-colors cursor-pointer">
                                                    <div class="flex-1">
                                                        <h4 class="font-medium mb-1"><c:out value="${r.title}"/></h4>
                                                        <p class="text-sm text-gray-600">by <c:out value="${r.writerNickname}"/></p>
                                                    </div>
                                                    <div class="flex items-center space-x-4 text-sm">
                                                        <div class="flex items-center space-x-1">
                                                            <i data-lucide="heart" class="w-4 h-4 fill-red-400 text-red-400"></i>
                                                            <span><c:out value="${r.likeCount}"/></span>
                                                            <span class="text-gray-500">(조회수 <c:out value="${r.viewCount}"/>)</span>
                                                        </div>
                                                        <span class="bg-gray-100 text-gray-600 px-2 py-1 rounded text-xs">${st.index + 1}위</span>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <!-- 중식 -->
                                        <div id="recipes-chinese" class="category-pane hidden">
                                            <c:forEach var="r" items="${categoryRecipes['중식']}" varStatus="st">
                                                <div class="flex items-center justify-between p-4 bg-gray-50/30 rounded-lg hover:bg-gray-50/50 transition-colors cursor-pointer">
                                                    <div class="flex-1">
                                                        <h4 class="font-medium mb-1"><c:out value="${r.title}"/></h4>
                                                        <p class="text-sm text-gray-600">by <c:out value="${r.writerNickname}"/></p>
                                                    </div>
                                                    <div class="flex items-center space-x-4 text-sm">
                                                        <div class="flex items-center space-x-1">
                                                            <i data-lucide="heart" class="w-4 h-4 fill-red-400 text-red-400"></i>
                                                            <span><c:out value="${r.likeCount}"/></span>
                                                            <span class="text-gray-500">(조회수 <c:out value="${r.viewCount}"/>)</span>
                                                        </div>
                                                        <span class="bg-gray-100 text-gray-600 px-2 py-1 rounded text-xs">${st.index + 1}위</span>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <!-- 일식 -->
                                        <div id="recipes-japanese" class="category-pane hidden">
                                            <c:forEach var="r" items="${categoryRecipes['일식']}" varStatus="st">
                                                <div class="flex items-center justify-between p-4 bg-gray-50/30 rounded-lg hover:bg-gray-50/50 transition-colors cursor-pointer">
                                                    <div class="flex-1">
                                                        <h4 class="font-medium mb-1"><c:out value="${r.title}"/></h4>
                                                        <p class="text-sm text-gray-600">by <c:out value="${r.writerNickname}"/></p>
                                                    </div>
                                                    <div class="flex items-center space-x-4 text-sm">
                                                        <div class="flex items-center space-x-1">
                                                            <i data-lucide="heart" class="w-4 h-4 fill-red-400 text-red-400"></i>
                                                            <span><c:out value="${r.likeCount}"/></span>
                                                            <span class="text-gray-500">(조회수 <c:out value="${r.viewCount}"/>)</span>
                                                        </div>
                                                        <span class="bg-gray-100 text-gray-600 px-2 py-1 rounded text-xs">${st.index + 1}위</span>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <!-- 디저트 -->
                                        <div id="recipes-dessert" class="category-pane hidden">
                                            <c:forEach var="r" items="${categoryRecipes['디저트']}" varStatus="st">
                                                <div class="flex items-center justify-between p-4 bg-gray-50/30 rounded-lg hover:bg-gray-50/50 transition-colors cursor-pointer">
                                                    <div class="flex-1">
                                                        <h4 class="font-medium mb-1"><c:out value="${r.title}"/></h4>
                                                        <p class="text-sm text-gray-600">by <c:out value="${r.writerNickname}"/></p>
                                                    </div>
                                                    <div class="flex items-center space-x-4 text-sm">
                                                        <div class="flex items-center space-x-1">
                                                            <i data-lucide="heart" class="w-4 h-4 fill-red-400 text-red-400"></i>
                                                            <span><c:out value="${r.likeCount}"/></span>
                                                            <span class="text-gray-500">(조회수 <c:out value="${r.viewCount}"/>)</span>
                                                        </div>
                                                        <span class="bg-gray-100 text-gray-600 px-2 py-1 rounded text-xs">${st.index + 1}위</span>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>

                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 사용자 레시피 탭 -->
                        <div id="communityTab" class="tab-content hidden">
                            <div class="bg-white border border-gray-200 rounded-lg">
                                <div class="p-6">
                                    <div class="flex items-center justify-between mb-6">
                                        <h3 class="text-xl" id="communityTitle">
                                            카테고리명 자리 사용자 레시피
                                        </h3>
                                        <button class="view-all-btn text-orange-500 flex items-center space-x-1 transition-all duration-200"
                                                data-type="user" data-category="${activeCategory}">
                                            <span>전체보기</span>
                                            <i data-lucide="arrow-right" class="w-4 h-4"></i>
                                        </button>
                                    </div>

                                    <div class="space-y-4">
                                        <!-- 한식 -->
                                        <div id="community-korean" class="category-pane">
                                            <c:forEach var="b" items="${categoryBoards['한식']}">
                                                <div class="flex items-center justify-between p-4 bg-gray-50/30 rounded-lg hover:bg-gray-50/50 transition-colors cursor-pointer">
                                                    <div class="flex-1">
                                                        <h4 class="font-medium mb-1"><c:out value="${b.title}"/></h4>
                                                        <p class="text-sm text-gray-600">by <c:out value="${b.writerNickname}"/></p>
                                                    </div>
                                                    <div class="flex items-center space-x-4 text-sm text-gray-500">
                                                        <span>답글 <c:out value="${b.commentCount}"/></span>
                                                        <span><c:out value="${b.createdAgo}"/></span>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <!-- 양식 -->
                                        <div id="community-western" class="category-pane hidden">
                                            <c:forEach var="b" items="${categoryBoards['양식']}">
                                                <div class="flex items-center justify-between p-4 bg-gray-50/30 rounded-lg hover:bg-gray-50/50 transition-colors cursor-pointer">
                                                    <div class="flex-1">
                                                        <h4 class="font-medium mb-1"><c:out value="${b.title}"/></h4>
                                                        <p class="text-sm text-gray-600">by <c:out value="${b.writerNickname}"/></p>
                                                    </div>
                                                    <div class="flex items-center space-x-4 text-sm text-gray-500">
                                                        <span>답글 <c:out value="${b.commentCount}"/></span>
                                                        <span><c:out value="${b.createdAgo}"/></span>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <!-- 중식 -->
                                        <div id="community-chinese" class="category-pane hidden">
                                            <c:forEach var="b" items="${categoryBoards['중식']}">
                                                <div class="flex items-center justify-between p-4 bg-gray-50/30 rounded-lg hover:bg-gray-50/50 transition-colors cursor-pointer">
                                                    <div class="flex-1">
                                                        <h4 class="font-medium mb-1"><c:out value="${b.title}"/></h4>
                                                        <p class="text-sm text-gray-600">by <c:out value="${b.writerNickname}"/></p>
                                                    </div>
                                                    <div class="flex items-center space-x-4 text-sm text-gray-500">
                                                        <span>답글 <c:out value="${b.commentCount}"/></span>
                                                        <span><c:out value="${b.createdAgo}"/></span>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <!-- 일식 -->
                                        <div id="community-japanese" class="category-pane hidden">
                                            <c:forEach var="b" items="${categoryBoards['일식']}">
                                                <div class="flex items-center justify-between p-4 bg-gray-50/30 rounded-lg hover:bg-gray-50/50 transition-colors cursor-pointer">
                                                    <div class="flex-1">
                                                        <h4 class="font-medium mb-1"><c:out value="${b.title}"/></h4>
                                                        <p class="text-sm text-gray-600">by <c:out value="${b.writerNickname}"/></p>
                                                    </div>
                                                    <div class="flex items-center space-x-4 text-sm text-gray-500">
                                                        <span>답글 <c:out value="${b.commentCount}"/></span>
                                                        <span><c:out value="${b.createdAgo}"/></span>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <!-- 디저트 -->
                                        <div id="community-dessert" class="category-pane hidden">
                                            <c:forEach var="b" items="${categoryBoards['디저트']}">
                                                <div class="flex items-center justify-between p-4 bg-gray-50/30 rounded-lg hover:bg-gray-50/50 transition-colors cursor-pointer">
                                                    <div class="flex-1">
                                                        <h4 class="font-medium mb-1"><c:out value="${b.title}"/></h4>
                                                        <p class="text-sm text-gray-600">by <c:out value="${b.writerNickname}"/></p>
                                                    </div>
                                                    <div class="flex items-center space-x-4 text-sm text-gray-500">
                                                        <span>답글 <c:out value="${b.commentCount}"/></span>
                                                        <span><c:out value="${b.createdAgo}"/></span>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- 포인트 랭킹 섹션 -->
        <section id="ranking" class="py-16 bg-white">
            <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div class="text-center mb-12">
                    <div class="flex items-center justify-center mb-4">
                        <i data-lucide="award" class="h-8 w-8 text-orange-500 mr-3"></i>
                        <h2 class="text-4xl font-black brand-gradient">포인트 랭킹 TOP 5</h2>
                    </div>
                    <p class="text-gray-600 max-w-2xl mx-auto">
                        가장 활발한 셰프들을 만나보세요! CheForest에서 활동 포인트 상위 5명입니다.
                    </p>
                </div>

                <div class="max-w-4xl mx-auto bg-white rounded-xl shadow-lg border border-gray-100">
                    <table class="w-full text-left text-gray-700">
                        <thead class="bg-gray-50 border-b">
                        <tr>
                            <th class="py-3 px-4 text-center w-16">순위</th>
                            <th class="py-3 px-4">닉네임</th>
                            <th class="py-3 px-4 text-center">등급</th>
                            <th class="py-3 px-4 text-right">포인트</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="m" items="${topMembers}" varStatus="loop">
                            <tr class="border-b hover:bg-orange-50 transition">
                                <td class="py-3 px-4 text-center font-bold text-gray-800">
                                    <c:choose>
                                        <c:when test="${loop.index == 0}">
                                            🥇
                                        </c:when>
                                        <c:when test="${loop.index == 1}">
                                            🥈
                                        </c:when>
                                        <c:when test="${loop.index == 2}">
                                            🥉
                                        </c:when>
                                        <c:otherwise>
                                            ${loop.index + 1}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="py-3 px-4 font-medium text-gray-800">
                                    <c:out value="${m.nickname}"/>
                                </td>
                                <td class="py-3 px-4 text-center">
                                <span class="inline-block bg-orange-100 text-orange-600 px-3 py-1 rounded-full text-sm font-semibold">
                                    <c:out value="${m.grade}"/>
                                </span>
                                </td>
                                <td class="py-3 px-4 text-right font-semibold text-orange-500">
                                    <fmt:formatNumber value="${m.point}" />P
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="text-center mt-8">
                    <button id="showMyRankBtn"
                            class="inline-flex items-center text-orange-500 hover:text-orange-600 font-semibold transition">
                        내 랭킹 보기
                        <i data-lucide="arrow-down" class="w-4 h-4 ml-1"></i>
                    </button>
                </div>

                <!-- ✅ 내 순위 표시 영역 (초기 숨김) -->
                <div id="myRankBox" class="hidden mt-6 text-center text-gray-600 transition-all duration-300">
                    <c:choose>
                        <c:when test="${not empty myRank}">
                            나의 현재 순위는
                            <span class="font-bold text-orange-600">${myRank}위</span>
                            입니다!
                        </c:when>
                        <c:otherwise>
                            로그인 후 내 랭킹을 확인할 수 있습니다.
                        </c:otherwise>
                    </c:choose>
                </div>

            </div>
        </section>



    </main>

    <!-- JavaScript 파일 -->
    <script src="/js/common/common.js"></script>
    <script src="/js/home.js"></script>
    <script>
        // Lucide 아이콘 초기화
        lucide.createIcons();
    </script>

<!-- ===== 챗봇 UI + JS 연결 (푸터 위로 이동) ===== -->
<jsp:include page="/WEB-INF/views/chat/chatbot.jsp" />

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const form = document.getElementById("mainSearchForm");
        const input = document.getElementById("searchKeyword");

        form.addEventListener("submit", function(e) {
            if (!input.value.trim()) {
                alert("검색어를 입력하세요!");
                input.focus();
                e.preventDefault();
            }
        });
    });
</script>

    <jsp:include page="/common/footer.jsp"/>
</body>
</html>