function showResult(region) {
    const resultBox = document.getElementById("result-box");

    // 초기 로딩 메시지
    resultBox.innerHTML = `
        <h3>${region}</h3>
        <p>데이터 불러오는 중...</p>
    `;

    // API 호출
    fetch(`/dust/with-recommend?sido=${region}`)
        .then(res => res.json())
        .then(data => {
            // ✅ 미세먼지/날씨 출력
            resultBox.innerHTML = `
              <h3>${region}</h3>
              <p>기온: ${data.weather?.temperature || "-"}</p>
              <p>습도: ${data.weather?.humidity || "-"}</p>
              <p>하늘: ${data.weather?.sky || "-"}</p>
              <p>미세먼지(PM10): ${data.pm10 || "-"} (${data.pm10Grade || "정보없음"})</p>
              <p>초미세먼지(PM2.5): ${data.pm25 || "-"} (${data.pm25Grade || "정보없음"})</p>
              <p>측정시간: ${data.dataTime || "-"}</p>

              <div id="recipe-box" class="recipe-box">
                  <h4 id="recipe-header">추천 레시피 ▼</h4>
                  <ul id="recipe-list"></ul>
              </div>
            `;

            const recipeListEl = document.getElementById("recipe-list");
            const recipeBoxEl = document.getElementById("recipe-box");

            const recipes = data.recipes || [];

            if (recipes.length > 0) {
                // ✅ 클릭 가능한 리스트로 출력
                recipeListEl.innerHTML = recipes
                    .map(r => `
                        <li class="recipe-item" 
                            style="cursor:pointer; color:#2a72ff; text-decoration:underline;"
                            data-id="${r.recipeId}">
                            ${r.titleKr || r.title || "레시피"}
                        </li>
                    `)
                    .join("");

                // ✅ 각 li 클릭 시 상세 페이지로 이동
                recipeListEl.querySelectorAll(".recipe-item").forEach(item => {
                    item.addEventListener("click", () => {
                        const id = item.dataset.id;
                        if (id) {
                            // 🔗 우리 프로젝트 구조에 맞게 수정
                            location.href = `/recipe/view?recipeId=${id}`;
                        }
                    });
                });

                recipeBoxEl.style.display = "block";
            } else {
                recipeBoxEl.style.display = "none";
            }
        })
        .catch(err => {
            console.error(err);
            resultBox.innerHTML = `
              <h3>${region}</h3>
              <p>데이터 불러오기 오류</p>
            `;
        });
}