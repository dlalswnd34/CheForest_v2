// ✅ CSRF 토큰 전역 세팅
const csrfToken  = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");

/**
 * 좋아요 버튼 초기화 (BOARD / RECIPE 공용)
 * @param {Object} options
 * @param {"BOARD"|"RECIPE"} options.likeType
 * @param {number} options.boardId
 * @param {number|null} options.recipeId
 * @param {number} options.memberIdx
 */
function initLikeButton({ likeType, boardId, recipeId, memberIdx }) {
    const btn = document.getElementById("likeBtn");
    const countText = document.getElementById("likeCountText");
    if (!btn || !countText) return;

    // ✅ 숫자 출력 헬퍼
    function renderCount(raw) {
        const n = parseInt(raw, 10);
        const safe = (isNaN(n) || n < 0) ? 0 : n;
        countText.textContent = `${safe}`;
    }

    // ✅ 좋아요 수 불러오기
    fetch(`/like/count?likeType=${likeType}&boardId=${boardId}&recipeId=${recipeId ?? ""}`)
        .then(res => res.json())
        .then(renderCount)
        .catch(() => renderCount(0));

    // ✅ 로그인 상태라면 초기 좋아요 여부 확인
    if (memberIdx && memberIdx > 0) {
        fetch(`/like/check?likeType=${likeType}&boardId=${boardId}&recipeId=${recipeId ?? ""}&memberIdx=${memberIdx}`)
            .then(res => res.json())
            .then(liked => {
                if (liked === true || liked === "true") btn.classList.add("liked");
            })
            .catch(() => {});
    }

    // ✅ 버튼 클릭 이벤트
    btn.addEventListener("click", async () => {
        if (!memberIdx || memberIdx <= 0) {
            alert("로그인 후 이용해주세요 😊");
            const redirectUrl = encodeURIComponent(location.pathname + location.search);
            location.href = `/auth/login?redirect=${redirectUrl}`;
            return;
        }

        const isLiked = btn.classList.contains("liked");
        const url = `${window.location.origin}/like/${isLiked ? "remove" : "add"}`;

        btn.disabled = true; // 중복 클릭 방지

        try {
            const res = await fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({ likeType, boardId, recipeId, memberIdx })
            });

            if (!res.ok) throw new Error("좋아요 요청 실패");
            const data = await res.json();

            // ✅ 하트 토글
            btn.classList.toggle("liked", !isLiked);

            // ✅ 서버에서 최신 count 반영
            if (data && data.likeCount !== undefined) {
                renderCount(data.likeCount);
            } else {
                // 서버 응답이 비어있으면 강제 동기화
                const countRes = await fetch(`/like/count?likeType=${likeType}&boardId=${boardId}&recipeId=${recipeId ?? ""}`);
                const count = await countRes.json();
                renderCount(count);
            }
        } catch (err) {
            console.error("좋아요 처리 실패:", err);
            // 에러 시에도 최신화
            const countRes = await fetch(`/like/count?likeType=${likeType}&boardId=${boardId}&recipeId=${recipeId ?? ""}`);
            const count = await countRes.json();
            renderCount(count);
        } finally {
            btn.disabled = false; // 버튼 복원
        }
    });
}
