/* CheForest Recipe Page JavaScript */
/* 레시피 페이지 전용 JavaScript (카테고리 + 카드 필터링 + 검색) */

// 전역 변수
let selectedCategory = 'all';
let searchQuery = '';
let sortBy = 'popularity';

// ✅ 카테고리 정의 (순서 고정)
const recipeCategories = [
    { id: 'all', name: '전체', icon: '🍽️', color: 'bg-gray-100 text-gray-800' },
    { id: '한식', name: '한식', icon: '🥢', color: 'korean' },
    { id: '양식', name: '양식', icon: '🍝', color: 'western' },
    { id: '중식', name: '중식', icon: '🥟', color: 'chinese' },
    { id: '일식', name: '일식', icon: '🍣', color: 'japanese' },
    { id: '디저트', name: '디저트', icon: '🧁', color: 'dessert' }
];

// ====== 카드 필터링 관련 ======
function getRecipeCards() {
    const popularCards = Array.from(document.querySelectorAll('#popularGrid .popular-recipe-card'));
    const regularCards = Array.from(document.querySelectorAll('#regularGrid .recipe-card'));
    return { popular: popularCards, regular: regularCards, all: [...popularCards, ...regularCards] };
}

function shouldShowRecipeCard(card) {
    const category = card.dataset.category;
    const title = card.dataset.title?.toLowerCase() || '';
    const description = card.dataset.description?.toLowerCase() || '';
    const matchesCategory = selectedCategory === 'all' || category === selectedCategory;
    const searchLower = searchQuery.toLowerCase();
    const matchesSearch = !searchQuery || title.includes(searchLower) || description.includes(searchLower);
    return matchesCategory && matchesSearch;
}

function sortRecipeCards(cards) {
    return cards.sort((a, b) => {
        const aLikes = parseInt(a.dataset.likes) || 0;
        const bLikes = parseInt(b.dataset.likes) || 0;
        const aViews = parseInt(a.dataset.views) || 0;
        const bViews = parseInt(b.dataset.views) || 0;
        const aCreated = new Date(a.dataset.created).getTime();
        const bCreated = new Date(b.dataset.created).getTime();
        switch (sortBy) {
            case 'popularity': return bLikes - aLikes;
            case 'views': return bViews - aViews;
            case 'newest': return bCreated - aCreated;
            default: return 0;
        }
    });
}

// ====== 카테고리 렌더링 ======
function renderRecipeCategories(counts = {}) {
    const container = document.getElementById('recipeCategoryButtons');
    if (!container) return;

    container.innerHTML = recipeCategories.map(cat => {
        const isActive = selectedCategory === cat.id;
        const count = counts[cat.id] ?? 0;

        // 이동 URL
        const url = cat.id === 'all'
            ? '/recipe/list'
            : `/recipe/list?categoryKr=${encodeURIComponent(cat.id)}`;

        return `
        <a href="${url}"
            class="w-full flex items-center justify-between px-4 py-2 rounded-lg border transition-all
                ${isActive
            ? 'active bg-gradient-to-r from-pink-500 to-orange-500 text-white border-transparent shadow-lg'
            : 'bg-white border-gray-200 hover:border-orange-300 hover:bg-orange-50 text-gray-700'}">
            <span class="flex items-center space-x-2">
                <span>${cat.icon}</span>
                <span>${cat.name}</span>
            </span>
            <span class="text-xs px-2 py-0.5 rounded-full
                ${isActive ? 'bg-white/20 text-white' : 'bg-gray-100 text-gray-700'}">
                ${count}
            </span>
        </a>`;
    }).join('');
}

// ====== 카드 렌더링 ======
function renderPopularRecipes() {
    const cards = getRecipeCards();
    const popularSection = document.getElementById('popularSection');
    const popularCount = document.getElementById('popularCount');
    let visibleCount = 0;
    cards.popular.forEach(card => {
        if (shouldShowRecipeCard(card)) { card.style.display = 'block'; visibleCount++; }
        else card.style.display = 'none';
    });
    if (visibleCount > 0) {
        popularSection.style.display = 'block';
        popularCount.textContent = `TOP ${visibleCount}`;
    } else {
        popularSection.style.display = 'none';
    }
}

function renderRegularRecipes() {
    const cards = getRecipeCards();
    const regularCount = document.getElementById('regularCount');
    const visibleCards = cards.regular.filter(card => shouldShowRecipeCard(card));
    const sortedCards = sortRecipeCards([...visibleCards]);
    cards.regular.forEach(card => card.style.display = 'none');
    const regularGrid = document.getElementById('regularGrid');
    if (regularGrid && sortedCards.length > 0) {
        sortedCards.forEach(card => {
            card.style.display = 'block';
            regularGrid.appendChild(card);
        });
    }
    regularCount.textContent = `${visibleCards.length}개`;
}

// ====== 결과 없음 ======
function toggleNoResultsSection() {
    const cards = getRecipeCards();
    const noResultsSection = document.getElementById('noResultsSection');
    const loadMoreSection = document.getElementById('loadMoreSection');
    const visiblePopular = cards.popular.filter(card => shouldShowRecipeCard(card)).length;
    const visibleRegular = cards.regular.filter(card => shouldShowRecipeCard(card)).length;
    if (visiblePopular === 0 && visibleRegular === 0) {
        noResultsSection.style.display = 'block';
        if (loadMoreSection) loadMoreSection.style.display = 'none';
    } else {
        noResultsSection.style.display = 'none';
        if (loadMoreSection) loadMoreSection.style.display = 'block';
    }
}

// ====== 개수 갱신 ======
function updateRecipeCount() {
    fetch('/recipe/counts')
        .then(res => res.json())
        .then(data => {
            renderRecipeCategories(data);
        })
        .catch(err => {
            console.error('레시피 카운트 로드 실패:', err);
            renderRecipeCategories(); // 실패 시 0 표시
        });
}

// ====== 동작 제어 ======
function switchRecipeCategory(categoryId) {
    selectedCategory = categoryId;
    updateRecipeContent();
}

function handleRecipeSearch() {
    const input = document.getElementById('recipeSearchInput');
    if (input) {
        searchQuery = input.value.trim();
        updateRecipeContent();
    }
}

function updateRecipeContent() {
    updateRecipeCount();
    renderPopularRecipes();
    renderRegularRecipes();
    toggleNoResultsSection();
}

// ====== 초기화 ======
function initializeRecipePage() {
    const c = new URLSearchParams(location.search).get('categoryKr');
    if (c) selectedCategory = decodeURIComponent(c);

    updateRecipeCount();
    console.log('✅ 레시피 페이지 초기화 완료! (카테고리 + 카운트 + 링크)');
}

function setupRecipeSearchEvents() {
    const input = document.getElementById('recipeSearchInput');
    if (!input) return;
    let t;
    input.addEventListener('input', e => {
        clearTimeout(t);
        t = setTimeout(handleRecipeSearch, 300);
    });
    input.addEventListener('keydown', e => {
        if (e.key === 'Enter') {
            clearTimeout(t);
            handleRecipeSearch();
        }
    });
}

document.addEventListener('DOMContentLoaded', initializeRecipePage);

// 전역 등록
window.CheForest = window.CheForest || {};
window.CheForest.recipe = { switchRecipeCategory, handleRecipeSearch, updateRecipeContent };