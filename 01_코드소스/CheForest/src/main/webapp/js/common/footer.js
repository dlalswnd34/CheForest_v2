/* CheForest Footer JavaScript */
/* 푸터 컴포넌트 전용 JavaScript */

// 뉴스레터 구독 처리 (실제 메일 발송용)
function handleNewsletterSubscription() {
    const emailInput = document.querySelector('.newsletter-email');
    const subscribeBtn = document.querySelector('.newsletter-subscribe-btn');
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

    if (!emailInput || !subscribeBtn) return;

    const email = emailInput.value.trim();

    // 이메일 유효성 검사
    if (!isValidEmail(email)) {
        showNotification('올바른 이메일 주소를 입력해주세요.', 'error');
        return;
    }

    subscribeBtn.textContent = '구독 중...';
    subscribeBtn.disabled = true;

    // 요청 헤더 설정
    const headers = { 'Content-Type': 'application/json' };
    if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;

    // 실제 서버에 구독 요청 전송
    fetch('/api/footer/newsletter', {
        method: 'POST',
        headers,
        body: JSON.stringify({ email })
    })
        .then(async res => {
            const data = await res.json().catch(() => ({}));
            if (!res.ok || !data.ok) {
                throw new Error(data.msg || '메일 발송 중 오류가 발생했습니다.');
            }
            showNotification(data.msg || '뉴스레터 구독이 완료되었습니다!', 'success');
            emailInput.value = '';
        })
        .catch(err => {
            console.error('뉴스레터 구독 실패:', err);
            showNotification(err.message || '서버 오류가 발생했습니다.', 'error');
        })
        .finally(() => {
            subscribeBtn.textContent = '구독하기';
            subscribeBtn.disabled = false;
        });
}

// 이메일 유효성 검사
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// 알림 표시 함수 (시각적 개선)
// 🔥 완전 확실한 버전 (Tailwind 없이 작동)
function showNotification(message, type = 'info') {
    const existing = document.querySelector('.notification');
    if (existing) existing.remove();

    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.textContent = message;

    // ✅ Tailwind 클래스 대신 inline style로 확실히 보이게 설정
    Object.assign(notification.style, {
        position: 'fixed',
        top: '24px',
        right: '24px',
        padding: '14px 22px',
        borderRadius: '10px',
        color: '#fff',
        fontSize: '15px',
        zIndex: '9999',
        boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
        transition: 'all 0.4s ease',
        opacity: '0',
        transform: 'translateX(30px)',
    });

    // ✅ 타입별 색상
    switch (type) {
        case 'success': notification.style.backgroundColor = '#22c55e'; break; // green-500
        case 'error': notification.style.backgroundColor = '#ef4444'; break;   // red-500
        case 'warning': notification.style.backgroundColor = '#f59e0b'; break; // yellow-500
        default: notification.style.backgroundColor = '#3b82f6';               // blue-500
    }

    document.body.appendChild(notification);

    // ✅ fade-in
    setTimeout(() => {
        notification.style.opacity = '1';
        notification.style.transform = 'translateX(0)';
    }, 10);

    // ✅ fade-out & 제거
    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transform = 'translateX(30px)';
        setTimeout(() => notification.remove(), 500);
    }, 3000);
}

// 알림 스타일 반환 (색상 유지)
function getNotificationStyle(type) {
    switch (type) {
        case 'success': return 'bg-green-500 text-white';
        case 'error': return 'bg-red-500 text-white';
        case 'warning': return 'bg-yellow-500 text-white';
        default: return 'bg-blue-500 text-white';
    }
}

// 소셜 미디어 링크 처리
function handleSocialClick(platform) {
    const socialLinks = {
        facebook: 'https://facebook.com/cheforest',
        twitter: 'https://twitter.com/cheforest',
        instagram: 'https://instagram.com/cheforest',
        youtube: 'https://youtube.com/cheforest'
    };
    
    if (socialLinks[platform]) {
        window.open(socialLinks[platform], '_blank');
    }
}

// 앱 다운로드 링크 처리
function handleAppDownload(platform) {
    const downloadLinks = {
        ios: 'https://apps.apple.com/app/cheforest',
        android: 'https://play.google.com/store/apps/details?id=com.cheforest'
    };
    
    if (downloadLinks[platform]) {
        window.open(downloadLinks[platform], '_blank');
    }
}

// QR 코드 호버 효과
function setupQRCodeEffect() {
    const qrCode = document.querySelector('.qr-code');
    if (qrCode) {
        qrCode.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.1)';
        });
        
        qrCode.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
        });
    }
}

// 푸터 링크 애니메이션 설정
function setupFooterLinkAnimations() {
    const footerLinks = document.querySelectorAll('.footer-link');
    footerLinks.forEach(link => {
        link.addEventListener('mouseenter', function() {
            this.style.transform = 'translateX(4px)';
        });
        
        link.addEventListener('mouseleave', function() {
            this.style.transform = 'translateX(0)';
        });
    });
}

// 뉴스레터 입력 이벤트 설정
function setupNewsletterEvents() {
    const emailInput = document.querySelector('.newsletter-email');
    const subscribeBtn = document.querySelector('.newsletter-subscribe-btn');
    
    if (emailInput && subscribeBtn) {
        // Enter 키 이벤트
        emailInput.addEventListener('keydown', function(event) {
            if (event.key === 'Enter') {
                handleNewsletterSubscription();
            }
        });
        
        // 구독 버튼 클릭 이벤트
        subscribeBtn.addEventListener('click', handleNewsletterSubscription);
        
        // 실시간 이메일 유효성 검사
        emailInput.addEventListener('input', function() {
            const email = this.value.trim();
            if (email && !isValidEmail(email)) {
                this.style.borderColor = '#ef4444';
            } else {
                this.style.borderColor = '';
            }
        });
    }
}

// 푸터 통계 애니메이션
function animateFooterStats() {
    const statCards = document.querySelectorAll('.footer-stat-card');
    
    // Intersection Observer로 스크롤 시 애니메이션
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.animation = 'slideInUp 0.6s ease-out';
            }
        });
    });
    
    statCards.forEach(card => {
        observer.observe(card);
    });
}

// 푸터 초기화
function initializeFooter() {
    setupNewsletterEvents();
    setupQRCodeEffect();
    setupFooterLinkAnimations();
    animateFooterStats();
}

// 페이지 로드 시 푸터 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeFooter();
});

// 푸터 함수들을 전역 객체에 등록
window.CheForest = window.CheForest || {};
window.CheForest.footer = {
    handleNewsletterSubscription,
    handleSocialClick,
    handleAppDownload,
    showNotification,
    initializeFooter
};