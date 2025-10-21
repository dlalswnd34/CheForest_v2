<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>게시글 수정 - CheForest</title>
  <link rel="stylesheet" href="/css/board/boardwrite.css"><!-- 작성/수정 공용 -->
  <script src="https://cdn.tailwindcss.com"></script>
  <link href="https://fonts.googleapis.com/css2?family=Gowun+Dodum:wght@400&display=swap" rel="stylesheet">
</head>
<body>
<jsp:include page="/common/header.jsp"/>

<div>
  <div class="nav-container">
    <div class="nav-content">
      <button class="back-btn" id="backBtn" type="button">
        <svg class="back-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="m15 18-6-6 6-6"/></svg>
        <a href="/board/list" class="back-to-list">레시피 목록으로</a>
      </button>
      <div class="nav-actions">
        <button class="submit-btn" id="submitBtn" type="button">레시피 수정</button>
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
      <h1 class="page-title">레시피 수정하기</h1>
    </div>
    <p class="page-description">등록된 레시피를 수정할 수 있습니다</p>
  </div>

  <!-- 수정 폼 -->
  <div class="create-content">
    <form class="create-form" id="updateForm" method="post" action="/board/edit" enctype="multipart/form-data">
      <sec:csrfInput/>
      <input type="hidden" name="boardId" value="${board.boardId}"/>

      <!-- 기본 정보 섹션 -->
      <div class="form-section">
        <h2 class="section-title">기본 정보</h2>

        <div class="form-group">
          <label class="form-label" for="title">레시피 제목 *</label>
          <input type="text" class="form-input" id="title" name="title"
                 value="<c:out value='${board.title}'/>"
                 placeholder="예: 매콤달콤 김치볶음밥" maxlength="100" required>
          <div class="form-help">최대 100자까지 입력 가능합니다</div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label class="form-label" for="category">카테고리 *</label>
            <select class="form-select" id="category" name="category" required>
              <option value="">카테고리 선택</option>
              <option value="한식"  <c:if test="${board.category eq '한식'}">selected</c:if>>🥢 한식</option>
              <option value="양식"  <c:if test="${board.category eq '양식'}">selected</c:if>>🍝 양식</option>
              <option value="중식"  <c:if test="${board.category eq '중식'}">selected</c:if>>🥟 중식</option>
              <option value="일식"  <c:if test="${board.category eq '일식'}">selected</c:if>>🍣 일식</option>
              <option value="디저트" <c:if test="${board.category eq '디저트'}">selected</c:if>>🧁 디저트</option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label" for="difficulty">난이도 *</label>
            <select class="form-select" id="difficulty" name="difficulty" required>
              <option value="">난이도 선택</option>
              <option value="쉬움"  <c:if test="${board.difficulty eq '쉬움'}">selected</c:if>>⭐ 쉬움</option>
              <option value="보통"  <c:if test="${board.difficulty eq '보통'}">selected</c:if>>⭐⭐ 보통</option>
              <option value="어려움" <c:if test="${board.difficulty eq '어려움'}">selected</c:if>>⭐⭐⭐ 어려움</option>
            </select>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label" for="cookTime">조리시간 *</label>
          <input type="text" class="form-input" id="cookTime" name="cookTime"
                 value="<c:out value='${board.cookTime}'/>"
                 placeholder="예: 30분" required>
          <div class="form-help">예: 30분, 1시간 30분 등</div>
        </div>
      </div>

      <!-- 대표 이미지 업로드 -->
      <div class="form-section">
        <h2 class="section-title">대표 이미지</h2>
        <div class="form-group">
          <label class="form-label" for="mainImage">완성 사진을 업로드하거나 기존 이미지를 유지하세요</label>
          <div class="image-upload-area" id="imageUploadArea">
            <!-- ✅ 이름을 BoardSaveReq에 맞춤 -->
            <input type="file" class="image-input" id="mainImage" name="mainImage" accept="image/*" hidden>
            <div class="upload-placeholder" id="uploadPlaceholder">
              <svg class="upload-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="17,8 12,3 7,8"/>
                <line x1="12" y1="3" x2="12" y2="15"/>
              </svg>
              <p class="upload-text">이미지를 업로드하거나 드래그하세요</p>
              <button type="button" class="upload-btn">파일 선택</button>
              <p class="upload-help">JPG, PNG (최대 5MB)</p>
            </div>
            <div class="image-preview" id="imagePreview" style="<c:out value='${empty board.thumbnail ? "display:none;" : "display:block;"}'/>">
              <img class="preview-img" id="previewImg" src="<c:out value='${board.thumbnail}'/>" alt="미리보기">
              <button type="button" class="remove-image" id="removeImage">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
              <button type="button" class="change-image" id="changeImage">이미지 변경</button>
            </div>
          </div>
          <div class="form-help">※ 기존 대표 이미지를 삭제하려면 아래 “기존 첨부”에서 해당 파일을 삭제하세요.</div>
        </div>
      </div>

      <!-- 기존 첨부 파일 리스트 (삭제 가능) -->
      <c:if test="${not empty fileList}">
        <div class="form-section">
          <h2 class="section-title">기존 첨부</h2>
          <div id="existingFiles" class="tags-preview">
            <c:forEach var="f" items="${fileList}">
              <c:url var="filePreviewUrl" value="/file/board/preview/${f.fileId}"/>
              <span class="tag-item existing-file" data-file-id="${f.fileId}">
                <a href="${filePreviewUrl}" target="_blank"><c:out value="${f.fileName}"/></a>
                <button type="button" class="tag-remove" onclick="markDeleteFile(${f.fileId}, this)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <line x1="18" y1="6" x2="6" y2="18"></line>
                    <line x1="6" y1="6" x2="18" y2="18"></line>
                  </svg>
                </button>
              </span>
            </c:forEach>
          </div>
          <!-- 삭제 아이디 누적 영역 -->
          <div id="deleteBucket"></div>
          <div class="form-help">삭제한 파일은 저장 시 실제로 제거됩니다.</div>
        </div>
      </c:if>

      <!-- 재료 -->
      <div class="form-section">
        <div class="section-header">
          <h2 class="section-title">재료</h2>
          <button type="button" class="add-btn" id="addIngredientBtn">
            <svg class="add-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            재료 추가
          </button>
        </div>

        <div class="ingredients-container" id="ingredientsContainer">
          <c:choose>
            <c:when test="${not empty ingredients}">
              <c:forEach var="ing" items="${ingredients}" varStatus="st">
                <div class="ingredient-item" data-index="${st.index}">
                  <div class="ingredient-inputs">
                    <div class="input-group">
                      <label class="input-label">재료명</label>
                      <input type="text" class="form-input" name="ingredientName[]"
                             value="<c:out value='${ing.name}'/>" placeholder="예: 김치">
                    </div>
                    <div class="input-group">
                      <label class="input-label">분량</label>
                      <input type="text" class="form-input" name="ingredientAmount[]"
                             value="<c:out value='${ing.amount}'/>" placeholder="예: 1컵">
                    </div>
                  </div>
                  <button type="button" class="remove-btn" onclick="removeIngredient(this)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                      <line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line>
                    </svg>
                  </button>
                </div>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <div class="ingredient-item" data-index="0">
                <div class="ingredient-inputs">
                  <div class="input-group">
                    <label class="input-label">재료명</label>
                    <input type="text" class="form-input" name="ingredientName[]" placeholder="예: 김치">
                  </div>
                  <div class="input-group">
                    <label class="input-label">분량</label>
                    <input type="text" class="form-input" name="ingredientAmount[]" placeholder="예: 1컵">
                  </div>
                </div>
                <button type="button" class="remove-btn" onclick="removeIngredient(this)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line>
                  </svg>
                </button>
              </div>
            </c:otherwise>
          </c:choose>
        </div>

        <div class="empty-state" id="ingredientsEmpty" style="display: none;">
          <p class="empty-text">재료를 추가해주세요</p>
        </div>
      </div>

      <!-- 조리법 (단계 사진 업로드) -->
      <div class="form-section">
        <div class="section-header">
          <h2 class="section-title">조리법</h2>
          <button type="button" class="add-btn" id="addInstructionBtn">
            <svg class="add-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            조리법 추가
          </button>
        </div>

        <div class="instructions-container" id="instructionsContainer">
          <c:choose>
            <c:when test="${not empty instructions}">
              <c:forEach var="ins" items="${instructions}" varStatus="st">
                <div class="instruction-item" data-index="${st.index}">
                  <div class="instruction-header">
                    <span class="step-number">${st.index + 1}단계</span>
                    <button type="button" class="remove-instruction-btn" onclick="removeInstruction(this)">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line>
                      </svg>
                    </button>
                  </div>

                  <div class="instruction-content">
                    <div class="form-group">
                      <label class="form-label">조리 방법</label>
                      <textarea class="form-textarea" name="instructionContent[]" rows="4" required><c:out value='${ins.text}'/></textarea>
                    </div>

                    <div class="form-group">
                      <label class="form-label">사진 (선택사항)</label>
                      <div class="instruction-image-upload">
                        <!-- ✅ 이름을 BoardSaveReq에 맞춤 -->
                        <input type="file" class="instruction-image-input" name="instructionImage" accept="image/*" hidden>
                        <div class="instruction-upload-placeholder" style="<c:out value='${empty ins.image ? "display:flex;" : "display:none;"}'/>">
                          <svg class="instruction-upload-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17,8 12,3 7,8"/><line x1="12" y1="3" x2="12" y2="15"/>
                          </svg>
                          <p class="instruction-upload-text">조리 사진을 업로드하세요</p>
                          <button type="button" class="instruction-upload-btn">사진 선택</button>
                        </div>
                        <div class="instruction-image-preview" style="<c:out value='${empty ins.image ? "display:none;" : "display:block;"}'/>">
                          <img class="instruction-preview-img" src="<c:out value='${ins.image}'/>" alt="조리 사진">
                          <div class="instruction-image-actions">
                            <button type="button" class="change-instruction-image">사진 변경</button>
                            <button type="button" class="remove-instruction-image">사진 삭제</button>
                          </div>
                        </div>
                      </div>
                      <div class="form-help">※ 기존 단계 사진을 삭제하려면 위 “기존 첨부”에서 해당 파일을 삭제하세요.</div>
                    </div>
                  </div>
                </div>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <div class="instruction-item" data-index="0">
                <div class="instruction-header">
                  <span class="step-number">1단계</span>
                  <button type="button" class="remove-instruction-btn" onclick="removeInstruction(this)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                      <line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line>
                    </svg>
                  </button>
                </div>
                <div class="instruction-content">
                  <div class="form-group">
                    <label class="form-label">조리 방법</label>
                    <textarea class="form-textarea" name="instructionContent[]" rows="4" required></textarea>
                  </div>
                  <div class="form-group">
                    <label class="form-label">사진 (선택사항)</label>
                    <div class="instruction-image-upload">
                      <input type="file" class="instruction-image-input" name="instructionImage" accept="image/*" hidden>
                      <div class="instruction-upload-placeholder">
                        <svg class="instruction-upload-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17,8 12,3 7,8"/><line x1="12" y1="3" x2="12" y2="15"/>
                        </svg>
                        <p class="instruction-upload-text">조리 사진을 업로드하세요</p>
                        <button type="button" class="instruction-upload-btn">사진 선택</button>
                      </div>
                      <div class="instruction-image-preview" style="display:none;">
                        <img class="instruction-preview-img" src="" alt="조리 사진">
                        <div class="instruction-image-actions">
                          <button type="button" class="change-instruction-image">사진 변경</button>
                          <button type="button" class="remove-instruction-image">사진 삭제</button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </c:otherwise>
          </c:choose>
        </div>

        <div class="empty-state" id="instructionsEmpty" style="display: none;">
          <p class="empty-text">조리 방법을 추가해주세요</p>
        </div>

        <!-- 버튼 -->
        <div class="form-actions">
          <button type="button" class="btn-cancel" id="cancelBtn">취소</button>
          <button type="submit" class="btn-submit" id="submitFormBtn">레시피 수정하기</button>
        </div>
      </div>
    </form>
  </div>
</div>

<!-- 확인 모달 -->
<div class="modal-overlay" id="confirmModal" style="display: none;">
  <div class="modal-content">
    <div class="modal-header"><h3 class="modal-title">레시피 수정 확인</h3></div>
    <div class="modal-body">
      <p>해당 레시피 내용을 수정하시겠습니까?</p>
      <p class="modal-note">삭제 표시한 파일은 저장 시 실제로 제거됩니다.</p>
    </div>
    <div class="modal-actions">
      <button type="button" class="btn-modal-cancel" id="modalCancelBtn">취소</button>
      <button type="button" class="btn-modal-confirm" id="modalConfirmBtn">수정하기</button>
    </div>
  </div>
</div>

<!-- 도움말 -->
<div class="help-sidebar" id="helpSidebar">
  <div class="help-content">
    <div class="help-header">
      <h3 class="help-title">📝 레시피 수정 가이드</h3>
      <button class="help-close" id="helpClose">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
      </button>
    </div>
    <div class="help-section">
      <h4 class="help-section-title">체크 사항</h4>
      <ul class="help-list">
        <li>제목/카테고리/난이도/조리시간 확인</li>
        <li>대표 이미지는 새 업로드 시 자동 교체</li>
        <li>기존 파일 삭제는 “기존 첨부”에서 처리</li>
      </ul>
    </div>
  </div>
</div>
<button class="help-toggle" id="helpToggle" type="button">
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
</button>

<script src="/js/common.js"></script>
<script src="/js/board/boardedit.js"></script>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>
