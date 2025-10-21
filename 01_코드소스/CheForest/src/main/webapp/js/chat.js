// let : 변수
// const : 상수
// function : 함수
// document : html 접근 (dom)
// async : 비동기 처리

// ----------------------------------------------------
// 🌟 [추가] 서버에서 가져올 최대 허용 이모티콘 개수 (기본값 0)
let USER_MAX_EMOTE_COUNT = 0;
// ----------------------------------------------------

// 🌟 [추가] 등급별 제목 정의 (emojiList의 4개씩 묶음에 대응)
const TIER_NAMES = ["씨앗 ⚡", "뿌리 ⚓", "새싹 🌱", "나무 🌲", "숲 🌳"];

// 하드코딩된 이모티콘 리스트 (4개씩 등급별로 묶여 있음)
const emojiList = [
    // 0: 씨앗 (4개)
    "/emoji/반가워요.png",
    "/emoji/잘가요.png",
    "/emoji/화이팅.png",
    "/emoji/추천해요.png",
    // 4: 뿌리 (4개)
    "/emoji/goooood.png",
    "/emoji/맛있어요.png",
    "/emoji/요리해요.png",
    "/emoji/우와.png",
    // 8: 새싹 (4개)
    "/emoji/아쉬운데요.png",
    "/emoji/슬퍼요.png",
    "/emoji/고소해요.png",
    "/emoji/부드러워요.png",
    // 12: 나무 (4개)
    "/emoji/싫은데요.png",
    "/emoji/싱거워요.png",
    "/emoji/짜요.png",
    "/emoji/매워요.png",
    // 16: 숲 (4개)
    "/emoji/진짜좋은데요.png",
    "/emoji/맛없어요.png",
    "/emoji/우욱.png",
    "/emoji/불쇼에요.png",
];

let stompClient = null;

// 🌟 [추가] 사용자 정보를 가져와 최대 이모티콘 허용 개수를 설정하는 비동기 함수
async function fetchUserInfo() {
    if (!IS_LOGGED_IN) {
        console.log("로그인되지 않아 이모티콘 정보를 가져오지 않습니다.");
        return;
    }

    try {
        // 서버의 /api/user/info 엔드포인트 호출
        const res = await fetch(CONTEXT_PATH + "/api/user/info");
        if (res.ok) {
            const data = await res.json();
            USER_MAX_EMOTE_COUNT = data.maxEmoteCount; // 서버 응답 값 저장
            console.log(`[이모티콘] 현재 등급 허용 개수: ${USER_MAX_EMOTE_COUNT}`);
        } else {
            console.error("사용자 정보 로드 실패:", res.status);
        }
    } catch (e) {
        console.error("사용자 정보 API 호출 중 오류 발생:", e);
    }
}


// 연결
function connect() {
    const socket = new SockJS(CONTEXT_PATH + "/ws")
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        // 구독
        stompClient.subscribe("/sub/message", (msg) => {
            // 🌟🌟🌟 이 부분이 핵심입니다! 🌟🌟🌟
            console.log("--- 서버로부터 수신된 원본 메시지 (msg.body) ---");
            console.log(msg.body);

            const chat = JSON.parse(msg.body);

            console.log("--- JSON 파싱 후 chat 객체 ---");
            console.log(chat);
            // 🌟🌟🌟 닉네임과 함께 senderId가 오는지 여기서 확인해야 합니다. 🌟🌟🌟
            showMessage(chat)
        })

        loadChatHistory();
    });
}

// 이전 채팅 불러오기
async function loadChatHistory() {
    const res = await fetch(CONTEXT_PATH + "/chat/history");
    const messages = await res.json();
    messages.forEach(chat => {
        showMessage(chat);
    });
}

// 출력
function showMessage(chat) {
    const chatBox = document.getElementById("chatBox");

    // 🌟 MY_ID와 chat.senderId를 모두 콘솔에 출력합니다. 🌟
    console.log("--------------------------------------");
    console.log("MY_ID (현재 유저 ID):", MY_ID);
    console.log("SenderId (수신 메시지 ID):", chat.senderId);
    console.log("--------------------------------------");

    // 1. **PK(ID)를 사용하여 현재 사용자가 보낸 메시지인지 확인**
    //    서버에서 senderId를 숫자로 받고, MY_ID도 숫자로 변환하여 비교하는 것이 가장 안전합니다.
    const isSentByCurrentUser = Number(chat.senderId) === Number(MY_ID);

    // 2. 메시지 유형에 따른 CSS 클래스 이름 설정
    const rowClassName = isSentByCurrentUser ? "message-message-my" : "message-message";
    const msgBubbleClassName = isSentByCurrentUser ? "message-my" : "message";

    // 3. 메시지 전체를 감쌀 행(row) 컨테이너 생성 (CSS로 가로 정렬 담당)
    const rowDiv = document.createElement("div");
    rowDiv.classList.add("message-row", rowClassName);

    // A. 프로필 이미지 컨테이너
    const profileImgDiv = document.createElement("div");
    profileImgDiv.classList.add("message-profile-img");
    const img = document.createElement("img");
    img.src = chat.profile || "/image/default.png";
    img.width = 40; img.height = 40; img.style.borderRadius = "50%";
    profileImgDiv.appendChild(img);

    // B. 말풍선과 닉네임을 담을 컨테이너 (CSS로 수직 정렬 담당)
    const contentWrapper = document.createElement("div");
    contentWrapper.classList.add("message-content-wrapper");

    // 닉네임 영역 (말풍선 위에 위치)
    const senderName = document.createElement("div");
    senderName.classList.add("message-sender-name");
    senderName.innerHTML = `<strong>${chat.sender}</strong>`;

    // 🌟 1. 닉네임 추가 순서 수정: 상대방 메시지일 경우 닉네임을 가장 먼저 contentWrapper에 추가합니다. 🌟
    if (!isSentByCurrentUser) {
        contentWrapper.appendChild(senderName); // (닉네임이 첫 번째 요소)
    }

    // 실제 말풍선 버블
    const msgBubble = document.createElement("div");
    msgBubble.classList.add("message-bubble", msgBubbleClassName);

    // 메시지 내용 (텍스트 또는 이모티콘)
    if (chat.message.startsWith("/emoji/")) {
        msgBubble.innerHTML = `<img src='${chat.message}' width='100' height='100'>`;
    } else {
        msgBubble.textContent = chat.message;
    }
    contentWrapper.appendChild(msgBubble); // (말풍선이 두 번째 요소)

    // C. 전송 시간 영역
    const timeDiv = document.createElement("div");
    timeDiv.classList.add("message-time");
    timeDiv.textContent = chat.time; // 예: "3분 전"

    // ** 상대방 메시지 (좌측 정렬): 프로필, 닉네임(위에), 버블, 시간 모두 표시 **
    if (!isSentByCurrentUser) {
        // [프로필] [닉네임+버블] [시간] 순서

        // 🌟 2. 메시지 조립 시, 여기서 닉네임을 다시 추가하지 않습니다. (중복 방지)

        rowDiv.appendChild(profileImgDiv);
        rowDiv.appendChild(contentWrapper);
        rowDiv.appendChild(timeDiv);
    }

    // ** 내 메시지 (우측 정렬): 버블과 시간만 표시 **
    else {
        // 1. 버블 래퍼 정렬 설정
        contentWrapper.style.alignItems = 'flex-end';

        // 2. 시간과 버블을 묶을 래퍼 생성
        const bubbleAndInfoWrapper = document.createElement("div");
        bubbleAndInfoWrapper.classList.add("my-bubble-info-wrapper");

        // 3. 시간과 말풍선을 래퍼에 추가 (시간, 버블 순서)
        bubbleAndInfoWrapper.appendChild(timeDiv);
        bubbleAndInfoWrapper.appendChild(contentWrapper);

        // 4. 최종 행(rowDiv)에 추가
        rowDiv.appendChild(bubbleAndInfoWrapper);
    }

    // 최종적으로 전체 행을 채팅창에 추가
    chatBox.appendChild(rowDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
}

// 발행
function sendMessage() {
    const input = document.getElementById("msgInput").value.trim();

    if (!input) {
        alert("메시지를 입력하세요.");
        return;
    }

    const message = {
        type: "TEXT",
        message: input
    };
    // 🚨 경로 충돌 해결 후, 서버 경로에 맞춰 수정 필요: /pub/chat/message
    stompClient.send("/pub/message", {}, JSON.stringify(message));
    document.getElementById("msgInput").value = "";
}

// 이모티콘 전송
function sendEmoji(url) {
    const message = {
        type: "IMAGE",
        message: url
    };
    // 🚨 경로 충돌 해결 후, 서버 경로에 맞춰 수정 필요: /pub/chat/message
    stompClient.send("/pub/message", {}, JSON.stringify(message));
}

// 🌟 [수정] 이모티콘 버튼 생성 및 잠금 처리 로직 추가 (등급 제목 삽입 로직 추가) 🌟
function loadEmojis() {
    const emojiBox = document.getElementById("emojiBox");
    emojiBox.innerHTML = ''; // 중복 방지를 위해 초기화

    for (let index = 0; index < emojiList.length; index++) {
        const url = emojiList[index];

        // 🌟🌟🌟 핵심 수정: 4개마다 등급 제목 삽입 🌟🌟🌟
        if (index % 4 === 0) {
            const tierIndex = index / 4; // 0, 1, 2, 3, 4
            const headerDiv = document.createElement("div");
            headerDiv.classList.add("emoji-tier-header");
            headerDiv.innerHTML = `<h4>${TIER_NAMES[tierIndex]}</h4>`;
            emojiBox.appendChild(headerDiv);
        }
        // 🌟🌟🌟 여기까지 등급 제목 삽입 🌟🌟🌟


        // 현재 이모티콘의 인덱스가 허용 개수보다 크거나 같으면 잠금 처리합니다.
        const isLocked = index >= USER_MAX_EMOTE_COUNT;

        const btn = document.createElement("button");
        btn.style.border = "none";
        btn.style.background = "transparent";
        btn.style.padding = "2px";

        // UI 스타일 변경: 잠금 상태일 경우 투명도와 커서를 변경
        btn.style.opacity = isLocked ? "0.3" : "1.0";
        btn.style.cursor = isLocked ? "not-allowed" : "pointer";

        btn.innerHTML = `<img src="${url}" width="75" height="75" />`;

        if (isLocked) {
            // 잠금된 이모티콘은 전송 대신 경고 메시지 표시
            btn.onclick = (e) => {
                e.preventDefault();
                alert(`잠금된 이모티콘입니다. 등급업 하세요.`);
            };
        } else {
            // 해금된 이모티콘만 전송 가능
            btn.onclick = () => sendEmoji(url);
        }

        emojiBox.appendChild(btn);
    }
}


// 버튼 클릭으로 전송
function handleClick(event) {
    sendMessage();
    event.preventDefault();
}

// 엔터키로 전송
function handleKey(event) {
    if (event.key === "Enter") {
        sendMessage();
        event.preventDefault();
    }
}

// 🌟 [수정] DOMContentLoaded를 async로 변경하고 fetchUserInfo를 먼저 호출합니다. 🌟
window.addEventListener("DOMContentLoaded", async () => {

    // 1. 사용자 정보(maxEmoteCount)를 먼저 가져옵니다. (로그인 안 되어 있으면 0 유지)
    await fetchUserInfo();

    // 2. 이모티콘 패널을 로드합니다. (가져온 maxEmoteCount 값을 사용)
    loadEmojis();

    // 3. STOMP 연결 및 채팅 기록 로드
    connect();

    // ... (기존 이벤트 리스너 로직 유지)

    const btn = document.getElementById("chatBtn");;
    const chatContainer = document.querySelector(".chat-container");
    const closeBtn = document.getElementById("chat-close-btn");
    const emojiToggleBtn = document.getElementById("emoji-toggle-btn");
    const emojiPanel = document.getElementById("emojiBox");
    const chatInput = document.getElementById("msgInput");
    const chatSendBtn = document.getElementById("chat-send-btn");

    // 1. 스티키 버튼 (채팅창만 토글)
    btn.addEventListener("click", () => {
        chatContainer.style.display =
            (chatContainer.style.display === "none" || chatContainer.style.display === "")
                ? "flex" // 🌟 채팅창을 엽니다. 🌟
                : "none";

        // 채팅창 닫힐 때, 패널도 닫습니다.
        if (chatContainer.style.display === "none") {
            emojiPanel.style.display = "none";
        }
    });

    // 2. 닫기 버튼 (채팅창만 닫기)
    closeBtn.addEventListener("click", () => {
        chatContainer.style.display = "none";
        emojiPanel.style.display = "none";
    });

// 3. 🌟 이모티콘 토글 버튼 로직 수정 🌟
    if (IS_LOGGED_IN) {
        // [로그인 상태]: 정상적으로 패널을 토글합니다.
        emojiToggleBtn.addEventListener("click", () => {
            emojiPanel.style.display =
                (emojiPanel.style.display === "none" || emojiPanel.style.display === "")
                    ? "flex"
                    : "none";
        });

    } else {
        // [로그아웃 상태]: 클릭 시 로그인 페이지로 이동 유도
        const redirectLogin = () => {
            if (confirm("로그인 후 채팅을 이용할 수 있습니다.\n지금 로그인하시겠습니까?")) {
                window.location.href = "/auth/login?redirect=" + encodeURIComponent(window.location.pathname);
            }
        };

        // 1️⃣ 이모티콘 토글 버튼
        emojiToggleBtn.addEventListener("click", (e) => {
            e.preventDefault();
            redirectLogin();
        });

        // 2️⃣ 입력창
        chatInput.placeholder = "로그인 하세요.";
        chatInput.readOnly = true;
        chatInput.addEventListener("click", (e) => {
            e.preventDefault();
            redirectLogin();
        });

        // 3️⃣ 전송 버튼
        // ✅ 기존 HTML onClick 제거 → handleClick(event) 차단
        chatSendBtn.removeAttribute("onClick");

        // ✅ 전송 버튼 클릭 시 로그인 이동만 수행
        chatSendBtn.addEventListener("click", (e) => {
            e.preventDefault();
            redirectLogin();
        });

        // 4️⃣ 이모티콘 패널 버튼
        document.querySelectorAll("#emojiBox button").forEach(btn => {
            btn.onclick = (e) => {
                e.preventDefault();
                redirectLogin();
            };
        });
    }
});