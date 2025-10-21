<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>대한민국 날씨 & 레시피 추천 지도</title>
    <link rel="stylesheet" href="/css/dust/dustmap.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>

<h2 class="text-2xl font-bold text-center my-6 text-gray-900">
    대한민국 지역별 날씨 & 레시피 추천
</h2>

<div class="map-container">
    <img src="/images/korea_map.png" alt="대한민국 지도">

    <!-- ✅ 지역 버튼들 -->
    <button class="region-btn" style="top:182px; left:285px;" onclick="showResult('서울')">서울</button>
    <button class="region-btn" style="top:140px; left:310px;" onclick="showResult('경기')">경기</button>
    <button class="region-btn" style="top:190px; left:245px;" onclick="showResult('인천')">인천</button>
    <button class="region-btn" style="top:159px; left:407px;" onclick="showResult('강원')">강원</button>
    <button class="region-btn" style="top:260px; left:350px;" onclick="showResult('충북')">충북</button>
    <button class="region-btn" style="top:275px; left:251px;" onclick="showResult('충남')">충남</button>
    <button class="region-btn" style="top:292px; left:307px;" onclick="showResult('세종')">세종</button>
    <button class="region-btn" style="top:317px; left:321px;" onclick="showResult('대전')">대전</button>
    <button class="region-btn" style="top:378px; left:297px;" onclick="showResult('전북')">전북</button>
    <button class="region-btn" style="top:447px; left:270px;" onclick="showResult('광주')">광주</button>
    <button class="region-btn" style="top:484px; left:272px;" onclick="showResult('전남')">전남</button>
    <button class="region-btn" style="top:295px; left:446px;" onclick="showResult('경북')">경북</button>
    <button class="region-btn" style="top:367px; left:428px;" onclick="showResult('대구')">대구</button>
    <button class="region-btn" style="top:425px; left:392px;" onclick="showResult('경남')">경남</button>
    <button class="region-btn" style="top:446px; left:472px;" onclick="showResult('부산')">부산</button>
    <button class="region-btn" style="top:398px; left:485px;" onclick="showResult('울산')">울산</button>
    <button class="region-btn" style="top:636px; left:247px;" onclick="showResult('제주')">제주</button>

    <!-- ✅ 결과 박스 (안에 레시피 박스 포함) -->
    <div id="result-box" class="result-box">
        <b>지역을 선택하세요</b>

        <!-- ✅ 레시피 박스 -->
        <div id="recipe-box" class="recipe-box">
            <h4 id="recipe-header">추천 레시피 ▼</h4>
            <ul id="recipe-list"></ul>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"/>

<script src="/js/dustmap.js"></script>
</body>
</html>
