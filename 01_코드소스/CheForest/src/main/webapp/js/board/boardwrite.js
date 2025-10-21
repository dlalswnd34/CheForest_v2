// CheForest 레시피 작성 JavaScript
// 주의: 이 파일은 기본 구조만 제공합니다. 실제 동작은 JSP 개발 시 구현해주세요.

document.addEventListener('DOMContentLoaded', function() {
    // DOM 요소들
    const createForm = document.getElementById('createForm');
    const backBtn = document.getElementById('backBtn');
    const saveDraftBtn = document.getElementById('saveDraftBtn');
    const submitBtn = document.getElementById('submitBtn');
    const submitFormBtn = document.getElementById('submitFormBtn');
    const cancelBtn = document.getElementById('cancelBtn');

    // 이미지 업로드 관련
    const imageUploadArea = document.getElementById('imageUploadArea');
    const mainImageInput = document.getElementById('thumbnail'); // [CHANGE#1] 기존 'mainImage' → 'thumbnail'
    const uploadPlaceholder = document.getElementById('uploadPlaceholder');
    const imagePreview = document.getElementById('imagePreview');
    const previewImg = document.getElementById('previewImg');
    const removeImageBtn = document.getElementById('removeImage');
    const changeImageBtn = document.getElementById('changeImage');

    // 재료 관련
    const addIngredientBtn = document.getElementById('addIngredientBtn');
    const ingredientsContainer = document.getElementById('ingredientsContainer');
    const ingredientsEmpty = document.getElementById('ingredientsEmpty');

    // 조리법 관련
    const addInstructionBtn = document.getElementById('addInstructionBtn');
    const instructionsContainer = document.getElementById('instructionsContainer');
    const instructionsEmpty = document.getElementById('instructionsEmpty');

    // 태그 관련
    const tagsInput = document.getElementById('tags');
    const tagsPreview = document.getElementById('tagsPreview');

    // 모달 관련
    const confirmModal = document.getElementById('confirmModal');
    const modalCancelBtn = document.getElementById('modalCancelBtn');
    const modalConfirmBtn = document.getElementById('modalConfirmBtn');

    // 도움말 사이드바 관련
    const helpSidebar = document.getElementById('helpSidebar');
    const helpToggle = document.getElementById('helpToggle');
    const helpClose = document.getElementById('helpClose');

    // 변수
    let ingredientCount = 1; // 기본 재료 1개
    let instructionCount = 1; // 기본 조리법 1개

    // 페이지 초기화
    initializePage();

    function initializePage() {
        // 기본 이벤트 리스너 설정
        setupEventListeners();

        // 기존 데이터 복원 (임시저장된 데이터)
        restoreDraftData();

        // 태그 미리보기 초기화
        updateTagsPreview();

        console.log('레시피 작성 페이지 초기화 완료');
    }

    function setupEventListeners() {
        // 뒤로가기 버튼
        if (backBtn) {
            backBtn.addEventListener('click', function (e) {
                e.preventDefault();
                if (!hasUnsavedChanges() || confirm('작성 중인 내용이 있습니다. 정말 나가시겠습니까?')) {
                    goToListSafely();
                }
            });
        }

        // 상단 제출 버튼
        if (submitBtn) {
            submitBtn.addEventListener('click', showConfirmModal);
        }

        // 하단 제출 버튼
        if (submitFormBtn) {
            submitFormBtn.addEventListener('click', function(e) {
                e.preventDefault();
                showConfirmModal();
            });
        }

        // 취소 버튼
        if (cancelBtn) {
            cancelBtn.addEventListener('click', function (e) {
                e.preventDefault();
                if (!hasUnsavedChanges() || confirm('작성 중인 내용이 사라집니다. 정말 취소하시겠습니까?')) {
                    goToListSafely();
                }
            });
        }

        // 이미지 업로드 이벤트
        setupImageUpload();

        // 재료 관련 이벤트
        setupIngredients();

        // 조리법 관련 이벤트
        setupInstructions();

        // 태그 관련 이벤트
        setupTags();

        // 모달 관련 이벤트
        setupModal();

        // 도움말 사이드바 이벤트
        setupHelpSidebar();

        // 폼 제출 이벤트
        if (createForm) {
            createForm.addEventListener('submit', function(e) {
                e.preventDefault();
                showConfirmModal();
            });
        }
    }

    // 이미지 업로드 설정
    function setupImageUpload() {
        if (!imageUploadArea || !mainImageInput) return;

        // 클릭으로 파일 선택
        imageUploadArea.addEventListener('click', function(e) {
            // ▼▼▼ 변경된 부분: null-safe 체크 추가 ▼▼▼
            const isActionBtn =
                (removeImageBtn && removeImageBtn.contains(e.target)) ||
                (changeImageBtn && changeImageBtn.contains(e.target));

            if (!isActionBtn) {
                mainImageInput.click();
            }
            // ▲▲▲ 변경된 부분 끝 ▲▲▲
        });

        // 파일 선택 시 미리보기
        mainImageInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                handleImageFile(file);
            }
        });

        // 드래그 앤 드롭
        imageUploadArea.addEventListener('dragover', function(e) {
            e.preventDefault();
            this.classList.add('dragover');
        });

        imageUploadArea.addEventListener('dragleave', function(e) {
            e.preventDefault();
            this.classList.remove('dragover');
        });

        imageUploadArea.addEventListener('drop', function(e) {
            e.preventDefault();
            this.classList.remove('dragover');

            const files = e.dataTransfer.files;
            if (files.length > 0) {
                const file = files[0];
                if (file.type.startsWith('image/')) {
                    mainImageInput.files = files;
                    handleImageFile(file);
                }
            }
        });

        // 이미지 제거
        if (removeImageBtn) {
            removeImageBtn.addEventListener('click', function(e) {
                e.stopPropagation();
                removeMainImage();
            });
        }

        // 이미지 변경
        if (changeImageBtn) {
            changeImageBtn.addEventListener('click', function(e) {
                e.stopPropagation();
                mainImageInput.click();
            });
        }
    }

    // 이미지 파일 처리
    function handleImageFile(file) {
        // 파일 크기 체크 (5MB)
        if (file.size > 5 * 1024 * 1024) {
            showNotification('파일 크기는 5MB 이하여야 합니다.', 'error');
            return;
        }

        // 파일 타입 체크
        if (!file.type.startsWith('image/')) {
            showNotification('이미지 파일만 업로드 가능합니다.', 'error');
            return;
        }

        // 미리보기 표시
        const reader = new FileReader();
        reader.onload = function(e) {
            if (previewImg && imagePreview && uploadPlaceholder) {
                previewImg.src = e.target.result;
                imagePreview.style.display = 'block';
                uploadPlaceholder.style.display = 'none';
            }
        };
        reader.readAsDataURL(file);
    }

    // 메인 이미지 제거
    function removeMainImage() {
        if (mainImageInput) {
            mainImageInput.value = '';
        }
        if (imagePreview && uploadPlaceholder) {
            imagePreview.style.display = 'none';
            uploadPlaceholder.style.display = 'flex';
        }
    }

    // 재료 관련 설정
    function setupIngredients() {
        if (addIngredientBtn) {
            addIngredientBtn.addEventListener('click', addIngredient);
        }
    }

    // 재료 추가
    function addIngredient() {
        const ingredientHTML = `
            <div class="ingredient-item fade-in" data-index="${ingredientCount}">
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
        `;

        if (ingredientsContainer) {
            ingredientsContainer.insertAdjacentHTML('beforeend', ingredientHTML);
            ingredientCount++;

            // 빈 상태 숨기기
            if (ingredientsEmpty) {
                ingredientsEmpty.style.display = 'none';
            }
        }
    }

    // 재료 제거 (전역 함수)
    window.removeIngredient = function(button) {
        const ingredientItem = button.closest('.ingredient-item');
        if (ingredientItem) {
            ingredientItem.remove();

            // 재료가 없으면 빈 상태 표시
            const remainingIngredients = ingredientsContainer.querySelectorAll('.ingredient-item');
            if (remainingIngredients.length === 0 && ingredientsEmpty) {
                ingredientsEmpty.style.display = 'block';
            }
        }
    };

    // 조리법 관련 설정
    function setupInstructions() {
        if (addInstructionBtn) {
            addInstructionBtn.addEventListener('click', addInstruction);
        }
    }

    // 조리법 추가
    function addInstruction() {
        const instructionHTML = `
            <div class="instruction-item fade-in" data-index="${instructionCount}">
                <div class="instruction-header">
                    <span class="step-number">${instructionCount + 1}단계</span>
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
                            <input type="file" class="instruction-image-input" 
                                   name="instructionImage" accept="image/*" hidden> <!-- [CHANGE#2] 기존 instructionImage[] → instructionImage -->
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
                </div>
            </div>
        `;

        if (instructionsContainer) {
            instructionsContainer.insertAdjacentHTML('beforeend', instructionHTML);

            // 새로 추가된 조리법의 이미지 업로드 이벤트 설정
            const newInstruction = instructionsContainer.lastElementChild;
            setupInstructionImageUpload(newInstruction);

            instructionCount++;

            // 빈 상태 숨기기
            if (instructionsEmpty) {
                instructionsEmpty.style.display = 'none';
            }

            // 단계 번호 업데이트
            updateStepNumbers();
        }
    }

    // 조리법 제거 (전역 함수)
    window.removeInstruction = function(button) {
        const instructionItem = button.closest('.instruction-item');
        if (instructionItem) {
            instructionItem.remove();

            // 조리법이 없으면 빈 상태 표시
            const remainingInstructions = instructionsContainer.querySelectorAll('.instruction-item');
            if (remainingInstructions.length === 0 && instructionsEmpty) {
                instructionsEmpty.style.display = 'block';
            }

            // 단계 번호 업데이트
            updateStepNumbers();
        }
    };

    // 단계 번호 업데이트
    function updateStepNumbers() {
        const instructions = instructionsContainer.querySelectorAll('.instruction-item');
        instructions.forEach((instruction, index) => {
            const stepNumber = instruction.querySelector('.step-number');
            if (stepNumber) {
                stepNumber.textContent = `${index + 1}단계`;
            }
        });
    }

    // 조리법 이미지 업로드 설정
    function setupInstructionImageUpload(instructionElement) {
        const imageUpload = instructionElement.querySelector('.instruction-image-upload');
        const imageInput = instructionElement.querySelector('.instruction-image-input');
        const placeholder = instructionElement.querySelector('.instruction-upload-placeholder');
        const preview = instructionElement.querySelector('.instruction-image-preview');
        const previewImg = instructionElement.querySelector('.instruction-preview-img');
        const uploadBtn = instructionElement.querySelector('.instruction-upload-btn');
        const changeBtn = instructionElement.querySelector('.change-instruction-image');
        const removeBtn = instructionElement.querySelector('.remove-instruction-image');

        if (!imageUpload || !imageInput) return;

        // 클릭으로 파일 선택
        uploadBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            imageInput.click();
        });

        changeBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            imageInput.click();
        });

        // 파일 선택 시 미리보기
        imageInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                handleInstructionImageFile(file, preview, previewImg, placeholder);
            }
        });

        // 이미지 제거
        removeBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            imageInput.value = '';
            preview.style.display = 'none';
            placeholder.style.display = 'flex';
        });

        // 드래그 앤 드롭
        imageUpload.addEventListener('dragover', function(e) {
            e.preventDefault();
            this.classList.add('dragover');
        });

        imageUpload.addEventListener('dragleave', function(e) {
            e.preventDefault();
            this.classList.remove('dragover');
        });

        imageUpload.addEventListener('drop', function(e) {
            e.preventDefault();
            this.classList.remove('dragover');

            const files = e.dataTransfer.files;
            if (files.length > 0) {
                const file = files[0];
                if (file.type.startsWith('image/')) {
                    imageInput.files = files;
                    handleInstructionImageFile(file, preview, previewImg, placeholder);
                }
            }
        });
    }

    // 조리법 이미지 파일 처리
    function handleInstructionImageFile(file, preview, previewImg, placeholder) {
        // 파일 크기 체크 (5MB)
        if (file.size > 5 * 1024 * 1024) {
            showNotification('파일 크기는 5MB 이하여야 합니다.', 'error');
            return;
        }

        // 파일 타입 체크
        if (!file.type.startsWith('image/')) {
            showNotification('이미지 파일만 업로드 가능합니다.', 'error');
            return;
        }

        // 미리보기 표시
        const reader = new FileReader();
        reader.onload = function(e) {
            previewImg.src = e.target.result;
            preview.style.display = 'block';
            placeholder.style.display = 'none';
        };
        reader.readAsDataURL(file);
    }

    // 기존 조리법의 이미지 업로드 설정 (페이지 로드 시)
    document.querySelectorAll('.instruction-item').forEach(setupInstructionImageUpload);

    // 태그 관련 설정
    function setupTags() {
        if (tagsInput) {
            tagsInput.addEventListener('input', updateTagsPreview);
            tagsInput.addEventListener('keydown', function(e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    updateTagsPreview();
                }
            });
        }
    }

    // 태그 미리보기 업데이트
    function updateTagsPreview() {
        if (!tagsInput || !tagsPreview) return;

        const tagsText = tagsInput.value.trim();
        tagsPreview.innerHTML = '';

        if (tagsText) {
            const tags = tagsText.split(',').map(tag => tag.trim()).filter(tag => tag);

            tags.forEach((tag, index) => {
                const tagElement = document.createElement('span');
                tagElement.className = 'tag-item fade-in';
                tagElement.innerHTML = `
                    ${tag}
                    <button type="button" class="tag-remove" onclick="removeTag(${index})">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <line x1="18" y1="6" x2="6" y2="18"></line>
                            <line x1="6" y1="6" x2="18" y2="18"></line>
                        </svg>
                    </button>
                `;
                tagsPreview.appendChild(tagElement);
            });
        }
    }

    // 태그 제거 (전역 함수)
    window.removeTag = function(index) {
        if (!tagsInput) return;

        const tags = tagsInput.value.split(',').map(tag => tag.trim()).filter(tag => tag);
        tags.splice(index, 1);
        tagsInput.value = tags.join(', ');
        updateTagsPreview();
    };

    // 모달 관련 설정
    function setupModal() {
        if (modalCancelBtn) {
            modalCancelBtn.addEventListener('click', hideConfirmModal);
        }

        if (modalConfirmBtn) {
            modalConfirmBtn.addEventListener('click', submitRecipe);
        }

        // 모달 외부 클릭 시 닫기
        if (confirmModal) {
            confirmModal.addEventListener('click', function(e) {
                if (e.target === confirmModal) {
                    hideConfirmModal();
                }
            });
        }
    }

    // 확인 모달 표시
    function showConfirmModal() {
        if (!validateForm()) {
            return;
        }

        if (confirmModal) {
            confirmModal.style.display = 'flex';
        }
    }

    // 확인 모달 숨기기
    function hideConfirmModal() {
        if (confirmModal) {
            confirmModal.style.display = 'none';
        }
    }

    // 페이지 떠날 때 경고 핸들러
    function beforeUnloadHandler(e) {
        if (hasUnsavedChanges()) {
            e.preventDefault();
            e.returnValue = '';
            return '';
        }
    }

    function goToListSafely() {
        // 이탈 경고 해제
        window.removeEventListener('beforeunload', beforeUnloadHandler);

        // 같은 도메인에서 왔으면 뒤로가기, 아니면 목록으로
        try {
            if (document.referrer) {
                const ref = new URL(document.referrer, location.origin);
                if (ref.origin === location.origin) {
                    history.back();
                    return;
                }
            }
        } catch (e) { /* ignore */ }

        location.href = '/board/list';
    }

// 이벤트 등록
    window.addEventListener('beforeunload', beforeUnloadHandler);

    // 레시피 제출
    function submitRecipe() {
        hideConfirmModal();

        // 👉 1) 로딩 상태 표시
        if (submitBtn) {
            submitBtn.classList.add('loading');
            submitBtn.disabled = true;
        }
        if (submitFormBtn) {
            submitFormBtn.classList.add('loading');
            submitFormBtn.disabled = true;
        }

        // 👉 2) repaint 이후 실행 (setTimeout 0ms)
        setTimeout(() => {
            if (createForm) {
                createForm.submit();
                localStorage.removeItem('recipe_draft');
            }

            // beforeunload 핸들러 제거
            window.removeEventListener('beforeunload', beforeUnloadHandler);
        }, 2000);  // 0~100ms 정도 주면 충분
    }

    // 도움말 사이드바 설정
    function setupHelpSidebar() {
        if (helpToggle) {
            helpToggle.addEventListener('click', function() {
                helpSidebar.classList.add('active');
            });
        }

        if (helpClose) {
            helpClose.addEventListener('click', function() {
                helpSidebar.classList.remove('active');
            });
        }

        // 사이드바 외부 클릭 시 닫기
        document.addEventListener('click', function(e) {
            if (helpSidebar && helpSidebar.classList.contains('active')) {
                if (!helpSidebar.contains(e.target) && e.target !== helpToggle) {
                    helpSidebar.classList.remove('active');
                }
            }
        });
    }

    // 자동 저장 설정
    function setupAutoSave() {
        const autoSaveFields = ['title', 'category', 'difficulty', 'cookTime', 'servings', 'description'];

        autoSaveFields.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (field) {
                field.addEventListener('input', debounce(saveDraft, 2000));
            }
        });
    }

    // 임시 저장
    function saveDraft() {
        const formData = {
            title: document.getElementById('title')?.value || '',
            category: document.getElementById('category')?.value || '',
            difficulty: document.getElementById('difficulty')?.value || '',
            cookTime: document.getElementById('cookTime')?.value || '',
            description: document.getElementById('description')?.value || '',
            tags: document.getElementById('tags')?.value || '',
            tips: document.getElementById('tips')?.value || '',
            ingredients: getIngredients(),
            instructions: getInstructions()
        };

        localStorage.setItem('recipe_draft', JSON.stringify(formData));
        showNotification('임시 저장되었습니다', 'success');
        console.log('임시 저장 완료');
    }

    // 임시 저장된 데이터 복원
    function restoreDraftData() {
        const draft = localStorage.getItem('recipe_draft');
        if (draft) {
            try {
                const formData = JSON.parse(draft);

                // 기본 필드 복원
                Object.keys(formData).forEach(key => {
                    if (key !== 'ingredients' && key !== 'instructions') {
                        const field = document.getElementById(key);
                        if (field && formData[key]) {
                            field.value = formData[key];
                        }
                    }
                });

                // 재료 복원
                if (formData.ingredients && formData.ingredients.length > 0) {
                    restoreIngredients(formData.ingredients);
                }

                // 조리법 복원
                if (formData.instructions && formData.instructions.length > 0) {
                    restoreInstructions(formData.instructions);
                }

                // 태그 미리보기 업데이트
                updateTagsPreview();

                console.log('임시 저장된 내용을 복원했습니다.');
            } catch (e) {
                console.error('임시 저장 내용 복원 실패:', e);
            }
        }
    }

    // 재료 데이터 가져오기
    function getIngredients() {
        const ingredients = [];
        const ingredientItems = ingredientsContainer.querySelectorAll('.ingredient-item');

        ingredientItems.forEach(item => {
            const name = item.querySelector('input[name="ingredientName[]"]')?.value || '';
            const amount = item.querySelector('input[name="ingredientAmount[]"]')?.value || '';

            if (name.trim() || amount.trim()) {
                ingredients.push({ name: name.trim(), amount: amount.trim() });
            }
        });

        return ingredients;
    }

    // 조리법 데이터 가져오기
    function getInstructions() {
        const instructions = [];
        const instructionItems = instructionsContainer.querySelectorAll('.instruction-item');

        instructionItems.forEach(item => {
            const content = item.querySelector('textarea[name="instructionContent[]"]')?.value || '';

            if (content.trim()) {
                instructions.push({ content: content.trim() });
            }
        });

        return instructions;
    }

    // 재료 복원
    function restoreIngredients(ingredients) {
        // 기존 재료 제거 (첫 번째 제외)
        const existingItems = ingredientsContainer.querySelectorAll('.ingredient-item');
        for (let i = 1; i < existingItems.length; i++) {
            existingItems[i].remove();
        }

        ingredients.forEach((ingredient, index) => {
            if (index === 0) {
                // 첫 번째 재료는 기존 것 사용
                const firstItem = ingredientsContainer.querySelector('.ingredient-item');
                if (firstItem) {
                    const nameInput = firstItem.querySelector('input[name="ingredientName[]"]');
                    const amountInput = firstItem.querySelector('input[name="ingredientAmount[]"]');

                    if (nameInput) nameInput.value = ingredient.name;
                    if (amountInput) amountInput.value = ingredient.amount;
                }
            } else {
                // 나머지는 추가
                addIngredient();
                const lastItem = ingredientsContainer.lastElementChild;
                const nameInput = lastItem.querySelector('input[name="ingredientName[]"]');
                const amountInput = lastItem.querySelector('input[name="ingredientAmount[]"]');

                if (nameInput) nameInput.value = ingredient.name;
                if (amountInput) amountInput.value = ingredient.amount;
            }
        });
    }

    // 조리법 복원
    function restoreInstructions(instructions) {
        // 기존 조리법 제거 (첫 번째 제외)
        const existingItems = instructionsContainer.querySelectorAll('.instruction-item');
        for (let i = 1; i < existingItems.length; i++) {
            existingItems[i].remove();
        }

        instructions.forEach((instruction, index) => {
            if (index === 0) {
                // 첫 번째 조리법은 기존 것 사용
                const firstItem = instructionsContainer.querySelector('.instruction-item');
                if (firstItem) {
                    const contentTextarea = firstItem.querySelector('textarea[name="instructionContent[]"]');
                    if (contentTextarea) contentTextarea.value = instruction.content;
                }
            } else {
                // 나머지는 추가
                addInstruction();
                const lastItem = instructionsContainer.lastElementChild;
                const contentTextarea = lastItem.querySelector('textarea[name="instructionContent[]"]');
                if (contentTextarea) contentTextarea.value = instruction.content;
            }
        });

        updateStepNumbers();
    }

    // 폼 유효성 검사
    function validateForm() {
        let isValid = true;
        const requiredFields = ['title', 'category', 'difficulty', 'cookTime'];

        // 기존 에러 메시지 제거
        document.querySelectorAll('.error-message').forEach(msg => msg.remove());
        document.querySelectorAll('.error').forEach(field => field.classList.remove('error'));

        // 필수 필드 검사
        requiredFields.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (field && !field.value.trim()) {
                showFieldError(field, '필수 입력 항목입니다.');
                isValid = false;
            }
        });

        // 제목 길이 검사
        const titleField = document.getElementById('title');
        if (titleField && titleField.value.length > 100) {
            showFieldError(titleField, '제목은 100자 이하로 입력해주세요.');
            isValid = false;
        }

        // 재료 검사
        const ingredients = getIngredients();
        if (ingredients.length === 0) {
            showNotification('최소 1개 이상의 재료를 입력해주세요.', 'error');
            isValid = false;
        }

        // 조리법 검사
        const instructions = getInstructions();
        if (instructions.length === 0) {
            showNotification('최소 1개 이상의 조리법을 입력해주세요.', 'error');
            isValid = false;
        }

        return isValid;
    }

    // 필드 에러 표시
    function showFieldError(field, message) {
        field.classList.add('error');

        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.textContent = message;

        field.parentNode.appendChild(errorDiv);
    }

    // 변경사항 확인
    function hasUnsavedChanges() {
        const title = document.getElementById('title')?.value.trim() || '';
        const description = document.getElementById('description')?.value.trim() || '';
        const ingredients = getIngredients();
        const instructions = getInstructions();

        return title || description || ingredients.length > 0 || instructions.length > 0;
    }

    // 알림 메시지 표시
    function showNotification(message, type = 'success') {
        const n = document.createElement('div');
        n.className = `notification ${type}`;
        n.textContent = message;
        document.body.appendChild(n);

        // 인라인 트랜지션(외부 CSS 간섭 최소화)
        n.style.opacity = '0';
        n.style.transform = 'translateX(20px)';
        n.style.transition = 'opacity 0.4s ease, transform 0.4s ease';

        // DOM 렌더 후 등장
        setTimeout(() => {
            n.style.opacity = '1';
            n.style.transform = 'translateX(0)';
        }, 10);

        // 3초 유지 후 서서히 사라짐
        setTimeout(() => {
            n.style.opacity = '0';
            n.style.transform = 'translateX(20px)';
            setTimeout(() => n.remove(), 400);
        }, 3000);
    }

    // 디바운스 함수
    function debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // 키보드 단축키
    document.addEventListener('keydown', function(e) {
        // Ctrl + S: 임시 저장
        if (e.ctrlKey && e.key === 's') {
            e.preventDefault();
            saveDraft();
        }

        // Esc: 모달 닫기
        if (e.key === 'Escape') {
            if (confirmModal && confirmModal.style.display === 'flex') {
                hideConfirmModal();
            }

            if (helpSidebar && helpSidebar.classList.contains('active')) {
                helpSidebar.classList.remove('active');
            }
        }
    });

    // 접근성 개선
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Tab') {
            document.body.classList.add('user-is-tabbing');
        }
    });

    document.addEventListener('mousedown', function() {
        document.body.classList.remove('user-is-tabbing');
    });
});

// 공통 유틸리티 함수들

// 파일 크기 포맷팅
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// 이미지 압축 함수 (선택사항)
function compressImage(file, maxWidth = 1920, quality = 0.8) {
    return new Promise((resolve) => {
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        const img = new Image();

        img.onload = function() {
            const ratio = Math.min(maxWidth / img.width, maxWidth / img.height);
            canvas.width = img.width * ratio;
            canvas.height = img.height * ratio;

            ctx.drawImage(img, 0, 0, canvas.width, canvas.height);

            canvas.toBlob(resolve, 'image/jpeg', quality);
        };

        img.src = URL.createObjectURL(file);
    });
}
