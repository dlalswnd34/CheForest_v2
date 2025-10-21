<!-- “미세먼지 단계(좋음/보통/나쁨/매우나쁨)에 따라 요리 레시피를 자동으로 추천해주는 AI 서버”

Spring Boot(메인 서버)에서 미세먼지 정보를 가져오면,
이 Flask 서버가 그 공기 상태(grade) 에 따라
Spring 서버의 /recipe/api/list 엔드포인트를 호출해서
적절한 카테고리의 요리를 랜덤으로 추천해줍니다. -->

<!-- 동작 흐름 요약 -->
# [1] 사용자가 DustMap 클릭 → 지역별 미세먼지 단계 확인
# [2] Spring Boot → Flask로 요청 보냄 (/recommend/ai?grade=보통)
# [3] Flask(app.py) → Spring Boot의 recipe API 호출
# [4] Pandas로 레시피 랜덤 추출
# [5] JSON 응답 → Spring Boot → 프론트에 출력