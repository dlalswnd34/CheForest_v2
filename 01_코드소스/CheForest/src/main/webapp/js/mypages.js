/**
 * 마이페이지 전용 JavaScript (탭 전환, 문의 내역 동적 로딩 등)
 */

// =================================================================================
// [0] 필수: alert/confirm 대체 Custom UI 함수 (인프라 제약 조건 충족)
// =================================================================================

// 임시 메시지 박스 컨테이너를 생성하거나 가져옵니다.
function createMessageBox() {
    let box = document.getElementById('mypage-message-box');
    if (!box) {
        box = document.createElement('div');
        box.id = 'mypage-message-box';
        // 최소한의 스타일로 페이지 우측 상단에 고정합니다.
        box.style.cssText = 'position: fixed; top: 20px; right: 20px; background: #fff; border: 1px solid #ccc; padding: 15px; z-index: 1000; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); max-width: 300px; transition: opacity 0.3s ease; opacity: 0; pointer-events: none;';
        document.body.appendChild(box);
    }
    return box;
}

/** alert()를 대체하여 사용자에게 메시지를 보여주는 함수 */
function showMessageBox(message, type = 'info') {
    const box = createMessageBox();
    let bgColor = '#e0f7fa'; // info (light blue)
    if (type === 'error') bgColor = '#ffebee'; // error (light red)
    if (type === 'success') bgColor = '#e8f5e9'; // success (light green)

    box.style.backgroundColor = bgColor;
    box.innerHTML = `<p style="margin: 0; color: #333; font-weight: 500;">${message}</p>`;
    box.style.opacity = 1;
    box.style.pointerEvents = 'auto';
    box.style.display = 'block';

    setTimeout(() => {
        box.style.opacity = 0;
        box.style.pointerEvents = 'none';
    }, 4000);
}

/** confirm()을 대체하는 함수 (실제로는 커스텀 모달로 구현해야 함) */
function showConfirmBox(message, onConfirm) {
    const isConfirmed = window.confirm(message);
    if (isConfirmed) {
        onConfirm();
    }
}


// =================================================================================
// [1] 문의 내역 연동 관련 전역 변수 및 함수 (HTML onclick에서 직접 호출되므로 외부에 위치)
// =================================================================================

let currentInquiryPage = 0; // 현재 페이지 번호 (0부터 시작)
const inquiryPageSize = 5; // 한 페이지당 문의 개수 (백엔드와 일치하도록 설정)

/**
 * 문의 내역 API를 호출하고 결과를 렌더링합니다.
 * 이 함수는 탭 전환 시 또는 페이징 버튼 클릭 시 호출됩니다.
 * @param {number} page - 요청할 페이지 번호 (0부터 시작)
 */
function fetchMyInquiries(page) {
    currentInquiryPage = page;
    const container = document.getElementById('inquiries-list-container');
    const countElement = document.getElementById('inquiry-count');
    const paginationContainer = document.getElementById('inquiries-pagination');

    const statusSelect = document.getElementById('inquiry-status');
    const sortSelect = document.getElementById('inquiry-sort');
    const status = statusSelect ? statusSelect.value : 'all';
    const sortDir = sortSelect ? sortSelect.value : 'desc';

    // 로딩 상태 표시
    container.innerHTML = '<p class="loading-message">문의 내역을 불러오는 중...</p>';
    countElement.textContent = '...';
    paginationContainer.innerHTML = '';

    // API URL
    const apiUrl = `/api/mypage/inquiries?page=${page}&size=${inquiryPageSize}&status=${encodeURIComponent(status)}&sort=createdAt,${sortDir}`;

    fetch(apiUrl)
        .then(response => {
            if (!response.ok) throw new Error('문의 내역 API 호출 실패: ' + response.statusText);
            return response.json();
        })
        .then(data => {
            renderInquiryList(data.data, container);
            countElement.textContent = data.total.toLocaleString();
            renderInquiryPagination(data.totalPages, data.page);
            const url = `${window.location.pathname}?tab=inquiries&page=${data.page}`;
            window.history.replaceState(null, '', url);
        })
        .catch(error => {
            console.error('문의 내역을 불러오는 중 오류 발생:', error);
            container.innerHTML = '<p class="error-message">문의 내역을 불러올 수 없습니다.</p>';
            countElement.textContent = '0';
        });
}

/**
 * 문의 내역 데이터를 HTML 카드로 변환하여 컨테이너에 삽입합니다.
 */
function renderInquiryList(inquiries, container) {
    if (!inquiries || inquiries.length === 0) {
        container.innerHTML = '<p class="no-data-message">작성된 문의 내역이 없습니다.</p>';
        return;
    }

    let html = '';
    inquiries.forEach(inquiry => {
        const isCompleted = inquiry.answerStatus === '답변완료';
        const statusClass = isCompleted ? 'completed' : 'in-progress';
        const statusText = isCompleted ? '답변완료' : '답변대기';

        const formattedDate = new Date(inquiry.createdAt).toLocaleDateString('ko-KR', {
            year: 'numeric', month: '2-digit', day: '2-digit'
        }).replace(/\. /g, '-').replace(/\./g, '');

        let answeredAtDate = 'N/A';
        if (inquiry.answerAt) {
            const dateObj = new Date(inquiry.answerAt);
            if (!isNaN(dateObj.getTime())) {
                answeredAtDate = dateObj.toLocaleDateString('ko-KR', {
                    year: 'numeric', month: '2-digit', day: '2-digit'
                }).replace(/\. /g, '-').replace(/\./g, '');
            }
        }

        let actionsHtml = '';
        if (isCompleted) {
            actionsHtml = `<button class="btn btn-outline btn-toggle-answer" onclick="toggleInquiryAnswer(this)">답변 보기</button>`;
        } else {
            actionsHtml = `
                <button class="btn btn-outline" onclick="editInquiry(${inquiry.inquiryId})">수정하기</button>
                <button class="btn btn-outline delete" onclick="confirmDeleteInquiry(${inquiry.inquiryId})">삭제하기</button>
            `;
        }

        html += `
            <div class="inquiry-card" id="inquiry-${inquiry.inquiryId}">
              <div class="inquiry-header">
                <div class="inquiry-title">${inquiry.title}</div>
                <span class="status-badge ${statusClass}">${statusText}</span>
              </div>
              <div class="inquiry-meta">
                <span class="inquiry-date">${formattedDate}</span>
              </div>
              <div class="inquiry-content">
                ${inquiry.questionContent.substring(0, 100)}${inquiry.questionContent.length > 100 ? '...' : ''}
              </div>
              ${isCompleted ? `
                <div class="inquiry-answer-container hidden" style="display: none; margin-top: 15px; padding: 15px; border-top: 1px dashed #ccc; background-color: #f5f8ff; border-radius: 4px;">
                    <div style="font-weight: bold; margin-bottom: 8px; color: #1565c0; display: flex; justify-content: space-between;">
                        <span>[문의 답변]</span>
                        <span style="font-weight: normal; font-size: 0.85em; color: #777;">작성일: ${answeredAtDate}</span>
                    </div>
                    <div class="answer-content" style="white-space: pre-wrap; color: #333; line-height: 1.6;">
                        ${inquiry.answerContent || '답변 내용이 없습니다.'}
                    </div>
                </div>
              ` : ''}
              <div class="inquiry-actions">
                ${actionsHtml}
              </div>
            </div>
        `;
    });
    container.innerHTML = html;
}

/**
 * 문의 내역의 페이징 UI를 렌더링합니다.
 */
function renderInquiryPagination(totalPages, currentPage1Based) {
    const paginationContainer = document.getElementById('inquiries-pagination');
    if (totalPages <= 1) {
        paginationContainer.innerHTML = '';
        return;
    }

    let html = '';
    const currentPage0Based = currentPage1Based - 1;

    const isPrevDisabled = currentPage0Based === 0;
    html += `<button class="page-btn nav-btn" onclick="if(!${isPrevDisabled}) fetchMyInquiries(${currentPage0Based - 1})" ${isPrevDisabled ? 'disabled' : ''}>&lt;</button>`;

    for (let i = 0; i < totalPages; i++) {
        const pageNumber = i + 1;
        const activeClass = (i === currentPage0Based) ? 'active' : '';
        html += `<button class="page-btn ${activeClass}" onclick="fetchMyInquiries(${i})">${pageNumber}</button>`;
    }

    const isNextDisabled = currentPage0Based === totalPages - 1;
    html += `<button class="page-btn nav-btn" onclick="if(!${isNextDisabled}) fetchMyInquiries(${currentPage0Based + 1})" ${isNextDisabled ? 'disabled' : ''}>&gt;</button>`;

    paginationContainer.innerHTML = html;
}

// =================================================================================
// [2] 문의 관련 버튼 클릭 이벤트 핸들러 (전역 함수)
// =================================================================================

function goToInquiry() {
    window.location.href = 'http://localhost:8080/qna';
}

function toggleInquiryAnswer(buttonElement) {
    const card = buttonElement.closest('.inquiry-card');
    const answerContainer = card ? card.querySelector('.inquiry-answer-container') : null;

    if (answerContainer) {
        if (answerContainer.style.display === 'none' || answerContainer.classList.contains('hidden')) {
            answerContainer.style.display = 'block';
            answerContainer.classList.remove('hidden');
            buttonElement.textContent = '답변 숨기기';
        } else {
            answerContainer.style.display = 'none';
            answerContainer.classList.add('hidden');
            buttonElement.textContent = '답변 보기';
        }
    }
}

function editInquiry(inquiryId) {
    const card = document.getElementById(`inquiry-${inquiryId}`);
    const title = card.querySelector(".inquiry-title").textContent;
    const content = card.querySelector(".inquiry-content").textContent;

    document.getElementById("edit-inquiry-id").value = inquiryId;
    document.getElementById("edit-inquiry-title").value = title;
    document.getElementById("edit-inquiry-content").value = content;
    document.getElementById("edit-inquiry-modal").classList.remove("hidden");
}

document.getElementById("cancel-edit").addEventListener("click", () => {
    document.getElementById("edit-inquiry-modal").classList.add("hidden");
});

document.getElementById("edit-inquiry-form").addEventListener("submit", function (e) {
    e.preventDefault();

    const inquiryId = document.getElementById("edit-inquiry-id").value;
    const newTitle = document.getElementById("edit-inquiry-title").value.trim();
    const newContent = document.getElementById("edit-inquiry-content").value.trim();

    if (!newTitle || !newContent) {
        showMessageBox("❌ 제목과 내용을 모두 입력해야 합니다.", "error");
        return;
    }

    const csrfToken  = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // 기존 경로 유지 (백엔드 이동 전 호환성 보장)
    fetch("/inquiries/my/update", {
        method: "POST",
        headers: { "Content-Type": "application/json", [csrfHeader]: csrfToken },
        body: JSON.stringify({ inquiryId, title: newTitle, content: newContent })
    })
        .then(response => response.text())
        .then(msg => {
            showMessageBox(msg, "success");
            document.getElementById("edit-inquiry-modal").classList.add("hidden");
            fetchMyInquiries(currentInquiryPage);
        })
        .catch(err => {
            console.error("수정 오류:", err);
            showMessageBox("❌ 문의 수정 중 오류가 발생했습니다.", "error");
        });
});

function confirmDeleteInquiry(inquiryId) {
    showConfirmBox(`문의 ID ${inquiryId}를 정말 삭제하시겠습니까? (답변 대기 중만 가능)`, () => {
        deleteInquiry(inquiryId);
    });
}

function deleteInquiry(inquiryId) {
    const csrfToken  = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // 기존 경로 유지 (백엔드 이동 전 호환성 보장)
    fetch('/inquiries/my/delete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', [csrfHeader]: csrfToken},
        body: JSON.stringify({ inquiryId: inquiryId })
    })
        .then(response => {
            if (response.ok) {
                return response.text().then(text => ({ success: true, message: text || "성공적으로 삭제되었습니다." }));
            }
            return response.text().then(text => {
                console.error(`API Error ${response.status}:`, text);
                return { success: false, message: `삭제에 실패했습니다. (HTTP 상태코드: ${response.status})` };
            });
        })
        .then(result => {
            showMessageBox(result.message, result.success ? 'success' : 'error');
            if (result.success) {
                fetchMyInquiries(currentInquiryPage);
            }
        })
        .catch(error => {
            showMessageBox("삭제 중 네트워크 또는 서버 오류가 발생했습니다.", 'error');
            console.error('삭제 오류 (네트워크):', error);
        });
}

// =================================================================================
// [3] DOMContentLoaded 블록 (기존 로직 통합)
// =================================================================================

// 게시글 삭제
function performDelete(url, itemName, itemElement) {
    if (itemElement) {
        itemElement.style.transition = 'opacity 0.3s ease';
        itemElement.style.opacity = '0.5';
        itemElement.style.pointerEvents = 'none';
    }

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch(url, {
        method: 'DELETE',
        headers: { [csrfHeader]: csrfToken }
    })
        .then(response => {
            if (response.ok) return response.json();
            return response.json().then(err => Promise.reject(err));
        })
        .then(data => {
            alert(data.message || `${itemName}이(가) 성공적으로 삭제되었습니다.`);

            if (itemElement) {
                itemElement.style.opacity = '0';
                setTimeout(() => itemElement.remove(), 500);
            }
        })
        .catch(error => {
            console.error(`${itemName} 삭제 중 오류 발생:`, error);
            alert(error.message || '삭제에 실패했습니다. 다시 시도해주세요.');

            if (itemElement) {
                itemElement.style.opacity = '1';
                itemElement.style.pointerEvents = 'auto';
            }
        });
}

// 좋아요 삭제
function performUnlike(likeType, id, itemElement) {
    if (itemElement) {
        itemElement.style.transition = 'opacity 0.3s ease';
        itemElement.style.opacity = '0.5';
        itemElement.style.pointerEvents = 'none';
    }

    const memberIdx = document.querySelector('.mypage-main').dataset.memberIdx;
    if (!memberIdx) {
        alert('사용자 정보를 찾을 수 없습니다. 다시 로그인해주세요.');
        if (itemElement) {
            itemElement.style.opacity = '1';
            itemElement.style.pointerEvents = 'auto';
        }
        return;
    }

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const requestData = { likeType, memberIdx };
    if (likeType === 'RECIPE') requestData.recipeId = id;
    else if (likeType === 'BOARD') requestData.boardId = id;

    fetch('/like/remove', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', [csrfHeader]: csrfToken },
        body: JSON.stringify(requestData)
    })
        .then(response => {
            if (response.ok) return response.json();
            return response.json().then(err => Promise.reject(err));
        })
        .then(data => {
            alert('좋아요가 취소되었습니다.');
            if (itemElement) {
                itemElement.style.opacity = '0';
                setTimeout(() => itemElement.remove(), 500);
            }
        })
        .catch(error => {
            console.error('좋아요 취소 중 오류 발생:', error);
            alert(error.message || '좋아요 취소에 실패했습니다.');
            if (itemElement) {
                itemElement.style.opacity = '1';
                itemElement.style.pointerEvents = 'auto';
            }
        });
}

document.addEventListener('DOMContentLoaded', function() {

    const profileUpdateForm = document.getElementById('profile-update-form');

    if (profileUpdateForm) {
        profileUpdateForm.addEventListener('submit', function (event) {
            event.preventDefault();

            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
            const formData = new FormData(profileUpdateForm);
            const submitBtn = profileUpdateForm.querySelector('button[type="submit"]');

            fetch('/auth/update', {
                method: 'POST',
                headers: { [csrfHeader]: csrfToken },
                body: new URLSearchParams(formData)
            })
                .then(response => response.json())
                .then(data => {
                    alert(data.message);
                    if (data.success) {
                        const newNickname = formData.get('nickname');
                        document.querySelector('.profile-title').textContent = newNickname + '님의 마이페이지';
                        document.getElementById('nickname').value = newNickname;

                        const headerNicknameElement = document.querySelector('#header-nickname');
                        if (headerNicknameElement) headerNicknameElement.textContent = newNickname;
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('❌ 요청 처리 중 오류가 발생했습니다.');
                })
                .finally(() => {
                // ✅ 무조건 버튼 잠금 + 회색 처리
                submitBtn.disabled = true;
                submitBtn.classList.add("btn-disabled");
            });
        });
    }

    if (window.lucide && lucide.createIcons) lucide.createIcons();
    initializeTabSwitching();

    function initializeTabSwitching() {
        const menuItems = document.querySelectorAll('.menu-item');

        menuItems.forEach(item => {
            item.addEventListener('click', function () {
                const tabId = this.dataset.tab;

                menuItems.forEach(menu => menu.classList.remove('active'));
                this.classList.add('active');

                const url = tabId === 'profile'
                    ? window.location.pathname
                    : `${window.location.pathname}?tab=${tabId}&page=1`;
                window.history.replaceState(null, '', url);

                showTab(tabId, true);
            });
        });
    }

    initializeProfileImage();

    function initializeProfileImage() {
        const profileImage = document.getElementById('profile-image');
        const avatarFallback = document.getElementById('avatar-fallback');

        if (profileImage && avatarFallback) {
            profileImage.addEventListener('error', function () {
                profileImage.style.display = 'none';
                avatarFallback.style.display = 'flex';
            });

            profileImage.addEventListener('load', function () {
                profileImage.style.display = 'block';
                avatarFallback.style.display = 'none';
            });
        }
    }

    const btnCreateRecipe = document.getElementById('btn-create-recipe');
    if (btnCreateRecipe) {
        btnCreateRecipe.addEventListener('click', function () {
            // 작성 페이지 이동 등
        });
    }

    document.addEventListener('click', function (e) {
        if (e.target.closest('.btn-edit')) return;
        if (e.target.closest('.btn-view') || e.target.closest('.btn-view-recipe')) return;

        const deleteBtn = e.target.closest('.btn-delete');
        if (deleteBtn) {
            e.preventDefault();

            if (deleteBtn.classList.contains('btn-unlike')) {
                const id = deleteBtn.dataset.id;
                const likeType = deleteBtn.dataset.likeType;
                const itemElement = deleteBtn.closest('.mypage-like-item');
                if (!id || !likeType) return;

                showConfirmBox('이 항목의 좋아요를 취소하시겠습니까?', () => {
                    performUnlike(likeType, id, itemElement);
                });
                return;
            }

            const id = deleteBtn.dataset.id;
            if (!id) return;

            const recipeItem = deleteBtn.closest('.recipe-item');
            const commentItem = deleteBtn.closest('.comment-item');

            let url, itemName, itemElement;

            if (recipeItem) {
                url = `/api/boards/${id}`;
                itemName = '게시글';
                itemElement = recipeItem;
            } else if (commentItem) {
                url = `/reviews/${id}`;
                itemName = '댓글';
                itemElement = commentItem;
            } else {
                return;
            }

            showConfirmBox(`이 ${itemName}을(를) 정말 삭제하시겠습니까?`, () => {
                performDelete(url, itemName, itemElement);
            });
        }
    });

    const passwordChangeForm = document.getElementById('password-change-form');
    if (passwordChangeForm) {
        passwordChangeForm.addEventListener('submit', function (event) {
            event.preventDefault();

            const currentPassword = document.getElementById('current-password').value.trim();
            const newPassword     = document.getElementById('new-password').value.trim();
            const confirmPassword = document.getElementById('confirm-password').value.trim();

            const pwPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z0-9])(?!.*\s).{10,20}$/;

            if (!pwPattern.test(newPassword)) {
                alert("❌ 비밀번호는 공백 없이 10~20자, 영문/숫자/특수문자를 모두 포함해야 합니다.");
                return;
            }
            if (newPassword !== confirmPassword) {
                alert("❌ 새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
                return;
            }

            const csrfToken  = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

            const submitBtn = passwordChangeForm.querySelector('button[type="submit"]');
            if (submitBtn) submitBtn.disabled = true;

            fetch('/auth/change-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', [csrfHeader]: csrfToken },
                body: JSON.stringify({ currentPassword, newPassword })
            })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => { throw new Error(text || '비밀번호 변경 실패'); });
                    }
                    return response.text();
                })
                .then(message => {
                    alert(message || '✅ 비밀번호가 성공적으로 변경되었습니다.');
                    passwordChangeForm.reset();
                })
                .catch(error => {
                    console.error('비밀번호 변경 오류:', error);
                    alert(error.message || '❌ 서버 오류가 발생했습니다.');
                })
                .finally(() => {
                    // ✅ 요청이 끝나면 버튼 잠금 + 회색 처리
                    submitBtn.disabled = true;
                    submitBtn.classList.add("btn-disabled");
                });
        });
    }

    const btnDeleteAccount = document.querySelector('.btn-danger');
    if (btnDeleteAccount) {
        btnDeleteAccount.addEventListener('click', function() {
            // 계정 삭제 확인 등
        });
    }

    const statCards = document.querySelectorAll('.stat-card');
    statCards.forEach(card => {
        card.addEventListener('mouseenter', function() {});
    });

    const menuList = document.querySelector('.menu-list');
    if (menuList && window.innerWidth <= 1024) {}

    window.addEventListener('resize', function() {});

    const statusSelect = document.getElementById('inquiry-status');
    const sortSelect = document.getElementById('inquiry-sort');

    if (statusSelect && sortSelect) {
        statusSelect.addEventListener('change', () => fetchMyInquiries(0));
        sortSelect.addEventListener('change', () => fetchMyInquiries(0));
    }

    // 페이지 로딩 완료 후 초기화
    initializePage();
});

document.addEventListener('DOMContentLoaded', function () {
    const settingsTab = document.querySelector('[data-tab="settings"]');
    if (settingsTab) {
        settingsTab.addEventListener('click', function (e) {
            e.preventDefault();

            const provider = document.getElementById('provider')?.value || '';
            const isSocial = provider !== '';

            if (isSocial) {
                showTab('settings', true);
            } else {
                showPasswordVerifyModal();
            }
        });
    }
});

// 비밀번호 확인 모달
function showPasswordVerifyModal() {
    const existingModal = document.getElementById('password-verify-modal');
    if (existingModal) existingModal.remove();

    const modal = document.createElement('div');
    modal.id = 'password-verify-modal';
    modal.classList.add('modal-overlay');
    modal.innerHTML = `
      <div class="modal-content">
        <h2>🔒 비밀번호 확인</h2>
        <p>계정 설정에 접근하려면 비밀번호를 입력하세요.</p>
        <input type="password" id="verify-password" placeholder="비밀번호 입력" />
        <div class="modal-actions">
          <button id="verify-btn" class="btn-confirm">확인</button>
          <button id="cancel-btn" class="btn-cancel">취소</button>
        </div>
      </div>
    `;

    modal.style.cssText = `
      position: fixed; top:0; left:0; width:100%; height:100%;
      background: rgba(0,0,0,0.5); display:flex; justify-content:center; align-items:center; z-index:2000;
    `;
    modal.querySelector('.modal-content').style.cssText = `
      background:#fff; padding:20px; border-radius:8px; width:350px; text-align:center;
    `;
    modal.querySelector('.modal-actions').style.cssText = `
      margin-top:15px; display:flex; justify-content:space-around;
    `;

    document.body.appendChild(modal);

    document.getElementById('verify-btn').addEventListener('click', () => {
        const password = document.getElementById('verify-password').value.trim();
        verifyPassword(password, modal);
    });
    document.getElementById('cancel-btn').addEventListener('click', () => {
        modal.remove();
        showTab('profile');
    });

    document.getElementById('verify-password').addEventListener('keydown', function (e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            document.getElementById('verify-btn').click();
        }
    });
}

// 서버에 비밀번호 검증 요청
function verifyPassword(password, modal) {
    if (!password) {
        alert("비밀번호를 입력해주세요.");
        return;
    }

    const csrfToken  = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch('/mypage/verify-settings', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', [csrfHeader]: csrfToken },
        body: JSON.stringify({ password })
    })
        .then(res => {
            if (res.ok) {
                modal.remove();
                showTab('settings', true);
            } else {
                alert("❌ 비밀번호가 올바르지 않습니다.");
            }
        })
        .catch(err => {
            console.error("비밀번호 검증 오류:", err);
            alert("서버 오류가 발생했습니다.");
        });
}

// 페이지 초기화 함수
function initializePage() {
    const urlParams = new URLSearchParams(window.location.search);
    const initialTabId = urlParams.get('tab') || 'profile';

    showTab(initialTabId, false);
    checkProfileImage();
}

// 탭 표시 함수
function showTab(tabId, isClickEvent = false) {
    const tabContents = document.querySelectorAll('.tab-content');
    const menuItems = document.querySelectorAll('.menu-item');

    tabContents.forEach(tab => tab.classList.remove('active'));
    menuItems.forEach(item => item.classList.remove('active'));

    const selectedTab = document.getElementById(`tab-${tabId}`);
    const selectedMenu = document.querySelector(`[data-tab="${tabId}"]`);

    if (selectedTab) {
        setTimeout(() => { selectedTab.classList.add('active'); }, 50);

        // 문의 탭
        if (tabId === 'inquiries') {
            const urlParams = new URLSearchParams(window.location.search);
            let pageToLoad = 0;
            if (!isClickEvent) {
                const pageParam = parseInt(urlParams.get('page'));
                if (!isNaN(pageParam) && pageParam > 0) pageToLoad = pageParam - 1;
            } else {
                pageToLoad = 0;
            }
            fetchMyInquiries(pageToLoad);
        }

        // [A] 내 레시피 / 내 댓글 / 좋아요 페이징 로드
        if (tabId === 'recipes') {
            fetchMypageList('my-posts', 0);
        }
        if (tabId === 'comments') {
            fetchMypageList('my-comments', 0);
        }
        if (tabId === 'liked') {
            // 두 탭 모두 첫 페이지 로드
            fetchMypageList('liked-recipes', 0);
            fetchMypageList('liked-posts', 0);
        }
    }

    if (selectedMenu) selectedMenu.classList.add('active');
}

// 프로필 이미지 체크 함수
function checkProfileImage() {
    const profileImage = document.getElementById('profile-image');
    const avatarFallback = document.getElementById('avatar-fallback');

    if (profileImage && avatarFallback) {
        profileImage.style.display = 'block';
        avatarFallback.style.display = 'none';
    }
}

// 폼 유효성/유틸 등(그대로 유지)
function validateNickname(nickname) { return true; }
function validateEmail(email) { return true; }
function validatePassword(password) { return true; }
function validatePasswordMatch(password, confirmPassword) { return password === confirmPassword; }
function showNotification(message, type = 'info') { console.log('알림:', message, type); }
function showLoading(show = true) { console.log('로딩 상태:', show); }
function showConfirmDialog(message, callback) { console.log('확인 다이얼로그:', message); }
function updateProfile(data) { console.log('프로필 업데이트:', data); }
function formatDate(date) { return date; }
function formatNumber(number) { return number.toLocaleString(); }
function formatFileSize(bytes) { return bytes + ' bytes'; }

// 좋아요 레시피/게시글 전환 탭
(function () {
    const root = document.getElementById('tab-liked');
    if (!root) return;

    const btns = root.querySelectorAll('.mypage-like-tabbtn');
    const panes = root.querySelectorAll('.mypage-like-pane');

    btns.forEach(btn => {
        btn.addEventListener('click', () => {
            const key = btn.getAttribute('data-like-tab'); // 'admin' | 'user'

            // 버튼 on/off
            btns.forEach(b => b.classList.toggle('is-active', b === btn));

            // 패널 on/off
            panes.forEach(p => {
                const isMatch = p.id === `mypage-like-pane-${key}`;
                p.classList.toggle('is-active', isMatch);
            });

            // 접근성 속성 업데이트
            btns.forEach(b => b.setAttribute('aria-selected', b === btn ? 'true' : 'false'));

            // [B] 탭 전환 시 해당 목록 로드
            if (key === 'admin') fetchMypageList('liked-recipes', 0);
            else fetchMypageList('liked-posts', 0);
        });
    });
})();

// 프로필 업로드
document.addEventListener('DOMContentLoaded', function () {
    const profileInput = document.getElementById('profile-upload-input');
    const profileImage = document.getElementById('profile-image');
    const csrfToken  = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    if (profileInput) {
        profileInput.addEventListener('change', function () {
            const file = this.files[0];
            if (!file) return;

            const formData = new FormData();
            formData.append("memberIdx", currentMemberIdx);
            formData.append('profileImage', file);

            fetch('/file/profile-upload', {
                method: 'POST',
                headers: { [csrfHeader]: csrfToken },
                body: formData
            })
                .then(res => res.json())
                .then(data => {
                    if (data.filePath) {
                        fetch('/mypage/profile/update', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded', [csrfHeader]: csrfToken },
                            body: new URLSearchParams({ filePath: data.filePath })
                        }).then(() => {
                            profileImage.src = data.filePath + '?v=' + new Date().getTime();
                            alert('✅ 프로필 이미지가 변경되었습니다.');
                        });
                    } else {
                        alert('❌ 업로드 실패: ' + (data.message || '서버 오류'));
                    }
                })
                .catch(err => {
                    console.error(err);
                    alert('❌ 업로드 중 오류가 발생했습니다.');
                });
        });
    }
});

/* ================================================================================
 * [A] 추가: 마이페이지 공통 페이징 (내 레시피 / 내 댓글 / 좋아요 2종)
 * ================================================================================ */

let currentMyPostsPage = 0;
let currentMyCommentsPage = 0;
let currentLikedRecipesPage = 0;
let currentLikedPostsPage = 0;

const MYPAGE_PAGE_SIZE = 5;

/**
 * 특정 탭의 데이터를 비동기로 가져와 렌더링합니다.
 * @param {'my-posts'|'my-comments'|'liked-recipes'|'liked-posts'} tabType
 * @param {number} page 0-based
 */
function fetchMypageList(tabType, page) {
    const container = document.getElementById(`${tabType}-list-container`);
    const pagination = document.getElementById(`${tabType}-pagination`);
    if (!container || !pagination) return;

    // 현재 페이지 상태 저장
    if (tabType === 'my-posts') currentMyPostsPage = page;
    if (tabType === 'my-comments') currentMyCommentsPage = page;
    if (tabType === 'liked-recipes') currentLikedRecipesPage = page;
    if (tabType === 'liked-posts') currentLikedPostsPage = page;

    container.innerHTML = `<p class="loading-message">불러오는 중...</p>`;
    pagination.innerHTML = '';

    let apiUrl = '';
    switch (tabType) {
        case 'my-posts':
            apiUrl = `/api/mypage/my-posts?page=${page}&size=${MYPAGE_PAGE_SIZE}`;
            break;
        case 'my-comments':
            apiUrl = `/api/mypage/my-comments?page=${page}&size=${MYPAGE_PAGE_SIZE}`;
            break;
        case 'liked-recipes':
            apiUrl = `/api/mypage/liked/recipes?page=${page}&size=${MYPAGE_PAGE_SIZE}`;
            break;
        case 'liked-posts':
            apiUrl = `/api/mypage/liked/posts?page=${page}&size=${MYPAGE_PAGE_SIZE}`;
            break;
        default:
            return;
    }

    fetch(apiUrl)
        .then(res => {
            if (!res.ok) throw new Error(`${tabType} API 호출 실패`);
            return res.json();
        })
        .then(data => {
            renderMypageList(tabType, data.data || [], container);
            renderMypagePagination(tabType, data.totalPages || 1, data.page || 1);
        })
        .catch(err => {
            console.error(`${tabType} 불러오기 실패:`, err);
            container.innerHTML = `<p class="error-message">데이터를 불러올 수 없습니다.</p>`;
        });
}

/**
 * 각 탭별 데이터 렌더링 (기존 클래스/이벤트 위임에 맞춤)
 */
function renderMypageList(tabType, list, container) {
    if (!list || list.length === 0) {
        container.innerHTML = '<p class="no-data-message">데이터가 없습니다.</p>';
        return;
    }

    let html = '';

    if (tabType === 'my-posts') {
        // recipe-item / btn-delete 등 기존 이벤트 위임에 맞춤
        html = list.map(p => `
            <div class="recipe-item">
                <img src="${p.thumbnail || '/images/default_thumbnail.png'}" alt="썸네일" class="recipe-image" />
                <div class="recipe-info">
                    <div class="recipe-title">${p.title}</div>
                    <div class="recipe-meta">
                        <span class="recipe-stat">🔍 ${p.viewCount ?? 0}</span>
                        <span class="recipe-stat">❤️ ${p.likeCount ?? 0}</span>
                        <span class="recipe-stat">${p.insertTime ? new Date(p.insertTime).toLocaleDateString('ko-KR') : ''}</span>
                    </div>
                </div>
                <div class="recipe-actions">
                    <a href="/board/view?boardId=${p.boardId}" class="btn-view">조회</a>
                    <a href="/board/edition?boardId=${p.boardId}" class="btn-edit">수정</a>
                    <button type="button" class="btn-delete" data-id="${p.boardId}">삭제</button>
                </div>
            </div>
        `).join('');
    }

    if (tabType === 'my-comments') {
        html = list.map(c => `
            <div class="comment-item link-card" data-href="/board/view?boardId=${c.boardId}">
                <div class="comment-header">
                    <h3 class="comment-recipe-title">${c.boardTitle}</h3>
                    <span class="comment-date">${c.insertTime ? new Date(c.insertTime).toLocaleDateString('ko-KR') : '-'}</span>
                </div>
                <p class="comment-content">${c.content ?? ''}</p>
                <div class="comment-actions">
                    <a class="btn-view" href="/board/view?boardId=${c.boardId}" onclick="event.stopPropagation();">레시피 보기</a>
                    <button type="button" class="btn-delete" data-id="${c.reviewId}">삭제</button>
                </div>
            </div>
        `).join('');
    }

    if (tabType === 'liked-recipes') {
        html = list.map(r => `
            <div class="mypage-like-item link-card" data-href="/recipe/view?recipeId=${r.recipeId}">
                <div class="mypage-like-left">
                    <img class="mypage-like-thumb"
                         src="${r.thumbnail || '/images/default_recipe.jpg'}"
                         alt="${r.titleKr || ''}"
                         onerror="this.src='/images/default_recipe.jpg'"/>
                    <div class="mypage-like-info">
                        <div class="mypage-like-title">${r.titleKr || ''}</div>
                        <div class="mypage-like-meta">
                            <span class="category-badge" data-category="${r.categoryKr || '기타'}">${r.categoryKr || '기타'}</span>
                        </div>
                    </div>
                </div>
                <div class="mypage-like-actions">
                    <a href="/recipe/view?recipeId=${r.recipeId}" class="btn-view" onclick="event.stopPropagation();">조회</a>
                    <button type="button" class="btn-delete btn-unlike" data-like-type="RECIPE" data-id="${r.recipeId}">삭제</button>
                </div>
            </div>
        `).join('');
    }

    if (tabType === 'liked-posts') {
        html = list.map(p => `
            <div class="mypage-like-item link-card" data-href="/board/view?boardId=${p.boardId}">
                <div class="mypage-like-left">
                    <img class="mypage-like-thumb"
                         src="${p.thumbnail || '/images/default_thumbnail.png'}"
                         alt="${p.title || ''}"
                         onerror="this.src='/images/default_thumbnail.png'"/>
                    <div class="mypage-like-info">
                        <div class="mypage-like-title">${p.title || ''}</div>
                        <div class="mypage-like-meta">
                            <span class="meta-author">by ${p.writerName || '-'}</span>
                            <span class="category-badge" data-category="${p.category || '기타'}">${p.category || '기타'}</span>
                        </div>
                    </div>
                </div>
                <div class="mypage-like-actions">
                    <a class="btn-view" href="/board/view?boardId=${p.boardId}" onclick="event.stopPropagation();">조회</a>
                    <button type="button" class="btn-delete btn-unlike" data-like-type="BOARD" data-id="${p.boardId}">삭제</button>
                </div>
            </div>
        `).join('');
    }

    container.innerHTML = html;
}

/**
 * 공통 페이지네이션 렌더링
 */
function renderMypagePagination(tabType, totalPages, currentPage1Based) {
    const pagination = document.getElementById(`${tabType}-pagination`);
    if (!pagination) return;
    if (!totalPages || totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }

    const current = currentPage1Based - 1;
    let html = '';

    html += `<button class="page-btn nav-btn" ${current === 0 ? 'disabled' : ''} onclick="fetchMypageList('${tabType}', ${current - 1})">&lt;</button>`;

    for (let i = 0; i < totalPages; i++) {
        html += `<button class="page-btn ${i === current ? 'active' : ''}" onclick="fetchMypageList('${tabType}', ${i})">${i + 1}</button>`;
    }

    html += `<button class="page-btn nav-btn" ${current === totalPages - 1 ? 'disabled' : ''} onclick="fetchMypageList('${tabType}', ${current + 1})">&gt;</button>`;

    pagination.innerHTML = html;
}

/* ======================================================================
 * [B] 닉네임 실시간 유효성 검사 (회원가입과 동일한 UX, 마이페이지 전용)
 * ====================================================================== */
document.addEventListener("DOMContentLoaded", () => {
    const nicknameInput = document.getElementById("nickname");
    const nicknameHelp = document.getElementById("nicknameHelp");
    const nicknameSuccess = document.getElementById("nicknameSuccess");
    const nicknameError = document.getElementById("nicknameError");
    const submitBtn = document.querySelector("#profile-update-form button[type='submit']");

    if (!nicknameInput) return; // 안전장치

    let originalNickname = nicknameInput.value.trim();
    let nicknameTimer;
    let nicknameValid = true;

    const showHelp = (msg) => {
        nicknameHelp.textContent = msg;
        nicknameHelp.style.display = "block";
        nicknameHelp.style.color = "#6b7280";
        nicknameSuccess.style.display = "none";
        nicknameError.style.display = "none";
    };

    const showSuccess = (msg) => {
        nicknameSuccess.textContent = msg;
        nicknameSuccess.style.display = "block";
        nicknameSuccess.style.color = "#16a34a";
        nicknameHelp.style.display = "none";
        nicknameError.style.display = "none";
    };

    const showError = (msg) => {
        nicknameError.querySelector("span")
            ? nicknameError.querySelector("span").textContent = msg
            : nicknameError.textContent = msg;
        nicknameError.style.display = "block";
        nicknameError.style.color = "#dc2626";
        nicknameHelp.style.display = "none";
        nicknameSuccess.style.display = "none";
    };

    const hideAll = () => {
        nicknameHelp.style.display = "none";
        nicknameSuccess.style.display = "none";
        nicknameError.style.display = "none";
    };

    // ✅ 초기 안내문
    showHelp("* 변경하실 닉네임은 2자 이상 입력하세요.");
    if (submitBtn) {
        submitBtn.disabled = true;             // 비활성화
        submitBtn.classList.add("btn-disabled"); // 회색 처리
    }

    nicknameInput.addEventListener("input", () => {
        clearTimeout(nicknameTimer);
        nicknameTimer = setTimeout(async () => {
            const nickname = nicknameInput.value.trim();

            hideAll();
            nicknameValid = false;
            submitBtn.disabled = true;
            submitBtn.classList.add("btn-disabled");

            // 너무 짧음
            if (nickname.length < 2) {
                showError("닉네임은 최소 2자 이상이어야 합니다.");
                return;
            }

            // 원래 닉네임이면 검사 생략
            if (nickname === originalNickname) {
                showHelp("현재 사용 중인 닉네임입니다.");
                nicknameValid = true;
                submitBtn.disabled = false;
                submitBtn.classList.remove("btn-disabled"); // 원래 색 복원
                return;
            }

            // 서버 중복검사
            try {
                const res = await fetch(`/auth/check-nickname?nickname=${encodeURIComponent(nickname)}`);
                const result = (await res.text()).trim();

                if (result === "true") {
                    showSuccess("사용 가능한 닉네임입니다.");
                    nicknameValid = true;
                    submitBtn.disabled = false;
                    submitBtn.classList.remove("btn-disabled"); // 원래 색 복원
                } else {
                    showError("이미 사용 중인 닉네임입니다.");
                    nicknameValid = false;
                    submitBtn.disabled = true;
                    submitBtn.classList.add("btn-disabled");
                }
            } catch (err) {
                console.error("닉네임 검사 오류:", err);
                showError("서버 오류가 발생했습니다.");
                nicknameValid = false;
                submitBtn.disabled = true;
                submitBtn.classList.add("btn-disabled");
            }
        }, 500);
    });

    // 폼 제출 시 유효성 확인
    const form = document.getElementById("profile-update-form");
    form.addEventListener("submit", (e) => {
        if (!nicknameValid) {
            e.preventDefault();
            showError("유효하지 않은 닉네임입니다.");
        }
    });
});


document.addEventListener("DOMContentLoaded", function () {
    const newPwEl = document.getElementById("new-password");
    const confirmEl = document.getElementById("confirm-password");
    const submitBtn = document.querySelector("#password-change-form button[type='submit']");
    const pwHelp = document.getElementById("pwHelp");
    const pwSuccess = document.getElementById("pwSuccess");
    const pwError = document.getElementById("pwError");
    const cpError = document.getElementById("cpError");
    const curEl = document.getElementById("current-password");

    const PW_PATTERN = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z0-9])(?!.*\s).{10,20}$/;

    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.classList.add("btn-disabled"); // 처음엔 회색 처리
    }

    function show(el) { el.style.display = "block"; }
    function hide(el) { el.style.display = "none"; }

    function validateRealtime() {
        hide(pwHelp); hide(pwSuccess); hide(pwError); hide(cpError);

        const pw = newPwEl.value.trim();
        const cp = confirmEl.value.trim();
        let ok = true;

        // 1️⃣ 형식 체크
        if (!PW_PATTERN.test(pw)) {
            hide(pwHelp); hide(pwSuccess); show(pwError);
            pwError.textContent = "형식이 맞지 않습니다. (10~20자, 영문/숫자/특수문자 포함)";
            ok = false;
        } else {
            hide(pwHelp); hide(pwError); show(pwSuccess);
        }

        // 2️⃣ 현재 PW 동일 체크
        if (curEl && curEl.value && pw === curEl.value) {
            hide(pwHelp); hide(pwSuccess); show(pwError);
            pwError.textContent = "새 비밀번호가 현재 비밀번호와 같습니다.";
            ok = false;
        }

        // 3️⃣ 새 비밀번호 확인 일치 체크
        if (cp && pw !== cp) {
            show(cpError);
            ok = false;
        } else {
            hide(cpError);
        }

        // 4️⃣ 버튼 활성화 + 색상 복원
        submitBtn.disabled = !ok;
        if (ok) {
            submitBtn.classList.remove("btn-disabled"); // 원래색 복원
        } else {
            submitBtn.classList.add("btn-disabled"); // 회색 처리
        }
    }

    newPwEl.addEventListener("input", validateRealtime);
    confirmEl.addEventListener("input", validateRealtime);
});
