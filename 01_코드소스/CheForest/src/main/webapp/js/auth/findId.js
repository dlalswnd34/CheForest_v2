// CheForest 아이디 찾기 페이지 JavaScript

const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

let findIdTimer = null;
let findIdTimeLeft = 0; // 초 단위 (300초 = 5분)

document.addEventListener('DOMContentLoaded', function() {
    initializeFindIdPage();
});

// 페이지 초기화 함수
function initializeFindIdPage() {
    const btnBack = document.getElementById('btn-back');
    const emailInput = document.getElementById('email');
    const emailCodeInput = document.getElementById('email-code');
    const btnSendCode = document.getElementById('btn-send-code');
    const btnVerifyCode = document.getElementById('btn-verify-code');

    if (btnBack) {
        btnBack.addEventListener('click', goToLogin);
    }

    if (emailInput) {
        emailInput.addEventListener('input', () => {
            resetVerificationState();
            updateSendCodeButton();
        });
    }

    if (emailCodeInput) {
        emailCodeInput.addEventListener('input', () => {
            updateVerifyCodeButton();
        });

        emailCodeInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') verifyEmailCode();
        });
    }

    if (btnSendCode) {
        btnSendCode.addEventListener('click', sendEmailCode);
    }

    if (btnVerifyCode) {
        btnVerifyCode.addEventListener('click', verifyEmailCode);
    }

    document.getElementById('btn-go-login')?.addEventListener('click', goToLogin);
    document.getElementById('btn-find-password')?.addEventListener('click', goToFindPassword);
    document.getElementById('btn-footer-login')?.addEventListener('click', goToLogin);
    document.getElementById('btn-footer-find-password')?.addEventListener('click', goToFindPassword);
    document.getElementById('btn-help')?.addEventListener('click', showCustomerSupport);

    setupRealTimeValidation();
    updateSendCodeButton();
    updateVerifyCodeButton();
}

// ================= 서버 연동 =================

// 인증번호 발송
async function sendEmailCode() {
    const email = document.getElementById('email').value.trim();
    if (!validateEmail(email)) return;

    showLoadingState('btn-send-code');
    try {
        const res = await fetch('/auth/find-id/send-code', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded', [csrfHeader]: csrfToken },
            body: new URLSearchParams({ email })
        });
        const result = await res.text();
        hideLoadingState('btn-send-code');

        if (result === 'OK') {
            showCodeInput();
            updateSendCodeButtonText('발송완료');
            showAlertModal('인증번호가 발송되었습니다. 이메일을 확인해주세요.');

            startFindIdTimer(5 * 60); // ✅ 5분 타이머 시작
        } else {
            showAlertModal(result || '계정을 찾을 수 없습니다.');
        }
    } catch (err) {
        hideLoadingState('btn-send-code');
        showAlertModal('서버 오류가 발생했습니다.');
    }
}

// 인증번호 확인
async function verifyEmailCode() {
    const email = document.getElementById('email').value.trim();
    const code = document.getElementById('email-code').value.trim();
    if (!validateCode(code)) return;

    showLoadingState('btn-verify-code');
    try {
        const res = await fetch('/auth/find-id/verify-code', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded', [csrfHeader]: csrfToken },
            body: new URLSearchParams({ email, code })
        });
        const result = await res.text();

        hideLoadingState('btn-verify-code');

        if (result && !result.startsWith('❌') && result !== 'FAIL') {
            clearFindIdTimer(); // ✅ 타이머 중단
            showResult(result);
        } else {
            showAlertModal(result || '인증번호가 올바르지 않습니다.');
        }
    } catch (err) {
        hideLoadingState('btn-verify-code');
        showAlertModal('서버 오류가 발생했습니다.');
    }
}

// ================= 타이머 관리 =================
function startFindIdTimer(seconds) {
    clearFindIdTimer(); // 기존 타이머 초기화
    findIdTimeLeft = seconds;

    const timerEl = createOrGetTimerElement();
    updateTimerDisplay(timerEl);

    findIdTimer = setInterval(() => {
        findIdTimeLeft--;
        if (findIdTimeLeft <= 0) {
            clearFindIdTimer();
            disableVerificationInputs();
            showAlertModal('⏰ 인증번호가 만료되었습니다. 다시 요청해주세요.');
            return;
        }
        updateTimerDisplay(timerEl);
    }, 1000);
}

function clearFindIdTimer() {
    if (findIdTimer) {
        clearInterval(findIdTimer);
        findIdTimer = null;
        removeTimerElement();
    }
}

function createOrGetTimerElement() {
    let timerEl = document.getElementById('timer-text');
    if (!timerEl) {
        timerEl = document.createElement('p');
        timerEl.id = 'timer-text';
        timerEl.style.color = '#ef4444';
        timerEl.style.fontSize = '0.9rem';
        timerEl.style.marginTop = '8px';
        timerEl.style.textAlign = 'left';
        document.getElementById('code-group').appendChild(timerEl);
    }
    return timerEl;
}

function removeTimerElement() {
    const el = document.getElementById('timer-text');
    if (el) el.remove();
}

function updateTimerDisplay(el) {
    const min = Math.floor(findIdTimeLeft / 60);
    const sec = findIdTimeLeft % 60;
    el.innerHTML = `⏳ 남은 시간: <span style="color:#ef4444;">${min}:${sec.toString().padStart(2, '0')}</span>`;
}

function disableVerificationInputs() {
    const codeInput = document.getElementById('email-code');
    const btnVerify = document.getElementById('btn-verify-code');
    codeInput.disabled = true;
    btnVerify.disabled = true;
}

// ================= 유틸리티 =================

// 이메일 검사
function validateEmail(email) {
    if (!email) {
        showAlertModal('이메일을 입력해주세요.');
        return false;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showAlertModal('올바른 이메일 형식이 아닙니다.');
        return false;
    }
    return true;
}

// 코드 검사
function validateCode(code) {
    if (!code) {
        showAlertModal('인증번호를 입력해주세요.');
        return false;
    }
    if (code.length !== 6) {
        showAlertModal('인증번호는 6자리여야 합니다.');
        return false;
    }
    return true;
}

// 인증번호 입력창 표시
function showCodeInput() {
    const codeGroup = document.getElementById('code-group');
    if (codeGroup) {
        codeGroup.style.display = 'flex';
        codeGroup.classList.add('fade-in');
        document.getElementById('email-code')?.focus();
    }
}

// 결과 표시
function showResult(foundId) {
    document.getElementById('verification-step')?.classList.add('fade-out');
    document.getElementById('card-footer')?.classList.add('fade-out');

    setTimeout(() => {
        document.getElementById('verification-step').style.display = 'none';
        document.getElementById('card-footer').style.display = 'none';

        const resultStep = document.getElementById('result-step');
        document.getElementById('found-id-text').textContent = foundId;
        resultStep.style.display = 'block';
        resultStep.classList.add('fade-in');
    }, 400);
}

// 초기화
function resetVerificationState() {
    document.getElementById('code-group').style.display = 'none';
    document.getElementById('email-code').value = '';
    updateSendCodeButtonText('인증발송');
}

// 버튼 활성화
function updateSendCodeButton() {
    const email = document.getElementById('email').value.trim();
    document.getElementById('btn-send-code').disabled = !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}
function updateVerifyCodeButton() {
    const code = document.getElementById('email-code').value.trim();
    document.getElementById('btn-verify-code').disabled = !code;
}
function updateSendCodeButtonText(text) {
    const btn = document.getElementById('btn-send-code');
    btn.querySelector('.btn-text').textContent = text;
    if (text === '발송완료') btn.disabled = true;
}

// 페이지 이동
function goToLogin() {
    window.location.href = '/auth/login';
}
function goToFindPassword() {
    window.location.href = '/auth/find-password';
}
function showCustomerSupport() {
    showAlertModal('고객센터(1588-0000)로 문의해주세요.');
}

// 로딩 상태
function showLoadingState(buttonId) {
    const btn = document.getElementById(buttonId);
    btn.classList.add('loading');
    btn.disabled = true;
}
function hideLoadingState(buttonId) {
    const btn = document.getElementById(buttonId);
    btn.classList.remove('loading');
    btn.disabled = false;
}

// ================= 모달 알림 =================
function showAlertModal(message) {
    const modal = document.getElementById('alertModal');
    const msg = document.getElementById('alertModalMessage');
    const closeBtn = document.getElementById('alertModalClose');

    if (msg) msg.textContent = message;
    if (modal) modal.style.display = 'flex';

    closeBtn?.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    document.addEventListener('keydown', function escHandler(e) {
        if (e.key === 'Escape') {
            modal.style.display = 'none';
            document.removeEventListener('keydown', escHandler);
        }
    });
}

function showModal(message) {
    document.getElementById("modalMessage").textContent = message;
    document.getElementById("alertModal").style.display = "flex";
}

function closeModal(id) {
    document.getElementById(id).style.display = "none";
}

// 실시간 유효성 검사
function setupRealTimeValidation() {
    const emailInput = document.getElementById('email');
    const codeInput = document.getElementById('email-code');
    if (emailInput) {
        emailInput.addEventListener('blur', () => {
            emailInput.style.borderColor = validateEmail(emailInput.value.trim()) ? '' : '#ef4444';
        });
    }
    if (codeInput) {
        codeInput.addEventListener('blur', () => {
            codeInput.style.borderColor = (codeInput.value.trim().length === 6) ? '' : '#ef4444';
        });
    }
}
