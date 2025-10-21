// === 전역 변수 ===
let chatbotBtn, chatbotWindow, closeBtn, msgBox, userInput, sendBtn, toggleBtn, quickBtns;

// JSP에서 contextPath 주입 안됐을 경우 대비
if (typeof contextPath === "undefined" || !contextPath) {
    var contextPath = "";
}

// === 메시지 출력 ===
function appendUserMessage(text) {
    const userMsg = document.createElement("div");
    userMsg.className = "message user-msg";
    userMsg.innerHTML = `<div class="bubble">${text}</div>`;
    msgBox.appendChild(userMsg);
    msgBox.scrollTop = msgBox.scrollHeight;
}
function appendBotMessage(html) {
    const botMsg = document.createElement("div");
    botMsg.className = "message bot-msg";
    botMsg.innerHTML = `
        <img src="${contextPath}/images/bear-mascot.png" alt="셰프봇" class="bot-avatar">
        <div class="bubble">${html}</div>
    `;
    msgBox.appendChild(botMsg);
    msgBox.scrollTop = msgBox.scrollHeight;
}

// === 캐러셀 카드 출력 ===
function renderCards(data, type) {
    if (!data || data.length === 0) {
        appendBotMessage("❌ 결과가 없습니다.");
        return;
    }

    let index = 0;
    const wrapperId = "carousel-" + Date.now();

    function getCard(item) {
        if (type === "recipe") {
            return `
                <div class="carousel-card"
                     onclick="window.open('${contextPath}/recipe/view?recipeId=${item.recipeId}', '_blank')">
                  <img class="carousel-img" src="${item.thumbnail ? item.thumbnail : contextPath + '/images/default-recipe.png'}" alt="레시피 이미지"/>
                  <h3>${item.titleKr}</h3>
                </div>`;
        }
        if (type === "board") {
            return `
                <div class="carousel-card"
                     onclick="window.open('${contextPath}/board/view?boardId=${item.boardId}', '_blank')">
                  <img class="carousel-img" src="${item.thumbnail ? item.thumbnail : contextPath + '/images/default-board.png'}" alt="게시글 이미지"/>
                  <h3>${item.title}</h3>
                </div>`;
        }
    }

    const html = `<div class="carousel-wrapper" id="${wrapperId}">${getCard(data[index])}</div>`;
    const wrapperDiv = document.createElement("div");
    wrapperDiv.innerHTML = html;
    msgBox.appendChild(wrapperDiv);
    msgBox.scrollTop = msgBox.scrollHeight;

    const wrapper = document.getElementById(wrapperId);

    // 자동 슬라이드 (3초마다 교체)
    setInterval(() => {
        index = (index + 1) % data.length;
        wrapper.innerHTML = getCard(data[index]);
    }, 3000);
}

// === 카테고리 버튼 ===
function showCategoryButtons(categories, type) {
    const botMsg = document.createElement("div");
    botMsg.className = "message bot-msg";
    const bubble = document.createElement("div");
    bubble.className = "bubble";

    const catDiv = document.createElement("div");
    catDiv.style.display = "flex";
    catDiv.style.flexWrap = "wrap";
    catDiv.style.gap = "6px";
    catDiv.style.marginTop = "8px";

    categories.forEach(cat => {
        const catBtn = document.createElement("button");
        catBtn.textContent = cat;
        catBtn.style.padding = "6px 10px";
        catBtn.style.border = "1px solid #ccc";
        catBtn.style.borderRadius = "6px";
        catBtn.style.cursor = "pointer";

        catBtn.onclick = () => {
            appendUserMessage(cat);
            $.ajax({
                url: contextPath + (type === "recipe" ? "/api/recipes/random" : "/api/boards/random"),
                type: "GET",
                data: { category: cat },
                dataType: "json",
                success: data => data ? renderCards([data], type) : appendBotMessage(`❌ "${cat}" ${type === "recipe" ? "레시피" : "유저레시피"} 없음`),
                error: () => appendBotMessage(`❌ ${type === "recipe" ? "추천 레시피" : "유저 레시피"} 불러오기 오류`)
            });
        };

        catDiv.appendChild(catBtn);
    });

    bubble.appendChild(catDiv);
    botMsg.appendChild(bubble);
    msgBox.appendChild(botMsg);
    msgBox.scrollTop = msgBox.scrollHeight;
}

// === 액션 매핑 ===
const actionHandlers = {
    "추천레시피": () => {
        appendBotMessage("🍴 사이트 레시피 카테고리를 골라주세요:");
        showCategoryButtons(["한식", "중식", "양식", "일식","디저트"], "recipe");
    },
    "추천메뉴": () => {
        appendBotMessage("👩‍🍳 유저 레시피 카테고리를 골라주세요:");
        showCategoryButtons(["한식", "중식", "양식", "일식","디저트"], "board");
    },
    "사이트레시피 랜덤추천": () => {
        appendBotMessage("🍴 카테고리를 골라주세요:");
        showCategoryButtons(["한식", "중식", "양식", "일식","디저트"], "recipe");
    },
    "유저레시피 랜덤추천": () => {
        appendBotMessage("👩‍🍳 카테고리를 골라주세요:");
        showCategoryButtons(["한식", "중식", "양식", "일식","디저트"], "board");
    },
    "사이트TOP5레시피": () => {
        $.ajax({
            url: contextPath + "/api/recipes/popular",
            type: "GET",
            dataType: "json",
            success: data => data.length ? renderCards(data, "recipe") : appendBotMessage("❌ 인기 레시피 없음"),
            error: () => appendBotMessage("❌ 인기 레시피 불러오기 오류")
        });
    },
    "유저TOP5레시피": () => {
        $.ajax({
            url: contextPath + "/api/boards/popular",
            type: "GET",
            dataType: "json",
            success: data => data.length ? renderCards(data, "board") : appendBotMessage("❌ 인기 게시글 없음"),
            error: () => appendBotMessage("❌ 인기 게시글 불러오기 오류")
        });
    },
    "문의하기": () => {
        const confirmed = confirm("QnA 페이지로 이동하시겠습니까?");
        if (confirmed) {
            window.location.href = contextPath + "/qna";
        } else {
            appendBotMessage("❌ QnA 페이지 이동이 취소되었습니다.");
        }
    },
    "사이트 이용가이드": () => {
        const confirmed = confirm("사이트 이용가이드 페이지로 이동하시겠습니까?");
        if (confirmed) {
            window.location.href = contextPath + "/support/guide";
        } else {
            appendBotMessage("❌ 이용가이드 페이지 이동이 취소되었습니다.");
        }
    },
};

// === 메시지 전송 ===
function sendMessage(text) {
    if (!text) return;
    appendUserMessage(text);

    if (actionHandlers[text]) {
        actionHandlers[text]();
        return;
    }

    // CSRF 토큰 읽기
    const csrfToken  = document.querySelector("meta[name='_csrf']").getAttribute("content");
    const csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

    // === GPT API 호출 (Spring Boot) ===
    $.ajax({
        url: contextPath + "/api/chatbot/ask",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ question: text }),
        beforeSend: function(xhr) {
            // CSRF 헤더 추가
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function(res) {
            let answer = null;
            if (res) {
                if (res.answer) answer = res.answer;
                else if (res.message) answer = res.message;
                else if (res.content) answer = res.content;
                else if (res.data && res.data.answer) answer = res.data.answer;
            }

            if (answer) {
                appendBotMessage(answer);
            } else {
                appendBotMessage("❌ 챗봇 응답이 없습니다.");
            }
        },
        error: function(xhr, status, err) {
            console.error("GPT 호출 오류:", err);
            appendBotMessage("❌ 챗봇 서버 호출 중 오류가 발생했습니다.");
        }
    });
}

// === DOM 준비 ===
document.addEventListener("DOMContentLoaded", () => {
    chatbotBtn = document.getElementById("chatbot-btn");
    chatbotWindow = document.getElementById("chatbot-window");
    closeBtn = document.getElementById("chatbot-close-btn");
    msgBox = document.getElementById("chatbot-messages");
    userInput = document.getElementById("chatbot-user-input");
    sendBtn = document.getElementById("chatbot-send-btn");

    toggleBtn = document.getElementById("chatbot-toggle-btn");
    quickBtns = document.getElementById("chatbot-quick-buttons");

    if (chatbotBtn) {
        chatbotBtn.addEventListener("click", () => {
            chatbotWindow.style.display = chatbotWindow.style.display === "flex" ? "none" : "flex";
            chatbotWindow.style.flexDirection = "column";
        });
    }
    if (closeBtn) closeBtn.addEventListener("click", () => chatbotWindow.style.display = "none");

    if (sendBtn) sendBtn.addEventListener("click", () => {
        sendMessage(userInput.value.trim());
        userInput.value = "";
    });
    if (userInput) userInput.addEventListener("keydown", e => {
        if (e.key === "Enter") {
            e.preventDefault();
            sendMessage(userInput.value.trim());
            userInput.value = "";
        }
    });

    document.querySelectorAll(".quick-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const action = btn.getAttribute("data-action");
            appendUserMessage(action);
            if (actionHandlers[action]) {
                actionHandlers[action]();
            } else {
                appendBotMessage(`"${action}"을 선택하셨습니다.`);
            }
        });
    });

    if (toggleBtn && quickBtns) {
        toggleBtn.addEventListener("click", () => {
            if (quickBtns.classList.contains("hidden")) {
                quickBtns.classList.remove("hidden");
                toggleBtn.textContent = "▼숨기기";
            } else {
                quickBtns.classList.add("hidden");
                toggleBtn.textContent = "▲올리기";
            }
        });
    }
});
