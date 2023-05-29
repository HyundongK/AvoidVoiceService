from flask import Flask, request, jsonify
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
import pickle

app = Flask(__name__)

# 나이브 베이즈 분류 모델 로드
with open('naivebayes_model.pkl', 'rb') as f:
    nb_model = pickle.load(f)

# TF-IDF 벡터화 모델 로드
with open('tfidf_vectorizer.pkl', 'rb') as f:
    tfidf_vectorizer = pickle.load(f)

# 텍스트 데이터 전처리 및 예측
def preprocess(text):
    # TF-IDF 벡터화
    vectorized_text = tfidf_vectorizer.transform([text])

    return vectorized_text

@app.route('/predict', methods=['GET'])
def hello():
    return jsonify("hello")

# API 엔드포인트 설정
# query parameter로 보내면 됨
@app.route('/predict', methods=['POST'])
def predict():
    # POST 요청에서 데이터 가져오기

    text = request.args.get('text', '')

    # 텍스트 데이터 전처리
    vectorized_text = preprocess(text)

    # 예측
    prediction = nb_model.predict(vectorized_text)

    # 예측 결과 반환
    return jsonify({'prediction': bool(prediction[0])})

if __name__ == '__main__':
    app.run(host='192.168.0.104', port=8080)
