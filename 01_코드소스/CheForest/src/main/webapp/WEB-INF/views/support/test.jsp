<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>레시피 성향 테스트</title>
    <link rel="stylesheet" href="/css/test.css">
</head>
<body>
<div id="page">

    <!-- 시작 화면 -->
    <div id="s-screen">
        <img src="/images/event/beginner.png" alt="요리곰돌이" class="start-img">
        <h1>나랑 어울리는 요리는?</h1>
        <!-- ✅ 클릭 시 start() 실행 -->
        <button type="button" class="my-btn" onclick="start()">테스트 시작</button>
    </div>

    <!-- 질문 화면 -->
    <div id="q-screen" style="display: none;">
        <div id="progress-bar-wrap">
            <div id="progress-text">0 / 4</div>
            <div id="progress-bar-bg">
                <div id="progress-bar-fill" style="width: 0%"></div>
            </div>
        </div>
        <h2 id="q-no"></h2>
        <h2 id="q-text"></h2>
        <div id="a-buttons"></div>
    </div>

    <!-- 결과 화면 -->
    <div id="r-screen" style="display: none;">
        <h1>나랑 어울리는 요리는</h1>
        <h3 id="r-text"></h3>
        <div class="result-btns">
            <button type="button" class="my-btn" onclick="location.reload()">다시하기</button>
            <a id="r-link" class="my-btn" href="#">레시피 보러가기</a>
        </div>
    </div>

</div>

<!-- ✅ JS 로직 -->
<script>
    const questions = [
        { qno: "Q1. ", question: "나는 요리", answers: [
                { text: "초보다", type: "양식", img: "/images/event/beginner.png" },
                { text: "고수다", type: "일식", img: "/images/event/chef.png" }
            ]},
        { qno: "Q2. ", question: "요리할 때에 나는", answers: [
                { text: "무조건 레시피 대로", type: "한식", img: "/images/event/recipe.png" },
                { text: "오늘은 내가 셰프", type: "중식", img: "/images/event/norecipe.png" }
            ]},
        { qno: "Q3. ", question: "나는", answers: [
                { text: "채식 주의자", type: "양식", img: "/images/event/okvege.png" },
                { text: "채식 '주의'자", type: "한식", img: "/images/event/novege.png" }
            ]},
        { qno: "Q4. ", question: "식사후에 설거지는", answers: [
                { text: "바로 해야지!", type: "중식", img: "/images/event/clean.png" },
                { text: "나중에 할래...", type: "일식", img: "/images/event/after.png" }
            ]}
    ];

    let currentQuestion = 0;
    let score = { 한식: 0, 양식: 0, 중식: 0, 일식: 0 };
    const ctx = "<%= request.getContextPath() %>";

    const typeToDbCategory = {
        "한식": "한식", "중식": "중식", "일식": "일식", "양식": "양식"
    };

    // ===================== 시작 함수 =====================
    window.start = function() {
        console.log("🚀 start() 실행됨");
        document.getElementById("s-screen").style.display = "none";
        document.getElementById("q-screen").style.display = "block";
        currentQuestion = 0;
        score = { 한식: 0, 양식: 0, 중식: 0, 일식: 0 };
        updateProgressBar();
        showQuestion();
    }

    // ===================== 질문 표시 =====================
    function showQuestion() {
        const q = questions[currentQuestion];
        document.getElementById("q-no").innerText = q.qno;
        document.getElementById("q-text").innerText = q.question;

        const answersDiv = document.getElementById("a-buttons");
        answersDiv.innerHTML = "";
        answersDiv.style.display = "flex";
        answersDiv.style.flexDirection = "row";
        answersDiv.style.gap = "28px";
        answersDiv.style.justifyContent = "center";
        answersDiv.style.alignItems = "flex-end";
        answersDiv.style.marginTop = "18px";

        q.answers.forEach(answer => {
            const wrapper = document.createElement("div");
            wrapper.style.display = "flex";
            wrapper.style.flexDirection = "column";
            wrapper.style.alignItems = "center";
            wrapper.style.gap = "17px";
            wrapper.style.marginBottom = "4px";

            const img = document.createElement("img");
            img.src = answer.img;
            img.alt = answer.text;
            img.className = "select-img";
            wrapper.appendChild(img);

            const btn = document.createElement("button");
            btn.className = "my-btn";
            btn.innerText = answer.text;
            btn.onclick = () => {
                score[answer.type]++;
                currentQuestion++;
                updateProgressBar();
                if (currentQuestion < questions.length) {
                    showQuestion();
                } else {
                    showResult();
                }
            };
            wrapper.appendChild(btn);
            answersDiv.appendChild(wrapper);
        });
    }

    // ===================== 진행바 갱신 =====================
    function updateProgressBar() {
        const percentage = (currentQuestion / questions.length) * 100;
        const bar = document.getElementById("progress-bar-fill");
        bar.style.width = percentage + "%";
        bar.style.borderRadius = (currentQuestion === questions.length) ? "8px" : "8px 0 0 8px";
        document.getElementById("progress-text").textContent =
            currentQuestion + " / " + questions.length;
    }

    // ===================== 결과 표시 =====================
    function showResult() {
        const bar = document.getElementById("progress-bar-fill");
        bar.style.width = "100%";
        bar.style.borderRadius = "8px";

        document.getElementById("q-screen").style.display = "none";
        document.getElementById("r-screen").style.display = "block";

        const maxScore = Math.max(...Object.values(score));
        const bestTypes = Object.keys(score).filter(type => score[type] === maxScore);
        const bestCategory = bestTypes[0];

        document.getElementById("r-text").textContent = bestCategory + "!";
        const categoryParam = typeToDbCategory[bestCategory] || "";
        document.getElementById("r-link").href =
            ctx + "/recipe/list?categoryKr=" + encodeURIComponent(categoryParam);
    }
</script>

</body>
</html>
