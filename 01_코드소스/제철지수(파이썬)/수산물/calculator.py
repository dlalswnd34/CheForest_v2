import requests, time, random
import pandas as pd
import numpy as np
from concurrent.futures import ThreadPoolExecutor, as_completed
from config import SERVICE_KEY, URLS

# ======================
# 1. API 호출 함수 (재시도 포함)
# ======================
def fetch_data(url, perPage=8000, retries=3):
    """한 달치 데이터를 페이지 단위로 수집"""
    time.sleep(random.uniform(0.5, 1.5))  # 스레드 시작 분산
    page, results = 1, []

    while True:
        params = {
            "page": page,
            "perPage": perPage,
            "returnType": "JSON",
            "serviceKey": SERVICE_KEY
        }

        attempt = 0
        while attempt < retries:
            try:
                r = requests.get(url, params=params, timeout=30)
                if r.status_code == 200:
                    data = r.json().get("data", [])
                    if not data:
                        return results
                    results.extend(data)
                    print(f"📄 {url.split(':')[-1]} → {page} 페이지 ({len(data)}건)")
                    page += 1
                    time.sleep(1)
                    break
                else:
                    attempt += 1
                    print(f"⚠️ 오류 {url} (시도 {attempt}/{retries}) → {r.text[:100]}")
                    time.sleep(1)
            except Exception as e:
                attempt += 1
                print(f"⚠️ 예외 {url} (시도 {attempt}/{retries}) → {e}")
                time.sleep(1)
        else:
            # retries 소진 → 중단
            break
    return results


# ======================
# 2. 병렬 수집
# ======================
def collect_all(urls, workers=5, perPage=8000):
    all_data = []
    with ThreadPoolExecutor(max_workers=workers) as executor:
        futures = [executor.submit(fetch_data, url, perPage) for url in urls]
        for future in as_completed(futures):
            all_data.extend(future.result())
    return all_data


# ======================
# 3. 제철지수 계산 함수
# ======================
def compute_seasonal_index(df, label):
    seasonal = (
        df.groupby(["season", "fish"])
        .agg(total_qty=("qty", "sum"),
             total_price=("total_price", "sum"))
        .reset_index()
    )

    # 불필요 품목 제거 (젓 포함)
    exclude_keywords = ["기타", "잡", "류", "젓", "가공", "김"]
    seasonal = seasonal[~seasonal["fish"].str.contains("|".join(exclude_keywords))]

    # 로그/정규화
    seasonal["qty_log"] = np.log1p(seasonal["total_qty"])
    seasonal["price_log"] = np.log1p(seasonal["total_price"])
    seasonal["qty_norm"] = seasonal.groupby("season")["qty_log"].transform(lambda x: x / x.max())
    seasonal["price_norm"] = seasonal.groupby("season")["price_log"].transform(lambda x: x / x.max())

    # 제철지수 = 0.8*수량 + 0.2*가격
    seasonal["seasonal_index"] = 0.8 * seasonal["qty_norm"] + 0.2 * seasonal["price_norm"]

    # 상위 30% 컷
    def filter_top_quantile(group):
        cutoff = group["total_qty"].quantile(0.7)
        return group[group["total_qty"] >= cutoff]

    seasonal_filtered = seasonal.groupby("season", group_keys=False).apply(filter_top_quantile)

    # Top10
    top10 = (
        seasonal_filtered.sort_values(["season", "seasonal_index"], ascending=[True, False])
        .groupby("season")
        .head(10)
    )

    print(f"\n=== {label} 제철지수 Top10 ===")
    print(top10[["season", "fish", "total_qty", "total_price", "seasonal_index"]])
    return top10


# ======================
# 4. 실행
# ======================
if __name__ == "__main__":
    all_data = collect_all(URLS, workers=5, perPage=8000)
    df = pd.DataFrame(all_data)

    # 컬럼 정리
    df.rename(columns={
        "위판일자": "date",
        "수산물표준코드명": "fish",
        "어종상태명": "state",
        "물량(킬로그램)": "qty",
        "총 판매액": "total_price"
    }, inplace=True)

    df["date"] = pd.to_datetime(df["date"], errors="coerce")
    df["month"] = df["date"].dt.month
    df["qty"] = pd.to_numeric(df["qty"], errors="coerce")
    df["total_price"] = pd.to_numeric(df["total_price"], errors="coerce")

    season_map = {
        12: "겨울", 1: "겨울", 2: "겨울",
        3: "봄", 4: "봄", 5: "봄",
        6: "여름", 7: "여름", 8: "여름",
        9: "가을", 10: "가을", 11: "가을"
    }
    df["season"] = df["month"].map(season_map)

    # 제철지수 계산
    # top10_hwareo = compute_seasonal_index(df[df["state"] == "활어"], "활어")
    # top10_seoneo = compute_seasonal_index(df[df["state"] == "선어"], "선어")
    top10_both = compute_seasonal_index(df[df["state"].isin(["활어", "선어"])], "활어+선어")

    # CSV 저장 (Top10 결과만)
    result = pd.concat([
        # top10_hwareo.assign(category="활어"),
        # top10_seoneo.assign(category="선어"),
        top10_both.assign(category="활어+선어")
    ])
    result.to_csv("seasonal_index_top10.csv", index=False, encoding="utf-8-sig")
    print("✅ 제철지수 Top10 CSV 저장 완료!")
