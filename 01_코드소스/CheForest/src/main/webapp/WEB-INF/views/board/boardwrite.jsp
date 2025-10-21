<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>게시글 작성 - CheForest</title>
  <link rel="stylesheet" href="/css/board/boardwrite.css">
  <script src="https://cdn.tailwindcss.com"></script>
  <link href="https://fonts.googleapis.com/css2?family=Gowun+Dodum:wght@400&display=swap" rel="stylesheet">
</head>
<body>
<jsp:include page="/common/header.jsp"/>

<div>
  <div class="nav-container">
    <div class="nav-content">
      <button class="back-btn" id="backBtn" type="button">
        <svg class="back-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path d="m15 18-6-6 6-6"/>
        </svg>
        <a href="/board/list" class="back-to-list">레시피 목록으로</a>
      </button>
      <div class="nav-actions">
        <button class="submit-btn" id="submitBtn" type="button">레시피 등록</button>
      </div>
    </div>
  </div>
</div>

<div class="create-container">
  <!-- 페이지 헤더 -->
  <div class="page-header">
    <div class="header-icon">
      <svg class="chef-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
        <path d="M17 21v-2a1 1 0 0 0-1-1v-1a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-2V4a2 2 0 0 0-2-2 2 2 0 0 0-2 2v2H8a2 2 0 0 0-2 2v7c0 1.1.9 2 2 2v1a1 1 0 0 0-1 1v2h10zM12 4a1 1 0 0 1 1 1v1h-2V5a1 1 0 0 1 1-1zM8 8h8v7H8V8z"/>
      </svg>
      <h1 class="page-title">레시피 작성하기</h1>
    </div>
    <p class="page-description">나만의 특별한 레시피를 CheForest 커뮤니티와 함께 나누어보세요</p>
  </div>

  <!-- 작성 폼 -->
  <div class="create-content">
    <form class="create-form" id="createForm" method="post" action="/board/add" enctype="multipart/form-data">
      <sec:csrfInput/>

      <!-- 기본 정보 섹션 -->
      <div class="form-section">
        <h2 class="section-title">기본 정보</h2>

        <div class="form-group">
          <label class="form-label" for="title">레시피 제목 *</label>
          <input type="text" class="form-input" id="title" name="title"
                 placeholder="예: 매콤달콤 김치볶음밥" maxlength="100" required>
          <div class="form-help">최대 100자까지 입력 가능합니다</div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label class="form-label" for="category">카테고리 *</label>
            <select class="form-select" id="category" name="category" required>
              <option value="">카테고리 선택</option>
              <option value="한식">🥢 한식</option>
              <option value="양식">🍝 양식</option>
              <option value="중식">🥟 중식</option>
              <option value="일식">🍣 일식</option>
              <option value="디저트">🧁 디저트</option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label" for="difficulty">난이도 *</label>
            <select class="form-select" id="difficulty" name="difficulty" required>
              <option value="">난이도 선택</option>
              <option value="쉬움">⭐ 쉬움</option>
              <option value="보통">⭐⭐ 보통</option>
              <option value="어려움">⭐⭐⭐ 어려움</option>
            </select>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label" for="cookTime">조리시간 *</label>
          <input type="text" class="form-input" id="cookTime" name="cookTime"
                 placeholder="예: 30분" required>
          <div class="form-help">예: 30분, 1시간 30분 등</div>
        </div>
      </div>

      <!-- 대표 이미지 섹션 -->
      <div class="form-section">
        <h2 class="section-title">대표 이미지</h2>

        <div class="form-group">
          <label class="form-label" for="thumbnail">완성된 요리 사진을 업로드해주세요</label>
          <div class="image-upload-area" id="imageUploadArea">
            <!-- [CHANGE#1] id/name을 컨트롤러와 일치: thumbnail -->
            <input type="file" class="image-input" id="thumbnail" name="thumbnail"
                   accept="image/*" hidden>
            <div class="upload-placeholder" id="uploadPlaceholder">
              <svg class="upload-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="17,8 12,3 7,8"/>
                <line x1="12" y1="3" x2="12" y2="15"/>
              </svg>
              <p class="upload-text">이미지를 업로드하거나 드래그하세요</p>
              <button type="button" class="upload-btn">파일 선택</button>
              <p class="upload-help">JPG, PNG 파일만 가능 (최대 5MB)</p>
            </div>
            <div class="image-preview" id="imagePreview" style="display: none;">
              <img class="preview-img" id="previewImg" src="" alt="미리보기">
              <button type="button" class="remove-image" id="removeImage">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
              <button type="button" class="change-image" id="changeImage">이미지 변경</button>
            </div>

            <!-- [CHANGE#1-1] 대표 이미지 간단 프리뷰 박스(선택) -->
            <div id="thumbPreview" class="preview" style="margin-top:8px;"></div>
          </div>
        </div>
      </div>

      <!-- 재료 섹션 -->
      <div class="form-section">
        <div class="section-header">
          <h2 class="section-title">재료</h2>
          <button type="button" class="add-btn" id="addIngredientBtn">
            <svg class="add-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <line x1="12" y1="5" x2="12" y2="19"/>
              <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            재료 추가
          </button>
        </div>

        <div class="ingredients-container" id="ingredientsContainer">
          <!-- 기본 재료 입력 1개 -->
          <div class="ingredient-item" data-index="0">
            <div class="ingredient-inputs">
              <div class="input-group">
                <label class="input-label">재료명</label>
                <input type="text" class="form-input" name="ingredientName[]"
                       placeholder="예: 김치">
              </div>
              <div class="input-group">
                <label class="input-label">분량</label>
                <input type="text" class="form-input" name="ingredientAmount[]"
                       placeholder="예: 1컵">
              </div>
            </div>
            <button type="button" class="remove-btn" onclick="removeIngredient(this)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
          </div>
        </div>

        <div class="empty-state" id="ingredientsEmpty" style="display: none;">
          <p class="empty-text">재료를 추가해주세요</p>
        </div>
      </div>

      <!-- 조리법 섹션 -->
      <div class="form-section">
        <div class="section-header">
          <h2 class="section-title">조리법</h2>
          <button type="button" class="add-btn" id="addInstructionBtn">
            <svg class="add-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <line x1="12" y1="5" x2="12" y2="19"/>
              <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            조리법 추가
          </button>
        </div>

        <div class="instructions-container" id="instructionsContainer">
          <!-- 기본 조리법 입력 1개 -->
          <div class="instruction-item" data-index="0">
            <div class="instruction-header">
              <span class="step-number">1단계</span>
              <button type="button" class="remove-instruction-btn" onclick="removeInstruction(this)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
            </div>

            <div class="instruction-content">
              <div class="form-group">
                <label class="form-label">조리 방법</label>
                <textarea class="form-textarea" name="instructionContent[]"
                          placeholder="조리 방법을 자세히 설명해주세요" rows="4" required></textarea>
              </div>

              <div class="form-group">
                <label class="form-label">사진 (선택사항)</label>
                <div class="instruction-image-upload">
                  <!-- [CHANGE#2] name에서 [] 제거: instructionImage -->
                  <input type="file" class="instruction-image-input"
                         name="instructionImage" accept="image/*" hidden>
                  <div class="instruction-upload-placeholder">
                    <svg class="instruction-upload-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                      <polyline points="17,8 12,3 7,8"/>
                      <line x1="12" y1="3" x2="12" y2="15"/>
                    </svg>
                    <p class="instruction-upload-text">조리 사진을 업로드하세요</p>
                    <button type="button" class="instruction-upload-btn">사진 선택</button>
                  </div>
                  <div class="instruction-image-preview" style="display: none;">
                    <img class="instruction-preview-img" src="" alt="조리 사진">
                    <div class="instruction-image-actions">
                      <button type="button" class="change-instruction-image">사진 변경</button>
                      <button type="button" class="remove-instruction-image">사진 삭제</button>
                    </div>
                  </div>
                </div>
              </div>
            </div> <!-- /.instruction-content -->
          </div> <!-- /.instruction-item -->
        </div> <!-- /.instructions-container -->

        <div class="empty-state" id="instructionsEmpty" style="display: none;">
          <p class="empty-text">조리 방법을 추가해주세요</p>
        </div>

        <!-- [CHANGE#2-1] 단계 이미지 전체 프리뷰(선택) -->
        <div id="stepPreview" class="preview" style="margin-top:12px;"></div>

        <!-- 버튼 영역 -->
        <div class="form-actions">
          <button type="button" class="btn-cancel" id="cancelBtn">취소</button>
          <button type="submit" class="btn-submit" id="submitFormBtn">레시피 등록하기</button>
        </div>
      </div>
    </form>
  </div>
</div>

<!-- 확인 모달 -->
<div class="modal-overlay" id="confirmModal" style="display: none;">
  <div class="modal-content">
    <div class="modal-header">
      <h3 class="modal-title">레시피 등록 확인</h3>
    </div>
    <div class="modal-body">
      <p>작성하신 레시피를 등록하시겠습니까?</p>
      <p class="modal-note">등록 후에는 레시피 수정 페이지에서 내용을 변경할 수 있습니다.</p>
    </div>
    <div class="modal-actions">
      <button type="button" class="btn-modal-cancel" id="modalCancelBtn">취소</button>
      <button type="button" class="btn-modal-confirm" id="modalConfirmBtn">등록하기</button>
    </div>
  </div>
</div>

<!-- 도움말 사이드바 -->
<div class="help-sidebar" id="helpSidebar">
  <div class="help-content">
    <div class="help-header">
      <h3 class="help-title">📝 레시피 작성 가이드</h3>
      <button class="help-close" id="helpClose">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <line x1="18" y1="6" x2="6" y2="18"></line>
          <line x1="6" y1="6" x2="18" y2="18"></line>
        </svg>
      </button>
    </div>

    <div class="help-section">
      <h4 class="help-section-title">✨ 좋은 레시피 작성법</h4>
      <ul class="help-list">
        <li>제목은 구체적이고 매력적으로 작성하세요</li>
        <li>완성된 요리 사진을 꼭 포함하세요</li>
        <li>재료는 정확한 분량과 함께 작성하세요</li>
        <li>조리법은 단계별로 자세히 설명하세요</li>
        <li>요리 팁을 함께 작성하면 더욱 도움이 됩니다</li>
      </ul>
    </div>

    <div class="help-section">
      <h4 class="help-section-title">📸 사진 촬영 팁</h4>
      <ul class="help-list">
        <li>자연광에서 촬영하면 더 맛있어 보여요</li>
        <li>완성된 요리를 다양한 각도에서 찍어보세요</li>
        <li>조리 과정 사진도 함께 올리면 좋아요</li>
        <li>깔끔한 배경에서 촬영하세요</li>
      </ul>
    </div>

    <div class="help-section">
      <h4 class="help-section-title">🚫 주의사항</h4>
      <ul class="help-list">
        <li>저작권이 있는 이미지는 사용하지 마세요</li>
        <li>정확하지 않은 정보는 피해주세요</li>
        <li>광고성 내용은 금지됩니다</li>
      </ul>
    </div>
  </div>
</div>

<!-- 도움말 열기 버튼 -->
<button class="help-toggle" id="helpToggle" type="button">
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
    <circle cx="12" cy="12" r="10"/>
    <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/>
    <line x1="12" y1="17" x2="12.01" y2="17"/>
  </svg>
</button>

<script src="/js/common/common.js"></script>
<script src="/js/board/boardwrite.js"></script>
<jsp:include page="/common/footer.jsp"/>

</body>
</html>
