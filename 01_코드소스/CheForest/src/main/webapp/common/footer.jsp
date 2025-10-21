<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="/css/common/common.css">
    <link rel="stylesheet" href="/css/common/footer.css">
    <meta name="_csrf" content="${_csrf.token}">
    <meta name="_csrf_header" content="${_csrf.headerName}">
</head>
<body>
    <!-- CheForest 꼬리말 화면 -->
    <footer class="bg-gradient-to-br from-white via-gray-100/30 to-white border-t border-gray-200 relative overflow-hidden">
        <!-- 배경 장식 -->
        <div class="absolute inset-0 opacity-5">
            <div class="absolute top-10 left-10 w-20 h-20 rounded-full bg-gradient-to-r from-pink-500 to-orange-500"></div>
            <div class="absolute top-40 right-20 w-16 h-16 rounded-full bg-gradient-to-r from-orange-500 to-pink-500"></div>
            <div class="absolute bottom-20 left-1/3 w-12 h-12 rounded-full bg-gradient-to-r from-pink-500 to-orange-500"></div>
        </div>

        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative">
            <!-- 뉴스레터 구독 섹션 -->
            <div class="py-12 border-b border-gray-200">
                <div class="text-center max-w-2xl mx-auto">
                    <div class="flex justify-center mb-4">
                        <div class="bg-gradient-to-r from-pink-500 to-orange-500 p-3 rounded-full">
                            <i data-lucide="mail" class="w-6 h-6 text-white"></i>
                        </div>
                    </div>
                    <h3 class="text-2xl font-medium mb-2 brand-gradient">
                        맛있는 소식을 받아보세요
                    </h3>
                    <p class="text-gray-600 mb-6">
                        매주 새로운 레시피와 요리 팁, 특별 이벤트 정보를 이메일로 받아보세요
                    </p>
                    <div class="flex max-w-md mx-auto">
                        <input 
                            type="email" 
                            placeholder="이메일 주소를 입력하세요"
                            class="newsletter-email flex-1 px-4 py-2 border border-gray-200 rounded-l-lg focus:border-orange-500 focus:ring-orange-500/20 bg-white"
                        />
                        <button class="newsletter-subscribe-btn btn-orange text-white px-6 py-2 rounded-r-lg font-medium">
                            <i data-lucide="send" class="w-4 h-4 mr-2 inline"></i>
                            구독하기
                        </button>
                    </div>
                    <p class="text-xs text-gray-500 mt-2">
                        언제든지 구독을 취소할 수 있습니다. 개인정보는 안전하게 보호됩니다.
                    </p>
                </div>
            </div>

            <!-- 메인 푸터 콘텐츠 -->
            <div class="py-12 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-6 gap-8">
                <!-- 회사 정보 -->
                <div class="lg:col-span-2">
                    <div class="flex items-center space-x-3 mb-6">
                        <div class="relative">
                            <i data-lucide="chef-hat" class="h-10 w-10 text-orange-500"></i>
                            <div class="absolute -top-1 -right-1 w-4 h-4 bg-gradient-to-r from-pink-500 to-orange-500 rounded-full flex items-center justify-center">
                                <i data-lucide="heart" class="w-2 h-2 text-white"></i>
                            </div>
                        </div>
                        <h3 class="text-2xl font-medium brand-gradient">
                            CheForest
                        </h3>
                    </div>
                    
                    <p class="text-gray-600 mb-6 leading-relaxed">
                        전 세계의 맛있는 레시피를 발견하고, 요리 경험을 공유하는 커뮤니티입니다. 
                        집에서도 쉽게 따라할 수 있는 다양한 요리법을 만나보세요.
                    </p>

                    <!-- 통계 -->
                    <div class="grid grid-cols-2 gap-4 mb-6">
                        <div class="text-center p-3 bg-gray-100/50 rounded-lg">
                            <div class="text-xl font-semibold text-orange-500">50K+</div>
                            <div class="text-xs text-gray-500">레시피</div>
                        </div>
                        <div class="text-center p-3 bg-gray-100/50 rounded-lg">
                            <div class="text-xl font-semibold text-pink-500">100K+</div>
                            <div class="text-xs text-gray-500">회원</div>
                        </div>
                    </div>

                    <!-- 소셜 미디어 -->
                    <div>
                        <p class="text-sm font-medium mb-3">팔로우하기</p>
                        <div class="flex space-x-3">
                            <a href="#" class="group bg-gray-100/50 hover:bg-gradient-to-r hover:from-pink-500 hover:to-orange-500 p-2 rounded-lg transition-all duration-300">
                                <i data-lucide="facebook" class="w-5 h-5 text-gray-500 group-hover:text-white transition-colors"></i>
                            </a>
                            <a href="#" class="group bg-gray-100/50 hover:bg-gradient-to-r hover:from-pink-500 hover:to-orange-500 p-2 rounded-lg transition-all duration-300">
                                <i data-lucide="twitter" class="w-5 h-5 text-gray-500 group-hover:text-white transition-colors"></i>
                            </a>
                            <a href="#" class="group bg-gray-100/50 hover:bg-gradient-to-r hover:from-pink-500 hover:to-orange-500 p-2 rounded-lg transition-all duration-300">
                                <i data-lucide="instagram" class="w-5 h-5 text-gray-500 group-hover:text-white transition-colors"></i>
                            </a>
                            <a href="#" class="group bg-gray-100/50 hover:bg-gradient-to-r hover:from-pink-500 hover:to-orange-500 p-2 rounded-lg transition-all duration-300">
                                <i data-lucide="youtube" class="w-5 h-5 text-gray-500 group-hover:text-white transition-colors"></i>
                            </a>
                        </div>
                    </div>
                </div>

                <!-- 카테고리 -->
                <div>
                    <h4 class="font-medium mb-4 flex items-center">
                        <i data-lucide="utensils" class="w-4 h-4 mr-2 text-orange-500"></i>
                        카테고리
                    </h4>
                    <ul class="space-y-3">
                        <c:forEach var="entry" items="${categoryTotals}">
                            <li>
                                <a href="#" class="group flex items-center justify-between text-gray-600 hover:text-orange-500 transition-colors">
                    <span class="group-hover:translate-x-1 transition-transform">
                        <c:out value="${entry.key}" />
                    </span>
                                    <span class="bg-gray-100 text-gray-600 px-2 py-1 rounded text-xs">
                        <fmt:formatNumber value="${entry.value}" type="number" />
                    </span>
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>

                <!-- 서비스 -->
                <div>
                    <h4 class="font-medium mb-4 flex items-center">
                        <i data-lucide="book-open" class="w-4 h-4 mr-2 text-orange-500"></i>
                        서비스
                    </h4>
                    <ul class="space-y-3">
                        <li>
                            <a href="#" class="group flex items-center text-gray-600 hover:text-orange-500 transition-colors">
                                <i data-lucide="globe" class="w-3 h-3 mr-2 group-hover:scale-110 transition-transform"></i>
                                <span class="group-hover:translate-x-1 transition-transform">레시피 검색</span>
                            </a>
                        </li>
                        <li>
                            <a href="#" class="group flex items-center text-gray-600 hover:text-orange-500 transition-colors">
                                <i data-lucide="users" class="w-3 h-3 mr-2 group-hover:scale-110 transition-transform"></i>
                                <span class="group-hover:translate-x-1 transition-transform">커뮤니티</span>
                            </a>
                        </li>
                        <li>
                            <a href="#" class="group flex items-center text-gray-600 hover:text-orange-500 transition-colors">
                                <i data-lucide="star" class="w-3 h-3 mr-2 group-hover:scale-110 transition-transform"></i>
                                <span class="group-hover:translate-x-1 transition-transform">인기 레시피</span>
                            </a>
                        </li>
                        <li>
                            <a href="#" class="group flex items-center text-gray-600 hover:text-orange-500 transition-colors">
                                <i data-lucide="coffee" class="w-3 h-3 mr-2 group-hover:scale-110 transition-transform"></i>
                                <span class="group-hover:translate-x-1 transition-transform">요리 클래스</span>
                            </a>
                        </li>
                        <li>
                            <a href="#" class="group flex items-center text-gray-600 hover:text-orange-500 transition-colors">
                                <i data-lucide="gift" class="w-3 h-3 mr-2 group-hover:scale-110 transition-transform"></i>
                                <span class="group-hover:translate-x-1 transition-transform">이벤트</span>
                            </a>
                        </li>
                    </ul>
                </div>

                <!-- 고객지원 -->
                <div>
                    <h4 class="font-medium mb-4 flex items-center">
                        <i data-lucide="shield" class="w-4 h-4 mr-2 text-orange-500"></i>
                        고객지원
                    </h4>
                    <ul class="space-y-3">
                        <li>
                            <a href="#" class="group flex items-center text-gray-600 hover:text-orange-500 transition-colors">
                                <i data-lucide="phone" class="w-3 h-3 mr-2"></i>
                                <span class="group-hover:translate-x-1 transition-transform">고객센터</span>
                            </a>
                        </li>
                        <li>
                            <a href="#" class="group flex items-center text-gray-600 hover:text-orange-500 transition-colors">
                                <i data-lucide="mail" class="w-3 h-3 mr-2"></i>
                                <span class="group-hover:translate-x-1 transition-transform">이메일 문의</span>
                            </a>
                        </li>
                        <li>
                            <a href="#" class="group flex items-center text-gray-600 hover:text-orange-500 transition-colors">
                                <i data-lucide="clock" class="w-3 h-3 mr-2"></i>
                                <span class="group-hover:translate-x-1 transition-transform">FAQ</span>
                            </a>
                        </li>
                        <li>
                            <a href="#" class="group flex items-center text-gray-600 hover:text-orange-500 transition-colors">
                                <i data-lucide="award" class="w-3 h-3 mr-2"></i>
                                <span class="group-hover:translate-x-1 transition-transform">품질보증</span>
                            </a>
                        </li>
                    </ul>

                    <!-- 운영시간 -->
                    <div class="mt-6 p-3 bg-gray-100/30 rounded-lg">
                        <div class="flex items-center text-sm text-gray-600 mb-1">
                            <i data-lucide="clock" class="w-3 h-3 mr-2"></i>
                            운영시간
                        </div>
                        <div class="text-xs text-gray-500">
                            평일 09:00 - 18:00<br>
                            주말 10:00 - 17:00
                        </div>
                    </div>
                </div>

                <!-- 모바일 앱 -->
                <div>
                    <h4 class="font-medium mb-4 flex items-center">
                        <i data-lucide="smartphone" class="w-4 h-4 mr-2 text-orange-500"></i>
                        모바일 앱
                    </h4>
                    
                    <p class="text-gray-600 text-sm mb-4">
                        언제 어디서나 CheForest와 함께하세요
                    </p>

                    <div class="space-y-3">
                        <a href="#" class="group flex items-center justify-between p-3 bg-gray-100/30 hover:bg-gradient-to-r hover:from-pink-500/10 hover:to-orange-500/10 rounded-lg transition-all">
                            <div class="flex items-center">
                                <div class="w-8 h-8 bg-black rounded-lg flex items-center justify-center mr-3">
                                    <i data-lucide="download" class="w-4 h-4 text-white"></i>
                                </div>
                                <div>
                                    <div class="text-sm font-medium">App Store</div>
                                    <div class="text-xs text-gray-500">iOS 다운로드</div>
                                </div>
                            </div>
                            <i data-lucide="arrow-right" class="w-4 h-4 text-gray-500 group-hover:text-orange-500 group-hover:translate-x-1 transition-all"></i>
                        </a>

                        <a href="#" class="group flex items-center justify-between p-3 bg-gray-100/30 hover:bg-gradient-to-r hover:from-pink-500/10 hover:to-orange-500/10 rounded-lg transition-all">
                            <div class="flex items-center">
                                <div class="w-8 h-8 bg-green-500 rounded-lg flex items-center justify-center mr-3">
                                    <i data-lucide="download" class="w-4 h-4 text-white"></i>
                                </div>
                                <div>
                                    <div class="text-sm font-medium">Google Play</div>
                                    <div class="text-xs text-gray-500">Android 다운로드</div>
                                </div>
                            </div>
                            <i data-lucide="arrow-right" class="w-4 h-4 text-gray-500 group-hover:text-orange-500 group-hover:translate-x-1 transition-all"></i>
                        </a>
                    </div>

                    <!-- QR 코드 -->
                    <div class="mt-4 p-3 bg-gray-100/30 rounded-lg text-center">
                        <div class="w-16 h-16 bg-gradient-to-r from-pink-500 to-orange-500 rounded-lg mx-auto mb-2 flex items-center justify-center">
                            <div class="w-12 h-12 bg-white rounded grid grid-cols-3 gap-1 p-1">
                                <div class="rounded-sm bg-gray-800"></div>
                                <div class="rounded-sm bg-gray-200"></div>
                                <div class="rounded-sm bg-gray-800"></div>
                                <div class="rounded-sm bg-gray-200"></div>
                                <div class="rounded-sm bg-gray-800"></div>
                                <div class="rounded-sm bg-gray-200"></div>
                                <div class="rounded-sm bg-gray-800"></div>
                                <div class="rounded-sm bg-gray-200"></div>
                                <div class="rounded-sm bg-gray-800"></div>
                            </div>
                        </div>
                        <div class="text-xs text-gray-500">QR코드로 빠른 다운로드</div>
                    </div>
                </div>
            </div>

            <!-- 하단 저작권 -->
            <div class="py-6 border-t border-gray-200">
                <div class="flex flex-col lg:flex-row justify-between items-center space-y-4 lg:space-y-0">
                    <div class="flex flex-col md:flex-row items-center space-y-2 md:space-y-0 md:space-x-4">
                        <p class="text-gray-600 text-sm">
                            © 2025 CheForest. All rights reserved.
                        </p>
                        <div class="flex items-center space-x-2 text-xs text-gray-500">
                            <span>Made with</span>
                            <i data-lucide="heart" class="w-3 h-3 text-red-500"></i>
                            <span>in Seoul, Korea</span>
                        </div>
                    </div>
                    
                    <div class="flex flex-wrap justify-center lg:justify-end gap-4 text-sm">
                        <a href="#privacy" class="text-gray-600 hover:text-orange-500 transition-colors">
                            개인정보처리방침
                        </a>
                        <a href="#terms" class="text-gray-600 hover:text-orange-500 transition-colors">
                            이용약관
                        </a>
                        <a href="#cookies" class="text-gray-600 hover:text-orange-500 transition-colors">
                            쿠키정책
                        </a>
                        <a href="#help" class="text-gray-600 hover:text-orange-500 transition-colors">
                            고객지원
                        </a>
                        <a href="#sitemap" class="text-gray-600 hover:text-orange-500 transition-colors">
                            사이트맵
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </footer>

    <!-- Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
    <script src="/js/common/common.js"></script>
    <script src="/js/common/footer.js"></script>
</body>
</html>