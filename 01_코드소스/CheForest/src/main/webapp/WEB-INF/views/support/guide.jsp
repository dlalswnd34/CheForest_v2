<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>CheForest 이용 가이드</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common/common.css">
  <style>
    body {
      text-align: center;
      margin-top: 60px;
      font-family: 'Noto Sans KR', sans-serif;
    }
    h2 {
      margin-bottom: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    }
    .pdf-wrap {
      display: flex;
      justify-content: center;
      margin: 40px auto;
    }
    iframe {
      width: 60%;
      height: 900px;
      border: none;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
      border-radius: 10px;
    }
    .download-btn {
      display: inline-block;
      margin-top: 25px;
      padding: 12px 24px;
      background: linear-gradient(to right, #5e61ff, #74c0ff);
      color: white;
      border-radius: 8px;
      text-decoration: none;
      font-weight: 600;
    }
    .download-btn:hover {
      opacity: 0.9;
    }
    .exit-btn-wrap {
      text-align: center;
      margin-top: 20px;
    }
    .exit-btn {
      background-color: #007bff;
      color: white;
      padding: 10px 25px;
      border: none;
      border-radius: 8px;
      font-size: 16px;
      cursor: pointer;
      transition: background-color 0.2s;
    }
    .exit-btn:hover {
      background-color: #0056b3;
    }

  </style>
</head>
<body>


<h2>📘 <span style="color:#5e61ff;">CheForest</span> 이용 가이드</h2>
<p style="color:#555;">아래에서 바로 가이드를 확인할 수 있습니다.</p>


<!-- 나가기 버튼 -->
<div class="exit-btn-wrap">
  <button onclick="goHome()" class="exit-btn">나가기</button>
</div>


<!-- ✅ contextPath로 PDF 경로를 지정 -->
<div class="pdf-wrap">
  <iframe src="${pageContext.request.contextPath}/pdf/guide.pdf"></iframe>
</div>


<!-- ✅ 다운로드 버튼 -->
<a href="${pageContext.request.contextPath}/pdf/guide.pdf"
   download="CheForest_이용가이드.pdf"
   class="download-btn">
  PDF 다운로드
</a>

<script>
  function goHome() {
    window.location.href = '/'; // 메인 페이지로 이동
  }
</script>
</body>
</html>
