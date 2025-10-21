<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
  int boardId = Integer.parseInt(request.getParameter("boardId"));
  int memberIdx = session.getAttribute("memberIdx") == null ? -1 : (int) session.getAttribute("memberIdx");
%>

<div class="like-wrap">
  <button class="likeButton" data-board-id="<%= boardId %>" data-member-idx="<%= memberIdx %>">♡</button>
  <div class="likeCount" data-board-id="<%= boardId %>">좋아요 수: </div>
</div>

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

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
  $(document).ready(function () {
    $(".likeButton").each(function () {
      const $btn = $(this);
      const boardId = $btn.data("board-id");
      const memberIdx = $btn.data("member-idx");

      if (memberIdx === -1 || memberIdx === "-1") {
        $btn.prop("disabled", true);
        return;
      }

      $.ajax({
        url: "/checkLike",
        type: "GET",
        data: { boardId, memberIdx },
        success: function (exists) {
          if (exists === true || exists === "true") {
            $btn.text("♥").addClass("liked");
          }
        }
      });

      $.ajax({
        url: "/countLike",
        type: "GET",
        data: { boardId },
        success: function (count) {
          $(`.likeCount[data-board-id=${boardId}]`).text("좋아요 수: " + count);
        }
      });

      $btn.on("click", function () {
        const isLiked = $btn.text() === "♥";
        const url = isLiked ? "/cancelLike" : "/addLike";

        $.ajax({
          url: url,
          type: "POST",
          contentType: "application/json",
          data: JSON.stringify({ boardId, memberIdx }),
          success: function () {
            $btn.text(isLiked ? "♡" : "♥").toggleClass("liked");

            $.ajax({
              url: "/countLike",
              type: "GET",
              data: { boardId },
              success: function (count) {
                $(`.likeCount[data-board-id=${boardId}]`).text("좋아요 수: " + count);
              }
            });
          }
        });
      });
    });
  });
</script>
