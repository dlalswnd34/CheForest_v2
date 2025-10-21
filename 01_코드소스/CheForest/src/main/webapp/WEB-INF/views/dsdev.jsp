<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>API 호출 테스트</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>

    <h2>🌍 세계 음식 레시피 수집기</h2>

    <!-- ✅ TheMealDB API 실행 버튼 -->
    <button id="callApiBtn">API 호출 + DB 저장</button>

    <!-- ✅ TheMealDB 중지 버튼 -->
    <button id="stopApiBtn" style="margin-left: 10px;">중지 요청</button>

    <!-- ✅ Spoonacular API 실행 버튼 -->
    <button id="callSpoonacularBtn" style="margin-left: 10px;">Spoonacular 실행</button>

    <!-- ✅ Spoonacular 중지 버튼 -->
    <button id="stopSpoonacularBtn" style="margin-left: 10px;">Spoonacular 중지</button>

    <!-- ✅ 결과 메시지 표시 영역 -->
    <p id="result" style="color: green; margin-top: 10px;"></p>

    <script>
        // ✅ TheMealDB 실행 버튼 클릭 시
        $("#callApiBtn").click(function () {
            $.get("/wf", function (data) {
                $("#result").css("color", "green").text(data);
            }).fail(function () {
                $("#result").css("color", "red").text("API 호출 실패");
            });
        });

        // ✅ TheMealDB 중지 버튼 클릭 시
        $("#stopApiBtn").click(function () {
            $.get("/stop", function (data) {
                $("#result").css("color", "orange").text(data);
            }).fail(function () {
                $("#result").css("color", "red").text("중지 요청 실패");
            });
        });

        // ✅ Spoonacular 실행 버튼 클릭 시
        $("#callSpoonacularBtn").click(function () {
            $.get("/import", function (data) {
                $("#result").css("color", "green").text(data);
            }).fail(function () {
                $("#result").css("color", "red").text("Spoonacular 호출 실패");
            });
        });

        // ✅ Spoonacular 중지 버튼 클릭 시
        $("#stopSpoonacularBtn").click(function () {
            $.get("/import/stop", function (data) {
                $("#result").css("color", "orange").text(data);
            }).fail(function () {
                $("#result").css("color", "red").text("Spoonacular 중지 실패");
            });
        });
    </script>

</body>
</html>
