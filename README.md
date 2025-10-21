# CheForest_v2

**요리 정보 제공 웹사이트**입니다.  
사용자에게 다양한 국가별 요리 레시피를 제공하고, 회원 가입, 로그인, 레시피 등록, 좋아요, 댓글 등 다양한 커뮤니티 기능과 부가 서비스를 제공합니다.

> 본 프로젝트는 5주간 진행된 팀 프로젝트로, **기존 CheForest(v1)** 의 기능을 기반으로  
> **검색 · 보안 · 성능 · UI · 배포를 고도화한 2차 프로젝트(v2)** 입니다.  
> Spring Boot (JPA, Spring Security, Gradle) 기반으로 개발되었으며,  
> **Elasticsearch · Oracle · AWS EC2** 환경에서 운영됩니다.

---

## 🔎 주요 기능
- 회원가입 및 로그인 (일반 + 소셜 로그인: Kakao / Google / Naver)
- 공공 및 외부 API + 번역 API(DeepL) 연동
- 레시피 카테고리별 검색 및 상세보기
- **Elasticsearch 통합검색** (레시피 + 게시판)
- 날씨 및 미세먼지 기반 레시피 추천
- 제철지수에 따른 레시피 추천
- 게시판(커뮤니티) 기능 및 댓글/좋아요
- 이메일 인증 및 유효성 검사 적용
- AI 챗봇 / 실시간 웹 채팅
- 파일 업로드 (썸네일, 프로필 이미지 등)
- 마이페이지 (포인트 · 등급 · 활동 내역 확인)
- 관리자 페이지 (통계, 검색 동기화, 로그 확인)

---

## ⚙️ 기술 스택
- **Backend**: Java 17 / Spring Boot 3.4.8 / Spring Security / JPA / QueryDSL / MapStruct / Gradle / Python / Flask  
- **Database**: Oracle 21c XE  
- **Frontend**: JSP / HTML / CSS / JavaScript / jQuery / Bootstrap / Tailwind CSS  
- **검색엔진**: Elasticsearch 8.14.3 / Logstash / Kibana  
- **API 연동**: Kakao · Naver · Google REST API / DeepL / Spoonacular / TheMealDB / 공공데이터포털 오픈 API  
- **DevOps**: AWS EC2 (Ubuntu 22.04) / Docker (Oracle XE) / systemd 서비스  
- **협업 도구**: Git / GitHub / Visual Studio Code / IntelliJ IDEA  
- **기타**: Figma / Lucide Icons

---

## 🚀 배포 스크립트
```bash
cd C:\work\cheforest
ssh -i "cheforest-key.pem" ubuntu@3.35.9.81

sudo systemctl status elasticsearch
sudo docker start oracle-xe

cd /home/ubuntu
nohup sudo java -jar cheforest.war > cheforest.log 2>&1 &
tail -f cheforest.log

 
## 팀원 구성
- 총 6인 참여  
- 기획, 백엔드, 프론트엔드, DB 설계, 디자인 등 역할을 분담하여 협업 

> 본 프로젝트는 팀원 간 코드 리뷰 및 브랜치 전략(PR → Merge → 삭제)을 통해 품질과 협업 효율을 높였습니다.
