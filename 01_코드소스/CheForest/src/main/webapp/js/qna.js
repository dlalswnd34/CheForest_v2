// Q&A 페이지 JavaScript
const csrfToken = document.querySelector("meta[name='_csrf']")?.content;
const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.content;

document.addEventListener('DOMContentLoaded', function() {
    // 전역 변수
    const searchInput = document.getElementById('searchInput');
    const searchBadge = document.getElementById('searchBadge');
    const searchText = document.getElementById('searchText');
    const faqList = document.getElementById('faqList');
    const noResults = document.getElementById('noResults');
    const faqCount = document.querySelector('.faq-count');
    const contactForm = document.getElementById('contactForm');

    // 탭 전환 기능
    initTabNavigation();

    // FAQ 아코디언 기능
    initFAQAccordion();

    // 검색 기능
    initSearch();

    // 도움됨/안도움 투표 기능
    initHelpfulVoting();

    // 문의하기 폼 기능
    initContactForm();

    // 브랜드 버튼 클릭 이벤트
    initBrandButtons();

    // 탭 전환 초기화
    function initTabNavigation() {
        const tabButtons = document.querySelectorAll('.tab-btn');
        const tabContents = document.querySelectorAll('.tab-content');

        tabButtons.forEach(button => {
            button.addEventListener('click', function() {
                const targetTab = this.getAttribute('data-tab');

                // 모든 탭 버튼 비활성화
                tabButtons.forEach(btn => btn.classList.remove('active'));
                // 클릭된 탭 버튼 활성화
                this.classList.add('active');

                // 모든 탭 컨텐츠 숨기기
                tabContents.forEach(content => content.classList.remove('active'));
                // 해당 탭 컨텐츠 보이기
                document.getElementById(targetTab + '-tab').classList.add('active');
            });
        });
    }

    // FAQ 아코디언 초기화
    function initFAQAccordion() {
        const faqItems = document.querySelectorAll('.faq-item');

        faqItems.forEach(item => {
            const question = item.querySelector('.faq-question');

            question.addEventListener('click', function() {
                const isOpen = item.classList.contains('open');

                // 모든 FAQ 아이템 닫기
                faqItems.forEach(faq => faq.classList.remove('open'));

                // 클릭된 아이템이 닫혀있었다면 열기
                if (!isOpen) {
                    item.classList.add('open');
                }
            });
        });
    }

    // 검색 기능 초기화
    function initSearch() {
        if (!searchInput) return;

        searchInput.addEventListener('input', function() {
            const query = this.value.toLowerCase().trim();
            performSearch(query);
        });
    }

    // 검색 수행
    function performSearch(query) {
        const faqItems = document.querySelectorAll('.faq-item');
        let visibleCount = 0;

        faqItems.forEach(item => {
            const question = item.getAttribute('data-question').toLowerCase();
            const answer = item.getAttribute('data-answer').toLowerCase();

            const isMatch = question.includes(query) || answer.includes(query);

            if (query === '' || isMatch) {
                item.style.display = 'block';
                visibleCount++;
            } else {
                item.style.display = 'none';
                item.classList.remove('open'); // 숨겨지는 아이템은 닫기
            }
        });

        // 검색 결과 표시 업데이트
        updateSearchResults(query, visibleCount);
    }

    // 검색 결과 표시 업데이트
    function updateSearchResults(query, visibleCount) {
        if (query === '') {
            // 검색어가 없으면 배지 숨기기
            if (searchBadge) searchBadge.style.display = 'none';
            if (noResults) noResults.style.display = 'none';
            if (faqList) faqList.style.display = 'block';
            if (faqCount) faqCount.textContent = '(8)';
        } else {
            // 검색어가 있으면 배지 보이기
            if (searchBadge) {
                searchBadge.style.display = 'block';
                if (searchText) searchText.textContent = `"${query}"`;
            }
            if (faqCount) faqCount.textContent = `(${visibleCount})`;

            if (visibleCount === 0) {
                // 검색 결과가 없으면
                if (faqList) faqList.style.display = 'none';
                if (noResults) noResults.style.display = 'block';
            } else {
                // 검색 결과가 있으면
                if (faqList) faqList.style.display = 'block';
                if (noResults) noResults.style.display = 'none';
            }
        }
    }

    // 검색 초기화
    window.resetSearch = function() {
        if (searchInput) {
            searchInput.value = '';
            performSearch('');
        }
    };

    // 도움됨/안도움 투표 초기화
    function initHelpfulVoting() {
        const helpfulButtons = document.querySelectorAll('.helpful-btn');

        helpfulButtons.forEach(button => {
            button.addEventListener('click', function() {
                const countSpan = this.querySelector('.helpful-count');
                const isYes = this.classList.contains('helpful-yes');
                let currentCount = parseInt(countSpan.textContent);

                // 투표 애니메이션과 카운트 증가
                this.style.transform = 'scale(0.95)';
                setTimeout(() => {
                    this.style.transform = 'scale(1)';
                }, 100);

                // 카운트 증가
                countSpan.textContent = currentCount + 1;

                // 버튼 비활성화 (중복 투표 방지)
                this.style.opacity = '0.7';
                this.style.pointerEvents = 'none';

                // 감사 메시지 표시
                showToastMessage(isYes ? '도움이 되었다는 의견을 보내주셔서 감사합니다!' : '의견을 보내주셔서 감사합니다. 더 나은 답변을 위해 노력하겠습니다.');
            });
        });
    }
    //  문의하기 폼
    function initContactForm() {
        if (!contactForm) return;

        const subjectInput = document.getElementById('subject');
        const messageInput = document.getElementById('message');
        const submitButton = contactForm.querySelector('.btn-submit');

        // 실시간 폼 검증
        function validateForm() {
            const isValid = subjectInput.value.trim() !== '' && messageInput.value.trim() !== '';
            submitButton.disabled = !isValid;
        }

        if (subjectInput) subjectInput.addEventListener('input', validateForm);
        if (messageInput) messageInput.addEventListener('input', validateForm);

        // 초기 검증
        validateForm();

        // 폼 제출 처리
        contactForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const subject = subjectInput.value.trim();
            const message = messageInput.value.trim();

            if (!subject || !message) {
                showToastMessage('모든 필수 항목을 입력해주세요.', 'error');
                return;
            }

            // 로그인 여부 확인
            let memberIdx;
            try {
                const res = await fetch('/auth/me'); // 로그인 사용자 정보 요청
                if (!res.ok) throw new Error('인증되지 않음');

                const userInfo = await res.json();
                memberIdx = userInfo.memberIdx;

                if (!memberIdx) throw new Error('memberIdx 없음');
            } catch (error) {
                console.error('로그인 확인 실패:', error);
                alert('로그인이 필요한 서비스입니다.')
                // showToastMessage('로그인이 필요한 기능입니다.', 'error');
                return;
            }

            // 제출 버튼 로딩 상태
            submitButton.disabled = true;
            submitButton.innerHTML = `
            <svg class="icon animate-spin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 12a9 9 0 11-6.219-8.56"/>
            </svg>
            전송 중...
        `;

            // 서버로 문의 전송
            fetch('/inquiries/ask', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    subject: subject,
                    message: message,
                    memberIdx: memberIdx // 로그인한 회원의 기본키
                })
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('서버 오류 발생');
                    }
                    return response.text();
                })
                .then(data => {
                    console.log('서버 응답:', data);

                    showToastMessage('문의가 성공적으로 접수되었습니다! 마이페이지에서 확인하세요.', 'success');

                    // 폼 초기화
                    contactForm.reset();

                    // 제출 버튼 원상복구
                    submitButton.disabled = false;
                    submitButton.innerHTML = `
                    <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M22 2L11 13"/>
                        <polygon points="22,2 15,22 11,13 2,9"/>
                    </svg>
                    문의 전송
                `;

                    validateForm();
                })
                .catch(error => {
                    console.error('전송 실패:', error);
                    showToastMessage('문의 전송에 실패했습니다. 잠시 후 다시 시도해주세요.', 'error');

                    // 버튼 상태 복구
                    submitButton.disabled = false;
                    submitButton.innerHTML = `
                    <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M22 2L11 13"/>
                        <polygon points="22,2 15,22 11,13 2,9"/>
                    </svg>
                    문의 전송
                `;
                });
        });
    }


    // 브랜드 버튼 초기화
    function initBrandButtons() {
        // 등급 시스템/성장 여정 버튼은 전역 함수로 처리
        // (헤더의 네비게이션 함수와 연동)
    }

    // 토스트 메시지 표시
    function showToastMessage(message, type = 'info') {
        // 기존 토스트 제거
        const existingToast = document.querySelector('.toast-message');
        if (existingToast) {
            existingToast.remove();
        }

        // 토스트 생성
        const toast = document.createElement('div');
        toast.className = `toast-message toast-${type}`;
        toast.textContent = message;

        // 스타일 적용
        Object.assign(toast.style, {
            position: 'fixed',
            top: '20px',
            right: '20px',
            backgroundColor: type === 'success' ? '#10b981' : type === 'error' ? '#ef4444' : '#3b82f6',
            color: 'white',
            padding: '12px 16px',
            borderRadius: '8px',
            boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
            zIndex: '10000',
            opacity: '0',
            transform: 'translateY(-10px)',
            transition: 'all 0.3s ease',
            maxWidth: '400px',
            lineHeight: '1.4'
        });

        document.body.appendChild(toast);

        // 애니메이션으로 표시
        setTimeout(() => {
            toast.style.opacity = '1';
            toast.style.transform = 'translateY(0)';
        }, 100);

        // 3초 후 제거
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateY(-10px)';
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 300);
        }, 3000);
    }
});

// 등급 페이지로 이동 (전역 함수)
function navigateToGrade() {

}

// CSS 애니메이션 추가
const style = document.createElement('style');
style.textContent = `
    .animate-spin {
        animation: spin 1s linear infinite;
    }
    
    @keyframes spin {
        from {
            transform: rotate(0deg);
        }
        to {
            transform: rotate(360deg);
        }
    }
`;
document.head.appendChild(style);