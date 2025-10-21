// CheForest 관리자 - 모든 탭 완전 구현 JavaScript

// 시큐리티 보안토큰
const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

// 전역 변수
let currentTab = 'dashboard';
let currentUserTab = 'all';
let currentRecipeTab = 'recipes';
let currentPostTab = 'posts';
let currentSettingsTab = 'site';

// 차트 인스턴스
let memberStatusChart = null;
let monthlyActivityChart = null;

// 샘플 데이터
const samplePosts = [
    {
        id: 1,
        title: '우리집 비빔밥 레시피',
        content: '엄마가 해주시던 정성 가득한 비빔밥, 나물 하나하나 정성스럽게 무쳐보세요!',
        author: '홈쿡마미',
        authorLevel: '나무',
        category: '한식',
        image: 'https://images.unsplash.com/photo-1718777791262-c66d11baaa3b?w=300&h=200&fit=crop',
        views: 8234,
        likes: 456,
        comments: 89,
        status: 'published',
        createdAt: '2024-12-15T14:30:00',
        isPinned: true,
        isHot: true,
        tags: ['비빔밥', '나물', '집밥', '건강식'],
        cookTime: '40분',
        difficulty: '보통',
        serves: '4인분'
    },
    {
        id: 2,
        title: '매콤한 김치 볶음밥',
        content: '김치가 시어질 때 만드는 최고의 요리! 스팸과 함께 볶으면 더욱 맛있어요',
        author: '김치러버',
        authorLevel: '새싹',
        category: '한식',
        image: 'https://images.unsplash.com/photo-1708388466726-54ff913ad930?w=300&h=200&fit=crop',
        views: 5432,
        likes: 298,
        comments: 67,
        status: 'published',
        createdAt: '2024-12-12T18:45:00',
        isHot: true,
        tags: ['김치볶음밥', '간편요리', '집밥'],
        cookTime: '15분',
        difficulty: '쉬움',
        serves: '2인분'
    },
    {
        id: 3,
        title: '집에서 만드는 라멘',
        content: '진한 돈코츠 육수부터 토핑까지, 집에서도 맛집 라멘을 만들 수 있어요!',
        author: '라멘마니아',
        authorLevel: '나무',
        category: '일식',
        image: 'https://images.unsplash.com/photo-1637024698421-533d83c7b883?w=300&h=200&fit=crop',
        views: 12456,
        likes: 789,
        comments: 156,
        status: 'published',
        createdAt: '2024-12-10T20:15:00',
        tags: ['라멘', '일식', '육수', '면요리'],
        cookTime: '2시간',
        difficulty: '어려움',
        serves: '2인분'
    },
    {
        id: 4,
        title: '이상한 요리 실험...',
        content: '아무거나 섞어서 만든 요리인데 맛있을까요? 잘 모르겠어요 ㅋㅋ',
        author: '요리초보99',
        authorLevel: '씨앗',
        category: '양식',
        image: 'https://images.unsplash.com/photo-1596458397260-255807e979f1?w=300&h=200&fit=crop',
        views: 234,
        likes: 12,
        comments: 45,
        status: 'reported',
        createdAt: '2024-12-08T16:20:00',
        reports: 8,
        tags: ['실험', '양식', '초보'],
        cookTime: '30분',
        difficulty: '보통',
        serves: '1인분'
    },
    {
        id: 5,
        title: '중국식 볶음밥 레시피',
        content: '중국집에서 먹던 그 맛! 웍헤이가 살아있는 볶음밥 만드는 비법',
        author: '중화요리사',
        authorLevel: '뿌리',
        category: '중식',
        image: 'https://images.unsplash.com/photo-1723691802798-fa6efc67b2c9?w=300&h=200&fit=crop',
        views: 6789,
        likes: 387,
        comments: 92,
        status: 'published',
        createdAt: '2024-12-05T12:10:00',
        tags: ['볶음밥', '중식', '웍헤이', '간편요리'],
        cookTime: '20분',
        difficulty: '쉬움',
        serves: '2인분'
    },
    {
        id: 6,
        title: '촉촉한 홈베이킹 케이크',
        content: '베이킹 초보도 실패 없이! 버터크림과 딸기로 예쁘게 데코레이션해보세요',
        author: '베이킹러버',
        authorLevel: '씨앗',
        category: '디저트',
        image: 'https://images.unsplash.com/photo-1613323885553-4b069992362d?w=300&h=200&fit=crop',
        views: 4321,
        likes: 234,
        comments: 45,
        status: 'private',
        createdAt: '2024-12-03T14:45:00',
        tags: ['케이크', '베이킹', '디저트', '홈베이킹'],
        cookTime: '1시간',
        difficulty: '보통',
        serves: '8인분'
    }
];

const sampleEvents = [
    {
        id: 1,
        title: "2025 CheForest 레시피 공모전",
        description: "창의적이고 건강한 레시피로 도전하세요! 우승자에게는 100만원 상금과 CheForest 명예의 전당에 이름이 등록됩니다.",
        image: "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=300&h=200&fit=crop",
        startDate: "2025-04-22",
        endDate: "2025-10-21",
        time: "24시간 접수",
        location: "온라인",
        participants: 1247,
        maxParticipants: 5000,
        prize: "총 상금 300만원",
        status: "active",
        category: "공모전",
        tags: ["레시피", "공모전", "상금"],
        createdAt: "2024-12-01T10:00:00",
        views: 15420,
        applications: 1247,
        isPromoted: true,
        budget: 3000000
    },
    {
        id: 2,
        title: "나랑 어울리는 요리 스타일 테스트",
        description: "나와 어울리는 요리를 찾아보세요! 간단한 질문으로 당신만의 요리 스타일을 발견해보세요",
        image: "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=300&h=200&fit=crop",
        startDate: "2024-12-01",
        endDate: "2025-12-31",
        time: "언제든지",
        location: "온라인",
        participants: 3456,
        prize: "개인 맞춤 레시피 추천",
        status: "active",
        category: "테스트",
        tags: ["성향테스트", "맞춤추천", "재미"],
        createdAt: "2024-11-15T09:00:00",
        views: 8930,
        applications: 3456,
        budget: 0
    },
    {
        id: 3,
        title: "한식 마스터 챌린지",
        description: "전통 한식부터 퓨전 한식까지! 한 달간 매주 새로운 한식 요리에 도전하고 포인트를 모아보세요.",
        image: "https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=300&h=200&fit=crop",
        startDate: "2025-02-01",
        endDate: "2025-02-28",
        time: "매주 월요일 챌린지 공개",
        location: "온라인",
        participants: 456,
        maxParticipants: 1000,
        prize: "우승자 한식 마스터 인증서",
        status: "draft",
        category: "이벤트",
        tags: ["한식", "챌린지", "월간이벤트"],
        createdAt: "2024-12-08T13:45:00",
        views: 1230,
        applications: 0,
        budget: 200000
    },
    {
        id: 4,
        title: "2024 CheForest 레시피 공모전",
        description: "2024년의 마지막을 장식한 성공적인 레시피 공모전! 총 892명이 참가하여 다양하고 창의적인 레시피를 선보였습니다.",
        image: "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=300&h=200&fit=crop",
        startDate: "2024-09-01",
        endDate: "2024-11-30",
        time: "3개월간 진행",
        location: "온라인",
        participants: 892,
        maxParticipants: 1000,
        prize: "총 상금 250만원",
        status: "ended",
        category: "공모전",
        tags: ["레시피", "공모전", "2024년", "완료"],
        createdAt: "2024-08-15T10:00:00",
        views: 12850,
        applications: 892,
        budget: 2500000
    }
];

const InquiryManager = {
    currentPage: 1,
    pageSize: 10,
    latestInquiries: [],

    loadInquiries: function (page = 1) {
        this.showLoading();

        // ① 검색어와 상태 가져오기
        const keyword = document.getElementById("inquiry-search")?.value || "";
        const status = document.getElementById("inquiry-status")?.value || "all";

        // ② API URL 구성
        const queryParams = new URLSearchParams({
            page: page - 1,
            size: this.pageSize,
            sort: "createdAt,desc",
            keyword: keyword,
            status: status
        });

        fetch(`/api/searchInquiries?${queryParams.toString()}`)
            .then(res => res.json())
            .then(data => {
                this.currentPage = page;
                this.latestInquiries = data.content;
                this.renderInquiries(data.content);
                InquiryManager.renderPagination(data.totalPages);
            })
            .catch(error => {
                console.error("문의사항 불러오기 실패:", error);
                this.showError();
            });
    },
    showLoading: function () {
        const container = document.getElementById("inquiries-management-list");
        if (container) {
            container.innerHTML = `<p style="padding: 20px; text-align: center; color: #94a3b8; font-size: 30px">불러오는 중...</p>`;
        }
    },

    showError: function () {
        const container = document.getElementById("inquiries-management-list");
        if (container) {
            container.innerHTML = `<p style="padding: 20px; text-align: center; color: red; font-size: 30px">데이터를 불러오는 데 실패했습니다.</p>`;
        }
    },
    renderInquiries: function (inquiries) {
        const container = document.getElementById("inquiries-management-list");
        if (!container) return;

        if (inquiries.length === 0) {
            container.innerHTML = "<p>문의 내역이 없습니다.</p>";
            return;
        }

        const contentHTML = inquiries.map(inquiry => `
            <div class="inquiry-card">
                <div class="inquiry-header">
                    <div class="inquiry-main-content">
                        <div class="inquiry-badges">
                            <span class="recipe-badge ${inquiry.answerStatus}">
                            <i data-lucide="${inquiry.answerStatus === '대기중' ? 'clock' : 'check-circle-2'}" style="width: 14px; height: 14px;"></i>
                            ${inquiry.answerStatus === '답변완료' ? '답변 완료' : '대기 중'}</span> 
                            <span style="font-size: 10px; color: #64748b;">#${inquiry.inquiryId}</span>
                        </div>
                        <h4 class="inquiry-main-title">${inquiry.title}</h4>
                        <p class="inquiry-description">${inquiry.questionContent}</p>
                        <div class="inquiry-author-info">
                            <span>👤 ${inquiry.nickname}</span>
                            <span>🕒 ${AdminAllTabs.formatTimeAgo(inquiry.createdAt)}</span>
                        </div>
                    </div>
                    <div style="display: flex; gap: 8px;">
                        <button class="event-action-btn primary" onclick="AdminAllTabs.openInquiryAnswer(${inquiry.inquiryId})">
                            <i data-lucide="${inquiry.answerContent ? 'edit-3' : 'reply'}" style="width: 14px; height: 14px;"></i>
                            ${inquiry.answerContent ? '수정' : '답변'}
                        </button>
                        <div style="position: relative;">
                          <button class="event-action-btn toggle-menu-btn" data-inquiry-id="${inquiry.inquiryId}">
                              <i data-lucide="more-vertical" style="width: 14px; height: 14px;"></i>
                          </button>
                      
                          <div class="inquiry-actions-menu" id="inquiry-actions-${inquiry.inquiryId}" style="display: none;">
                              <div class="menu-title">문의 관리</div>
                              <div class="menu-item" onclick="InquiryManager.registerAsFAQ(${inquiry.inquiryId})">공개 FAQ로 등록/해제</div>
                              <div class="menu-item delete" onclick="InquiryManager.deleteInquiry(${inquiry.inquiryId})">삭제</div>
                          </div>
                      </div>
                      
                    </div>
                </div>
                ${inquiry.answerContent ? `
                    <div class="inquiry-answer-section">
                        <div class="inquiry-answer-header">
                            <div class="inquiry-answer-meta">
                                <i data-lucide="check-circle-2" style="width: 14px; height: 14px;"></i>
                                <span>관리자 답변</span>
                                <span>(${AdminAllTabs.formatTimeAgo(inquiry.answerAt)})</span>
                            </div>
                        </div>
                        <div class="inquiry-answer-content">${inquiry.answerContent}</div>
                    </div>
                ` : ''}
            </div>
        `).join('');

        const paginationHTML = `
        <div id="paginationInquiry" class="pagination-container"></div>`;

        container.innerHTML = contentHTML + paginationHTML;

        lucide.createIcons();
        // 토글 이벤트 실행
        this.bindInquiryMenuEvents();
    },

    renderPagination: function (totalPages) {
        const pagination = document.getElementById("paginationInquiry");
        if (!pagination) return;

        let buttons = "";
        for (let i = 1; i <= totalPages; i++) {
            buttons += `<button class="${i === this.currentPage ? 'active' : ''}" onclick="InquiryManager.loadInquiries(${i})">${i}</button>`;
        }
        pagination.innerHTML = buttons;
    },
    bindInquiryMenuEvents: function () {
        // 메뉴 토글 버튼 이벤트
        document.querySelectorAll('.toggle-menu-btn').forEach(button => {
            button.addEventListener('click', (event) => {
                event.stopPropagation();
                const inquiryId = button.getAttribute('data-inquiry-id');
                const menu = document.getElementById(`inquiry-actions-${inquiryId}`);

                document.querySelectorAll('.inquiry-actions-menu').forEach(m => {
                    if (m !== menu) m.style.display = 'none';
                });

                menu.style.display = (menu.style.display === 'block') ? 'none' : 'block';
            });
        });

        // 외부 클릭 시 메뉴 닫기
        document.addEventListener('click', () => {
            document.querySelectorAll('.inquiry-actions-menu').forEach(menu => {
                menu.style.display = 'none';
            });
        });
    },
    // FAQ 등록
    registerAsFAQ: async function (inquiryId){
        if (!inquiryId) {
            this.showNotification('문의 ID를 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }
        const button = document.querySelector(`button.toggle-menu-btn[data-inquiry-id="${inquiryId}"]`);
        const originalHTML = button.innerHTML;

        try {
            // 👉 로딩 상태 표시
            button.disabled = true;
            button.innerHTML = `<span class="loading-spinner" style="width:14px; height:14px;"></span>`;

            const response = await fetch('/inquiries/FAQ', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    inquiryId: inquiryId,
                })
            });

            if (response.ok) {
                const message = await response.text();
                AdminAllTabs.showNotification(message, 'success');
            } else {
                AdminAllTabs.showNotification('FAQ 등록에 실패했습니다.', 'error');
            }
        } catch (error) {
            console.error("에러 발생:", error);
            this.showNotification('서버 오류가 발생했습니다. 관리자에게 문의하세요', 'error');
        }  finally {
            // 👉 로딩 끝 - 버튼 원상복구
            button.disabled = false;
            button.innerHTML = originalHTML;
            InquiryManager.loadInquiries();
            PendingInquiryManager.fetchInquiriesAllCount();
            PendingInquiryManager.fetchPendingCount();
            PendingInquiryManager.fetchAnsweredCount();
            PendingInquiryManager.fetchTodayCount();
            PendingInquiryManager.fetchPendingInquiries();
        }

    },
    // 문의사항 삭제하기
    deleteInquiry: async function (inquiryId){
        if (!inquiryId) {
            this.showNotification('문의 ID를 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }
        const button = document.querySelector(`button.toggle-menu-btn[data-inquiry-id="${inquiryId}"]`);
        const originalHTML = button.innerHTML;

        try {
            // 👉 로딩 상태 표시
            button.disabled = true;
            button.innerHTML = `<span class="loading-spinner" style="width:14px; height:14px;"></span>`;

            const response = await fetch('/inquiries/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    inquiryId: inquiryId,
                })
            });

            if (response.ok) {
                const message = await response.text();
                AdminAllTabs.showNotification(message, 'success');
            } else {
                AdminAllTabs.showNotification('문의사항 삭제에 실패했습니다..', 'error');
            }
        } catch (error) {
            console.error("에러 발생:", error);
            this.showNotification('서버 오류가 발생했습니다. 관리자에게 문의하세요', 'error');
        }finally {
            // 👉 로딩 끝 - 버튼 원상복구
            button.disabled = false;
            button.innerHTML = originalHTML;
            InquiryManager.loadInquiries();
            PendingInquiryManager.fetchInquiriesAllCount();
            PendingInquiryManager.fetchPendingCount();
            PendingInquiryManager.fetchAnsweredCount();
            PendingInquiryManager.fetchTodayCount();
            PendingInquiryManager.fetchPendingInquiries();
        }
    }

};
const MemberManager = {
    currentPage: 1,
    pageSize: 10,
    latestMemberList: [],

    loadAllMember: function (page = 1) {
        this.showLoading();

        //  검색어
        const keyword = document.getElementById("user-search")?.value || "";

        const queryParams = new URLSearchParams({
            page: page - 1,
            size: this.pageSize,
            sort: "insertTime,desc",
            keyword: keyword
        });

        fetch(`/api/allMember?${queryParams.toString()}`)
            .then(res => res.json())
            .then(data => {
                this.currentPage = page;
                this.latestMemberList = data.data;
                this.renderMember(data.data);
                this.renderAllPagination(data.totalPages);
            })
            .catch(error => {
                console.error("문의사항 불러오기 실패:", error);
                this.showError();
            });
    },
    loadOnlineMember: function (page = 1) {
        this.showLoading();

        fetch(`/admin/loggedInUserList`)
            .then(res => res.json())
            .then(data => {
                this.currentPage = 1;
                this.latestMemberList = data.loggedInUserList;
                this.renderOnlineMember(data.loggedInUserList);
            })
            .catch(error => {
                console.error("접속유저 불러오기 실패:", error);
                this.showError();
            });

    },
    loadSuspendedMember: function (page = 1) {
        this.showLoading();

        //  검색어
        const keyword = document.getElementById("user-search")?.value || "";

        const queryParams = new URLSearchParams({
            page: page - 1,
            size: this.pageSize,
            sort: "insertTime,desc",
            keyword: keyword
        });

        fetch(`/api/suspendedMember?${queryParams.toString()}`)
            .then(res => res.json())
            .then(data => {
                this.currentPage = page;
                this.latestMemberList = data.data;
                this.renderSuspendedMember(data.data);
                this.renderSuspendedPagination(data.totalPages);
            })
            .catch(error => {
                console.error("문의사항 불러오기 실패:", error);
                this.showError();
            });
    },

    showLoading: function () {
        const container = document.getElementById("table-container");
        if (container) {
            container.innerHTML = `<p style="padding: 20px; text-align: center; color: #94a3b8; font-size: 30px">불러오는 중...</p>`;
        }
    },
    showError: function () {
        const container = document.getElementById("table-container");
        if (container) {
            container.innerHTML = `<p style="padding: 20px; text-align: center; color: red; font-size: 30px">데이터를 불러오는 데 실패했습니다.</p>`;
        }
    },
    // 화면 생성
    renderMember: function (MemberList) {
        const container = document.getElementById("table-container");
        if (!container) return;

        if (MemberList.length === 0) {
            container.innerHTML = "<p>해당하는 회원이 없습니다.</p>";
            return;
        }

        const contentHTML1 = `<table class="data-table">
        <thead>
            <tr>
                <th>회원 정보</th>
                <th>등급/상태</th>
                <th>활동</th>
                <th>가입일</th>
                <th>최근 로그인</th>
                <th style="text-align: right;">관리</th>
            </tr>
        </thead>
        <tbody id="users-table-body">`;

        const contentHTML2 = MemberList.map(user => {
            const nickname = user.nickname ?? '알수없음';
            const profile = user.profile ?? '';
            const isOnline = user.isOnline ?? false;
            const email = user.email ?? '';
            const grade = user.grade ?? '없음';
            const status = user.status ?? '정상';
            const boardCount = user.boardCount ?? 0;
            const boardReviewCount = user.boardReviewCount ?? 0;
            const insertTime = user.insertTime ?? '';
            const lastLoginTime = user.lastLoginTime ?? '';
            const memberIdx = user.memberIdx ?? 0;

            const avatarHTML = profile
                ? `<img src="${profile}" alt="${nickname}">`
                : `<span>${String(nickname).charAt(0)}</span>`;

            const statusIcon = AdminAllTabs.getStatusIconName ? AdminAllTabs.getStatusIconName(status) : 'help-circle';

            return `
            <tr> 
                <td>
                    <div class="user-info">
                        <div class="user-avatar">
                            <div class="avatar">
                                ${avatarHTML}
                            </div>
                            ${isOnline ? '<div class="online-indicator"></div>' : ''}
                        </div>
                        <div class="user-details">
                            <h4>${nickname} ${isOnline ? '<span class="online-badge">온라인</span>' : ''}</h4>
                            <div class="user-email">
                                ${email ? '<i data-lucide="mail" style="width: 12px; height: 12px;"></i>' : ''}
                                ${email}
                            </div>
                        </div>
                    </div>
                </td>
                <td>
                    <div style="display: flex; flex-direction: column; gap: 4px;">
                        <div class="grade-badge grade-${grade}">${grade}</div>
                        <div class="status-badge status-${status}">
                            <i data-lucide="${statusIcon}" class="status-icon"></i>
                            ${status}
                        </div>
                    </div>
                </td>
                <td>
                    <div style="font-size: 12px;">
                        <div style="display: flex; align-items: center; gap: 4px; margin-bottom: 2px;">
                            <i data-lucide="file-text" style="width: 12px; height: 12px; color: #94a3b8;"></i>
                            ${boardCount} 게시글
                        </div>
                        <div style="display: flex; align-items: center; gap: 4px;">
                            <i data-lucide="message-circle" style="width: 12px; height: 12px; color: #94a3b8;"></i>
                            ${boardReviewCount} 댓글
                        </div>
                    </div>
                </td>
                <td>
                    <div style="font-size: 12px; color: #64748b;">
                        ${AdminAllTabs.formatDate ? AdminAllTabs.formatDate(insertTime) : ''}
                    </div>
                </td>
                <td>
                    <div style="font-size: 12px; color: #64748b;">
                        ${AdminAllTabs.formatDate ? AdminAllTabs.formatDate(lastLoginTime) : ''}
                        <div style="font-size: 10px; color: #94a3b8; margin-top: 2px;">
                            ${AdminAllTabs.formatTime ? AdminAllTabs.formatTime(lastLoginTime) : ''}
                        </div>
                    </div>
                </td>
                <td style="display: flex;">
                     <div style="position: relative; margin-left: auto;">
                          <button class="event-action-btn toggle-menu-btn" data-member-id="${memberIdx}">
                              <i data-lucide="more-vertical" style="width: 14px; height: 14px;"></i>
                          </button>
                      
                          <div class="inquiry-actions-menu" id="inquiry-actions-${memberIdx}" style="display: none;">
                              <div class="menu-title">회원 관리</div>
                              <div class="menu-item" onclick="MemberManager.applySuspension(${memberIdx})">제재하기</div>
                              <div class="menu-item delete" onclick="MemberManager.deleteUser(${memberIdx})">삭제</div>
                          </div>
                      </div>
                </td>
            </tr>
        `;
        }).join('');

        const contentHTML3 = `</tbody></table>`;
        const paginationHTML = `<div id="pagination" class="pagination-container"></div>`;

        container.innerHTML = contentHTML1 + contentHTML2 + contentHTML3 + paginationHTML;

        lucide.createIcons();

        this.bindMemberMenuEvents();
    },
    renderOnlineMember: function (MemberList) {
        const container = document.getElementById("table-container");
        if (!container) return;

        if (MemberList.length === 0) {
            container.innerHTML = "<p>해당하는 회원이 없습니다.</p>";
            return;
        }

        const contentHTML1 = `<table class="data-table">
        <thead>
            <tr>
                <th>회원 정보</th>
                <th>등급/상태</th>
                <th>가입일</th>
                <th>최근 로그인</th>
                <th style="text-align: right;">관리</th>
            </tr>
        </thead>
        <tbody id="users-table-body">`;

        const contentHTML2 = MemberList.map(user => {
            const nickname = user.nickname ?? '알수없음';
            const profile = user.profile ?? '';
            const isOnline = user.isOnline ?? '온라인';
            const email = user.email ?? '';
            const grade = user.grade ?? '없음';
            const status = user.status ?? '정상';
            const boardCount = user.boardCount ?? 0;
            const boardReviewCount = user.boardReviewCount ?? 0;
            const insertTime = user.insertTime ?? '';
            const lastLoginTime = user.lastLoginTime ?? '';
            const memberIdx = user.memberIdx ?? 0;

            const avatarHTML = profile
                ? `<img src="${profile}" alt="${nickname}">`
                : `<span>${String(nickname).charAt(0)}</span>`;

            const statusIcon = AdminAllTabs.getStatusIconName ? AdminAllTabs.getStatusIconName(status) : 'help-circle';

            return `
            <tr> 
                <td>
                    <div class="user-info">
                        <div class="user-avatar">
                            <div class="avatar">
                                ${avatarHTML}
                            </div>
                            ${isOnline ? '<div class="online-indicator"></div>' : ''}
                        </div>
                        <div class="user-details">
                            <h4>${nickname} ${isOnline ? '<span class="online-badge">온라인</span>' : ''}</h4>
                            <div class="user-email">
                                ${email ? '<i data-lucide="mail" style="width: 12px; height: 12px;"></i>' : ''}
                                ${email}
                            </div>
                        </div>
                    </div>
                </td>
                <td>
                    <div style="display: flex; flex-direction: column; gap: 4px;">
                        <div class="grade-badge grade-${grade}">${grade}</div>
                        <div class="status-badge status-${status}">
                            <i data-lucide="${statusIcon}" class="status-icon"></i>
                            ${status}
                        </div>
                    </div>
                </td>
                <td>
                    <div style="font-size: 12px; color: #64748b;">
                        ${AdminAllTabs.formatDate ? AdminAllTabs.formatDate(insertTime) : ''}
                    </div>
                </td>
                <td>
                    <div style="font-size: 12px; color: #64748b;">
                        ${AdminAllTabs.formatDate ? AdminAllTabs.formatDate(lastLoginTime) : ''}
                        <div style="font-size: 10px; color: #94a3b8; margin-top: 2px;">
                            ${AdminAllTabs.formatTime ? AdminAllTabs.formatTime(lastLoginTime) : ''}
                        </div>
                    </div>
                </td>
                <td style="display: flex;">
                     <div style="position: relative; margin-left: auto;">
                          <button class="event-action-btn toggle-menu-btn" data-member-id="${memberIdx}">
                              <i data-lucide="more-vertical" style="width: 14px; height: 14px;"></i>
                          </button>
                      
                          <div class="inquiry-actions-menu" id="inquiry-actions-${memberIdx}" style="display: none;">
                              <div class="menu-title">회원 관리</div>
                              <div class="menu-item" onclick="MemberManager.applyOnlineSuspension(${memberIdx})">제재하기</div>
                              <div class="menu-item delete" onclick="MemberManager.deleteOnlineUser(${memberIdx})">삭제</div>
                          </div>
                      </div>
                </td>
            </tr>
        `;
        }).join('');

        const contentHTML3 = `</tbody></table>`;
        const paginationHTML = `<div id="pagination" class="pagination-container"></div>`;

        container.innerHTML = contentHTML1 + contentHTML2 + contentHTML3 + paginationHTML;

        lucide.createIcons();
        this.bindMemberMenuEvents();
    },
    // [수정됨] renderSuspendedMember 함수 내부의 버튼이 변경되었습니다.
    renderSuspendedMember: function (MemberList) {
        const container = document.getElementById("table-container");
        if (!container) return;

        if (MemberList.length === 0) {
            container.innerHTML = "<p>해당하는 회원이 없습니다.</p>";
            return;
        }

        const contentHTML1 = `<table class="data-table">
        <thead>
            <tr>
                <th>회원 정보</th>
                <th>등급/상태</th>
                <th>활동</th>
                <th>가입일</th>
                <th>최근 로그인</th>
                <th style="text-align: right;">관리</th>
            </tr>
        </thead>
        <tbody id="users-table-body">`;

        const contentHTML2 = MemberList.map(user => {
            const nickname = user.nickname ?? '알수없음';
            const profile = user.profile ?? '';
            const isOnline = user.isOnline ?? false;
            const email = user.email ?? '';
            const grade = user.grade ?? '없음';
            const status = user.status ?? '정상';
            const boardCount = user.boardCount ?? 0;
            const boardReviewCount = user.boardReviewCount ?? 0;
            const insertTime = user.insertTime ?? '';
            const lastLoginTime = user.lastLoginTime ?? '';
            const memberIdx = user.memberIdx ?? 0;

            const avatarHTML = profile
                ? `<img src="${profile}" alt="${nickname}">`
                : `<span>${String(nickname).charAt(0)}</span>`;

            const statusIcon = AdminAllTabs.getStatusIconName ? AdminAllTabs.getStatusIconName(status) : 'help-circle';

            return `
            <tr> 
                <td>
                    <div class="user-info">
                        <div class="user-avatar">
                            <div class="avatar">
                                ${avatarHTML}
                            </div>
                            ${isOnline ? '<div class="online-indicator"></div>' : ''}
                        </div>
                        <div class="user-details">
                            <h4>${nickname} ${isOnline ? '<span class="online-badge">온라인</span>' : ''}</h4>
                            <div class="user-email">
                                ${email ? '<i data-lucide="mail" style="width: 12px; height: 12px;"></i>' : ''}
                                ${email}
                            </div>
                        </div>
                    </div>
                </td>
                <td>
                    <div style="display: flex; flex-direction: column; gap: 4px;">
                        <div class="grade-badge grade-${grade}">${grade}</div>
                        <div class="status-badge status-${status}">
                            <i data-lucide="${statusIcon}" class="status-icon"></i>
                            ${status}
                        </div>
                    </div>
                </td>
                <td>
                    <div style="font-size: 12px;">
                        <div style="display: flex; align-items: center; gap: 4px; margin-bottom: 2px;">
                            <i data-lucide="file-text" style="width: 12px; height: 12px; color: #94a3b8;"></i>
                            ${boardCount} 게시글
                        </div>
                        <div style="display: flex; align-items: center; gap: 4px;">
                            <i data-lucide="message-circle" style="width: 12px; height: 12px; color: #94a3b8;"></i>
                            ${boardReviewCount} 댓글
                        </div>
                    </div>
                </td>
                <td>
                    <div style="font-size: 12px; color: #64748b;">
                        ${AdminAllTabs.formatDate ? AdminAllTabs.formatDate(insertTime) : ''}
                    </div>
                </td>
                <td>
                    <div style="font-size: 12px; color: #64748b;">
                        ${AdminAllTabs.formatDate ? AdminAllTabs.formatDate(lastLoginTime) : ''}
                        <div style="font-size: 10px; color: #94a3b8; margin-top: 2px;">
                            ${AdminAllTabs.formatTime ? AdminAllTabs.formatTime(lastLoginTime) : ''}
                        </div>
                    </div>
                </td>
                <td style="display: flex;">
                     <div style="position: relative; margin-left: auto;">
                          <button class="event-action-btn toggle-menu-btn" data-member-id="${memberIdx}">
                              <i data-lucide="more-vertical" style="width: 14px; height: 14px;"></i>
                          </button>
                      
                          <div class="inquiry-actions-menu" id="inquiry-actions-${memberIdx}" style="display: none;">
                              <div class="menu-title">회원 관리</div>
                              <div class="menu-item" onclick="MemberManager.liftSuspension(${memberIdx})">제재 해제</div>
                              <div class="menu-item delete" onclick="MemberManager.deleteSuspendedUser(${memberIdx})">삭제</div>
                          </div>
                      </div>
                </td>
            </tr>
        `;
        }).join('');

        const contentHTML3 = `</tbody></table>`;
        const paginationHTML = `<div id="pagination" class="pagination-container"></div>`;

        container.innerHTML = contentHTML1 + contentHTML2 + contentHTML3 + paginationHTML;

        lucide.createIcons();
        this.bindMemberMenuEvents();
    },
    // 페이지네이션
    renderAllPagination: function (totalPages) {
        const pagination = document.getElementById("pagination");
        if (!pagination) return;

        let buttons = "";
        for (let i = 1; i <= totalPages; i++) {
            buttons += `<button class="${i === this.currentPage ? 'active' : ''}" onclick="MemberManager.loadAllMember(${i})">${i}</button>`;
        }
        pagination.innerHTML = buttons;
    },
    renderOnlinePagination: function (totalPages) {
        const pagination = document.getElementById("pagination");
        if (!pagination) return;

        let buttons = "";
        for (let i = 1; i <= totalPages; i++) {
            buttons += `<button class="${i === this.currentPage ? 'active' : ''}" onclick="MemberManager.loadOnlineMember(${i})">${i}</button>`;
        }
        pagination.innerHTML = buttons;
    },
    renderSuspendedPagination: function (totalPages) {
        const pagination = document.getElementById("pagination");
        if (!pagination) return;

        let buttons = "";
        for (let i = 1; i <= totalPages; i++) {
            buttons += `<button class="${i === this.currentPage ? 'active' : ''}" onclick="MemberManager.loadSuspendedMember(${i})">${i}</button>`;
        }
        pagination.innerHTML = buttons;
    },
    // 회원 제재하기
    applySuspension: async function (memberIdx){
        if (!memberIdx) {
            this.showNotification('해당 관리번호의 회원을 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }
        const button = document.querySelector(`button.toggle-menu-btn[data-member-id="${memberIdx}"]`);
        const originalHTML = button.innerHTML;

        try {
            // 👉 로딩 상태 표시
            button.disabled = true;
            button.innerHTML = `<span class="loading-spinner" style="width:14px; height:14px;"></span>`;

            const response = await fetch('/admin/member/applySuspension', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    memberIdx: memberIdx,
                })
            });

            if (response.ok) {
                const message = await response.text();
                AdminAllTabs.showNotification(message, 'success');
            } else {
                AdminAllTabs.showNotification('회원제재에 실패했습니다.', 'error');
            }
        } catch (error) {
            console.error("에러 발생:", error);
            this.showNotification('서버 오류가 발생했습니다. 관리자에게 문의하세요', 'error');
        }  finally {
            // 👉 로딩 끝 - 버튼 원상복구
            button.disabled = false;
            button.innerHTML = originalHTML;
            MemberManager.loadAllMember(MemberManager.currentPage);

        }

    },
    applyOnlineSuspension: async function (memberIdx){
        if (!memberIdx) {
            this.showNotification('해당 관리번호의 회원을 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }
        const button = document.querySelector(`button.toggle-menu-btn[data-member-id="${memberIdx}"]`);
        const originalHTML = button.innerHTML;

        try {
            // 👉 로딩 상태 표시
            button.disabled = true;
            button.innerHTML = `<span class="loading-spinner" style="width:14px; height:14px;"></span>`;

            const response = await fetch('/admin/member/applySuspension', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    memberIdx: memberIdx,
                })
            });

            if (response.ok) {
                const message = await response.text();
                AdminAllTabs.showNotification(message, 'success');
            } else {
                AdminAllTabs.showNotification('회원제재에 실패했습니다.', 'error');
            }
        } catch (error) {
            console.error("에러 발생:", error);
            this.showNotification('서버 오류가 발생했습니다. 관리자에게 문의하세요', 'error');
        }  finally {
            // 👉 로딩 끝 - 버튼 원상복구
            button.disabled = false;
            button.innerHTML = originalHTML;
            MemberManager.loadOnlineMember(MemberManager.currentPage);

        }

    },
    // [삭제됨] applySuspendedSuspension 함수는 이제 필요 없으므로 삭제했습니다.
    // [추가됨] '제재 해제'를 위한 새로운 함수입니다.
    liftSuspension: async function (memberIdx){
        if (!memberIdx) {
            AdminAllTabs.showNotification('해당 관리번호의 회원을 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }
        const button = document.querySelector(`button.toggle-menu-btn[data-member-id="${memberIdx}"]`);
        const originalHTML = button.innerHTML;

        try {
            button.disabled = true;
            button.innerHTML = `<span class="loading-spinner" style="width:14px; height:14px;"></span>`;

            const response = await fetch('/admin/member/applySuspension', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    memberIdx: memberIdx,
                })
            });

            if (response.ok) {
                const message = await response.text();
                AdminAllTabs.showNotification(message, 'success');
            } else {
                AdminAllTabs.showNotification('회원 제재 해제에 실패했습니다.', 'error');
            }
        } catch (error) {
            console.error("에러 발생:", error);
            this.showNotification('서버 오류가 발생했습니다. 관리자에게 문의하세요', 'error');
        }  finally {
            button.disabled = false;
            button.innerHTML = originalHTML;
            MemberManager.loadSuspendedMember(MemberManager.currentPage);
        }
    },
    // 회원 삭제
    deleteUser: async function (memberIdx){
        if (!memberIdx) {
            this.showNotification('해당 관리번호의 회원을 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }
        const button = document.querySelector(`button.toggle-menu-btn[data-member-id="${memberIdx}"]`);
        const originalHTML = button.innerHTML;

        try {
            // 👉 로딩 상태 표시
            button.disabled = true;
            button.innerHTML = `<span class="loading-spinner" style="width:14px; height:14px;"></span>`;

            const response = await fetch('/admin/member/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    memberIdx: memberIdx
                })
            });

            if (response.ok) {
                const message = await response.text();
                AdminAllTabs.showNotification(message, 'success');
            } else {
                AdminAllTabs.showNotification('회원 삭제에 실패했습니다.', 'error');
            }
        } catch (error) {
            console.error("에러 발생:", error);
            this.showNotification('서버 오류가 발생했습니다. 관리자에게 문의하세요', 'error');
        }  finally {
            // 👉 로딩 끝 - 버튼 원상복구
            button.disabled = false;
            button.innerHTML = originalHTML;
            MemberManager.loadAllMember(MemberManager.currentPage);
        }

    },
    deleteOnlineUser: async function (memberIdx){
        if (!memberIdx) {
            this.showNotification('해당 관리번호의 회원을 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }
        const button = document.querySelector(`button.toggle-menu-btn[data-member-id="${memberIdx}"]`);
        const originalHTML = button.innerHTML;

        try {
            // 👉 로딩 상태 표시
            button.disabled = true;
            button.innerHTML = `<span class="loading-spinner" style="width:14px; height:14px;"></span>`;

            const response = await fetch('/admin/member/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    memberIdx: memberIdx
                })
            });

            if (response.ok) {
                const message = await response.text();
                AdminAllTabs.showNotification(message, 'success');
            } else {
                AdminAllTabs.showNotification('회원 삭제에 실패했습니다.', 'error');
            }
        } catch (error) {
            console.error("에러 발생:", error);
            this.showNotification('서버 오류가 발생했습니다. 관리자에게 문의하세요', 'error');
        }  finally {
            // 👉 로딩 끝 - 버튼 원상복구
            button.disabled = false;
            button.innerHTML = originalHTML;
            MemberManager.loadOnlineMember(MemberManager.currentPage);
        }

    },
    deleteSuspendedUser: async function (memberIdx){
        if (!memberIdx) {
            this.showNotification('해당 관리번호의 회원을 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }
        const button = document.querySelector(`button.toggle-menu-btn[data-member-id="${memberIdx}"]`);
        const originalHTML = button.innerHTML;

        try {
            // 👉 로딩 상태 표시
            button.disabled = true;
            button.innerHTML = `<span class="loading-spinner" style="width:14px; height:14px;"></span>`;

            const response = await fetch('/admin/member/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    memberIdx: memberIdx
                })
            });

            if (response.ok) {
                const message = await response.text();
                AdminAllTabs.showNotification(message, 'success');
            } else {
                AdminAllTabs.showNotification('회원 삭제에 실패했습니다.', 'error');
            }
        } catch (error) {
            console.error("에러 발생:", error);
            this.showNotification('서버 오류가 발생했습니다. 관리자에게 문의하세요', 'error');
        }  finally {
            // 👉 로딩 끝 - 버튼 원상복구
            button.disabled = false;
            button.innerHTML = originalHTML;
            MemberManager.loadSuspendedMember(MemberManager.currentPage);
        }

    },
    // 버튼 동작
    bindMemberMenuEvents: function () {
        // 메뉴 토글 버튼 이벤트
        document.querySelectorAll('.toggle-menu-btn').forEach(button => {
            button.addEventListener('click', (event) => {
                event.stopPropagation();
                const memberIdx = button.getAttribute('data-member-id');
                const menu = document.getElementById(`inquiry-actions-${memberIdx}`);

                document.querySelectorAll('.inquiry-actions-menu').forEach(m => {
                    if (m !== menu) m.style.display = 'none';
                });

                menu.style.display = (menu.style.display === 'block') ? 'none' : 'block';
            });
        });

        // 외부 클릭 시 메뉴 닫기
        document.addEventListener('click', () => {
            document.querySelectorAll('.inquiry-actions-menu').forEach(menu => {
                menu.style.display = 'none';
            });
        });
    }
};
const RecipeManager = {
    currentPage: 1,
    pageSize: 10,
    latestRecipes: [],

    loadRecipes: function (page = 1) {
        this.showLoading();

        // ① 검색어와 상태 가져오기
        const keyword = document.getElementById("recipe-search")?.value || "";
        const categoryKr = document.getElementById("recipe-category")?.value || "";
        const searchType = document.getElementById("recipe-searchType")?.value || "";


        // ② API URL 구성
        const queryParams = new URLSearchParams({
            page: page - 1,
            size: this.pageSize,
            keyword: keyword,
            categoryKr: categoryKr,
            searchType: searchType
        });

        fetch(`/admin/getRecipes?${queryParams.toString()}`)
            .then(res => res.json())
            .then(data => {
                this.currentPage = page;
                this.latestRecipes = data.data;
                this.renderRecipes(data.data);
                this.renderPagination(data.totalPages);
            })
            .catch(error => {
                console.error("레시피 불러오기 실패:", error);
                this.showError();
            });
    },
    showLoading: function () {
        const container = document.getElementById("recipes-grid");
        if (container) {
            container.innerHTML = `<p style="padding: 20px; text-align: center; color: #94a3b8; font-size: 30px">불러오는 중...</p>`;
        }
    },
    showError: function () {
        const container = document.getElementById("recipes-grid");
        if (container) {
            container.innerHTML = `<p style="padding: 20px; text-align: center; color: red; font-size: 30px">데이터를 불러오는 데 실패했습니다.</p>`;
        }
    },
    renderRecipes: function (Recipes) {
        const container = document.getElementById("recipes-grid");
        if (!container) return;

        if (Recipes.length === 0) {
            container.innerHTML = "<p>해당하는 레시피가 없습니다.</p>";
            return;
        }

        const contentHTML = Recipes.map(recipe => {
            const shortDescription = recipe.instructionKr.length > 100
                ? recipe.instructionKr.slice(0, 100) + "..."
                : recipe.instructionKr;

            return `
        <div class="post-management-item">
            <img src="${recipe.thumbnail}" alt="${recipe.titleKr}" class="post-image">
            <div class="post-details">
                <h4 class="post-main-title">${recipe.titleKr}</h4>
                <p class="post-description">${shortDescription}</p>
                <div class="post-badges">
                    <span class="recipe-badge category">${recipe.categoryKr}</span>
                </div>
                <div class="post-management-meta">
                    <span>👀 ${recipe.viewCount.toLocaleString()}</span>
                    <span>❤️ ${recipe.likeCount}</span>
                </div>
            </div>
            <div class="post-management-actions">
                <button class="event-action-btn viewDetailsBt" data-recipe-id="${recipe.recipeId}" onclick="RecipeManager.viewDetails('${recipe.recipeId}')">
                    <i data-lucide="eye" style="width: 14px; height: 14px;"></i>
                    상세보기
                </button>
                <button class="event-action-btn deleteRecipeBt" data-recipe-id="${recipe.recipeId}" onclick="RecipeManager.deleteRecipe('${recipe.recipeId}')">
                    <i data-lucide="trash" style="width: 14px; height: 14px;"></i>
                    삭제
                </button>
            </div>
        </div>
    `;
        }).join('');


        const paginationHTML = `
        <div id="paginationRecipes" class="pagination-container"></div>`;

        container.innerHTML = contentHTML + paginationHTML;

        lucide.createIcons();
        // 토글 이벤트 실행
    },
    renderPagination: function (totalPages) {
        const pagination = document.getElementById("paginationRecipes");
        if (!pagination) return;

        const current = this.currentPage;
        let buttons = "";

        // 이전 화살표
        if (current > 1) {
            buttons += `<button onclick="RecipeManager.loadRecipes(${current - 1})">&laquo;</button>`;
        } else {
            buttons += `<button disabled>&laquo;</button>`;
        }

        // 페이지 번호 최대 5개 표시
        // 현재 페이지를 가운데로 배치하되, 시작과 끝은 제한
        let startPage = Math.max(1, current - 2);
        let endPage = Math.min(totalPages, startPage + 4);

        // startPage 조정 (endPage가 끝에 가까우면 다시 앞쪽으로 밀기)
        startPage = Math.max(1, endPage - 4);

        for (let i = startPage; i <= endPage; i++) {
            buttons += `<button class="${i === current ? 'active' : ''}" onclick="RecipeManager.loadRecipes(${i})">${i}</button>`;
        }

        // 다음 화살표
        if (current < totalPages) {
            buttons += `<button onclick="RecipeManager.loadRecipes(${current + 1})">&raquo;</button>`;
        } else {
            buttons += `<button disabled>&raquo;</button>`;
        }

        pagination.innerHTML = buttons;
    },

    // 상세보기
    viewDetails: async function (recipeId){
        if (!recipeId) {
            this.showNotification('일치하는 레시피ID를 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }
        const button = document.querySelector(`button.viewDetailsBt[data-recipe-id="${recipeId}"]`);
        const originalHTML = button.innerHTML;

        button.disabled = true;
        button.innerHTML = `<span class="loading-spinner" style="width:14px; height:14px;"></span>`;
        const url = `/recipe/view?recipeId=${encodeURIComponent(recipeId)}`;
        window.open(url, '_blank');  // 새 탭(또는 새 창)으로 열기


        button.disabled = false;
        button.innerHTML = originalHTML;
    },
    // 레시피 삭제하기
    deleteRecipe: async function (recipeId){
        if (!recipeId) {
            this.showNotification('해당ID의 레시피를 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }
        const button = document.querySelector(`button.deleteRecipeBt[data-recipe-id="${recipeId}"]`);
        const originalHTML = button.innerHTML;

        try {
            // 👉 로딩 상태 표시
            button.disabled = true;
            button.innerHTML = `<span class="loading-spinner" style="width:14px; height:14px;"></span>`;

            const response = await fetch('/admin/deleteRecipes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    recipeId: recipeId,
                })
            });

            if (response.ok) {
                const message = await response.text();
                AdminAllTabs.showNotification(message, 'success');
            } else {
                AdminAllTabs.showNotification('레시피 삭제에 실패했습니다..', 'error');
            }
        } catch (error) {
            console.error("에러 발생:", error);
            this.showNotification('서버 오류가 발생했습니다. 관리자에게 문의하세요', 'error');
        }finally {
            // 👉 로딩 끝 - 버튼 원상복구
            button.disabled = false;
            button.innerHTML = originalHTML;
            RecipeManager.loadRecipes(RecipeManager.currentPage);

        }
    }

};
const PostManager = {
    currentPage: 1,
    pageSize: 10,
    latestRecipes: [],

    loadPosts: function (page = 1) {
        this.showLoading();

        // ① 검색어와 상태 가져오기
        const keyword = document.getElementById("post-search")?.value || "";
        const category = document.getElementById("post-category")?.value || "";
        const searchType = document.getElementById("post-searchType")?.value || "";


        // ② API URL 구성
        const queryParams = new URLSearchParams({
            page: page - 1,
            size: this.pageSize,
            keyword: keyword,
            category: category,
            searchType: searchType
        });

        fetch(`/admin/getPosts?${queryParams.toString()}`)
            .then(res => res.json())
            .then(data => {
                PostManager.currentPage = page;
                PostManager.latestRecipes = data.data;
                PostManager.renderRecipes(data.data);
                PostManager.renderPagination(data.totalPages);
            })
            .catch(error => {
                console.error("게시물 불러오기 실패:", error);
                this.showError();
            });
    },
    showLoading: function () {
        const container = document.getElementById("posts-management-list");
        if (container) {
            container.innerHTML = `<p style="padding: 20px; text-align: center; color: #94a3b8; font-size: 30px">불러오는 중...</p>`;
        }
    },
    showError: function () {
        const container = document.getElementById("posts-management-list");
        if (container) {
            container.innerHTML = `<p style="padding: 20px; text-align: center; color: red; font-size: 30px">데이터를 불러오는 데 실패했습니다.</p>`;
        }
    },
    renderRecipes: function (Recipes) {
        const container = document.getElementById("posts-management-list");
        if (!container) return;

        if (Recipes.length === 0) {
            container.innerHTML = "<p>해당하는 레시피가 없습니다.</p>";
            return;
        }

        const contentHTML = Recipes.map(recipe => {
            const content = recipe.content || ''; // null-safe 처리
            const shortDescription = content.length > 100
                ? content.slice(0, 100) + "..."
                : content;

            return `
        <div class="post-management-item">
          <div class="image-container">
            <img src="${recipe.thumbnail}" alt="${recipe.title}" class="post-image">
            <button class="overlay-detail-btn" 
                    data-board-id="${recipe.boardId}" 
                    onclick="PostManager.viewRecipeDetail(${recipe.boardId})">
              상세보기
            </button>
          </div>
            <div class="post-details">
                <h4 class="post-main-title">${recipe.title}</h4>
                <p class="post-description">${shortDescription}</p>
                <div class="post-badges">
                    <span class="recipe-badge category">${recipe.category}</span>
                </div>
                <div class="post-management-meta">
                    <span>👀 ${recipe.viewCount.toLocaleString()}</span>
                    <span>❤️ ${recipe.likeCount}</span>
                </div>
            </div>
            <div class="post-management-actions">
                <button class="event-action-btn viewDetailsBt" data-board-id="${recipe.boardId}" onclick="PostManager.viewDetails(${recipe.boardId})">
                    <i data-lucide="eye" style="width: 14px; height: 14px;"></i>
                    수정하기
                </button>
                <button class="event-action-btn deleteRecipeBt" data-board-id="${recipe.boardId}" onclick="PostManager.deleteRecipe('${recipe.boardId}')">
                    <i data-lucide="trash" style="width: 14px; height: 14px;"></i>
                    삭제
                </button>
            </div>
        </div>
    `;
        }).join('');


        const paginationHTML = `
        <div id="paginationPosts" class="pagination-container"></div>`;

        container.innerHTML = contentHTML + paginationHTML;

        lucide.createIcons();
        // 토글 이벤트 실행
    },
    renderPagination: function (totalPages) {
        const pagination = document.getElementById("paginationPosts");
        if (!pagination) return;

        const current = this.currentPage;
        let buttons = "";

        // 이전 화살표
        if (current > 1) {
            buttons += `<button onclick="PostManager.loadPosts(${current - 1})">&laquo;</button>`;
        } else {
            buttons += `<button disabled>&laquo;</button>`;
        }

        // 페이지 번호 최대 5개 표시
        // 현재 페이지를 가운데로 배치하되, 시작과 끝은 제한
        let startPage = Math.max(1, current - 2);
        let endPage = Math.min(totalPages, startPage + 4);

        // startPage 조정 (endPage가 끝에 가까우면 다시 앞쪽으로 밀기)
        startPage = Math.max(1, endPage - 4);

        for (let i = startPage; i <= endPage; i++) {
            buttons += `<button class="${i === current ? 'active' : ''}" onclick="PostManager.loadPosts(${i})">${i}</button>`;
        }

        // 다음 화살표
        if (current < totalPages) {
            buttons += `<button onclick="PostManager.loadPosts(${current + 1})">&raquo;</button>`;
        } else {
            buttons += `<button disabled>&raquo;</button>`;
        }

        pagination.innerHTML = buttons;
    },

    // 상세조회
    viewRecipeDetail: async function (boardId) {
        if (!boardId) {
            this.showNotification('일치하는 게시글 ID를 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }

        const button = document.querySelector(`.overlay-detail-btn[data-board-id="${boardId}"]`);
        const originalHTML = button.innerHTML;

        button.disabled = true;
        button.innerHTML = `<span class="loading-spinner" style="width:14px; height:14px;"></span>`;

        const url = `/board/view?boardId=${encodeURIComponent(boardId)}`;
        window.open(url, '_blank');  // 새 탭에서 상세보기 열기

        button.disabled = false;
        button.innerHTML = originalHTML;
    },

    // 수정하기
    viewDetails: async function (boardId){
        if (!boardId) {
            this.showNotification('일치하는 레시피ID를 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }
        const button = document.querySelector(`.viewDetailsBt[data-board-id="${boardId}"]`);
        const originalHTML = button.innerHTML;

        button.disabled = true;
        button.innerHTML = `<span class="loading-spinner" style="width:14px; height:14px;"></span>`;
        const url = `/board/edition?boardId=${encodeURIComponent(boardId)}`;
        window.open(url, '_blank');  // 새 탭(또는 새 창)으로 열기


        button.disabled = false;
        button.innerHTML = originalHTML;
    },
    // 게시물 삭제하기
    deleteRecipe: async function (recipeId){
        if (!recipeId) {
            this.showNotification('해당ID의 게시물을 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }
        const button = document.querySelector(`button.deleteRecipeBt[data-board-id="${recipeId}"]`);
        const originalHTML = button.innerHTML;

        try {
            // 👉 로딩 상태 표시
            button.disabled = true;
            button.innerHTML = `<span class="loading-spinner" style="width:14px; height:14px;"></span>`;

            const response = await fetch('/admin/deletePost', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    boardId: recipeId,
                })
            });

            if (response.ok) {
                const message = await response.text();
                AdminAllTabs.showNotification(message, 'success');
            } else {
                AdminAllTabs.showNotification('게시물 삭제에 실패했습니다..', 'error');
            }
        } catch (error) {
            console.error("에러 발생:", error);
            this.showNotification('서버 오류가 발생했습니다. 관리자에게 문의하세요', 'error');
        }finally {
            // 👉 로딩 끝 - 버튼 원상복구
            button.disabled = false;
            button.innerHTML = originalHTML;
            PostManager.loadPosts(PostManager.currentPage);

        }
    }

};



// AdminAllTabs 메인 객체
const AdminAllTabs = {
    // 초기화
    initialize() {
        this.setupNavigation();
        this.setupTabEvents();
        this.renderCurrentTab();
    },

    // 네비게이션 설정
    setupNavigation() {
        const navLinks = document.querySelectorAll('.nav-link');
        navLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const tabName = link.getAttribute('data-tab');
                if (tabName) {
                    this.switchTab(tabName);
                }
            });
        });
    },

    // 탭 이벤트 설정
    setupTabEvents() {
        document.addEventListener('click', (e) => {
            const userTabBtn = e.target.closest('[data-user-tab]');
            if (userTabBtn) {
                const tabName = userTabBtn.getAttribute('data-user-tab');
                console.log('[DEBUG] user tab 클릭됨:', tabName); // ✅ 디버깅용
                this.switchUserTab(tabName);
            }

            const recipeTabBtn = e.target.closest('[data-recipe-tab]');
            if (recipeTabBtn) {
                const tabName = recipeTabBtn.getAttribute('data-recipe-tab');
                this.switchRecipeTab(tabName);
            }

            const postTabBtn = e.target.closest('[data-post-tab]');
            if (postTabBtn) {
                const tabName = postTabBtn.getAttribute('data-post-tab');
                this.switchPostTab(tabName);
            }

            const settingsTabBtn = e.target.closest('[data-settings-tab]');
            if (settingsTabBtn) {
                const tabName = settingsTabBtn.getAttribute('data-settings-tab');
                this.switchSettingsTab(tabName);
            }
        });
    },

    // 메인 탭 전환
    switchTab(tabName) {
        currentTab = tabName;

        // 모든 탭 콘텐츠 숨기기
        document.querySelectorAll('.tab-content').forEach(content => {
            content.classList.remove('active');
        });

        // 선택된 탭 콘텐츠 보이기
        const targetTab = document.getElementById(`${tabName}-tab`);
        if (targetTab) {
            targetTab.classList.add('active');
        }

        // 네비게이션 활성 상태 업데이트
        document.querySelectorAll('.nav-link').forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('data-tab') === tabName) {
                link.classList.add('active');
            }
        });

        // 페이지 제목 업데이트
        this.updatePageTitle(tabName);

        // 탭별 콘텐츠 렌더링
        this.renderCurrentTab();
    },

    // 페이지 제목 업데이트
    updatePageTitle(tabName) {
        const pageTitle = document.querySelector('.page-title');
        const titleMap = {
            'dashboard': '대시보드',
            'users': '회원 관리',
            'recipes': '레시피 관리',
            'posts': '게시글 관리',
            'events': '이벤트 관리',
            'inquiries': '문의사항',
            'settings': '설정'
        };

        if (pageTitle && titleMap[tabName]) {
            pageTitle.textContent = titleMap[tabName];
        }
    },

    // 현재 탭 렌더링
    renderCurrentTab() {
        switch(currentTab) {
            case 'users':
                this.renderUsersManagement();
                break;
            case 'recipes':
                this.renderRecipesManagement();
                break;
            case 'posts':
                this.renderPostsManagement();
                break;
            case 'events':
                this.renderEventsManagement();
                break;
            case 'inquiries':
                this.renderInquiriesManagement();
                break;
            case 'settings':
                this.renderSettingsManagement();
                break;
        }
    },

    // 회원 관리 렌더링
    renderUsersManagement() {

        // 초기로드
        const activeUserTabBtn = document.querySelector('.tab-btn.active[data-user-tab]');
        const userTabValue = activeUserTabBtn ? activeUserTabBtn.getAttribute('data-user-tab') : 'all';
        this.switchUserTab(userTabValue);
        //  검색창 검색시 재로드
        const searchInput = document.getElementById("user-search");
        if (searchInput) {
            searchInput.addEventListener("keydown", (event) => {
                const activeUserTabBtn = document.querySelector('.tab-btn.active[data-user-tab]');
                const userTabValue = activeUserTabBtn ? activeUserTabBtn.getAttribute('data-user-tab') : 'all';
                if (event.key === "Enter") {
                    if (userTabValue === 'all') {
                        MemberManager.loadAllMember();
                    } else if (userTabValue === 'online') {
                        AdminAllTabs.showNotification('현재 접속인원은 검색기능을 지원하지 않습니다.', 'error');
                        MemberManager.loadOnlineMember();
                    } else if (userTabValue === 'suspended') {
                        MemberManager.loadSuspendedMember();
                    }
                }
            });
        }

    },

    // 회원 탭 전환
    switchUserTab(tabName) {
        currentUserTab = tabName;

        // 탭 버튼 활성화 상태 업데이트
        document.querySelectorAll('[data-user-tab]').forEach(btn => {
            btn.classList.remove('active');
            if (btn.getAttribute('data-user-tab') === tabName) {
                btn.classList.add('active');
            }
        });
        // tabName 값에 따라 함수 실행
        if (tabName === 'all') {
            MemberManager.loadAllMember();
        } else if (tabName === 'online') {
            MemberManager.loadOnlineMember();
        } else if (tabName === 'suspended') {
            MemberManager.loadSuspendedMember();
        }

    },

    // 레시피 관리 렌더링
    renderRecipesManagement() {
        RecipeManager.loadRecipes();
        const searchInput = document.getElementById("recipe-search");
        if (searchInput) {
            searchInput.addEventListener("keydown", (event) => {
                if (event.key === "Enter") {
                    RecipeManager.loadRecipes();
                }
            });
        }

        const statusSelect = document.getElementById("recipe-category");
        if (statusSelect) {
            statusSelect.addEventListener("change", () => {
                RecipeManager.loadRecipes();
            });
        }
        const statusSelect2 = document.getElementById("recipe-searchType");
        if (statusSelect2) {
            statusSelect2.addEventListener("change", () => {
                RecipeManager.loadRecipes();
            });
        }

    },

    // 레시피 탭 전환
    switchRecipeTab(tabName) {
        currentRecipeTab = tabName;

        document.querySelectorAll('[data-recipe-tab]').forEach(btn => {
            btn.classList.remove('active');
            if (btn.getAttribute('data-recipe-tab') === tabName) {
                btn.classList.add('active');
            }
        });

        this.renderRecipesManagement();
    },

    // 게시글 관리 렌더링
    renderPostsManagement() {
        PostManager.loadPosts();
        const searchInput = document.getElementById("post-search");
        if (searchInput) {
            searchInput.addEventListener("keydown", (event) => {
                if (event.key === "Enter") {
                    PostManager.loadPosts();
                }
            });
        }

        const statusSelect = document.getElementById("post-category");
        if (statusSelect) {
            statusSelect.addEventListener("change", () => {
                PostManager.loadPosts();
            });
        }
        const statusSelect2 = document.getElementById("post-searchType");
        if (statusSelect2) {
            statusSelect2.addEventListener("change", () => {
                PostManager.loadPosts();
            });
        }

    },

    // 게시글 탭 전환
    switchPostTab(tabName) {
        currentPostTab = tabName;

        document.querySelectorAll('[data-post-tab]').forEach(btn => {
            btn.classList.remove('active');
            if (btn.getAttribute('data-post-tab') === tabName) {
                btn.classList.add('active');
            }
        });

        this.renderPostsManagement();
    },

    // 이벤트 관리 렌더링
    renderEventsManagement() {
        const eventsGrid = document.getElementById('events-grid');
        if (!eventsGrid) return;

        eventsGrid.innerHTML = sampleEvents.map(event => `
            <div class="event-card">
                <img src="${event.image}" alt="${event.title}" class="event-image">
                <div class="event-body">
                    <div class="event-header">
                        <h4 class="event-title">${event.title}</h4>
                        <div class="recipe-badge ${this.getStatusClass(event.status)}">${this.getStatusText(event.status)}</div>
                    </div>
                    <div class="event-meta">
                        <div class="event-meta-item">
                            <i data-lucide="calendar" class="event-meta-icon"></i>
                            ${this.formatDate(event.startDate)} ~ ${this.formatDate(event.endDate)}
                        </div>
                        <div class="event-meta-item">
                            <i data-lucide="map-pin" class="event-meta-icon"></i>
                            ${event.location}
                        </div>
                        <div class="event-meta-item">
                            <i data-lucide="trophy" class="event-meta-icon"></i>
                            ${event.category}
                        </div>
                        <div class="event-meta-item">
                            <i data-lucide="users" class="event-meta-icon"></i>
                            ${event.participants.toLocaleString()}명 참가
                        </div>
                    </div>
                    <div class="event-stats">
                        <span>👀 ${event.views.toLocaleString()}</span>
                        <span>📝 ${event.applications.toLocaleString()}</span>
                        <span>💰 ${(event.budget / 10000).toLocaleString()}만원</span>
                    </div>
                    <div class="event-actions">
                        <button class="event-action-btn" onclick="AdminAllTabs.showNotification('이벤트 상세보기', 'info')">
                            <i data-lucide="eye" style="width: 14px; height: 14px;"></i>
                            상세
                        </button>
                        <button class="event-action-btn primary" onclick="AdminAllTabs.editEvent(${event.id})">
                            <i data-lucide="edit-3" style="width: 14px; height: 14px;"></i>
                            편집
                        </button>
                    </div>
                </div>
            </div>
        `).join('');

        lucide.createIcons();
    },

    // 문의사항 관리 렌더링
    renderInquiriesManagement() {
        InquiryManager.loadInquiries();
        const searchInput = document.getElementById("inquiry-search");
        if (searchInput) {
            searchInput.addEventListener("keydown", (event) => {
                if (event.key === "Enter") {
                    InquiryManager.loadInquiries(1);
                }
            });
        }

        const statusSelect = document.getElementById("inquiry-status");
        if (statusSelect) {
            statusSelect.addEventListener("change", () => {
                InquiryManager.loadInquiries(1);
            });
        }

    },

    // 설정 관리 렌더링
    renderSettingsManagement() {
        // 설정 탭 이벤트만 설정 (HTML에 이미 구현됨)
        this.switchSettingsTab('site');
    },

    // 설정 탭 전환
    switchSettingsTab(tabName) {
        currentSettingsTab = tabName;

        document.querySelectorAll('[data-settings-tab]').forEach(btn => {
            btn.classList.remove('active');
            if (btn.getAttribute('data-settings-tab') === tabName) {
                btn.classList.add('active');
            }
        });

        document.querySelectorAll('.settings-tab-content').forEach(content => {
            content.classList.remove('active');
        });

        const targetContent = document.getElementById(`${tabName}-settings`);
        if (targetContent) {
            targetContent.classList.add('active');
        }
    },

    // DB 동기화 시작
    // startDbSync() {
    //     const progressBar = document.getElementById('db-sync-progress');
    //     const progressFill = progressBar.querySelector('.progress-fill');
    //     const progressText = progressBar.querySelector('.progress-text');
    //
    //     progressBar.style.display = 'block';
    //     let progress = 0;
    //
    //     const interval = setInterval(() => {
    //         progress += 10;
    //         progressFill.style.width = progress + '%';
    //         progressText.textContent = progress + '%';
    //
    //         if (progress >= 100) {
    //             clearInterval(interval);
    //             setTimeout(() => {
    //                 progressBar.style.display = 'none';
    //                 this.showNotification('데이터베이스 동기화가 완료되었습니다!', 'success');
    //             }, 1000);
    //         }
    //     }, 300);
    // },
    startDbSync: async function () {
        const progressBar = document.getElementById('db-sync-progress');
        if (!progressBar) return;

        const progressFill = progressBar.querySelector('.progress-fill');
        const progressText = progressBar.querySelector('.progress-text');

        // 이 카드 안의 버튼만 비활성화/복구
        const card = progressBar.closest('.db-action-card');
        const btn = card ? card.querySelector('.action-btn.gradient-btn') : null;
        const originalHTML = btn ? btn.innerHTML : null;

        // 진행바 초기화 + 버튼 잠금
        progressBar.style.display = 'block';
        progressFill.style.width = '0%';
        progressText.textContent = '0%';
        if (btn) {
            btn.disabled = true;
            btn.innerHTML = `<i data-lucide="loader-2" class="btn-icon spin"></i> 동기화 중`;
            if (window.lucide && lucide.createIcons) lucide.createIcons();
        }

        // 가짜 진행률(응답 올 때까지 90%까지만)
        let fake = 0;
        const tick = () => {
            if (fake < 90) {
                fake += 2;
                progressFill.style.width = fake + '%';
                progressText.textContent = fake + '%';
            }
        };
        const timer = setInterval(tick, 250);

        try {
            const res = await fetch('/api/logstash/start', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                }
            });

            // 응답 수신 → 진행률 마무리
            clearInterval(timer);
            const data = await res.json().catch(() => ({}));

            if (res.status === 429) {
                // 이미 실행 중
                this.showNotification(data.message || 'Logstash 작업이 이미 실행 중입니다.', 'warning');
                progressFill.style.width = '0%';
                progressText.textContent = '0%';
                return;
            }
            if (!res.ok) {
                throw new Error(data.error || data.message || `HTTP ${res.status}`);
            }

            // 성공
            progressFill.style.width = '100%';
            progressText.textContent = '100%';
            this.showNotification(data.message || '통합검색 동기화가 완료되었습니다.', 'success');
            setLastSync();
        } catch (err) {
            clearInterval(timer);
            this.showNotification(`동기화 실패: ${err.message}`, 'error');
            progressFill.style.width = '0%';
            progressText.textContent = '0%';
        } finally {
            // 진행바 잠깐 보여주고 숨김 + 버튼 복구
            setTimeout(() => { progressBar.style.display = 'none'; }, 800);
            if (btn) {
                btn.disabled = false;
                btn.innerHTML = originalHTML;
                if (window.lucide && lucide.createIcons) lucide.createIcons();
            }
        }
    },


    // API 동기화 시작
    startApiSync() {
        const progressBar = document.getElementById('api-sync-progress');
        const progressFill = progressBar.querySelector('.progress-fill');
        const progressText = progressBar.querySelector('.progress-text');

        progressBar.style.display = 'block';
        let progress = 0;

        const interval = setInterval(() => {
            progress += 15;
            progressFill.style.width = progress + '%';
            progressText.textContent = progress + '%';

            if (progress >= 100) {
                clearInterval(interval);
                setTimeout(() => {
                    progressBar.style.display = 'none';
                    this.showNotification('API 데이터 불러오기가 완료되었습니다!', 'success');
                }, 1000);
            }
        }, 400);
    },

    // 이벤트 폼 열기
    openEventForm() {
        const modal = document.getElementById('event-modal');
        if (modal) {
            modal.style.display = 'flex';
            document.body.style.overflow = 'hidden';
        }
    },

    // 이벤트 폼 닫기
    closeEventForm() {
        const modal = document.getElementById('event-modal');
        if (modal) {
            modal.style.display = 'none';
            document.body.style.overflow = 'auto';
        }
    },

    // 이벤트 저장
    saveEvent() {
        const form = document.getElementById('event-form');
        const formData = new FormData(form);

        // 여기서 실제 저장 로직 구현
        this.showNotification('이벤트가 성공적으로 저장되었습니다!', 'success');
        this.closeEventForm();
    },

    // 이벤트 편집
    editEvent(eventId) {
        const event = sampleEvents.find(e => e.id === eventId);
        if (event) {
            // 폼에 데이터 채우기
            this.openEventForm();
            document.querySelector('.modal-title').innerHTML = `
                <i data-lucide="trophy" class="title-icon"></i>
                이벤트 수정
            `;

            // 폼 필드에 값 설정
            const form = document.getElementById('event-form');
            form.title.value = event.title;
            form.description.value = event.description;
            form.category.value = event.category;
            form.budget.value = event.budget;
            form.startDate.value = event.startDate;
            form.endDate.value = event.endDate;
            form.time.value = event.time;
            form.location.value = event.location;
            form.maxParticipants.value = event.maxParticipants || '';
            form.prize.value = event.prize;
            form.tags.value = event.tags.join(', ');
        }
    },

    // 문의사항 답변 열기
    openInquiryAnswer(inquiryId) {
        const inquiry = InquiryManager.latestInquiries.find(i => i.inquiryId === inquiryId);
        if (!inquiry) return;

        const modal = document.getElementById('inquiry-answer-modal');
        const originalInquiry = document.getElementById('original-inquiry');
        const answerContent = document.getElementById('answer-content');

        // 원본 문의 내용 표시
        originalInquiry.innerHTML = `
            <div style="display: flex; justify-content: space-between; margin-bottom: 12px;">
                <div style="display: flex; gap: 8px;">
                     <span class="recipe-badge ${inquiry.answerStatus}">
                     ${inquiry.answerStatus === '답변완료' ? '답변 완료' : '대기 중'}</span> 
                    <span id="openInquiryId" data-inquiry-id="${inquiry.inquiryId}" style="font-size: 10px; color: #64748b;">#${inquiry.inquiryId}</span>
                </div>
                <span style="font-size: 12px; color: #64748b;">${AdminAllTabs.formatTimeAgo(inquiry.createdAt)}</span>
            </div>
            <h4 style="font-weight: 600; margin-bottom: 8px;">${inquiry.title}</h4>
            <p style="color: #374151; margin-bottom: 12px; line-height: 1.5;">${inquiry.questionContent}</p>
            <div style="display: flex; gap: 16px; font-size: 12px; color: #64748b;">
                <span>👤 ${inquiry.nickname}</span>               
            </div>
        `;

        // 기존 답변이 있으면 채우기
        answerContent.value = inquiry.answerContent || '';

        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    },

    // 문의사항 답변 닫기
    closeInquiryAnswer() {
        const modal = document.getElementById('inquiry-answer-modal');
        if (modal) {
            modal.style.display = 'none';
            document.body.style.overflow = 'auto';
        }
    },

    // 답변 제출
    submitAnswer: async function (){
        const answerContent = document.getElementById('answer-content').value.trim();
        const inquiryId = document.getElementById('openInquiryId').dataset.inquiryId;

        if (!inquiryId) {
            this.showNotification('문의 ID를 찾을 수 없습니다. 다시 시도해주세요.', 'error');
            return;
        }

        if (!answerContent.trim()) {
            this.showNotification('답변 내용을 입력해주세요.', 'error');
            return;
        }
        try {
            const response = await fetch('/inquiries/answer', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    inquiryId: inquiryId,
                    answerContent: answerContent
                })
            });

            if (response.ok) {
                this.showNotification('답변이 성공적으로 전송되었습니다!', 'success');
                this.closeInquiryAnswer();
            } else {
                this.showNotification('답변 저장에 실패했습니다.', 'error');
            }
        } catch (error) {
            console.error("에러 발생:", error);
            this.showNotification('서버 오류가 발생했습니다. 관리자에게 문의하세요', 'error');
        }
        InquiryManager.loadInquiries();
        PendingInquiryManager.fetchInquiriesAllCount();
        PendingInquiryManager.fetchPendingCount();
        PendingInquiryManager.fetchAnsweredCount();
        PendingInquiryManager.fetchTodayCount();
        PendingInquiryManager.fetchPendingInquiries();
    },

    // 사용자 액션 표시
    showUserActions(userId) {
        this.showNotification(`회원 ID ${userId}의 관리 메뉴`, 'info');
    },

    // 레시피 액션 표시
    showRecipeActions(recipeId) {
        this.showNotification(`레시피 ID ${recipeId}의 관리 메뉴`, 'info');
    },

    // 문의사항 액션 표시
    showInquiryActions(inquiryId) {
        this.showNotification(`문의사항 ID ${inquiryId}의 관리 메뉴`, 'info');
    },

    // 알림 표시
    showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.textContent = message;

        // 스타일 설정
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 16px 20px;
            border-radius: 12px;
            color: white;
            font-weight: 500;
            font-family: 'Gowun Dodum', sans-serif;
            z-index: 9999;
            opacity: 0;
            transform: translateX(100%);
            transition: all 0.3s ease;
            max-width: 300px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        `;

        // 타입별 배경색
        const colors = {
            info: 'linear-gradient(135deg, #f97316, #ec4899)',
            success: 'linear-gradient(135deg, #10b981, #059669)',
            warning: 'linear-gradient(135deg, #f59e0b, #d97706)',
            error: 'linear-gradient(135deg, #ef4444, #dc2626)'
        };
        notification.style.background = colors[type] || colors.info;

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
    },

    // 유틸리티 함수들
    getStatusIconName(status) {
        switch (status) {
            case '정상': return 'check-circle';
            case '활성': return 'check-circle';
            case 'active': return 'check-circle';
            case 'inactive': return 'clock';
            case '정지': return 'alert-triangle';
            case 'suspended': return 'alert-triangle';
            case 'banned': return 'ban';
            case 'published': return 'check-circle';
            case 'private': case 'hidden': return 'eye-off';
            case 'draft': return 'edit-3';
            case 'reported': return 'flag';
            case 'pending': return 'clock';
            case 'answered': return 'check-circle-2';
            case 'ended': return 'check-circle';
            case 'paused': return 'pause';
            case 'cancelled': return 'x';
            default: return 'check-circle';
        }
    },

    getStatusText(status) {
        switch (status) {
            case 'active': return '활성';
            case 'inactive': return '비활성';
            case 'suspended': return '정지';
            case 'banned': return '차단';
            case 'published': return '공개';
            case 'private': return '비공개';
            case 'draft': return '임시저장';
            case 'reported': return '신고됨';
            case 'pending': return '대기중';
            case 'answered': return '답변완료';
            case 'ended': return '종료';
            case 'paused': return '일시정지';
            case 'cancelled': return '취소';
            default: return status;
        }
    },

    getStatusClass(status) {
        switch (status) {
            case 'active':
            case 'published':
            case 'answered': return 'status-active';
            case 'pending': return 'status-pending';
            case 'private':
            case 'draft': return 'status-private';
            case 'reported': return 'status-reported';
            case 'suspended': return 'status-suspended';
            case 'banned': return 'status-banned';
            default: return 'status-active';
        }
    },

    getCategoryText(category) {
        switch (category) {
            case 'recipe': return '레시피';
            case 'account': return '계정';
            case 'grade': return '등급';
            case 'payment': return '결제';
            case 'technical': return '기술';
            case 'suggestion': return '제안';
            case 'report': return '신고';
            case 'other': return '기타';
            default: return category;
        }
    },

    formatDate(dateString) {
        return new Date(dateString).toLocaleDateString('ko-KR');
    },

    formatTime(dateString) {
        return new Date(dateString).toLocaleTimeString('ko-KR', {
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    formatTimeAgo(dateString) {
        if (!dateString) return '날짜 없음';

        const date = new Date(dateString);
        if (isNaN(date.getTime())) return '잘못된 날짜';

        const now = new Date();
        const diffInHours = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60));

        if (diffInHours < 1) return '방금 전';
        if (diffInHours < 24) return `${diffInHours}시간 전`;
        if (diffInHours < 48) return '어제';
        return `${Math.floor(diffInHours / 24)}일 전`;
    }
};

// 전역 객체로 노출
window.AdminAllTabs = AdminAllTabs;

/* ========= 외부 레시피 수집 컨트롤(폴링 버전) ========= */
(function(){
    const ENDPOINTS = {
        datako:      '/admin/import/datako/run',
        spoonacular: '/admin/import/spoonacular/run',
        wf:          '/admin/import/wf/run',
        stop:        '/admin/import/stop',
        status: (task) => `/admin/import/status/${task}`
    };

    let running = false;
    let timer   = null;
    let currentTask = null;

    function qs(sel){ return document.querySelector(sel); }

    function getProgressEls(){
        const bar  = qs('#api-sync-progress');
        if(!bar) return {};
        return {
            bar,
            fill: bar.querySelector('.progress-fill'),
            text: bar.querySelector('.progress-text')
        };
    }

    function setProgress(p){
        const {bar, fill, text} = getProgressEls();
        if(!bar) return;
        bar.style.display = 'block';
        const pct = Math.max(0, Math.min(100, Math.round(p)));
        fill.style.width  = `${pct}%`;
        text.textContent  = `${pct}%`;
    }

    function resetProgress(hide=true){
        const {bar} = getProgressEls();
        setProgress(0);
        if(bar && hide) bar.style.display = 'none';
    }

    function lockButtons(lock){
        // 하위 실행 버튼 3개
        document.querySelectorAll('#api-sub-buttons .sub-btn.blue-btn')
            .forEach(b=>b.disabled = lock);
        // 중단 버튼
        const stopBtn = qs('#api-stop-btn');
        if(stopBtn) stopBtn.disabled = !lock; // 실행 중일 때만 활성
        // 메인 토글 버튼
        const main = qs('#main-api-btn');
        if(main){ main.disabled = lock; }
    }

    // 하위 버튼 보이기/숨기기
    window.toggleApiButtons = function(){
        const box = qs('#api-sub-buttons');
        if(!box) return;
        box.style.display = (box.style.display === 'none' || box.style.display === '') ? 'flex' : 'none';
    };

    // 상태 폴링 시작
    async function startPolling(task){
        clearInterval(timer);
        timer = setInterval(async ()=>{
            try{
                const res = await fetch(ENDPOINTS.status(task), {
                    headers: { [csrfHeader]: csrfToken }
                });
                if(!res.ok) throw new Error(`HTTP ${res.status}`);
                const s = await res.json();

                // percent 반영
                const pct = typeof s.percent === 'number' ? s.percent : 0;
                setProgress(pct);

                // 완료/정지/에러 처리
                const finished = !s.running || pct >= 100;
                if (finished) {
                    clearInterval(timer);
                    running = false;
                    lockButtons(false);

                    if (s.error) {
                        AdminAllTabs.showNotification(`❌ 실패: ${s.error}`, 'error');
                        setProgress(0);
                    } else if (pct >= 100) {
                        AdminAllTabs.showNotification('✅ 데이터 불러오기 완료', 'success');
                        setProgress(100);
                        setLastSync(); // ✅ 이 줄이 “마지막 동기화” 갱신합니다
                    } else {
                        AdminAllTabs.showNotification('⚠️ 작업이 중단되었습니다', 'warning');
                        setProgress(0);
                    }

                    setTimeout(() => resetProgress(true), 800);
                }
            }catch(e){
                // 폴링 실패는 일시적일 수 있으니, UI만 유지
                // 네트워크 계속 실패하면 사용자 중단/재실행으로 복구 가능
            }
        }, 700);
    }

    // 실행
    window.runApi = async function(task){
        if(running){ AdminAllTabs.showNotification('다른 작업이 진행 중입니다.', 'warning'); return; }
        if(!ENDPOINTS[task]){ AdminAllTabs.showNotification('알 수 없는 작업입니다.', 'error'); return; }

        currentTask = task;
        running = true;
        lockButtons(true);
        setProgress(0);
        AdminAllTabs.showNotification(`${task} 실행 시작...`, 'info');

        try{
            const res = await fetch(ENDPOINTS[task], {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                }
            });

            if(res.status === 429){
                running = false;
                lockButtons(false);
                setProgress(0);
                AdminAllTabs.showNotification('이미 실행 중입니다.', 'warning');
                return;
            }
            if(!res.ok){
                const t = await res.text().catch(()=> '');
                throw new Error(t || `HTTP ${res.status}`);
            }

            // 서버에서 정상 시작되면 상태 폴링
            startPolling(task);
        }catch(err){
            running = false;
            lockButtons(false);
            setProgress(0);
            AdminAllTabs.showNotification(`❌ 시작 실패: ${err.message}`, 'error');
        }finally{
            if(window.lucide && lucide.createIcons) lucide.createIcons();
        }
    };

    // 모두 중지
    window.stopAll = async function(){
        if(!running){
            AdminAllTabs.showNotification('진행 중인 작업이 없습니다.', 'info');
            return;
        }
        try{
            clearInterval(timer);
            const res = await fetch(ENDPOINTS.stop, {
                method: 'POST',
                headers: { [csrfHeader]: csrfToken }
            });
            if(!res.ok){
                const t = await res.text().catch(()=> '');
                throw new Error(t || `HTTP ${res.status}`);
            }
            AdminAllTabs.showNotification('⚠️ 작업이 중단되었습니다', 'warning');
        }catch(err){
            AdminAllTabs.showNotification(`중단 실패: ${err.message}`, 'error');
        }finally{
            running = false;
            lockButtons(false);
            resetProgress(true);
        }
    };
})();

// ✅ ① 현재 시각을 "MM/DD HH:mm" 형식으로 반환
function nowSyncString() {
    const d = new Date();
    const pad = (n) => String(n).padStart(2, '0'); // 1자리 숫자 → 2자리로
    return `${pad(d.getMonth() + 1)}/${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

// ✅ ② 화면의 #last-sync-text 값을 바꿔주는 함수
function setLastSync(text) {
    const el = document.getElementById('last-sync-text');
    if (el) el.textContent = text || nowSyncString();
}

// 페이지 DOM이 다 만들어지면 관리자 초기화 실행
document.addEventListener('DOMContentLoaded', () => {
    if (window.AdminAllTabs && typeof AdminAllTabs.initialize === 'function') {
        AdminAllTabs.initialize();   // 내부에서 setLastSync()도 호출됨
    } else {
        console.error('AdminAllTabs.initialize()를 찾을 수 없습니다.');
    }
});