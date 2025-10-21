// 이메일 인증 타이머용 전역
let emailTimer;
let timeLeft = 300; // 5분 (초 단위)

// ==============================
// 전역 함수 (JSP inline 이벤트에서도 호출 가능)
// ==============================
function clearError(fieldId) {
    const errorEl = document.getElementById(fieldId + "Error");
    if (errorEl) {
        errorEl.style.display = "none";
        const span = errorEl.querySelector("span");
        if (span) span.textContent = "";
    }
}

// ==============================
// CheForest 회원가입 JavaScript (실시간 검증 + AJAX 방식)
// ==============================
document.addEventListener("DOMContentLoaded", function () {
    // CSRF 토큰 (Spring Security)
    const csrfToken = document.querySelector("meta[name='_csrf']")?.content;
    const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.content;

    // ✅ [구조 수정] AJAX 함수를 바깥으로 이동하여 어디서든 사용할 수 있게 합니다.
    async function ajaxRequest(url, method, data = {}, contentType = 'application/x-www-form-urlencoded; charset=UTF-8') {
        const headers = {
            'Content-Type': contentType
        };
        if (csrfHeader && csrfToken && ['POST', 'PUT', 'DELETE', 'PATCH'].includes(method.toUpperCase())) {
            headers[csrfHeader] = csrfToken;
        }

        const options = { method, headers };

        if (method.toUpperCase() === "GET") {
            if (Object.keys(data).length > 0) {
                url += (url.includes("?") ? "&" : "?") + new URLSearchParams(data).toString();
            }
        } else {
            if (contentType === 'application/json') {
                options.body = JSON.stringify(data); // 데이터를 JSON 문자열로 변환
            } else {
                options.body = new URLSearchParams(data).toString();
            }
        }

        try {
            const response = await fetch(url, options);
            const responseText = await response.text();

            if (!response.ok) {
                throw new Error(responseText || `서버 에러: ${response.status}`);
            }

            const resContentType = response.headers.get("content-type");
            if (resContentType && resContentType.includes("application/json")) {
                return JSON.parse(responseText);
            } else {
                return responseText;
            }
        } catch (error) {
            console.error("AJAX 요청 실패:", error);
            throw error;
        }
    }

    // HTML 요소 가져오기
    const signupForm = document.getElementById("signupForm");
    const userIdInput = document.getElementById("userId");
    const emailInput = document.getElementById("email");
    const emailCodeInput = document.getElementById("emailCode");
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirmPassword");
    const nicknameInput = document.getElementById("nickname");

    const emailVerificationSection = document.getElementById("emailVerificationSection");
    const emailSuccess = document.getElementById("emailSuccess");
    const emailSentMessage = document.getElementById("emailSentMessage");
    const nicknameSuccess = document.getElementById("nicknameSuccess");
    const userIdSuccess = document.getElementById("userIdSuccess");

    const sendEmailBtn = document.getElementById("sendEmailBtn");
    const verifyEmailBtn = document.getElementById("verifyEmailBtn");

    const successModal = document.getElementById("successModal");
    const modalTitle = document.getElementById("modalTitle");
    const modalMessage = document.getElementById("modalMessage");
    const modalOkBtn = document.getElementById("modalOkBtn");
    const toast = document.getElementById("toast");
    const toastMessage = document.getElementById("toastMessage");

    let verificationState = {
        userIdChecked: false,
        emailSent: false,
        emailVerified: false,
        nicknameChecked: false,
    };

    // (이하 다른 이벤트 리스너들은 변경 없음)
    let userIdTimer;
    userIdInput.addEventListener("keyup", () => {
        clearTimeout(userIdTimer);
        userIdTimer = setTimeout(async () => {
            const id = userIdInput.value.trim();
            hideElement(userIdSuccess);
            clearError("userId");
            verificationState.userIdChecked = false;

            if (id.length < 8) {
                return showError("userId", "아이디는 최소 8자 이상이어야 합니다.");
            }
            try {
                const result = await ajaxRequest("/auth/check-id", "GET", { loginId: id });
                if (String(result) === 'true') {
                    verificationState.userIdChecked = true;
                    showElement(userIdSuccess);
                    clearError("userId");
                } else {
                    hideElement(userIdSuccess);
                    showError("userId", "이미 사용중인 아이디입니다.");
                }
            } catch (err) {
                showErrorModal("아이디 중복 확인 중 오류가 발생했습니다.");
            }
        }, 300);
    });

    let nicknameTimer;
    nicknameInput.addEventListener("keyup", () => {
        clearTimeout(nicknameTimer);
        nicknameTimer = setTimeout(async () => {
            const nickname = nicknameInput.value.trim();
            hideElement(nicknameSuccess);
            clearError("nickname");
            verificationState.nicknameChecked = false;

            if (nickname.length < 2) {
                return showError("nickname", "닉네임은 최소 2자 이상이어야 합니다.");
            }
            try {
                const result = await ajaxRequest("/auth/check-nickname", "GET", { nickname });
                if (String(result) === 'true') {
                    verificationState.nicknameChecked = true;
                    showElement(nicknameSuccess);
                    clearError("nickname");
                } else {
                    hideElement(nicknameSuccess);
                    showError("nickname", "이미 사용중인 닉네임입니다.");
                }
            } catch (err) {
                showErrorModal("닉네임 중복 확인 중 오류가 발생했습니다.");
            }
        }, 300);
    });

    passwordInput.addEventListener("keyup", () => {
        const password = passwordInput.value.trim();
        const pwPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()_+\-={}[\]|:;"'<>,.?/]).{10,20}$/;
        if (!pwPattern.test(password)) {
            showError("password", "비밀번호는 영문, 숫자, 특수문자를 포함해 10~20자로 입력하세요.");
        } else {
            clearError("password");
        }
    });

    confirmPasswordInput.addEventListener("keyup", () => {
        if (passwordInput.value !== confirmPasswordInput.value) {
            showError("confirmPassword", "비밀번호가 일치하지 않습니다.");
        } else {
            clearError("confirmPassword");
        }
    });

    emailInput.addEventListener("keyup", () => {
        const email = emailInput.value.trim();
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(email)) {
            showError("email", "올바른 이메일 형식이 아닙니다.");
            sendEmailBtn.disabled = true;
        } else {
            clearError("email");
            sendEmailBtn.disabled = false;
        }
    });

    if (sendEmailBtn) {
        sendEmailBtn.addEventListener("click", async () => {
            const email = emailInput.value.trim();
            if (!email) return showErrorModal("이메일을 입력해주세요.");

            try {
                // 이메일 발송은 x-www-form-urlencoded 방식 유지
                const result = await ajaxRequest("/auth/send-email-code", "POST", { email });
                if (String(result).trim().toUpperCase() === "OK") {
                    verificationState.emailSent = true;
                    showElement(emailVerificationSection);
                    showElement(emailSentMessage);
                    startEmailTimer();
                    showToast("인증번호가 발송되었습니다.", "success");
                } else {
                    showErrorModal("이메일 발송 실패", result);
                }
            } catch (err) {
                showErrorModal("이메일 발송 실패", err.message);
            }
        });
    }

    if (verifyEmailBtn) {
        verifyEmailBtn.addEventListener("click", async () => {
            const code = emailCodeInput.value.trim();
            const email = emailInput.value.trim();
            if (!code) return showErrorModal("인증번호를 입력해주세요.");
            if (!email) return showErrorModal("이메일을 입력해주세요.");

            try {
                const result = await ajaxRequest("/auth/verify-email-code", "POST", { email, code });
                if (String(result).trim().toUpperCase() === "OK") {
                    verificationState.emailVerified = true;
                    hideElement(emailCodeInput);
                    hideElement(verifyEmailBtn);
                    hideElement(emailSentMessage);
                    showElement(emailSuccess);
                    emailInput.disabled = true;
                    sendEmailBtn.disabled = true;

                    // ✅ 타이머 중단 및 숨김 처리 추가
                    clearInterval(emailTimer);
                    const emailCodeError = document.getElementById("emailCodeError");
                    if (emailCodeError) emailCodeError.style.display = "none";

                    showToast("이메일 인증이 완료되었습니다.", "success");
                } else {
                    showErrorModal("인증 실패", "인증번호가 일치하지 않습니다.");
                }
            } catch (err) {
                showErrorModal("인증 실패", err.message);
            }
        });
    }

    // ==============================
// 이메일 인증 타이머 (5분)
// ==============================
    function startEmailTimer() {
        clearInterval(emailTimer);
        timeLeft = 300;

        const emailCodeError = document.getElementById("emailCodeError");
        const span = emailCodeError.querySelector("span");

        emailTimer = setInterval(() => {
            const minutes = Math.floor(timeLeft / 60);
            const seconds = timeLeft % 60;
            if (span) {
                span.textContent = `남은 시간: ${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
                emailCodeError.style.display = "block";
            }
            if (timeLeft <= 0) {
                clearInterval(emailTimer);
                showErrorModal("인증시간 만료", "인증번호가 만료되었습니다. 다시 요청해주세요.");
                hideElement(emailVerificationSection);
                verificationState.emailSent = false;
            }
            timeLeft--;
        }, 1000);
    }

    // ✅ 회원가입 최종 제출
    if (signupForm) {
        signupForm.addEventListener("submit", async function (e) {
            e.preventDefault();
            if (!validateSignupForm()) return;

            const submitBtn = signupForm.querySelector('button[type="submit"]');

            try {
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.textContent = '가입 처리 중...';
                }

                const formData = {
                    loginId: userIdInput.value.trim(),
                    email: emailInput.value.trim(),
                    password: passwordInput.value.trim(),
                    confirmPassword: confirmPasswordInput.value.trim(),
                    nickname: nicknameInput.value.trim(),
                    emailAuthCode: emailCodeInput.value.trim()
                };

                // 👇 [핵심 수정사항] 최종 회원가입 요청을 원래의 Form 데이터 방식으로 되돌립니다.
                const result = await ajaxRequest("/auth/register/addition", "POST", formData);

                if (String(result).trim().toUpperCase() === "OK") {
                    showSuccessModal("회원가입 완료!", "성공적으로 가입되었습니다. 로그인 페이지로 이동합니다.");
                    if (modalOkBtn) {
                        modalOkBtn.onclick = () => {
                            window.location.href = "/auth/login";
                        };
                    }
                } else {
                    showErrorModal("회원가입 실패", result);
                }
            } catch (err) {
                showErrorModal("회원가입 실패", err.message);
            } finally {
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.textContent = '회원가입';
                }
            }
        });
    }

    // (이하 헬퍼 함수들은 변경 없음)
    function validateSignupForm() {
        if (!verificationState.userIdChecked) {
            showErrorModal("아이디 중복 확인을 완료해주세요.");
            return false;
        }
        if (!verificationState.nicknameChecked) {
            showErrorModal("닉네임 중복 확인을 완료해주세요.");
            return false;
        }
        if (!verificationState.emailVerified) {
            showErrorModal("이메일 인증을 완료해주세요.");
            return false;
        }
        const pw = passwordInput.value.trim();
        const cpw = confirmPasswordInput.value.trim();

        // ✅ 비밀번호 정규식 (10~20자, 영문/숫자/특수문자 포함, 공백 불가)
        const pwPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z0-9])(?!.*\s).{10,20}$/;

        if (!pwPattern.test(pw)) {
            showErrorModal(
                "비밀번호 조건 불일치",
                "비밀번호는 공백 없이 10~20자, 영문/숫자/특수문자를 모두 포함해야 합니다."
            );
            return false;
        }

        if (pw !== cpw) {
            showErrorModal("비밀번호가 서로 일치하지 않습니다.");
            return false;
        }
        return true;
    }

    function showError(fieldId, message) {
        const errorEl = document.getElementById(fieldId + "Error");
        if (errorEl) {
            const span = errorEl.querySelector("span");
            if (span) span.textContent = message;
            errorEl.style.display = "block";
        }
    }

    function showElement(el) { if (el) el.style.display = "block"; }
    function hideElement(el) { if (el) el.style.display = "none"; }

    function showSuccessModal(title, message) {
        if (modalTitle) modalTitle.textContent = title;
        if (modalMessage) modalMessage.textContent = message;
        if (successModal) successModal.style.display = "flex";
    }

    function showErrorModal(title, message = "") {
        if (modalTitle) modalTitle.textContent = title;
        if (modalMessage) modalMessage.textContent = message;
        if (successModal) successModal.style.display = "flex";
        if(modalOkBtn) modalOkBtn.onclick = () => successModal.style.display = 'none';
    }

    function showToast(message, type = "info") {
        if (!toast) return;
        toastMessage.textContent = message;
        toast.className = `toast ${type}`;
        toast.style.display = "block";
        setTimeout(() => (toast.style.display = "none"), 3000);
    }
});
