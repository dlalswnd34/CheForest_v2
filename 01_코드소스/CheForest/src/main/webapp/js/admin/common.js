/* CheForest Common JavaScript */
/* 전체 사이트에서 공통으로 사용되는 JavaScript 함수들 */

// 전역 변수
window.CheForest = window.CheForest || {};

// 페이지 네비게이션 함수
function showPage(page) {
    console.log('Navigate to:', page);
    // JSP에서 실제 페이지 이동 로직을 구현
    // 예: window.location.href = page + '.jsp';
}

// 네비게이션 활성화 상태 업데이트
function updateActiveNavigation(page) {
    // 모든 네비게이션 아이템에서 활성화 상태 제거
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('text-orange-500');
        item.classList.add('text-gray-700');
        const underline = item.querySelector('.nav-underline');
        if (underline) {
            underline.style.transform = 'scaleX(0)';
        }
    });
    
    // 현재 페이지 네비게이션 아이템 활성화
    const activeItem = document.querySelector(`[data-page="${page}"]`);
    if (activeItem) {
        activeItem.classList.remove('text-gray-700');
        activeItem.classList.add('text-orange-500');
        const underline = activeItem.querySelector('.nav-underline');
        if (underline) {
            underline.style.transform = 'scaleX(1)';
        }
    }
}

// Lucide 아이콘 초기화
function initializeLucideIcons() {
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
}

// 페이지 로드 시 초기화
function initializeCommon() {
    // Lucide 아이콘 초기화
    initializeLucideIcons();
    
    // 현재 페이지 감지 및 네비게이션 업데이트
    const currentPage = getCurrentPage();
    updateActiveNavigation(currentPage);
}

// 현재 페이지 감지 함수
function getCurrentPage() {
    const path = window.location.pathname;
    const page = path.split('/').pop().replace('.jsp', '').replace('.html', '');
    return page || 'home';
}

// 스무스 스크롤 함수
function smoothScrollTo(targetId) {
    const target = document.getElementById(targetId);
    if (target) {
        target.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }
}

// 디바운스 함수
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 모바일 여부 감지
function isMobile() {
    return window.innerWidth <= 768;
}

// 반응형 핸들러
function handleResize() {
    // 리사이즈 시 필요한 로직
    if (isMobile()) {
        // 모바일 전용 로직
    } else {
        // 데스크톱 전용 로직
    }
}

// 이벤트 리스너 등록
window.addEventListener('resize', debounce(handleResize, 250));

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', initializeCommon);

// === 레시피 페이지 공통 함수 추가 ===

// 이미지 에러 핸들링
function handleImageError(img, fallbackUrl = 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400') {
    img.onerror = null; // 무한 루프 방지
    img.src = fallbackUrl;
}

// 숫자 애니메이션 함수
function animateNumber(element, start, end, duration = 1000) {
    const range = end - start;
    const increment = end > start ? 1 : -1;
    const stepTime = Math.abs(Math.floor(duration / range));
    
    let current = start;
    const timer = setInterval(() => {
        current += increment;
        element.textContent = current;
        
        if (current === end) {
            clearInterval(timer);
        }
    }, stepTime);
}

// 요소가 뷰포트에 보이는지 확인
function isElementInViewport(element) {
    const rect = element.getBoundingClientRect();
    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
        rect.right <= (window.innerWidth || document.documentElement.clientWidth)
    );
}

// 레이지 로딩 구현
function setupLazyLoading() {
    const images = document.querySelectorAll('img[data-src]');
    
    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.removeAttribute('data-src');
                    imageObserver.unobserve(img);
                }
            });
        });
        
        images.forEach(img => imageObserver.observe(img));
    } else {
        // 폴백: IntersectionObserver 미지원 시
        images.forEach(img => {
            img.src = img.dataset.src;
            img.removeAttribute('data-src');
        });
    }
}

// === 게시판 페이지 공통 함수 추가 ===

// 좋아요 토글 애니메이션
function toggleLike(element, isLiked = false) {
    if (isLiked) {
        element.classList.add('heart-liked');
        element.style.color = '#ef4444';
        setTimeout(() => {
            element.classList.remove('heart-liked');
        }, 600);
    } else {
        element.style.color = '#6b7280';
    }
}

// 텍스트 트런케이션 (말줄임표)
function truncateText(text, maxLength) {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

// 한국어 시간 포맷팅
function formatKoreanTime(date) {
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const diffInMinutes = Math.floor(diff / (1000 * 60));
    const diffInHours = Math.floor(diff / (1000 * 60 * 60));
    const diffInDays = Math.floor(diff / (1000 * 60 * 60 * 24));
    
    if (diffInMinutes < 1) return '방금 전';
    if (diffInMinutes < 60) return `${diffInMinutes}분 전`;
    if (diffInHours < 24) return `${diffInHours}시간 전`;
    if (diffInDays < 7) return `${diffInDays}일 전`;
    if (diffInDays < 30) return `${Math.floor(diffInDays / 7)}주 전`;
    return `${Math.floor(diffInDays / 30)}개월 전`;
}

// 검색 하이라이트
function highlightSearchText(text, searchQuery) {
    if (!searchQuery) return text;
    const regex = new RegExp(`(${searchQuery})`, 'gi');
    return text.replace(regex, '<mark class="bg-yellow-200">$1</mark>');
}

// 로컬 스토리지 유틸리티
function getLocalStorage(key, defaultValue = null) {
    try {
        const item = localStorage.getItem(key);
        return item ? JSON.parse(item) : defaultValue;
    } catch (error) {
        console.error('로컬 스토리지 읽기 오류:', error);
        return defaultValue;
    }
}

function setLocalStorage(key, value) {
    try {
        localStorage.setItem(key, JSON.stringify(value));
        return true;
    } catch (error) {
        console.error('로컬 스토리지 쓰기 오류:', error);
        return false;
    }
}

// 쿠키 유틸리티
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
}

function setCookie(name, value, days = 7) {
    const expires = new Date();
    expires.setTime(expires.getTime() + (days * 24 * 60 * 60 * 1000));
    document.cookie = `${name}=${value};expires=${expires.toUTCString()};path=/`;
}

// === 이벤트 페이지 공통 함수 추가 ===

// 알림 표시 공통 함수
function showNotification(message, type = 'info', duration = 3000) {
    // 기존 알림이 있다면 제거
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }
    
    // 새 알림 생성
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    
    // DOM에 추가
    document.body.appendChild(notification);
    
    // 애니메이션으로 표시
    setTimeout(() => {
        notification.style.opacity = '1';
        notification.style.transform = 'translateX(0)';
    }, 100);
    
    // 지정된 시간 후 자동 제거
    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transform = 'translateX(100%)';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 300);
    }, duration);
}

// 로딩 상태 표시 공통 함수
function showLoadingState(element) {
    if (!element) return;
    element.classList.add('card-loading');
    element.style.pointerEvents = 'none';
    element.style.opacity = '0.7';
    
    // 로딩 오버레이 추가
    const overlay = document.createElement('div');
    overlay.className = 'loading-overlay';
    overlay.style.cssText = `
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(255, 255, 255, 0.8);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 10;
    `;
    
    // 로딩 스피너 추가
    const spinner = document.createElement('div');
    spinner.innerHTML = `
        <svg class="animate-spin h-6 w-6 text-orange-500" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
    `;
    overlay.appendChild(spinner);
    element.appendChild(overlay);
}

// 로딩 상태 숨김 공통 함수
function hideLoadingState(element) {
    if (!element) return;
    element.classList.remove('card-loading');
    element.style.pointerEvents = '';
    element.style.opacity = '';
    
    // 로딩 오버레이 제거
    const overlay = element.querySelector('.loading-overlay');
    if (overlay) {
        overlay.remove();
    }
}

// 카드 클릭 애니메이션 공통 함수
function addCardClickAnimation(card) {
    if (!card) return;
    
    card.style.transform = 'scale(0.98)';
    setTimeout(() => {
        card.style.transform = '';
    }, 150);
}

// 이미지 페이드인 로딩 효과
function setupImageFadeIn() {
    const images = document.querySelectorAll('img');
    images.forEach(img => {
        img.style.opacity = '0';
        img.style.transition = 'opacity 0.3s ease';
        
        img.addEventListener('load', function() {
            this.style.opacity = '1';
        });
        
        // 이미 로드된 이미지 처리
        if (img.complete) {
            img.style.opacity = '1';
        }
    });
}

// 스크롤 애니메이션 트리거
function setupScrollAnimations() {
    const animatedElements = document.querySelectorAll('.fade-in, .slide-up');
    
    if ('IntersectionObserver' in window) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                }
            });
        }, {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        });
        
        animatedElements.forEach(el => {
            el.style.opacity = '0';
            el.style.transform = 'translateY(20px)';
            el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
            observer.observe(el);
        });
    }
}

// 터치 디바이스 호버 효과 대체
function setupTouchHoverEffects() {
    if ('ontouchstart' in window) {
        const hoverElements = document.querySelectorAll('.event-card, .btn');
        
        hoverElements.forEach(element => {
            element.addEventListener('touchstart', function() {
                this.classList.add('touch-active');
            });
            
            element.addEventListener('touchend', function() {
                setTimeout(() => {
                    this.classList.remove('touch-active');
                }, 300);
            });
        });
    }
}

// 키보드 네비게이션 지원
function setupKeyboardNavigation() {
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Tab') {
            document.body.classList.add('user-is-tabbing');
        }
    });
    
    document.addEventListener('mousedown', function() {
        document.body.classList.remove('user-is-tabbing');
    });
}

// 공통 유틸리티 함수들을 전역 객체에 등록
window.CheForest.common = {
    showPage,
    updateActiveNavigation,
    initializeLucideIcons,
    smoothScrollTo,
    debounce,
    isMobile,
    getCurrentPage,
    handleImageError,
    animateNumber,
    isElementInViewport,
    setupLazyLoading,
    toggleLike,
    truncateText,
    formatKoreanTime,
    highlightSearchText,
    getLocalStorage,
    setLocalStorage,
    getCookie,
    setCookie,
    // 이벤트 페이지 공통 함수들
    showNotification,
    showLoadingState,
    hideLoadingState,
    addCardClickAnimation,
    setupImageFadeIn,
    setupScrollAnimations,
    setupTouchHoverEffects,
    setupKeyboardNavigation
};