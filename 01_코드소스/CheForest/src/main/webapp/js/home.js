/* CheForest Home Page JavaScript */
/* JSP용 홈페이지 JavaScript - 구조와 이벤트 핸들러만 */

// 홈페이지 관련 전역 변수
let currentSlide = 0;
let slideInterval;
let activeCategory = 'korean';
let activeTab = 'recipes';

// === 배너 슬라이더 기능 ===

// 슬라이드 이동 (JSP에서 슬라이드 데이터를 가져와서 처리)
function goToSlide(slideIndex) {
    const slides = document.querySelectorAll('.slide');
    const dots = document.querySelectorAll('.slide-dot');

    if (slides.length === 0) return;

    // 현재 슬라이드 숨김
    slides[currentSlide].style.opacity = '0';
    dots[currentSlide].classList.remove('scale-125', 'bg-white');
    dots[currentSlide].classList.add('bg-white/50');

    // 새 슬라이드 표시
    currentSlide = slideIndex;
    slides[currentSlide].style.opacity = '1';
    dots[currentSlide].classList.remove('bg-white/50');
    dots[currentSlide].classList.add('scale-125', 'bg-white');

    // 자동 슬라이드 타이머 재시작
    resetSlideTimer();
}

// 다음 슬라이드
function nextSlide() {
    const totalSlides = document.querySelectorAll('.slide').length;
    const nextIndex = (currentSlide + 1) % totalSlides;
    goToSlide(nextIndex);
}

// 이전 슬라이드
function previousSlide() {
    const totalSlides = document.querySelectorAll('.slide').length;
    const prevIndex = currentSlide === 0 ? totalSlides - 1 : currentSlide - 1;
    goToSlide(prevIndex);
}

// 슬라이드 타이머 재시작
function resetSlideTimer() {
    clearInterval(slideInterval);
    slideInterval = setInterval(nextSlide, 5000);
}

// === 카테고리 네비게이션 기능 ===

// 카테고리 전환 (JSP에서 AJAX로 카테고리별 데이터 로드)
function switchCategory(category) {
    activeCategory = category;

    // 1) 버튼 상태 업데이트 (onclick 호출/이벤트 양쪽 지원)
    document.querySelectorAll('.category-btn').forEach(btn => {
        btn.classList.remove('active');
        btn.classList.remove('bg-gradient-to-r','from-pink-500','to-orange-500','text-white','shadow-2xl','shadow-pink-500/30','border-transparent','transform','scale-105');
        btn.classList.add('bg-white','text-gray-700','shadow-lg','shadow-gray-200/50','border-gray-100');
    });
    const btn = document.querySelector(`.category-btn[data-category="${category}"]`) || document.querySelector(`button[onclick*="${category}"]`);
    if (btn) {
        btn.classList.add('active');
        btn.classList.remove('bg-white','text-gray-700','shadow-lg','shadow-gray-200/50','border-gray-100');
        btn.classList.add('bg-gradient-to-r','from-pink-500','to-orange-500','text-white','shadow-2xl','shadow-pink-500/30','border-transparent','transform','scale-105');
    }

    // 2) 모든 카테고리 pane 숨기고, 선택된 것만 보이기
    document.querySelectorAll('#recipesList .category-pane').forEach(p => p.classList.add('hidden'));
    document.querySelectorAll('#communityTab .category-pane').forEach(p => p.classList.add('hidden'));
    const rPane = document.getElementById(`recipes-${category}`);
    const cPane = document.getElementById(`community-${category}`);
    if (rPane) rPane.classList.remove('hidden');
    if (cPane) cPane.classList.remove('hidden');

    // 3) 제목 텍스트 갱신
    const names = { korean: '한식', western: '양식', chinese: '중식', japanese: '일식', dessert: '디저트' };
    const categoryTitle   = document.getElementById('categoryTitle');
    const communityTitle  = document.getElementById('communityTitle');
    if (categoryTitle && names[category])  categoryTitle.textContent  = `${names[category]} CheForest 레시피`;
    if (communityTitle && names[category]) communityTitle.textContent = `${names[category]} 사용자 레시피`;
}


// 탭 전환
function switchTab(tab) {
    activeTab = tab;

    // 탭 버튼 업데이트
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
        btn.classList.remove('bg-white', 'shadow-sm', 'text-gray-900');
        btn.classList.add('text-gray-600');
    });

    const clickedTab = event.target;
    clickedTab.classList.add('active');
    clickedTab.classList.add('bg-white', 'shadow-sm', 'text-gray-900');
    clickedTab.classList.remove('text-gray-600');

    // 탭 콘텐츠 전환
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.add('hidden');
    });
    document.getElementById(tab + 'Tab').classList.remove('hidden');
}

// 카테고리 콘텐츠 업데이트 (JSP에서 구현할 함수)
function updateCategoryContent(category) {
    // JSP에서 AJAX를 통해 카테고리별 레시피 데이터를 가져와서 업데이트
    // 또는 form submit으로 페이지 새로고침
    console.log('카테고리 변경:', category);

    // 제목 업데이트는 클라이언트에서 처리 가능
    const categoryNames = {
        'korean': '한식',
        'western': '양식',
        'chinese': '중식',
        'japanese': '일식',
        'dessert': '디저트'
    };

    const categoryTitle = document.getElementById('categoryTitle');
    const communityTitle = document.getElementById('communityTitle');

    if (categoryTitle && categoryNames[category]) {
        categoryTitle.textContent = `${categoryNames[category]} CheForest 레시피`;
    }
    if (communityTitle && categoryNames[category]) {
        communityTitle.textContent = `${categoryNames[category]} 사용자 레시피`;
    }
}

// === 이벤트 핸들러 설정 ===

// 배너 슬라이더 초기화
function initializeBannerSlider() {
    // 슬라이드 버튼 이벤트 핸들러
    const prevBtn = document.getElementById('prevSlideBtn');
    const nextBtn = document.getElementById('nextSlideBtn');

    if (prevBtn) prevBtn.addEventListener('click', previousSlide);
    if (nextBtn) nextBtn.addEventListener('click', nextSlide);

    // 도트 인디케이터 이벤트 핸들러
    const dots = document.querySelectorAll('.slide-dot');
    dots.forEach((dot, index) => {
        dot.addEventListener('click', () => goToSlide(index));
    });

    // 자동 슬라이드 시작
    const totalSlides = document.querySelectorAll('.slide').length;
    if (totalSlides > 1) {
        slideInterval = setInterval(nextSlide, 5000);

        // 마우스 오버 시 자동 슬라이드 정지
        const bannerSlider = document.getElementById('bannerSlider');
        if (bannerSlider) {
            bannerSlider.addEventListener('mouseenter', () => {
                clearInterval(slideInterval);
            });

            bannerSlider.addEventListener('mouseleave', () => {
                slideInterval = setInterval(nextSlide, 5000);
            });
        }
    }
}

// 카테고리 버튼 이벤트 핸들러 설정
function initializeCategoryButtons() {
    const categoryBtns = document.querySelectorAll('.category-btn');
    categoryBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const category = this.getAttribute('data-category');
            if (category) {
                switchCategory(category);
            }
        });
    });
}

// 탭 버튼 이벤트 핸들러 설정
function initializeTabButtons() {
    const recipesTabBtn = document.getElementById('recipesTabBtn');
    const communityTabBtn = document.getElementById('communityTabBtn');

    if (recipesTabBtn) {
        recipesTabBtn.addEventListener('click', function() {
            switchTab('recipes');
        });
    }

    if (communityTabBtn) {
        communityTabBtn.addEventListener('click', function() {
            switchTab('community');
        });
    }
}

// 기타 버튼 이벤트 핸들러 설정
function initializeButtons() {
    const moreRecipesBtn = document.getElementById('moreRecipesBtn');
    const moreEventsBtn = document.getElementById('moreEventsBtn');

    if (moreRecipesBtn) {
        moreRecipesBtn.addEventListener('click', function() {
            // JSP에서 레시피 페이지로 이동 처리
            location.href = '/recipe/list';
        });
    }

    const categoryMap = {
        korean: "한식",
        western: "양식",
        chinese: "중식",
        japanese: "일식",
        dessert: "디저트"
    };

    // 전체보기 버튼 클릭 이벤트
    document.querySelectorAll('.view-all-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const type = this.dataset.type;          // cheforest or user
            const categoryEng = activeCategory;      // 현재 선택된 카테고리(영문)
            const categoryKor = categoryMap[categoryEng]; // 한글 변환
            let url = '';

            // 카테고리별 페이지로 이동
            switch (type) {
                case 'cheforest':
                    // ✅ page, size 등은 넣지 않고 categoryKr만 깔끔히 전달
                    url = `/recipe/list?categoryKr=${encodeURIComponent(categoryKor)}`;
                    break;
                case 'user':
                    url = `/board/list?category=${encodeURIComponent(categoryKor)}`;
                    break;
            }

            location.href = url;
        });
    });
}

// === 홈페이지 초기화 ===

function initializeHome() {
    // 배너 슬라이더 초기화
    initializeBannerSlider();

    // 카테고리 버튼 초기화
    initializeCategoryButtons();

    // 탭 버튼 초기화
    initializeTabButtons();

    // 기타 버튼 초기화
    initializeButtons();

    // 기본 카테고리 콘텐츠 로드
    updateCategoryContent(activeCategory);
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeHome();
});

// 페이지 언로드 시 타이머 정리
window.addEventListener('beforeunload', function() {
    if (slideInterval) {
        clearInterval(slideInterval);
    }
});

// 홈페이지 함수들을 전역으로 노출 (JSP에서 필요시 호출)
window.CheForest = window.CheForest || {};
window.CheForest.home = {
    goToSlide,
    nextSlide,
    previousSlide,
    switchCategory,
    switchTab,
    updateCategoryContent,
    initializeHome
};

// 내 랭킹 보기 버튼 토글
document.addEventListener("DOMContentLoaded", function() {
    const showMyRankBtn = document.getElementById("showMyRankBtn");
    const myRankBox = document.getElementById("myRankBox");

    if (showMyRankBtn && myRankBox) {
        showMyRankBtn.addEventListener("click", () => {
            myRankBox.classList.toggle("hidden");

            // 아이콘 회전 효과
            const icon = showMyRankBtn.querySelector("i");
            if (icon) {
                icon.classList.toggle("rotate-180");
                icon.classList.add("transition-transform", "duration-300");
            }
        });
    }
});