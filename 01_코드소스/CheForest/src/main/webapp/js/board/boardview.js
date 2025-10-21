(function () {
    // ========= 전역 값 (JSP에서 내려줌) =========
    const boardId = window.boardId || 0;
    const loginUser = window.loginUser || { memberIdx: 0, nickname: "익명" };

    // ========= CSRF =========
    const csrfToken  = document.querySelector('meta[name="_csrf"]')?.content || "";
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || "X-CSRF-TOKEN";
    const withCsrf = (headers = {}) => (csrfToken ? { ...headers, [csrfHeader]: csrfToken } : headers);

    // ========= DOM 캐시 =========
    const $commentsList = document.getElementById("commentsList");
    const $commentBtn   = document.getElementById("commentSubmitBtn");
    const $commentTa    = document.getElementById("commentTextarea");
    const $pager        = document.getElementById("commentsPager");

    // ========= API =========
    const API = {
        list:   ()             => `/reviews/board/${boardId}`,
        create: ()             => `/reviews`,
        update: (reviewId)     => `/reviews/${reviewId}`,
        remove: (reviewId)     => `/reviews/${reviewId}`,
    };

    // ========= 유틸 =========
    const getProfile = (p) => {
        if (!p || !String(p).trim()) return "/images/default_profile.png";
        const path = String(p).trim();
        // 이미 절대 경로인 경우 그대로 사용
        if (path.startsWith("/") || path.startsWith("http")) return path;
        return `/uploads/${path}`;
    };    const esc  = (s) => String(s ?? "").replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;");
    const br   = (s) => esc(s).replaceAll("\n","<br/>");

    // 서버 ReviewDto → 화면 모델 정규화 (닉네임 필드 최우선 매핑)
    function normalize(d) {
        return {
            id: d.reviewId,
            text: d.content ?? d.text ?? "",
            likeCount: d.likeCount ?? 0,
            createdAt: d.createdAt ?? d.insertTimeStr ?? "",
            writer: {
                memberIdx: d.writerIdx ?? d.writer?.memberIdx,
                nickname: d.writerNickname ?? d.writer?.nickname ?? d.nickname ?? d.memberNickname ?? "익명",
                profile: d.writerProfile ?? d.profile ?? d.writer?.profile ?? "",
                grade: d.writerGrade ?? d.grade ?? d.writer?.grade ?? ""
            },
            replies: Array.isArray(d.replies) ? d.replies.map(normalize) : []
        };
    }

    // ========= 아이콘 (간단 inline) =========
    const iconThumb = '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor"><path d="M7 10v11H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2z"/><path d="M7 10l4-8 2 5h5.5a2.5 2.5 0 0 1 0 5H17l1 5a2 2 0 0 1-2 2H7"/></svg>';
    const iconReply = '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor"><path d="M3 12h13l-4-4m4 4-4 4"/></svg>';

    // ========= 렌더러 =========
    function renderReply(r) {
        const mine = Number(loginUser.memberIdx || 0) === Number(r.writer.memberIdx || -1);
        return `
    <div class="reply-item" data-reply-id="${r.id}" data-raw="${esc(r.text)}">
      <div class="reply-border">
        <div class="reply-avatar">
          <img src="${getProfile(r.writer.profile)}" alt="avatar" class="avatar-img" onerror="this.src='/images/default_profile.png'"/>
        </div>
        <div class="reply-details">
          <div class="name-row">
            <span class="reply-author">${esc(r.writer.nickname)}</span>
            ${r.writer.grade ? `<span class="user-grade grade-${esc(r.writer.grade)}">${esc(r.writer.grade)}</span>` : ``}
            <span class="reply-time">${esc(r.createdAt)}</span>
          </div>
          <div class="reply-text"><div class="reply-plain">${br(r.text)}</div></div>

          <div class="reply-actions">
            <button class="like-vote" data-act="reply-like">${iconThumb}<span class="vote-count">${r.likeCount || 0}</span></button>
            <div class="right-actions">
              ${mine ? `
                <button class="edit-btn small" data-act="reply-edit">수정</button>
                <button class="delete-btn small" data-act="reply-delete">삭제</button>` : ``}
            </div>
          </div>
        </div>
      </div>
    </div>`;
    }

    function renderComment(c) {
        const mine = Number(loginUser.memberIdx || 0) === Number(c.writer.memberIdx || -1);
        return `
    <div class="comment-item" data-comment-id="${c.id}" data-raw="${esc(c.text)}">
      <div class="comment-header">
        <div class="commenter-avatar">
          <img src="${getProfile(c.writer.profile)}" alt="avatar" class="avatar-img" onerror="this.src='/images/default_profile.png'"/>
        </div>
        <div class="commenter-details">
          <div class="name-row">
            <span class="commenter-name">${esc(c.writer.nickname)}</span>
            ${c.writer.grade ? `<span class="user-grade grade-${esc(c.writer.grade)}">${esc(c.writer.grade)}</span>` : ``}
            <span class="comment-time">${esc(c.createdAt)}</span>
          </div>
          <div class="comment-body"><div class="comment-plain">${br(c.text)}</div></div>
        </div>
      </div>

      <div class="comment-actions">
        <button class="like-vote" data-act="like">${iconThumb}<span class="vote-count">${c.likeCount || 0}</span></button>
        <button class="reply-btn" data-act="toggle-reply">${iconReply}답글</button>
        <div class="right-actions">
          ${mine ? `
            <button class="edit-btn" data-act="edit">수정</button>
            <button class="delete-btn" data-act="delete">삭제</button>` : ``}
        </div>
      </div>

      <div class="reply-write" style="display:none;">
        <textarea class="reply-textarea" placeholder="답글을 입력하세요"></textarea>
        <div class="reply-actions">
          <button class="reply-submit-btn" data-act="reply-submit">답글 등록</button>
          <button class="cancel-btn" data-act="reply-cancel">취소</button>
        </div>
      </div>

      <div class="replies">${c.replies.map(renderReply).join("")}</div>
    </div>`;
    }

    // ========= 페이지네이션 상태 =========
    const PAGE_SIZE = 3;     // 한 페이지당 상위 댓글 3개
    let   ALL_COMMENTS = []; // 상위 댓글 트리 전체
    let   CURRENT_PAGE = 1;

    function renderCurrentPage() {
        if (!$commentsList) return;

        const totalTop   = ALL_COMMENTS.length;
        const totalPages = Math.max(1, Math.ceil(totalTop / PAGE_SIZE));
        if (CURRENT_PAGE > totalPages) CURRENT_PAGE = totalPages;

        const start = (CURRENT_PAGE - 1) * PAGE_SIZE;
        const end   = start + PAGE_SIZE;

        const pageSlice = ALL_COMMENTS.slice(start, end);
        $commentsList.innerHTML = pageSlice.map(renderComment).join("");

        renderPager(totalPages);
    }

    function renderPager(totalPages) {
        if (!$pager) return;
        if (totalPages <= 1) { $pager.innerHTML = ""; return; }

        const STEP = 10;
        const canPrev = (CURRENT_PAGE - STEP) >= 1;
        const canNext = (CURRENT_PAGE + STEP) <= totalPages;

        let html = "";
        html += `<button data-page="prev" ${!canPrev ? "disabled": ""}>«</button>`;
        for (let p = 1; p <= totalPages; p++) {
            html += `<button data-page="${p}" class="${p === CURRENT_PAGE ? "active" : ""}">${p}</button>`;
        }
        html += `<button data-page="next" ${!canNext ? "disabled" : ""}>»</button>`;
        $pager.innerHTML = html;
    }

    function initPagerEvents() {
        if (!$pager) return;
        $pager.addEventListener("click", (e) => {
            const btn = e.target.closest("button[data-page]");
            if (!btn) return;
            const val = btn.getAttribute("data-page");
            const totalPages = Math.max(1, Math.ceil(ALL_COMMENTS.length / PAGE_SIZE));
            const STEP = 10;

            if (val === "prev") {
                CURRENT_PAGE = Math.max(1, CURRENT_PAGE - STEP);
            } else if (val === "next") {
                CURRENT_PAGE = Math.min(totalPages, CURRENT_PAGE + STEP);
            } else {
                const num = parseInt(val, 10);
                if (!isNaN(num)) CURRENT_PAGE = num;
            }
            renderCurrentPage();
        });
    }

    // ========= 데이터 통신 =========
    async function loadComments() {
        if (!$commentsList) return;
        try {
            const res = await fetch(API.list(), { method: "GET" });
            if (!res.ok) throw new Error(res.status);
            const data = await res.json();

            ALL_COMMENTS = Array.isArray(data) ? data.map(normalize) : [];
            if (ALL_COMMENTS.length === 0) {
                $commentsList.innerHTML = `<div class="empty-box">아직 댓글이 없습니다.</div>`;
                if ($pager) $pager.innerHTML = "";
                return;
            }
            CURRENT_PAGE = 1;        // 첫 로드 1페이지
            renderCurrentPage();
        } catch (e) {
            console.error(e);
            $commentsList.innerHTML = `<div class="empty-box">댓글을 불러오지 못했습니다.</div>`;
            if ($pager) $pager.innerHTML = "";
        }
    }

    // 등록/수정/삭제 후 현재 페이지 유지
    async function reloadKeepPage() {
        const prevPage = CURRENT_PAGE;
        try {
            const res = await fetch(API.list(), { method: "GET" });
            if (!res.ok) throw new Error(res.status);
            const data = await res.json();
            ALL_COMMENTS = Array.isArray(data) ? data.map(normalize) : [];

            const totalPages = Math.max(1, Math.ceil(ALL_COMMENTS.length / PAGE_SIZE));
            CURRENT_PAGE = Math.min(prevPage, totalPages);
            renderCurrentPage();
        } catch (e) {
            console.error(e);
            loadComments();
        }
    }

    // ========= 기타 UI =========
    function initTabs() {
        const triggers = document.querySelectorAll(".tab-trigger");
        const contents = document.querySelectorAll(".tab-content");
        if (!triggers.length || !contents.length) return;
        triggers.forEach(btn => {
            btn.addEventListener("click", () => {
                const key = btn.getAttribute("data-tab");
                triggers.forEach(b => b.classList.remove("active"));
                contents.forEach(c => c.classList.remove("active"));
                btn.classList.add("active");
                const target = document.getElementById(`${key}Content`);
                if (target) target.classList.add("active");
            });
        });
    }

    function initReactions() {
        const likeBtn = document.getElementById("likeBtn");
        const bookmarkBtn = document.getElementById("bookmarkBtn");
        if (likeBtn) likeBtn.addEventListener("click", () => likeBtn.classList.toggle("liked"));
        if (bookmarkBtn) bookmarkBtn.addEventListener("click", () => bookmarkBtn.classList.toggle("bookmarked"));
    }

    function initIcons() {
        if (window.lucide && typeof window.lucide.createIcons === "function") {
            window.lucide.createIcons();
        }
    }

    // ========= 댓글 등록 =========
    function initCommentCreate() {
        if (!$commentBtn || !$commentTa) return;
        $commentBtn.addEventListener("click", async () => {
            const text = ($commentTa.value || "").trim();
            if (!text) return alert("댓글을 입력하세요.");
            try {
                await createReview({ content: text });
                $commentTa.value = "";
                await reloadKeepPage();     // 페이지 유지
            } catch (e) {
                console.error(e);
                alert("댓글 등록 실패");
            }
        });
    }

    // ========= 통신 함수 =========
    async function createReview({ content, parentId = null }) {
        const payload = { boardId, writerIdx: loginUser.memberIdx, content };
        if (parentId) payload.parentId = parentId;
        const res = await fetch(API.create(), {
            method: "POST",
            headers: withCsrf({ "Content-Type": "application/json" }),
            body: JSON.stringify(payload),
        });
        if (!res.ok) throw new Error(res.status);
        return res.json();
    }

    async function updateReview(reviewId, content) {
        const res = await fetch(API.update(reviewId), {
            method: "PUT",
            headers: withCsrf({ "Content-Type": "application/json" }),
            body: JSON.stringify({ content }),
        });
        if (!res.ok) throw new Error(res.status);
        return res.json();
    }

    async function deleteReview(reviewId) {
        const res = await fetch(API.remove(reviewId), {
            method: "DELETE",
            headers: withCsrf(),
        });
        if (!res.ok) throw new Error(res.status);
    }

// ========= 델리게이션 (좋아요 / 수정 / 삭제 / 답글) =========
    function initCommentDelegation() {
        if (!$commentsList) return;

        $commentsList.addEventListener("click", async (e) => {
            const $btn = e.target.closest("button,[data-act]");
            if (!$btn) return;
            const act = $btn.dataset.act;
            const $comment = e.target.closest(".comment-item");
            const $reply = e.target.closest(".reply-item");

            try {
                // ===== 댓글 =====
                if ($comment) {
                    const id = Number($comment.dataset.commentId);

                    // ✅ 댓글 좋아요
                    if (act === "like") {
                        const $cnt = $btn.querySelector(".vote-count");

                        try {
                            // 1️⃣ 서버에 좋아요 여부 확인
                            const checkRes = await fetch(
                                `/like/check?memberIdx=${loginUser.memberIdx}&likeType=REVIEW&reviewId=${id}`
                            );
                            const alreadyLiked = await checkRes.json();

                            // 2️⃣ 상태에 따라 add / remove 구분
                            const url = alreadyLiked ? "/like/remove" : "/like/add";

                            const res = await fetch(url, {
                                method: "POST",
                                headers: withCsrf({ "Content-Type": "application/json" }),
                                body: JSON.stringify({
                                    likeType: "REVIEW",
                                    reviewId: id,
                                    memberIdx: loginUser.memberIdx
                                })
                            });

                            if (!res.ok) throw new Error("댓글 좋아요 처리 실패");
                            const data = await res.json();

                            // 3️⃣ UI 갱신
                            $btn.classList.toggle("liked", !alreadyLiked);
                            if ($cnt) $cnt.textContent = data.likeCount ?? 0;

                        } catch (err) {
                            console.error(err);
                            alert("댓글 좋아요 처리 중 오류가 발생했습니다.");
                        }
                        return;
                    }

                    // ✅ 답글창 열기
                    if (act === "toggle-reply") {
                        const $p = $comment.querySelector(".reply-write");
                        if ($p) $p.style.display = ($p.style.display === "none" || !$p.style.display) ? "block" : "none";
                        return;
                    }

                    // ✅ 답글 등록
                    if (act === "reply-submit") {
                        const $ta = $comment.querySelector(".reply-textarea");
                        const text = ($ta?.value || "").trim();
                        if (!text) return alert("답글을 입력하세요.");
                        await createReview({ content: text, parentId: id });
                        await reloadKeepPage();
                        return;
                    }

                    // ✅ 답글 취소
                    if (act === "reply-cancel") {
                        const $p = $comment.querySelector(".reply-write");
                        if ($p) $p.style.display = "none";
                        return;
                    }

                    // ✅ 댓글 수정
                    if (act === "edit") {
                        if ($comment.dataset.editing === "1") return;
                        $comment.dataset.editing = "1";
                        const raw = $comment.dataset.raw ?? $comment.querySelector(".comment-plain")?.textContent ?? "";
                        const $body = $comment.querySelector(".comment-body");
                        if ($body.querySelector(".edit-textarea")) return;
                        $body.innerHTML = `
                        <textarea class="edit-textarea">${esc(raw)}</textarea>
                        <div class="edit-actions">
                            <button class="cancel-btn" data-act="edit-cancel">취소</button>
                            <button class="save-btn" data-act="edit-save">저장</button>
                        </div>`;
                        return;
                    }

                    if (act === "edit-cancel") {
                        delete $comment.dataset.editing;
                        await reloadKeepPage();
                        return;
                    }

                    if (act === "edit-save") {
                        const $ta = $comment.querySelector(".edit-textarea");
                        const text = ($ta?.value || "").trim();
                        if (!text) return alert("내용을 입력하세요.");
                        await updateReview(id, text);
                        delete $comment.dataset.editing;
                        await reloadKeepPage();
                        return;
                    }

                    // ✅ 댓글 삭제
                    if (act === "delete") {
                        if (!confirm("댓글을 삭제할까요?")) return;
                        await deleteReview(id);
                        await reloadKeepPage();
                        return;
                    }
                }

                // ===== 대댓글 =====
                if ($reply) {
                    const rid = Number($reply.dataset.replyId);

                    // ✅ 대댓글 좋아요
                    if (act === "reply-like") {
                        const $cnt = $btn.querySelector(".vote-count");

                        try {
                            // 1️⃣ 서버에 좋아요 여부 확인
                            const checkRes = await fetch(
                                `/like/check?memberIdx=${loginUser.memberIdx}&likeType=REVIEW&reviewId=${rid}`
                            );
                            const alreadyLiked = await checkRes.json();

                            // 2️⃣ 상태에 따라 add / remove 구분
                            const url = alreadyLiked ? "/like/remove" : "/like/add";

                            const res = await fetch(url, {
                                method: "POST",
                                headers: withCsrf({ "Content-Type": "application/json" }),
                                body: JSON.stringify({
                                    likeType: "REVIEW",
                                    reviewId: rid,
                                    memberIdx: loginUser.memberIdx
                                })
                            });

                            if (!res.ok) throw new Error("대댓글 좋아요 처리 실패");
                            const data = await res.json();

                            // 3️⃣ UI 갱신
                            $btn.classList.toggle("liked", !alreadyLiked);
                            if ($cnt) $cnt.textContent = data.likeCount ?? 0;

                        } catch (err) {
                            console.error(err);
                            alert("대댓글 좋아요 처리 중 오류가 발생했습니다.");
                        }
                        return;
                    }

                    // ✅ 대댓글 수정
                    if (act === "reply-edit") {
                        if ($reply.dataset.editing === "1") return;
                        $reply.dataset.editing = "1";
                        const raw = $reply.dataset.raw ?? $reply.querySelector(".reply-plain")?.textContent ?? "";
                        const $txt = $reply.querySelector(".reply-text");
                        if ($txt.querySelector(".edit-textarea")) return;
                        $txt.innerHTML = `
                        <textarea class="edit-textarea">${esc(raw)}</textarea>
                        <div class="edit-actions">
                            <button class="cancel-btn" data-act="reply-edit-cancel">취소</button>
                            <button class="save-btn" data-act="reply-edit-save">저장</button>
                        </div>`;
                        return;
                    }

                    if (act === "reply-edit-cancel") {
                        delete $reply.dataset.editing;
                        await reloadKeepPage();
                        return;
                    }

                    if (act === "reply-edit-save") {
                        const $ta = $reply.querySelector(".edit-textarea");
                        const text = ($ta?.value || "").trim();
                        if (!text) return alert("내용을 입력하세요.");
                        await updateReview(rid, text);
                        delete $reply.dataset.editing;
                        await reloadKeepPage();
                        return;
                    }

                    // ✅ 대댓글 삭제
                    if (act === "reply-delete") {
                        if (!confirm("대댓글을 삭제할까요?")) return;
                        await deleteReview(rid);
                        await reloadKeepPage();
                        return;
                    }
                }
            } catch (err) {
                console.error(err);
                alert("처리 중 오류가 발생했습니다.");
            }
        });
    }

    // ========= 초기 좋아요 상태 동기화 =========
    async function syncInitialLikes() {
        if (!$commentsList || !loginUser.memberIdx) return;

        // 모든 댓글과 대댓글의 좋아요 버튼 탐색
        const likeButtons = $commentsList.querySelectorAll(".like-vote");
        for (const btn of likeButtons) {
            const comment = btn.closest(".comment-item, .reply-item");
            if (!comment) continue;

            // 각 버튼의 reviewId 찾기
            const reviewId = comment.dataset.commentId || comment.dataset.replyId;
            if (!reviewId) continue;

            try {
                const res = await fetch(
                    `/like/check?memberIdx=${loginUser.memberIdx}&likeType=REVIEW&reviewId=${reviewId}`
                );
                const liked = await res.json();

                // 좋아요가 되어 있으면 색상 적용
                btn.classList.toggle("liked", liked === true);
            } catch (err) {
                console.error("초기 좋아요 동기화 실패:", err);
            }
        }
    }

    // ========= 초기화 =========
    document.addEventListener("DOMContentLoaded", async () => {
        initTabs();
        initReactions();
        initIcons();

        initPagerEvents();     // 페이지네이션 이벤트
        initCommentCreate();
        initCommentDelegation();

        await loadComments();
        await syncInitialLikes();
    });
})();
