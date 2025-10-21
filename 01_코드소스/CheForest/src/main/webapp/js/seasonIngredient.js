// seasonIngredient.js (lean, single-init)
(function () {
    'use strict';
    if (window.__seasonInitDone) return; window.__seasonInitDone = true;

    const SEASONS = ['spring','summer','autumn','winter'];
    const META = {
        spring: { emoji: '🌸', title: '봄철 식재료' },
        summer: { emoji: '☀️', title: '여름철 식재료' },
        autumn: { emoji: '🍂', title: '가을철 식재료' },
        winter: { emoji: '⛄', title: '겨울철 식재료' }
    };

    const body = document.body;
    const container = document.querySelector('.ingredients-container');
    const tabList = document.querySelector('.tab-list');
    const countEl = document.querySelector('.ingredients-count');
    const iconEl = document.querySelector('.season-icon');
    const titleEl = document.querySelector('.page-title');

    function showPanel(season){
        document.querySelectorAll('[data-season-panel]').forEach(p=>{
            p.hidden = (p.dataset.seasonPanel !== season);
        });
    }

    function setActiveTab(season){
        document.querySelectorAll('.tab-button').forEach(b=>{
            b.classList.toggle('active', b.dataset.season === season);
        });
    }

    function applySeason(season){
        if (!SEASONS.includes(season)) season = 'spring';

        // body / container 클래스 스위칭
        SEASONS.forEach(s=>{
            body.classList.remove(`season-${s}`);
            if (container) container.classList.remove(`season-${s}`);
        });
        body.classList.add(`season-${season}`);
        if (container) container.classList.add(`season-${season}`);

        // 헤더 텍스트/아이콘
        const m = META[season];
        if (iconEl) iconEl.textContent = m.emoji;
        if (titleEl) titleEl.textContent = m.title;

        // 패널 + 탭 활성
        showPanel(season);
        setActiveTab(season);

        // 개수 갱신
        if (countEl) {
            const n = document.querySelectorAll(`.ingredients-grid[data-season-panel="${season}"] .ingredient-card`).length;
            countEl.textContent = `총 ${n}가지 식재료`;
        }
    }

    // 탭 클릭(단일 리스너)
    if (tabList){
        tabList.addEventListener('click', (e)=>{
            const btn = e.target.closest('.tab-button');
            if (!btn) return;
            e.preventDefault();
            applySeason(btn.dataset.season);
        });
    }

    // 초기 적용: .tab-button.active 기준 (없으면 spring)
    const initial = (document.querySelector('.tab-button.active')?.dataset.season) || 'spring';
    applySeason(initial);

    // (선택) JSP에 onclick이 남아 있어도 깨지지 않게 백컴패트 유지
    window.switchSeasonTab = applySeason;
})();
