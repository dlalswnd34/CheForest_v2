/* CheForest Header JavaScript */
/* 헤더 컴포넌트 전용 JavaScript */

// 헤더 관련 전역 변수
let isIngredientsDropdownOpen = false;
let isMobileMenuOpen = false;

// 모바일 메뉴 토글
function toggleMobileMenu() {
    const mobileMenu = document.getElementById('mobileMenu');
    if (mobileMenu) {
        isMobileMenuOpen = !isMobileMenuOpen;
        if (isMobileMenuOpen) {
            mobileMenu.classList.remove('hidden');
        } else {
            mobileMenu.classList.add('hidden');
        }
    }
}

// 재료 드롭다운 표시
function showIngredientsDropdown() {
    const dropdown = document.getElementById('ingredientsDropdown');
    if (dropdown && !isIngredientsDropdownOpen) {
        isIngredientsDropdownOpen = true;
        dropdown.classList.remove('opacity-0', 'invisible', '-translate-y-2');
        dropdown.classList.add('opacity-100', 'visible', 'translate-y-0');
    }
}

// 재료 드롭다운 숨김
function hideIngredientsDropdown() {
    const dropdown = document.getElementById('ingredientsDropdown');
    if (dropdown && isIngredientsDropdownOpen) {
        isIngredientsDropdownOpen = false;
        dropdown.classList.add('opacity-0', 'invisible', '-translate-y-2');
        dropdown.classList.remove('opacity-100', 'visible', 'translate-y-0');
    }
}

// 검색 기능
function handleSearch() {
    const searchInput = document.querySelector('.search-input');
    if (searchInput) {
        const query = searchInput.value.trim();
        if (query) {
            console.log('Search for:', query);
            showPage('search');
        }
    }
}

// 검색 입력 이벤트 핸들러
// function setupSearchHandlers() {
//     const searchInput = document.querySelector('.search-input');
//     if (searchInput) {
//         // Enter 키 이벤트
//         searchInput.addEventListener('keydown', function(event) {
//             if (event.key === 'Enter') {
//                 handleSearch();
//             }
//         });
//
//         // 포커스 이벤트
//         searchInput.addEventListener('focus', function() {
//             showPage('search');
//         });
//     }

//     // 검색 버튼 클릭 이벤트
//     const searchButtons = document.querySelectorAll('.search-btn');
//     searchButtons.forEach(button => {
//         button.addEventListener('click', handleSearch);
//     });
// }

// 드롭다운 마우스 이벤트 설정
function setupDropdownEvents() {
    const dropdownTrigger = document.querySelector('[onmouseenter="showIngredientsDropdown()"]');
    const dropdown = document.getElementById('ingredientsDropdown');

    if (dropdownTrigger && dropdown) {
        // 마우스 enter/leave 이벤트
        dropdownTrigger.addEventListener('mouseenter', showIngredientsDropdown);
        dropdownTrigger.addEventListener('mouseleave', () => {
            setTimeout(hideIngredientsDropdown, 150);
        });

        dropdown.addEventListener('mouseenter', showIngredientsDropdown);
        dropdown.addEventListener('mouseleave', hideIngredientsDropdown);
    }
}

// 모바일 메뉴 외부 클릭 시 닫기
function setupMobileMenuCloseOnOutsideClick() {
    document.addEventListener('click', function(event) {
        const mobileMenu = document.getElementById('mobileMenu');
        const menuButton = document.querySelector('[onclick="toggleMobileMenu()"]');

        if (isMobileMenuOpen && mobileMenu && menuButton) {
            if (!mobileMenu.contains(event.target) && !menuButton.contains(event.target)) {
                toggleMobileMenu();
            }
        }
    });
}

// 헤더 스크롤 효과
function handleHeaderScroll() {
    const header = document.querySelector('header');
    if (header) {
        const scrolled = window.scrollY > 10;
        if (scrolled) {
            header.classList.add('shadow-md');
        } else {
            header.classList.remove('shadow-md');
        }
    }
}

// 헤더 초기화
function initializeHeader() {
    // setupSearchHandlers();
    setupDropdownEvents();
    setupMobileMenuCloseOnOutsideClick();

    // 스크롤 이벤트 리스너
    window.addEventListener('scroll', window.CheForest.common.debounce(handleHeaderScroll, 10));
}

// 페이지 로드 시 헤더 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 공통 스크립트가 로드될 때까지 기다림
    if (window.CheForest && window.CheForest.common) {
        initializeHeader();
    } else {
        setTimeout(initializeHeader, 100);
    }
});

// 헤더 함수들을 전역 객체에 등록
window.CheForest = window.CheForest || {};
window.CheForest.header = {
    toggleMobileMenu,
    showIngredientsDropdown,
    hideIngredientsDropdown,
    handleSearch,
    initializeHeader
};
document.getElementById('searchBtn').addEventListener('click', function() {
    const query = document.getElementById('searchInput').value.trim();
    if (query) {
        // URL 인코딩해서 이동
        window.location.href = `/search?totalKeyword=${encodeURIComponent(query)}`;
    } else {
        alert('검색어를 입력해주세요.');
    }
});
document.getElementById('searchInput').addEventListener('keydown', function(e) {
    if (e.key === 'Enter') {
        e.preventDefault();
        document.getElementById('searchBtn').click();
    }
});
setInterval(function () {
    navigator.sendBeacon("/ping");
}, 1000);