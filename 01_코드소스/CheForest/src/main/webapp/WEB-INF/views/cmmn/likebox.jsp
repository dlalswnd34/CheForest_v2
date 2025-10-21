<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>좋아요 테스트</title>
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <style>
    .like-wrap {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-top: 10px;
    }
    .likeButton {
      font-size: 32px;
      border: none;
      background: none;
      cursor: pointer;
      color: gray;
    }
    .likeButton.liked {
      color: red;
    }
    .likeCount {
      margin-top: 6px;
      font-size: 14px;
      color: black;
    }
  </style>
</head>
<body>
  <h2>좋아요 테스트</h2>

  <%-- 로그인 여부 체크 (전역) --%>
  <%
    Object sessionUser = session.getAttribute("memberIdx");
    int memberIdx = (sessionUser == null) ? -1 : Integer.parseInt(sessionUser.toString());
  %>

  <script>
    const memberIdxGlobal = <%= memberIdx %>;
    const contextPath = "<%= request.getContextPath() %>";
    const boardId = 123;

    if (memberIdxGlobal === -1) {
      alert("로그인 후 이용 가능한 기능입니다.");
    }
  </script>

  <%-- 예시 게시글 1개 --%>
  <div class="post" data-board-id="123">
    <p>게시글 123</p>
    <div class="like-wrap">
      <button id="likeButton" class="likeButton" data-board-id="123" data-member-idx="<%= memberIdx %>">♡</button>
      <div class="likeCount" id="likeCount" data-board-id="123">좋아요 수: </div>
    </div>
  </div>

  <script>
    const memberIdx = memberIdxGlobal;

    if (memberIdx === -1) {
      $("#likeButton").prop("disabled", true);
    }

    function toggleLike() {
      if (memberIdx === -1) {
        alert("로그인 후 이용해주세요.");
        return;
      }

      $.ajax({
        url: contextPath + "/checkLike.do",
        type: "GET",
        data: {
          boardId: boardId,
          memberIdx: memberIdx
        },
        success: function(exists) {
          if (exists === true || exists === "true") {
            removeLike();
          } else {
            addLike();
          }
        },
        error: function(xhr) {
          console.error("상태 확인 실패:", xhr.responseText);
        }
      });
    }

    function addLike() {
      $.ajax({
        url: contextPath + "/addLike.do",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ boardId, memberIdx }),
        success: function() {
          getLikeCount();
          updateButton(true);
        },
        error: function(xhr) {
          console.error("좋아요 등록 실패:", xhr.responseText);
        }
      });
    }

    function removeLike() {
      $.ajax({
        url: contextPath + "/cancelLike.do",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ boardId, memberIdx }),
        success: function() {
          getLikeCount();
          updateButton(false);
        },
        error: function(xhr) {
          console.error("좋아요 취소 실패:", xhr.responseText);
        }
      });
    }

    function getLikeCount() {
      $.ajax({
        url: contextPath + "/countLike.do",
        type: "GET",
        data: { boardId },
        success: function(count) {
          $("#likeCount").text("좋아요 수: " + count);
        },
        error: function(xhr) {
          console.error("좋아요 수 가져오기 실패:", xhr.responseText);
        }
      });
    }

    function updateButton(isLiked) {
      $("#likeButton").html(isLiked ? "♥" : "♡");
      $("#likeButton").toggleClass("liked", isLiked);
    }

    function checkInitialStatus() {
      if (memberIdx === -1) return;

      $.ajax({
        url: contextPath + "/checkLike.do",
        type: "GET",
        data: {
          boardId: boardId,
          memberIdx: memberIdx
        },
        success: function(exists) {
          updateButton(exists === true || exists === "true");
        }
      });
    }

    $(document).ready(function() {
      getLikeCount();
      checkInitialStatus();
      $("#likeButton").click(toggleLike);
    });
  </script>
</body>
</html>
