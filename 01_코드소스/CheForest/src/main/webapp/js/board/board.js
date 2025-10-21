/* CheForest Board Page JavaScript */
/* 사용자 레시피 게시판 페이지 전용 JavaScript (HTML 기반 필터링) */

// 게시판 페이지 관련 전역 변수
let boardSelectedCategory = 'all';
let boardSearchQuery = '';
let boardSortBy = 'newest';

(function syncCategoryFromURL() {
    const c = new URLSearchParams(location.search).get('category');
    if (c && c.trim()) boardSelectedCategory = decodeURIComponent(c);
})();

// 카테고리 데이터
const boardCategories = [
    { id: 'all', name: '전체', icon: '🍽️', color: 'bg-gray-100 text-gray-800' },
    { id: '한식', name: '한식', icon: '🥢', color: 'korean' },
    { id: '양식', name: '양식', icon: '🍝', color: 'western' },
    { id: '중식', name: '중식', icon: '🥟', color: 'chinese' },
    { id: '일식', name: '일식', icon: '🍣', color: 'japanese' },
    { id: '디저트', name: '디저트', icon: '🧁', color: 'dessert' }
];

// === HTML 카드 기반 필터링 함수들 ===

// HTML에서 레시피 카드 요소들 가져오기
function getBoardRecipeCards() {
    const popularCards = Array.from(document.querySelectorAll('#boardPopularGrid .board-popular-card'));
    const regularCards = Array.from(document.querySelectorAll('#boardRegularGrid .board-recipe-card'));

    return {
        popular: popularCards,
        regular: regularCards,
        all: [...popularCards, ...regularCards]
    };
}

// 카드 필터링 함수
function shouldShowCard(card) {
    const category = card.dataset.category;
    const title = card.dataset.title?.toLowerCase() || '';
    const description = card.dataset.description?.toLowerCase() || '';

    // 카테고리 필터
    const matchesCategory = boardSelectedCategory === 'all' || category === boardSelectedCategory;

    // 검색 필터
    const searchLower = boardSearchQuery.toLowerCase();
    const matchesSearch = !boardSearchQuery ||
                         title.includes(searchLower) ||
                         description.includes(searchLower);

    return matchesCategory && matchesSearch;
}

// 카드 정렬 함수 (일반 카드만)
function sortBoardCards(cards) {
    return cards.sort((a, b) => {
        const aLikes = parseInt(a.dataset.likes) || 0;
        const bLikes = parseInt(b.dataset.likes) || 0;
        const aRating = parseFloat(a.dataset.rating) || 0;
        const bRating = parseFloat(b.dataset.rating) || 0;
        const aViews = parseInt(a.dataset.views) || 0;
        const bViews = parseInt(b.dataset.views) || 0;
        const aCreated = new Date(a.dataset.created).getTime();
        const bCreated = new Date(b.dataset.created).getTime();

        switch (boardSortBy) {
            case 'popularity':
                return bLikes - aLikes;
            case 'rating':
                return bRating - aRating;
            case 'newest':
                return bCreated - aCreated;
            case 'views':
                return bViews - aViews;
            default:
                return 0;
        }
    });
}

// === UI 업데이트 함수들 ===

// 카테고리 버튼 렌더링
// function renderBoardCategories() {
//     const categoryButtons = document.getElementById('boardCategoryButtons');
//     if (!categoryButtons) return;

// 각 카테고리별 총 카드 개수 계산
function renderBoardCategories(categoryCounts = {}) {   // ← 기본값 {} 추가
    const categoryButtons = document.getElementById('boardCategoryButtons');
    if (!categoryButtons) return;

    categoryButtons.innerHTML = boardCategories.map(category => `
    <button
        class="board-category-button w-full flex items-center space-x-2 px-4 py-2 rounded-lg border transition-all ${
        boardSelectedCategory === category.id
            ? 'active bg-gradient-to-r from-pink-500 to-orange-500 text-white border-transparent shadow-lg'
            : 'bg-white border-gray-200 hover:border-orange-300 hover:bg-orange-50 text-gray-700'
    }"
        onclick="switchBoardCategory('${category.id}')"
    >
        <span class="text-base">${category.icon}</span>
        <span>${category.name}</span>
        <span class="board-category-count text-xs px-2 py-0.5 rounded-full ${
        boardSelectedCategory === category.id
            ? 'bg-white/20 text-white'
            : 'bg-gray-100 text-gray-700'
    }">
            ${categoryCounts[category.id] || 0}
        </span>
    </button>
    `).join('');
}



// 카테고리 셀렉트 옵션 렌더링
function renderBoardCategorySelect() {
    const categorySelect = document.getElementById('boardCategorySelect');
    if (!categorySelect) return;

    categorySelect.innerHTML = boardCategories.map(category => `
        <option value="${category.id}" ${boardSelectedCategory === category.id ? 'selected' : ''}>
            ${category.icon} ${category.name}
        </option>
    `).join('');
}

// 인기 게시글 필터링 및 렌더링
function renderBoardPopularRecipes() {
    const cards = getBoardRecipeCards();
    const popularSection = document.getElementById('boardPopularSection');
    const popularCount = document.getElementById('boardPopularCount');

    let visibleCount = 0;

    cards.popular.forEach(card => {
        if (shouldShowCard(card)) {
            card.style.display = 'block';
            visibleCount++;
        } else {
            card.style.display = 'none';
        }
    });

    if (visibleCount > 0) {
        popularSection.style.display = 'block';
        popularCount.textContent = `TOP ${visibleCount}`;
    } else {
        popularSection.style.display = 'none';
    }
}

// 일반 게시글 필터링, 정렬 및 렌더링
function renderBoardRegularRecipes() {
    const cards = getBoardRecipeCards();
    const regularCount = document.getElementById('boardRegularCount');

    // 필터링
    const visibleCards = cards.regular.filter(card => shouldShowCard(card));

    // 정렬
    const sortedCards = sortBoardCards([...visibleCards]);

    // 모든 카드 숨기기
    cards.regular.forEach(card => {
        card.style.display = 'none';
    });

    // 정렬된 순서대로 표시
    const regularGrid = document.getElementById('boardRegularGrid');
    if (regularGrid && sortedCards.length > 0) {
        // 정렬된 순서대로 DOM에서 재배치
        sortedCards.forEach(card => {
            card.style.display = 'block';
            regularGrid.appendChild(card); // 순서 재배치
        });
    }

    regularCount.textContent = `${visibleCards.length}개`;
}

// 결과 없음 섹션 표시/숨김
function toggleBoardNoResultsSection() {
    const cards = getBoardRecipeCards();
    const noResultsSection = document.getElementById('boardNoResultsSection');
    const loadMoreSection = document.getElementById('boardLoadMoreSection');

    const visiblePopular = cards.popular.filter(card => shouldShowCard(card)).length;
    const visibleRegular = cards.regular.filter(card => shouldShowCard(card)).length;

    if (visiblePopular === 0 && visibleRegular === 0) {
        noResultsSection.style.display = 'block';
        loadMoreSection.style.display = 'none';
    } else {
        noResultsSection.style.display = 'none';
        loadMoreSection.style.display = 'block';
    }
}

// 게시판 레시피 개수 업데이트
function updateBoardRecipeCount() {
    const recipeCount = document.getElementById('boardRecipeCount');
    const regularCount = document.getElementById('boardRegularCount');

    fetch('/board/counts')
        .then(response => response.json())
        .then(counts => {
            const currentCount = counts[boardSelectedCategory] || 0;

            if (recipeCount) {
                recipeCount.textContent = `총 ${currentCount}개의 커뮤니티 레시피`;
            }
            if (regularCount) {
                regularCount.textContent = `${currentCount}개`;
            }

            renderBoardCategories(counts);
        })
        .catch(error => console.error("Error fetching recipe counts:", error));
}

// 카테고리 제목 업데이트
function updateBoardCategoryTitle() {
    const categoryTitle = document.getElementById('boardCategoryTitle');
    const selectedCategoryData = boardCategories.find(c => c.id === boardSelectedCategory);

    if (categoryTitle && selectedCategoryData) {
        categoryTitle.textContent = selectedCategoryData.name === '전체' ? '전체 레시피' : `${selectedCategoryData.name} 레시피`;
    }
}

// === 이벤트 핸들러들 ===

// 카테고리 전환
function switchBoardCategory(categoryId) {
    if (categoryId === 'all') {
        location.href = '/board/list';
    } else {
        location.href = '/board/list?category=' + encodeURIComponent(categoryId);
    }
}

// 검색 처리
function handleBoardSearch() {
    const searchInput = document.getElementById('boardSearchInput');
    if (searchInput) {
        boardSearchQuery = searchInput.value.trim();
        updateBoardContent();
    }
}

// 정렬 처리
function handleBoardSort() {
    const sortSelect = document.getElementById('boardSortSelect');
    if (sortSelect) {
        boardSortBy = sortSelect.value;
        updateBoardContent();
    }
}

// 카테고리 셀렉트 처리
function handleBoardCategorySelect() {
    const categorySelect = document.getElementById('boardCategorySelect');
    if (categorySelect) {
        boardSelectedCategory = categorySelect.value;
        updateBoardContent();
        renderBoardCategories();
    }
}

// 카테고리 배지 스타일 적용
function fixCategoryBadges() {
    // 모든 카테고리 배지 찾기 및 수정
    const badges = document.querySelectorAll('span');

    badges.forEach(badge => {
        const text = badge.textContent.trim();
        const hasGenericBg = badge.classList.contains('bg-white/90') ||
                           badge.classList.contains('bg-red-500/90') ||
                           badge.classList.contains('bg-green-500/90');

        if (hasGenericBg) {
            // 카테고리별 스타일 적용
            if (text === '한식') {
                badge.className = 'category-badge korean';
            } else if (text === '양식') {
                badge.className = 'category-badge western';
            } else if (text === '중식') {
                badge.className = 'category-badge chinese';
            } else if (text === '일식') {
                badge.className = 'category-badge japanese';
            } else if (text === '디저트') {
                badge.className = 'category-badge dessert';
            } else if (text === 'HOT') {
                badge.className = 'category-badge hot-badge';
            } else if (text === 'NEW') {
                badge.className = 'category-badge new-badge';
            }
        }
    });
}

// 게시판 콘텐츠 전체 업데이트
function updateBoardContent() {
    updateBoardCategoryTitle();
    updateBoardRecipeCount();
    renderBoardPopularRecipes();
    renderBoardRegularRecipes();
    toggleBoardNoResultsSection();

    // 카테고리 배지 스타일 적용
    fixCategoryBadges();

    // Lucide 아이콘 재초기화
    if (window.CheForest && window.CheForest.common) {
        window.CheForest.common.initializeLucideIcons();
    } else if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
}

// 검색 입력 이벤트 설정
function setupBoardSearchEvents() {
    const searchInput = document.getElementById('boardSearchInput');
    if (searchInput) {
        // 실시간 검색 (디바운스 적용)
        let searchTimeout;
        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                handleBoardSearch();
            }, 300);
        });

        // Enter 키 이벤트
        searchInput.addEventListener('keydown', function(event) {
            if (event.key === 'Enter') {
                clearTimeout(searchTimeout);
                handleBoardSearch();
            }
        });
    }
}

// 셀렉트 이벤트 설정
function setupBoardSelectEvents() {
    const sortSelect = document.getElementById('boardSortSelect');
    const categorySelect = document.getElementById('boardCategorySelect');

    if (sortSelect) {
        sortSelect.addEventListener('change', handleBoardSort);
    }

    if (categorySelect) {
        categorySelect.addEventListener('change', handleBoardCategorySelect);
    }
}

// === 게시판 페이지 초기화 ===

function initializeBoardPage() {
    // 카테고리 렌더링
    renderBoardCategories();
    renderBoardCategorySelect();

    // 초기 게시판 콘텐츠 렌더링
    updateBoardContent();

    // 이벤트 설정
    setupBoardSearchEvents();
    setupBoardSelectEvents();

    // 카테고리 배지 스타일 적용 (초기화 시에도 실행)
    fixCategoryBadges();

    // 현재 네비게이션 상태 업데이트
    if (window.CheForest && window.CheForest.common) {
        window.CheForest.common.updateActiveNavigation('board');
    }

    console.log('✅ 게시판 페이지 초기화 완료! (HTML 기반 필터링 + 카테고리 배지 스타일 적용)');
}

// 페이지 로드 시 게시판 페이지 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 공통 스크립트가 로드될 때까지 기다림
    if (window.CheForest && window.CheForest.common) {
        initializeBoardPage();
    } else {
        setTimeout(initializeBoardPage, 100);
    }
});

// 게시판 페이지 함수들을 전역 객체에 등록
window.CheForest = window.CheForest || {};
window.CheForest.board = {
    switchBoardCategory,
    handleBoardSearch,
    handleBoardSort,
    handleBoardCategorySelect,
    updateBoardContent,
    initializeBoardPage,
    getBoardRecipeCards,
    shouldShowCard,
    sortBoardCards
};