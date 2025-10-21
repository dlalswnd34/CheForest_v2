// CheForest 레시피 수정 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 폼/버튼
    const updateForm    = document.getElementById('updateForm');
    const backBtn       = document.getElementById('backBtn');
    const submitBtn     = document.getElementById('submitBtn');
    const submitFormBtn = document.getElementById('submitFormBtn');
    const cancelBtn     = document.getElementById('cancelBtn');

    // 대표 이미지 업로드
    const imageUploadArea   = document.getElementById('imageUploadArea');
    const mainImageInput    = document.getElementById('mainImage');
    const uploadPlaceholder = document.getElementById('uploadPlaceholder');
    const imagePreview      = document.getElementById('imagePreview');
    const previewImg        = document.getElementById('previewImg');
    const removeImageBtn    = document.getElementById('removeImage');
    const changeImageBtn    = document.getElementById('changeImage');

    // 재료/조리법
    const addIngredientBtn   = document.getElementById('addIngredientBtn');
    const ingredientsContainer = document.getElementById('ingredientsContainer');
    const ingredientsEmpty     = document.getElementById('ingredientsEmpty');

    const addInstructionBtn  = document.getElementById('addInstructionBtn');
    const instructionsContainer = document.getElementById('instructionsContainer');
    const instructionsEmpty     = document.getElementById('instructionsEmpty');

    // 기존 파일 삭제 누적
    const deleteBucket = document.getElementById('deleteBucket');

    // 모달
    const confirmModal   = document.getElementById('confirmModal');
    const modalCancelBtn = document.getElementById('modalCancelBtn');
    const modalConfirmBtn= document.getElementById('modalConfirmBtn');

    // 초기 카운트
    let ingredientCount  = ingredientsContainer ? ingredientsContainer.querySelectorAll('.ingredient-item').length : 0;
    let instructionCount = instructionsContainer ? instructionsContainer.querySelectorAll('.instruction-item').length : 0;

    // 초기화
    setupEventListeners();
    document.querySelectorAll('.instruction-item').forEach(setupInstructionImageUpload);
    updateStepNumbers();

    function setupEventListeners() {
        if (backBtn) {
            backBtn.addEventListener('click', () => {
                if (hasUnsavedChanges() && !confirm('수정 중인 내용이 있습니다. 나가시겠습니까?')) return;
                location.href = '/board/list';
            });
        }

        if (submitBtn)     submitBtn.addEventListener('click', showConfirmModal);
        if (submitFormBtn) submitFormBtn.addEventListener('click', (e) => { e.preventDefault(); showConfirmModal(); });

        if (cancelBtn) {
            cancelBtn.addEventListener('click', () => {
                if (hasUnsavedChanges() && !confirm('수정 중인 내용이 사라집니다. 취소하시겠습니까?')) return;
                location.href = '/board/list';
            });
        }

        // 대표 이미지 업로드 영역
        setupMainImageUpload();

        // 재료/조리법
        if (addIngredientBtn) addIngredientBtn.addEventListener('click', addIngredient);
        if (addInstructionBtn) addInstructionBtn.addEventListener('click', addInstruction);

        // 모달
        if (modalCancelBtn) modalCancelBtn.addEventListener('click', hideConfirmModal);
        if (modalConfirmBtn) modalConfirmBtn.addEventListener('click', submitRecipe);

        // 이탈 경고
        window.addEventListener('beforeunload', beforeUnloadHandler);

        // 폼 직접 제출 방지 → 모달 경유
        if (updateForm) {
            updateForm.addEventListener('submit', function(e) {
                e.preventDefault();
                showConfirmModal();
            });
        }
    }

    // 대표 이미지 업로드 처리
    function setupMainImageUpload() {
        if (!imageUploadArea || !mainImageInput) return;

        imageUploadArea.addEventListener('click', function(e) {
            if (removeImageBtn && removeImageBtn.contains(e.target)) return;
            if (changeImageBtn && changeImageBtn.contains(e.target)) return;
            mainImageInput.click();
        });

        mainImageInput.addEventListener('change', function(e) {
            const file = e.target.files && e.target.files[0];
            if (!file) return;
            if (file.size > 5 * 1024 * 1024) { showNotification('파일 크기는 5MB 이하여야 합니다.', 'error'); mainImageInput.value=''; return; }
            if (!file.type.startsWith('image/')) { showNotification('이미지 파일만 업로드 가능합니다.', 'error'); mainImageInput.value=''; return; }

            const reader = new FileReader();
            reader.onload = function(ev) {
                previewImg.src = ev.target.result;
                imagePreview.style.display = 'block';
                uploadPlaceholder.style.display = 'none';
            };
            reader.readAsDataURL(file);
        });

        if (removeImageBtn) {
            removeImageBtn.addEventListener('click', function(e) {
                e.stopPropagation();
                // 신규 선택만 취소 (기존 첨부 삭제는 아래 '기존 첨부'에서 처리)
                if (mainImageInput) mainImageInput.value = '';
                if (imagePreview) imagePreview.style.display = 'none';
                if (uploadPlaceholder) uploadPlaceholder.style.display = 'flex';
            });
        }

        if (changeImageBtn) {
            changeImageBtn.addEventListener('click', function(e) {
                e.stopPropagation();
                mainImageInput.click();
            });
        }
    }

    // 재료 추가/삭제
    function addIngredient() {
        const html = `
      <div class="ingredient-item fade-in" data-index="${ingredientCount}">
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
      </div>`;
        if (ingredientsContainer) {
            ingredientsContainer.insertAdjacentHTML('beforeend', html);
            ingredientCount++;
            if (ingredientsEmpty) ingredientsEmpty.style.display = 'none';
        }
    }
    window.removeIngredient = function(btn) {
        const item = btn.closest('.ingredient-item');
        if (!item) return;
        item.remove();
        const remain = ingredientsContainer.querySelectorAll('.ingredient-item').length;
        if (remain === 0 && ingredientsEmpty) ingredientsEmpty.style.display = 'block';
    };

    // 조리법 추가/삭제
    function addInstruction() {
        const html = `
      <div class="instruction-item fade-in" data-index="${instructionCount}">
        <div class="instruction-header">
          <span class="step-number">${instructionCount + 1}단계</span>
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
      </div>`;
        if (instructionsContainer) {
            instructionsContainer.insertAdjacentHTML('beforeend', html);
            const added = instructionsContainer.lastElementChild;
            setupInstructionImageUpload(added);
            instructionCount++;
            if (instructionsEmpty) instructionsEmpty.style.display = 'none';
            updateStepNumbers();
        }
    }
    window.removeInstruction = function(btn) {
        const item = btn.closest('.instruction-item');
        if (!item) return;
        item.remove();
        const remain = instructionsContainer.querySelectorAll('.instruction-item').length;
        if (remain === 0 && instructionsEmpty) instructionsEmpty.style.display = 'block';
        updateStepNumbers();
    };
    function updateStepNumbers() {
        const items = instructionsContainer.querySelectorAll('.instruction-item');
        items.forEach((el, idx) => {
            const step = el.querySelector('.step-number');
            if (step) step.textContent = `${idx + 1}단계`;
        });
    }

    // 단계 사진 업로드
    function setupInstructionImageUpload(instructionElement) {
        const imageUpload = instructionElement.querySelector('.instruction-image-upload');
        const imageInput  = instructionElement.querySelector('.instruction-image-input');
        const placeholder = instructionElement.querySelector('.instruction-upload-placeholder');
        const preview     = instructionElement.querySelector('.instruction-image-preview');
        const previewImg  = instructionElement.querySelector('.instruction-preview-img');
        const uploadBtn   = instructionElement.querySelector('.instruction-upload-btn');
        const changeBtn   = instructionElement.querySelector('.change-instruction-image');
        const removeBtn   = instructionElement.querySelector('.remove-instruction-image');

        if (!imageUpload || !imageInput || !placeholder || !preview || !previewImg) return;

        uploadBtn && uploadBtn.addEventListener('click', e => { e.stopPropagation(); imageInput.click(); });
        changeBtn && changeBtn.addEventListener('click', e => { e.stopPropagation(); imageInput.click(); });

        imageInput.addEventListener('change', function(e) {
            const file = e.target.files && e.target.files[0];
            if (!file) return;
            if (file.size > 5 * 1024 * 1024) { showNotification('파일 크기는 5MB 이하여야 합니다.', 'error'); imageInput.value=''; return; }
            if (!file.type.startsWith('image/')) { showNotification('이미지 파일만 업로드 가능합니다.', 'error'); imageInput.value=''; return; }
            const reader = new FileReader();
            reader.onload = function(ev) {
                previewImg.src = ev.target.result;
                preview.style.display = 'block';
                placeholder.style.display = 'none';
            };
            reader.readAsDataURL(file);
        });

        removeBtn && removeBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            imageInput.value = '';
            preview.style.display = 'none';
            placeholder.style.display = 'flex';
        });

        imageUpload.addEventListener('dragover', function(e) { e.preventDefault(); this.classList.add('dragover'); });
        imageUpload.addEventListener('dragleave', function(e) { e.preventDefault(); this.classList.remove('dragover'); });
        imageUpload.addEventListener('drop', function(e) {
            e.preventDefault(); this.classList.remove('dragover');
            const file = e.dataTransfer.files && e.dataTransfer.files[0];
            if (!file || !file.type.startsWith('image/')) return;
            imageInput.files = e.dataTransfer.files;
            const reader = new FileReader();
            reader.onload = function(ev) {
                previewImg.src = ev.target.result;
                preview.style.display = 'block';
                placeholder.style.display = 'none';
            };
            reader.readAsDataURL(file);
        });
    }

    // 기존 첨부 삭제 마킹 → deleteImageIds 히든 추가
    window.markDeleteFile = function(fileId, btnEl) {
        // UI 삭제
        const tag = btnEl.closest('.tag-item');
        if (tag) tag.remove();
        // 히든 누적
        if (deleteBucket) {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'deleteImageIds';
            input.value = String(fileId);
            deleteBucket.appendChild(input);
        }
        showNotification('삭제 목록에 추가되었습니다');
    };

    // 모달
    function showConfirmModal() {
        if (!validateForm()) return;
        if (confirmModal) confirmModal.style.display = 'flex';
    }
    function hideConfirmModal() {
        if (confirmModal) confirmModal.style.display = 'none';
    }

    // 제출
    function submitRecipe() {
        hideConfirmModal();
        submitBtn && (submitBtn.classList.add('loading'), submitBtn.disabled = true);
        submitFormBtn && (submitFormBtn.classList.add('loading'), submitFormBtn.disabled = true);
        setTimeout(() => {
            updateForm && updateForm.submit();
            window.removeEventListener('beforeunload', beforeUnloadHandler);
        }, 100);
    }

    // 유효성 검사
    function validateForm() {
        let ok = true;
        const required = ['title', 'category', 'difficulty', 'cookTime'];
        document.querySelectorAll('.error-message').forEach(n => n.remove());
        document.querySelectorAll('.error').forEach(el => el.classList.remove('error'));

        required.forEach(id => {
            const el = document.getElementById(id);
            if (el && !el.value.trim()) { showFieldError(el, '필수 입력 항목입니다.'); ok = false; }
        });

        const titleField = document.getElementById('title');
        if (titleField && titleField.value.length > 100) {
            showFieldError(titleField, '제목은 100자 이하로 입력해주세요.'); ok = false;
        }

        if (getIngredients().length === 0) { showNotification('최소 1개 이상의 재료를 입력해주세요.', 'error'); ok = false; }
        if (getInstructions().length === 0) { showNotification('최소 1개 이상의 조리법을 입력해주세요.', 'error'); ok = false; }

        return ok;
    }
    function showFieldError(field, msg) {
        field.classList.add('error');
        const div = document.createElement('div');
        div.className = 'error-message';
        div.textContent = msg;
        field.parentNode.appendChild(div);
    }

    function getIngredients() {
        const list = [];
        if (!ingredientsContainer) return list;
        ingredientsContainer.querySelectorAll('.ingredient-item').forEach(it => {
            const name = it.querySelector('input[name="ingredientName[]"]')?.value?.trim() || '';
            const amount = it.querySelector('input[name="ingredientAmount[]"]')?.value?.trim() || '';
            if (name || amount) list.push({name, amount});
        });
        return list;
    }
    function getInstructions() {
        const list = [];
        if (!instructionsContainer) return list;
        instructionsContainer.querySelectorAll('.instruction-item').forEach(it => {
            const content = it.querySelector('textarea[name="instructionContent[]"]')?.value?.trim() || '';
            if (content) list.push({content});
        });
        return list;
    }

    function hasUnsavedChanges() {
        const title = document.getElementById('title')?.value.trim() || '';
        return !!(title || getIngredients().length > 0 || getInstructions().length > 0);
    }
    function beforeUnloadHandler(e) {
        if (hasUnsavedChanges()) { e.preventDefault(); e.returnValue = ''; return ''; }
    }

    function showNotification(message, type = 'success') {
        const n = document.createElement('div');
        n.className = `notification ${type}`;
        n.textContent = message;
        document.body.appendChild(n);

        // 기본 스타일 적용
        n.style.opacity = '0';
        n.style.transform = 'translateX(20px)';
        n.style.transition = 'opacity 0.4s ease, transform 0.4s ease';

        // DOM 렌더 후 애니메이션 시작
        setTimeout(() => {
            n.style.opacity = '1';
            n.style.transform = 'translateX(0)';
        }, 10); // 약간의 딜레이로 transition 정상 작동

        // 3초 유지 후 서서히 사라짐
        setTimeout(() => {
            n.style.opacity = '0';
            n.style.transform = 'translateX(20px)';
            setTimeout(() => n.remove(), 400); // transition 종료 후 삭제
        }, 3000);
    }
});

// ✅ [추가] 썸네일 & 조리법 이미지 삭제 시 deleteImageIds 추가
document.addEventListener('DOMContentLoaded', function () {
    const deleteBucket = document.getElementById('deleteBucket'); // ✅ 삭제 누적 영역

    // --- 대표 이미지 삭제(X 버튼)
    const removeImageBtn = document.getElementById('removeImage');
    if (removeImageBtn) {
        removeImageBtn.addEventListener('click', function (e) {
            e.stopPropagation();

            const preview = document.getElementById('imagePreview');
            const img = preview?.querySelector('img');
            const fileIdMatch = img?.src?.match(/\/(\d+)(?:\?.*)?$/);
            const fileId = fileIdMatch ? fileIdMatch[1] : null;

            if (fileId && confirm('대표 이미지를 삭제하시겠습니까?')) {
                // ✅ 삭제 대상 hidden input 추가
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'deleteImageIds';
                input.value = String(fileId);
                deleteBucket.appendChild(input);

                // ✅ 미리보기 완전 제거
                if (img) img.remove();
                preview.innerHTML = '';
                preview.style.display = 'none';

                // ✅ placeholder 복귀
                const placeholder = document.getElementById('uploadPlaceholder');
                if (placeholder) placeholder.style.display = 'flex';

                showNotification('대표 이미지가 삭제 목록에 추가되었습니다.');
            }
        });
    }

    // --- 조리법 단계 이미지 삭제 버튼
    document.querySelectorAll('.remove-instruction-image').forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.stopPropagation();

            const preview = this.closest('.instruction-image-preview');
            const img = preview?.querySelector('img');
            const fileIdMatch = img?.src?.match(/\/(\d+)(?:\?.*)?$/);
            const fileId = fileIdMatch ? fileIdMatch[1] : null;

            if (fileId && confirm('이 조리법 이미지를 삭제하시겠습니까?')) {
                // ✅ 히든 input 추가 (DB 삭제용)
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'deleteImageIds';
                input.value = String(fileId);
                deleteBucket.appendChild(input);

                // ✅ 미리보기 완전 제거
                if (img) img.remove();
                preview.innerHTML = ''; // 내부 비움
                preview.style.display = 'none';

                // ✅ placeholder 복귀
                const placeholder = preview.previousElementSibling;
                if (placeholder) placeholder.style.display = 'flex';

                showNotification('조리법 이미지가 삭제 목록에 추가되었습니다.');
            }
        });
    });
});