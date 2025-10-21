/* 이벤트 페이지 전용 JavaScript */

// 이벤트 페이지 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeEventsPage();
});

// 이벤트 페이지 초기화 함수
function initializeEventsPage() {
    console.log('이벤트 페이지가 로드되었습니다.');
    
    // 카드 호버 효과 초기화
    initializeCardHoverEffects();
    
    // 이미지 로딩 에러 처리
    handleImageErrors();
    
    // 버튼 상태 초기화
    initializeButtonStates();
}

// 카드 호버 효과 초기화
function initializeCardHoverEffects() {
    const eventCards = document.querySelectorAll('.event-card');
    
    eventCards.forEach(card => {
        // 마우스 엔터 이벤트
        card.addEventListener('mouseenter', function() {
            const image = this.querySelector('.card-image, .test-character');
            if (image) {
                image.style.transform = 'scale(1.05)';
            }
        });
        
        // 마우스 리브 이벤트
        card.addEventListener('mouseleave', function() {
            const image = this.querySelector('.card-image, .test-character');
            if (image) {
                image.style.transform = 'scale(1)';
            }
        });
    });
}

// 이미지 로딩 에러 처리
function handleImageErrors() {
    const images = document.querySelectorAll('.card-image, .test-character');
    
    images.forEach(img => {
        img.addEventListener('error', function() {
            console.warn('이미지 로딩 실패:', this.src);
            // 기본 이미지로 교체하거나 플레이스홀더 표시
            this.style.backgroundColor = '#f3f4f6';
            this.style.display = 'flex';
            this.style.alignItems = 'center';
            this.style.justifyContent = 'center';
            this.innerHTML = '<span style="color: #9ca3af;">이미지를 불러올 수 없습니다</span>';
        });
    });
}

// 버튼 상태 초기화
function initializeButtonStates() {
    const buttons = document.querySelectorAll('.btn');
    
    buttons.forEach(button => {
        button.addEventListener('click', function(e) {
            // 버튼 클릭 효과
            this.style.transform = 'scale(0.98)';
            setTimeout(() => {
                this.style.transform = '';
            }, 150);
        });
    });
}

// 이벤트 참여 함수
function participateEvent(eventId) {
    console.log('이벤트 참여:', eventId);
    // 실제로는 이벤트 참여 페이지로 이동하거나 모달을 띄움
    // 예: window.location.href = 'event-participate.jsp?id=' + eventId;
}

// 이벤트 상세 보기 함수
function viewEventDetail(eventId) {
    console.log('이벤트 상세 보기:', eventId);
    // 실제로는 상세 페이지로 이동
    // 예: window.location.href = 'event-detail.jsp?id=' + eventId;
}

// 성향 테스트 시작 함수
function startTest() {
    console.log('성향 테스트 시작');
    // 실제로는 테스트 페이지로 이동
    // 예: window.location.href = 'personality-test.jsp';
}

// 더 많은 이벤트 보기 함수
function viewMoreEvents() {
    console.log('더 많은 이벤트 보기');
    // 실제로는 이벤트 목록 페이지로 이동
    // 예: window.location.href = 'events-list.jsp';
}

// 로딩 상태 표시
function showLoadingState(element) {
    element.classList.add('loading');
    element.style.pointerEvents = 'none';
    element.style.opacity = '0.7';
}

// 로딩 상태 숨김
function hideLoadingState(element) {
    element.classList.remove('loading');
    element.style.pointerEvents = '';
    element.style.opacity = '';
}

// 알림 표시 함수
function showNotification(message, type = 'info') {
    // 기존 알림이 있다면 제거
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }
    
    // 새 알림 생성
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    
    // 스타일 적용
    Object.assign(notification.style, {
        position: 'fixed',
        top: '20px',
        right: '20px',
        padding: '12px 20px',
        borderRadius: '8px',
        color: 'white',
        fontWeight: '500',
        zIndex: '9999',
        opacity: '0',
        transform: 'translateX(100%)',
        transition: 'all 0.3s ease'
    });
    
    // 타입별 배경색 설정
    switch (type) {
        case 'success':
            notification.style.background = 'linear-gradient(135deg, #22c55e, #16a34a)';
            break;
        case 'error':
            notification.style.background = 'linear-gradient(135deg, #ef4444, #dc2626)';
            break;
        case 'warning':
            notification.style.backgroundColor = '#f59e0b';
            break;
        default:
            notification.style.background = 'linear-gradient(135deg, #ec4899, #f59e0b)';
    }
    
    // DOM에 추가
    document.body.appendChild(notification);
    
    // 애니메이션으로 표시
    setTimeout(() => {
        notification.style.opacity = '1';
        notification.style.transform = 'translateX(0)';
    }, 100);
    
    // 3초 후 자동 제거
    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transform = 'translateX(100%)';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 300);
    }, 3000);
}

// 이벤트 카드 클릭 이벤트 (카드 전체 클릭시 상세보기)
document.addEventListener('click', function(e) {
    const eventCard = e.target.closest('.event-card');
    if (eventCard && !e.target.closest('.btn')) {
        const eventId = eventCard.getAttribute('data-event-id');
        if (eventId === '2') {
            startTest();
        } else {
            viewEventDetail(eventId);
        }
    }
});

// 키보드 접근성 지원
document.addEventListener('keydown', function(e) {
    if (e.key === 'Enter' || e.key === ' ') {
        const focusedCard = document.activeElement.closest('.event-card');
        if (focusedCard) {
            e.preventDefault();
            focusedCard.click();
        }
    }
});

// 반응형 그리드 최적화
function optimizeGridLayout() {
    const grid = document.querySelector('.events-grid');
    const cards = document.querySelectorAll('.event-card');
    
    if (window.innerWidth < 768) {
        // 모바일에서는 단일 컬럼
        grid.style.gridTemplateColumns = '1fr';
    } else {
        // 데스크톱에서는 2컬럼
        grid.style.gridTemplateColumns = '1fr 1fr';
    }
}

// 윈도우 리사이즈 이벤트
window.addEventListener('resize', debounce(optimizeGridLayout, 250));

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

// 페이지 성능 모니터링
if ('performance' in window) {
    window.addEventListener('load', function() {
        setTimeout(() => {
            const perfData = performance.getEntriesByType('navigation')[0];
            console.log('이벤트 페이지 로딩 시간:', perfData.loadEventEnd - perfData.loadEventStart, 'ms');
        }, 0);
    });
}