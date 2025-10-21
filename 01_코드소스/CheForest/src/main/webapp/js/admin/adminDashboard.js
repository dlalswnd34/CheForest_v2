// 현재시간 출력
function updateCurrentTime() {
    const now = new Date();

    const dateElement = document.getElementById('date-text');
    const timeElement = document.getElementById('time-text');

    const dateOptions = {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
    };

    const dateStr = now.toLocaleDateString('ko-KR', dateOptions);

    const hours = now.getHours();
    const minutes = now.getMinutes();
    const seconds = now.getSeconds();
    const timeStr = `${hours}시 ${minutes}분 ${seconds}초`;

    if (dateElement) dateElement.textContent = dateStr;
    if (timeElement) timeElement.textContent = timeStr;
}

setInterval(updateCurrentTime, 1000); // 1초마다 갱신
updateCurrentTime(); // 최초 1회 즉시 호출



function fetchStats() {
    $.ajax({
        url: "/admin/stats",
        method: "GET",
        cache: false, // <-- 캐시 방지
        success: function(data) {
            $("#activeUsers").text(data.activeUsers);
            $(".loggedInUsers").text(data.loggedInUsers);
            $("#peakConcurrentUsers").text(data.peakConcurrentUsers);
            $("#totalVisitorsToday").text(data.totalVisitorsToday);
            $("#totalVisitMemberToday").text(data.totalVisitMemberToday);
        },
        error: function() {
            console.error("통계 정보를 불러오지 못했습니다.");
        }
    });
}

// 페이지 로드 시 최초 호출
fetchStats();

// 이후 1초마다 갱신
setInterval(fetchStats, 1000);

setInterval(function () {
    navigator.sendBeacon("/ping");
}, 1000);

function fetchLoggedInUsers() {
    $.ajax({
        url: "/admin/loggedInUserList",
        method: "GET",
        cache: false,
        success: function (data) {
            const list = data.loggedInUserList || [];
            const $listContainer = $("#loggedInUserList");
            $listContainer.empty();

            list.forEach(function (userId) {
                $listContainer.append("<li>" + userId + "</li>");
            });
        },
        error: function () {
            console.error("로그인 중인 유저 목록을 가져오지 못했습니다.");
        }
    });
}

// 페이지 로드 시 호출
fetchLoggedInUsers();

// 주기적으로 갱신
setInterval(fetchLoggedInUsers, 1000);

function startLogstash() {
    const btn = document.getElementById('logstash-btn');
    btn.disabled = true;

    fetch('/api/logstash/start', {
        method: 'POST',
        headers: { [csrfHeader]: csrfToken }
    })
        .then(res => res.json())
        .then(data => alert(data.message))
        .catch(err => alert('Error: ' + err))
        .finally(() => btn.disabled = false);
}

const CustomAdmin = {

    // 차트 초기화
    initializeCharts(accountStatusCounts,monthlyData) {
        this.initializeMemberStatusChart(accountStatusCounts);
        this.initializeMonthlyActivityChart(monthlyData);
    },

// 회원 상태 분포 차트
    initializeMemberStatusChart(accountStatusCounts) {


        const ctx = document.getElementById('memberStatusChart');
        if (!ctx) return;

        const data = {

            labels: ['활동계정', '휴면계정', '제재계정'],
            datasets: [{
                data: [accountStatusCounts.activeCount, accountStatusCounts.dormantCount, accountStatusCounts.suspendedCount],
                backgroundColor: ['#f97316', '#cf9664', '#ec4899'],
                borderWidth: 0,
                cutout: '70%'
            }]
        };

        const config = {
            type: 'doughnut',
            data: data,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        titleColor: '#fff',
                        bodyColor: '#fff',
                        borderColor: 'rgba(255, 255, 255, 0.1)',
                        borderWidth: 1,
                        cornerRadius: 8
                    }
                },
                elements: {
                    arc: {
                        borderWidth: 0
                    }
                }
            }
        };

        memberStatusChart = new Chart(ctx, config);
    },

// 월별 활동 현황 차트
    initializeMonthlyActivityChart(monthlyData) {
        const ctx = document.getElementById('monthlyActivityChart');
        if (!ctx) return;

        const labels = monthlyData.map(item => {
            const [year, month] = item.month.split("-");
            return parseInt(month) + "월";
        });

        const boardCounts = monthlyData.map(item => item.boardCount);
        const memberCounts = monthlyData.map(item => item.memberCount);

        const maxBoardCount = Math.max(...boardCounts);
        const maxMemberCount = Math.max(...memberCounts);
        const dynamicMaxY = Math.ceil(Math.max(maxBoardCount, maxMemberCount) / 100) * 100;  // 100 단위 올림 처리



        const data = {
            labels: labels,
            datasets: [
                {
                    label: '게시글',
                    data: boardCounts,
                    backgroundColor: '#f97316',
                    borderRadius: 6,
                    maxBarThickness: 40
                },
                {
                    label: '가입자수',
                    data: memberCounts,
                    backgroundColor: '#ec4899',
                    borderRadius: 6,
                    maxBarThickness: 40
                }
            ]
        };

        const config = {
            type: 'bar',
            data: data,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        titleColor: '#fff',
                        bodyColor: '#fff',
                        borderColor: 'rgba(255, 255, 255, 0.1)',
                        borderWidth: 1,
                        cornerRadius: 8
                    }
                },
                scales: {
                    x: {
                        grid: {
                            display: false
                        },
                        border: {
                            display: false
                        },
                        ticks: {
                            color: '#64748b',
                            font: {
                                size: 12,
                                family: 'Gowun Dodum'
                            }
                        }
                    },
                    y: {
                        beginAtZero: true,
                        max: dynamicMaxY,
                        grid: {
                            color: '#f1f5f9'
                        },
                        border: {
                            display: false
                        },
                        ticks: {
                            color: '#64748b',
                            font: {
                                size: 12,
                                family: 'Gowun Dodum'
                            },
                            stepSize: 100
                        }
                    }
                }
            }
        };

        monthlyActivityChart = new Chart(ctx, config);
    }

}
// 전역 객체로 노출
window.CustomAdmin = CustomAdmin;
// 관리자페이지 홈화면 문의사항
const PendingInquiryManager = {
    latestInquiries: [],

    fetchInquiriesAllCount() {
        fetch("/api/inquiries/countAllInquiries")
            .then(response => {
                if (!response.ok) {
                    throw new Error("서버 응답 실패");
                }
                return response.json();
            })
            .then(count => {
                // 모든 class="pending-count" 요소에 count 값 넣기
                const elements = document.querySelectorAll(".inquiriesAllCount");
                elements.forEach(el => {
                    el.textContent = count;
                });
            })
            .catch(error => {
                console.error("대기중 문의 수 조회 실패:", error);
            });
    },
    fetchPendingCount() {
        fetch("/api/inquiries/countPendingInquiries")
            .then(response => {
                if (!response.ok) {
                    throw new Error("서버 응답 실패");
                }
                return response.json();
            })
            .then(count => {
                // 모든 class="pending-count" 요소에 count 값 넣기
                const elements = document.querySelectorAll(".inquiriesPendingCount");
                elements.forEach(el => {
                    el.textContent = count;
                });
            })
            .catch(error => {
                console.error("대기중 문의 수 조회 실패:", error);
            });
    },
    fetchAnsweredCount() {
        fetch("/api/inquiries/countAnsweredInquiries")
            .then(response => {
                if (!response.ok) {
                    throw new Error("서버 응답 실패");
                }
                return response.json();
            })
            .then(count => {
                // 모든 class="pending-count" 요소에 count 값 넣기
                const elements = document.querySelectorAll(".inquiriesAnsweredCount");
                elements.forEach(el => {
                    el.textContent = count;
                });
            })
            .catch(error => {
                console.error("대기중 문의 수 조회 실패:", error);
            });
    },
    fetchTodayCount() {
        fetch("/api/inquiries/countTodayInquiries")
            .then(response => {
                if (!response.ok) {
                    throw new Error("서버 응답 실패");
                }
                return response.json();
            })
            .then(count => {
                // 모든 class="pending-count" 요소에 count 값 넣기
                const elements = document.querySelectorAll(".inquiriesTodayCount");
                elements.forEach(el => {
                    el.textContent = count;
                });
            })
            .catch(error => {
                console.error("대기중 문의 수 조회 실패:", error);
            });
    },

    fetchPendingInquiries() {
        fetch("/api/inquiries/pending")
            .then(response => {
                if (!response.ok) throw new Error("서버 응답 실패");
                return response.json();
            })
            .then(data => {
                this.latestInquiries = data;
                this.renderInquiriesList(data);
            })
            .catch(error => {
                console.error("대기중 문의 리스트 불러오기 실패:", error);
            });
    },

    renderInquiriesList(data) {
        const container = document.getElementById("inquiry-list");

        if (!data || data.length === 0) {
            container.innerHTML = "<p>현재 대기중인 문의가 없습니다.</p>";
            return;
        }

        let html = "";
        data.forEach(inquiry => {
            html += `
                <div class="inquiry-item" onclick="PendingInquiryManager.openInquiryAnswer(${inquiry.inquiryId})">
                    <div class="inquiry-content">
                        <h4 class="inquiry-title">${inquiry.title}</h4>
                        <div class="inquiry-meta">
                            <span class="inquiry-user">👤 ${inquiry.nickname ?? '회원번호: ' + inquiry.memberIdx}</span>
                            <span class="inquiry-time">🕒 ${AdminAllTabs.formatTimeAgo(inquiry.createdAt)}</span>
                        </div>
                    </div>
                    <span class="inquiry-status new">${inquiry.answerStatus === '답변완료' ? '답변 완료' : '대기 중'}</span>
                </div>
            `;
        });

        container.innerHTML = html;
    },

    // 문의사항 답변 열기
    openInquiryAnswer(inquiryId) {
        const inquiry = PendingInquiryManager.latestInquiries.find(i => i.inquiryId === inquiryId);
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
    }
};

// ✅ 페이지 로드 시 자동 실행
document.addEventListener("DOMContentLoaded", () => {
    PendingInquiryManager.fetchInquiriesAllCount();
    PendingInquiryManager.fetchPendingCount();
    PendingInquiryManager.fetchAnsweredCount();
    PendingInquiryManager.fetchTodayCount();
    PendingInquiryManager.fetchPendingInquiries();
});




