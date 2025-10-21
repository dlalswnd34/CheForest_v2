<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CheForest - 요리 레시피 공유 사이트</title>
    <link rel="stylesheet" href="/css/common/common.css">
    <link rel="stylesheet" href="/css/home.css">

    <!-- ✅ CSRF Meta -->
    <sec:csrfMetaTags/>
</head>
<body>
<!-- CheForest Header -->
<header class="bg-white border-b border-gray-200 sticky top-0 z-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- 상단: 로고, 검색창, 사용자 메뉴 -->
        <div class="flex items-center justify-between h-16">
            <!-- 로고 -->
            <div class="logo-container flex items-center space-x-4 cursor-pointer" onclick="showPage('home')">
                <div class="relative">
                    <img
                            src="/images/cheforesticon.png"   <%-- static/images/cheforesticon.png --%>
                            alt="CheForest 로고"
                            class="logo-hat h-12 w-12"
                            loading="eager"
                            decoding="async"
                    />
                </div>
                <h1 class="text-3xl font-black tracking-tight brand-gradient">CheForest</h1>
            </div>

            <!-- 중앙 검색바 -->
            <div class="hidden md:flex flex-1 justify-center px-8">
                <div class="relative w-full max-w-lg">
                    <div class="relative">
                        <i data-lucide="search" class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4"></i>
                        <input
                                type="text"
                                id="searchInput"
                                placeholder="통합검색..."
                                class="search-input pl-10 pr-12 py-2 w-full border-2 border-gray-200 rounded-full bg-gray-50 cursor-pointer"
                        />
                        <button
                                id="searchBtn"
                                class="search-btn absolute right-1 top-1/2 transform -translate-y-1/2 h-7 px-3 text-white rounded-full text-sm"
                                onclick="totalsearch()"
                        >
                            <i data-lucide="search" class="h-3 w-3"></i>
                        </button>
                    </div>
                </div>
            </div>

            <!-- 우측 사용자 메뉴 -->
            <div class="flex items-center space-x-2">
                <!-- 관리자 모드: 관리자만 노출 -->
                <sec:authorize access="hasAuthority('ADMIN')">
                    <a href="<c:url value='/admin'/>" class="hidden sm:flex header-icon-btn p-2 rounded-lg" title="관리자 모드">
                        <i data-lucide="shield" class="h-6 w-6"></i>
                    </a>
                </sec:authorize>

                <!-- 등급 안내 -->
                <button class="header-icon-btn p-2 rounded-lg" onclick="showPage('grade')" title="등급 안내">
                    <i data-lucide="award" class="h-6 w-6"></i>
                </button>

                <!-- 마이페이지 -->
                <a href="<c:url value='/mypage'/>" class="hidden sm:flex header-icon-btn p-2 rounded-lg" title="마이페이지">
                    <i data-lucide="user" class="h-6 w-6"></i>
                </a>

                <!-- ✅ 로그인 상태: 환영문 + 로그아웃 버튼 -->
                <sec:authorize access="isAuthenticated()">
                    <span id="header-nickname" class="hidden md:inline text-sm text-gray-600">
                        <sec:authentication property="principal.member.nickname" />
                    </span>
                    <span class="hidden md:inline text-sm text-gray-600">님 환영합니다!</span>
                    <form action="<c:url value='/auth/logout'/>" method="post" class="hidden sm:inline-block">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button type="submit" class="login-btn text-white px-4 py-2 rounded-lg font-medium">
                            로그아웃
                        </button>
                    </form>
                </sec:authorize>

                <!-- ✅ 비로그인 상태: 로그인 버튼 -->
                <sec:authorize access="isAnonymous()">
                    <a href="<c:url value='/auth/login'/>" class="hidden sm:flex login-btn text-white px-4 py-2 rounded-lg font-medium">
                        로그인
                    </a>
                </sec:authorize>

                <!-- 모바일 토글 버튼 -->
                <button class="sm:hidden mobile-menu-btn p-2 rounded-lg" onclick="toggleMobileMenu()">
                    <i data-lucide="menu" class="h-5 w-5"></i>
                </button>
            </div>
        </div>

        <!-- 하단: 메인 네비게이션 -->
        <nav class="hidden sm:flex items-center justify-center space-x-8 py-3">
            <button
                    onclick="showPage('home')"
                    class="nav-item relative font-medium transition-colors text-gray-700 hover:text-orange-500"
                    data-page="home"
            >
                홈
                <span class="nav-underline"></span>
            </button>

            <button
                    onclick="showPage('recipes')"
                    class="nav-item relative font-medium transition-colors text-gray-700 hover:text-orange-500"
                    data-page="recipes"
            >
                <div class="text-center">
                    <div class="text-sm leading-tight">CheForest</div>
                    <div class="text-sm leading-tight">레시피</div>
                </div>
                <span class="nav-underline"></span>
            </button>

            <button
                    onclick="showPage('board')"
                    class="nav-item relative font-medium transition-colors text-gray-700 hover:text-orange-500"
                    data-page="board"
            >
                <div class="text-center">
                    <div class="text-sm leading-tight">사용자</div>
                    <div class="text-sm leading-tight">레시피</div>
                </div>
                <span class="nav-underline"></span>
            </button>

                <button
                        onclick="showPage('season')"
                        class="nav-item relative flex items-center space-x-1 font-medium transition-colors text-gray-700 hover:text-orange-500"
                        data-page="ingredients"
                >
                    <span>제철 식재료</span>
                    <span class="nav-underline"></span>
                </button>

            <div class="relative group">
                <button
                        class="nav-item relative font-medium transition-colors text-gray-700 hover:text-orange-500"
                        data-page="events"
                >
                    SUPPORT
                    <span class="nav-underline"></span>
                </button>

                <!-- 드롭다운 메뉴 -->
                <div
                        id="ingredientsDropdown"
                        class="absolute top-full left-0 mt-2 w-56 bg-white border border-gray-200 rounded-lg shadow-xl
               opacity-0 invisible group-hover:opacity-100 group-hover:visible group-hover:translate-y-0
               transform -translate-y-2 transition-all duration-200 z-50"
                >
                    <div class="py-2">
                        <button
                                onclick="showPage('guide')"
                                class="w-full text-left px-6 py-3 text-base text-gray-700 hover:bg-orange-50 hover:text-orange-500 transition-colors font-medium"
                        >
                            사이트 이용 가이드
                        </button>
                        <button
                                onclick="showPage('test')"
                                class="w-full text-left px-6 py-3 text-base text-gray-700 hover:bg-orange-50 hover:text-orange-500 transition-colors font-medium"
                        >
                            나의 요리 취향 찾기
                        </button>
                        <button
                                onclick="showPage('dust')"
                                class="w-full text-left px-6 py-3 text-base text-gray-700 hover:bg-orange-50 hover:text-orange-500 transition-colors font-medium"
                        >
                            전국 날씨별 레시피 추천
                        </button>
                    </div>
                </div>
            </div>

            <button
                    onclick="showPage('qna')"
                    class="nav-item relative font-medium transition-colors text-gray-700 hover:text-orange-500"
                    data-page="qna"
            >
                Q&A
                <span class="nav-underline"></span>
            </button>
        </nav>

        <!-- 모바일 네비게이션 -->
        <nav id="mobileMenu" class="hidden sm:hidden py-3 border-t border-gray-200">
            <div class="flex flex-col space-y-2">
                <a href="<c:url value='/'/>" class="w-full text-left font-medium py-2 px-3 rounded hover:bg-gray-50 text-gray-700 hover:text-orange-500 transition-colors">홈</a>
                <a href="<c:url value='/recipe/list'/>" class="w-full text-left font-medium py-2 px-3 rounded hover:bg-gray-50 text-gray-700 hover:text-orange-500 transition-colors">
                    <div><div>CheForest</div><div>레시피</div></div>
                </a>
                <a href="<c:url value='/board/list'/>" class="w-full text-left font-medium py-2 px-3 rounded hover:bg-gray-50 text-gray-700 hover:text-orange-500 transition-colors">
                    <div><div>사용자</div><div>레시피</div></div>
                </a>
                <a href="<c:url value='/season'/>" class="w-full text-left font-medium py-2 px-3 rounded hover:bg-gray-50 text-gray-700 hover:text-orange-500 transition-colors">계절 식재료</a>
                <a href="<c:url value='/grade'/>" class="w-full text-left font-medium py-2 px-3 rounded hover:bg-gray-50 text-gray-700 hover:text-orange-500 transition-colors">등급 안내</a>
                <!-- ✅ Support (모바일 아코디언) -->
                <button type="button"
                        class="w-full flex justify-between items-center text-left font-medium py-2 px-3 rounded hover:bg-gray-50 text-gray-700 hover:text-orange-500 transition-colors"
                        onclick="toggleSubmenu('supportSubmenu')">
                    Support
                    <svg id="supportArrow" class="w-4 h-4 transition-transform duration-200"
                         xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                         stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M19 9l-7 7-7-7"/>
                    </svg>
                </button>

                <!-- ✅ Support 하위 탭 -->
                <div id="supportSubmenu" class="hidden pl-6">
                    <a href="<c:url value='/support/guide'/>"
                       class="block py-1 px-3 text-sm text-gray-600 hover:text-orange-500 transition-colors">
                        📘 사이트 이용 가이드
                    </a>
                    <a href="<c:url value='/support/test'/>"
                       class="block py-1 px-3 text-sm text-gray-600 hover:text-orange-500 transition-colors">
                        🍳 나의 요리 취향 찾기
                    </a>
                    <a href="<c:url value='/dustmap'/>"
                       class="block py-1 px-3 text-sm text-gray-600 hover:text-orange-500 transition-colors">
                        🌦️ 전국 날씨별 레시피 추천
                    </a>
                </div>
                <a href="<c:url value='/qna'/>" class="w-full text-left font-medium py-2 px-3 rounded hover:bg-gray-50 text-gray-700 hover:text-orange-500 transition-colors">Q&A</a>

                <!-- 관리자 모드: 관리자만 -->
                <sec:authorize access="hasAuthority('ADMIN')">
                    <a href="<c:url value='/admin'/>" class="w-full text-left font-medium py-2 px-3 rounded hover:bg-gray-50 text-gray-700 hover:text-orange-500 transition-colors">🛡️ 관리자 모드</a>
                </sec:authorize>

                <a href="<c:url value='/mypage'/>" class="w-full text-left font-medium py-2 px-3 rounded hover:bg-gray-50 text-gray-700 hover:text-orange-500 transition-colors">👤 마이페이지</a>

                <!-- 로그인 상태 -->
                <sec:authorize access="isAuthenticated()">
                  <span class="px-3 text-sm text-gray-600">
                    <sec:authentication property="principal.member.nickname" />님 환영합니다!
                  </span>
                    <div class="pt-4 border-t border-gray-200 mt-2">
                        <form action="<c:url value='/auth/logout'/>" method="post">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button type="submit" class="w-full login-btn text-white py-2 px-4 rounded-lg font-medium">로그아웃</button>
                        </form>
                    </div>
                </sec:authorize>

                <!-- 비로그인 상태 -->
                <sec:authorize access="isAnonymous()">
                    <div class="pt-4 border-t border-gray-200 mt-2">
                        <a href="<c:url value='/auth/login'/>" class="w-full inline-block text-center login-btn text-white py-2 px-4 rounded-lg font-medium">로그인</a>
                    </div>
                </sec:authorize>
            </div>
        </nav>
    </div>
    <!-- 닉네임 변경 모달 -->
    <div id="nicknameModal" class="fixed inset-0 bg-black bg-opacity-50 hidden items-center justify-center z-50">
        <div class="bg-white rounded-lg shadow-xl w-full max-w-md p-6">
            <h2 class="text-lg font-bold mb-4">닉네임 변경</h2>
            <p class="text-sm text-gray-600 mb-4">
                닉네임이 자동으로 변경되었습니다.<br/><br/>
                <span class="block">
               🔹 원래 닉네임:
               <strong class="text-gray-700">
                   <c:out value="${sessionScope.originalNickname}" default="(없음)"/>
               </strong>
            </span>
                <span class="block mt-1">
               🔹 자동 생성된 닉네임:
               <strong class="text-orange-500">
                   <c:out value="${pageContext.request.userPrincipal != null
                       ? pageContext.request.userPrincipal.principal.member.nickname : ''}" default="(없음)"/>
               </strong>
            </span>
                <br/>
                새로운 닉네임을 입력해주세요.
            </p>

            <!-- ✅ Form 방식 -->
            <form action="<c:url value='/auth/nickname/update'/>" method="post" class="space-y-3">
                <input type="text" name="nickname" id="newNickname"
                       class="w-full border border-gray-300 rounded-lg px-3 py-2"
                       placeholder="새 닉네임 입력" required />

                <!-- CSRF -->
                <sec:csrfInput/>

                <div class="flex justify-end space-x-2">
                    <button type="button" onclick="closeNicknameModal()" class="px-4 py-2 bg-gray-200 rounded-lg">취소</button>
                    <button type="submit" class="px-4 py-2 bg-orange-500 text-white rounded-lg">변경하기</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ✅ 조건부 모달 실행 -->
    <sec:authorize access="isAuthenticated()">
        <c:if test="${pageContext.request.userPrincipal.principal.member.nickname ne null
                 and fn:contains(pageContext.request.userPrincipal.principal.member.nickname, '_')}">
            <script>
                window.addEventListener("DOMContentLoaded", function () {
                    document.getElementById("nicknameModal").classList.remove("hidden");
                    document.getElementById("nicknameModal").classList.add("flex");
                });
            </script>
        </c:if>
    </sec:authorize>
</header>

    <!-- Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
    <script src="/js/common/common.js"></script>
    <script src="/js/common/header.js"></script>

<script>
    lucide.createIcons();

    function closeNicknameModal() {
        document.getElementById("nicknameModal").classList.add("hidden");
    }

    document.querySelector("#nicknameModal form")?.addEventListener("submit", async function (e) {
        e.preventDefault(); // 기본 제출 막음

        const newNickname = document.getElementById("newNickname").value.trim();
        if (!newNickname) {
            alert("닉네임을 입력해주세요.");
            return;
        }

        try {
            // 닉네임 중복 체크
            const available = await ajaxRequest("/auth/check-nickname", "GET", { nickname: newNickname });
            if (!available) {
                alert("이미 사용중인 닉네임입니다. 다른 닉네임을 입력해주세요.");
                return;
            }

            // 중복 없으면 원래 form 제출 실행
            e.target.submit();

        } catch (err) {
            console.error(err);
            alert("서버 오류가 발생했습니다.");
        }
    });
</script>

<script>
    function toggleSubmenu(id) {
        const submenu = document.getElementById(id);
        const arrow = document.getElementById("supportArrow");

        const isHidden = submenu.classList.contains("hidden");
        submenu.classList.toggle("hidden");

        if (isHidden) {
            arrow.classList.add("rotate-180");
        } else {
            arrow.classList.remove("rotate-180");
        }
    }
</script>

</body>
</html>