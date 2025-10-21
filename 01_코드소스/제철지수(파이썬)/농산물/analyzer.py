# analyzer_crop_top10.py
import pandas as pd
import numpy as np
import glob
import os

CROP_DIR = r"data_crop"

def load_crop_data(folder):
    """data_crop 폴더에서 연도별 CSV 읽어서 메모리로 합치기"""
    files = glob.glob(os.path.join(folder, "crop_*.csv"))
    df_list = [pd.read_csv(f, parse_dates=["date"]) for f in files]  # ✅ date를 바로 datetime으로 읽기
    return pd.concat(df_list, ignore_index=True)


def compute_seasonal_index(df):
    """농산물 제철지수 계산 (0.7 × qty + 0.3 × price)"""
    df["qty"] = pd.to_numeric(df["qty"], errors="coerce").fillna(0)
    df["price"] = pd.to_numeric(df["price"], errors="coerce").fillna(0)

    # month → season 매핑
    df["month"] = df["date"].dt.month
    season_map = {
        12: "겨울", 1: "겨울", 2: "겨울",
        3: "봄", 4: "봄", 5: "봄",
        6: "여름", 7: "여름", 8: "여름",
        9: "가을", 10: "가을", 11: "가을"
    }
    df["season"] = df["month"].map(season_map)

    # 계절별 집계
    grouped = df.groupby(["season", "product"]).agg(
        total_qty=("qty", "sum"),
        total_price=("price", "sum")
    ).reset_index()

    # 로그 변환 + 정규화
    grouped["qty_log"] = np.log1p(grouped["total_qty"])
    grouped["price_log"] = np.log1p(grouped["total_price"])
    grouped["qty_norm"] = grouped.groupby("season")["qty_log"].transform(lambda x: x / x.max())
    grouped["price_norm"] = grouped.groupby("season")["price_log"].transform(lambda x: x / x.max())

    # 농산물 가중치 (0.7 / 0.3)
    grouped["seasonal_index"] = 0.7 * grouped["qty_norm"] + 0.3 * grouped["price_norm"]

    # 계절별 Top10 (봄/여름/가을/겨울 각각 10개)
    top10_list = []
    for s in ["봄", "여름", "가을", "겨울"]:
        season_df = grouped[grouped["season"] == s]
        season_top10 = season_df.sort_values("seasonal_index", ascending=False).head(10)
        top10_list.append(season_top10)

    top10 = pd.concat(top10_list, ignore_index=True)

    return top10


if __name__ == "__main__":
    # CSV → 메모리 로드
    df_crop = load_crop_data(CROP_DIR)

    # 제철지수 Top10 계산
    crop_top10 = compute_seasonal_index(df_crop)

    # 결과 저장 (최종 산출물만 남김)
    crop_top10.to_csv("seasonal_index_crop_top10.csv", index=False, encoding="utf-8-sig")

    print("✅ 농산물 제철지수 Top10 저장 완료")
    print(crop_top10[["season", "product", "total_qty", "total_price", "seasonal_index"]])
