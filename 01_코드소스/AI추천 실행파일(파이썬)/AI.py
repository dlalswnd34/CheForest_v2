import pandas as pd
import requests
from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route("/recommend/ai")
def recommend_ai():
    grade = request.args.get("grade", "좋음")  # 기본값: 좋음
    all_recipes = []

    if grade == "좋음":
        categories = ["한식", "일식"]
        for category in categories:
            url = f"http://localhost:8080/recipe/api/list?categoryKr={category}"
            try:
                resp = requests.get(url)
                data = resp.json()
                if data:
                    all_recipes.extend(data)
            except Exception as e:
                print("API 오류:", e)

    elif grade == "보통":
        categories = ["양식", "중식"]
        for category in categories:
            url = f"http://localhost:8080/recipe/api/list?categoryKr={category}"
            try:
                resp = requests.get(url)
                data = resp.json()
                if data:
                    all_recipes.extend(data)
            except Exception as e:
                print("API 오류:", e)

    elif grade in ["나쁨", "매우나쁨"]:
        # ✅ dustGood=Y 호출
        url = "http://localhost:8080/recipe/api/list?dustGood=Y"
        try:
            resp = requests.get(url)
            data = resp.json()
            if data:
                all_recipes.extend(data)
        except Exception as e:
            print("API 오류:", e)

    else:
        return jsonify([])

    if not all_recipes:
        return jsonify([])

    df = pd.DataFrame(all_recipes)
    sample = df.sample(n=min(5, len(df)))  # 최대 5개 랜덤

    return jsonify(sample.to_dict(orient="records"))

if __name__ == "__main__":
    app.run(port=5000, debug=True)
