import requests
import pandas as pd
import datetime
import os
from concurrent.futures import ThreadPoolExecutor, as_completed
from tqdm import tqdm

API_KEY = "--"   # 본인 키 입력
SERVICE_ID = "Grid_20240625000000000658_1"   # 농산물용 서비스 ID
BASE_URL = f"http://211.237.50.150:7080/openapi/{API_KEY}/json/{SERVICE_ID}"

SAVE_DIR = "data_crop"
os.makedirs(SAVE_DIR, exist_ok=True)


def fetch_one_day(date_str, page_size=1000):
    """특정 일자의 데이터 수집"""
    start, end = 1, page_size
    all_rows = []
    while True:
        url = f"{BASE_URL}/{start}/{end}?REGIST_DT={date_str}"
        r = requests.get(url, timeout=30)
        if r.status_code != 200:
            break
        data = r.json().get(SERVICE_ID, {}).get("row", [])
        if not data:
            break
        all_rows.extend(data)
        start += page_size
        end += page_size
    return all_rows


def collect_one_year(year, workers=10):
    """특정 연도의 모든 데이터를 병렬 수집"""
    start_date = datetime.date(year, 1, 1)
    end_date   = datetime.date(year, 12, 31)
    date_list = [
        (start_date + datetime.timedelta(days=n)).strftime("%Y%m%d")
        for n in range((end_date - start_date).days + 1)
    ]

    all_data = []
    with ThreadPoolExecutor(max_workers=workers) as executor:
        futures = {executor.submit(fetch_one_day, d): d for d in date_list}
        for _ in tqdm(as_completed(futures), total=len(futures), desc=f"{year}년"):
            try:
                all_data.extend(_.result())
            except Exception as e:
                print(f"⚠️ {futures[_]} 실패: {e}")

    if not all_data:
        print(f"⚠ {year} 데이터 없음")
        return pd.DataFrame()

    df = pd.DataFrame(all_data)

    # ✅ 필요한 컬럼만 추출 및 표준화
    df = df.rename(columns={
        "REGIST_DT": "date",
        "MIDNAME": "product",
        "TOTQTY": "qty",
        "TOTAMT": "price"
    })

    # 문자열 → 날짜형 변환
    df["date"] = pd.to_datetime(df["date"], format="%Y%m%d", errors="coerce")

    df = df[["date","product","qty","price"]]

    return df


if __name__ == "__main__":
    for year in range(2022, 2026):  # 2022 ~ 2025
        df = collect_one_year(year, workers=10)
        if not df.empty:
            save_path = os.path.join(SAVE_DIR, f"crop_{year}.csv")
            df.to_csv(save_path, index=False, encoding="utf-8-sig")
            print(f"✅ {year} 저장 완료 ({len(df)}건)")
