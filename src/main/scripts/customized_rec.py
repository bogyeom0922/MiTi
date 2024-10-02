import pymysql  # MySQL 연결을 위한 라이브러리
import numpy as np  # 숫자 배열 및 계산을 위한 라이브러리
from scipy.spatial.distance import cosine  # 코사인 유사도 계산을 위한 함수

# user_record와 album 테이블에서 데이터 가져오는 함수
def fetch_records_and_albums(connection):
    with connection.cursor() as cursor:
        # user_record 테이블에서 user_id와 album_id 가져오기
        cursor.execute("SELECT user_id, album_id FROM user_record")
        records = cursor.fetchall()  # 결과를 튜플 리스트로 가져옴

        # album 테이블에서 id와 크롤링한 필드 값 가져오기
        cursor.execute("""
            SELECT id, music_acousticness, music_danceability, music_energy,
                   music_liveness, music_loudness, music_tempo, music_valence
            FROM album
        """)
        albums = cursor.fetchall()  # 결과를 튜플 리스트로 가져옴
    return records, albums  # 결과를 반환

# 사용자가 재생한 음악들의 평균 특성 벡터 계산
def calculate_average_features(user_records, album_dict, feature_indices):
    feature_sums = np.zeros(len(feature_indices))  # feature_indices 길이만큼 0으로 초기화된 배열 생성
    count = 0  # 유효한 앨범 개수를 세기 위한 변수

    for user_id, album_id in user_records:
        album_id_int = int(album_id)  # album_id를 정수로 변환
        if album_id_int in album_dict:
            feature_vector = np.array(album_dict[album_id_int], dtype=float)  # 실수로 변환
            selected_features = feature_vector[feature_indices]  # 지정된 인덱스의 특성 벡터 선택
            feature_sums += selected_features  # 특성 벡터를 합산
            count += 1  # 유효한 앨범 개수 증가

    if count == 0:  # 사용자가 재생한 앨범이 없으면
        return np.zeros(len(feature_indices))  # 0으로 초기화된 배열 반환
    else:
        return feature_sums / count  # 합산된 벡터를 앨범 개수로 나누어 평균 벡터 반환

# 메인 함수
def main():
    # MySQL 연결
    connection = pymysql.connect(
        host='mitidb.cvm64ss6y2xv.ap-northeast-2.rds.amazonaws.com',
        user='minseo',
        password='Alstj!!809',
        database='mitiDB'
    )

    # 각 그룹의 특성 인덱스 정의 (0부터 시작)
    features_group1_indices = [1, 2, 4, 5, 6]  # danceability, energy, tempo, loudness, valence
    features_group2_indices = [0, 3]  # acousticness, liveness

    try:
        # user_record와 album 테이블에서 데이터 가져오기
        records, albums = fetch_records_and_albums(connection)
        # album_id를 정수로 변환하여 딕셔너리 생성
        album_dict = {album[0]: np.array(album[1:], dtype=float) for album in albums}  # 실수로 변환

        # user_id별로 추천 결과 계산
        user_ids = set(user_id for user_id, _ in records)
        user_recommendations = {}

        for user_id in user_ids:
            # 해당 user_id의 레코드만 필터링
            user_records = [(uid, aid) for uid, aid in records if uid == user_id]

            print(f"Processing user_id: {user_id}")  # 추가된 로그

            # 각 그룹에 대해 평균 특성 벡터 계산
            average_features_group1 = calculate_average_features(user_records, album_dict, features_group1_indices)
            average_features_group2 = calculate_average_features(user_records, album_dict, features_group2_indices)

            # 각 앨범 특성 벡터와 사용자 평균 특성 벡터 간 코사인 유사도 계산 후 리스트에 저장
            similarities_group1 = []
            similarities_group2 = []

            for album_id, features in album_dict.items():
                try:
                    feature_group1 = features[features_group1_indices]
                    feature_group2 = features[features_group2_indices]

                    if np.all(feature_group1 == 0) or np.all(average_features_group1 == 0):
                        similarity_group1 = 0
                    else:
                        similarity_group1 = 1 - cosine(average_features_group1, feature_group1)

                    if np.all(feature_group2 == 0) or np.all(average_features_group2 == 0):
                        similarity_group2 = 0
                    else:
                        similarity_group2 = 1 - cosine(average_features_group2, feature_group2)

                    similarities_group1.append((album_id, similarity_group1))
                    similarities_group2.append((album_id, similarity_group2))

                except Exception as e:
                    print(f"Error processing album_id {album_id}: {e}")

            # 각 그룹별로 유사도 내림차순으로 정렬
            sorted_group1 = sorted(similarities_group1, key=lambda x: x[1], reverse=True)
            sorted_group2 = sorted(similarities_group2, key=lambda x: x[1], reverse=True)

            # 중복된 앨범만 선택하여 상위 20개 앨범 리스트에 추가
            top_albums = []
            seen_album_ids = set()

            for album_id, similarity in sorted_group1 + sorted_group2:
                if album_id in seen_album_ids:
                    continue
                top_albums.append((album_id, similarity))
                seen_album_ids.add(album_id)
                if len(top_albums) == 20:
                    break

            user_recommendations[user_id] = top_albums

        # 결과를 customized_rec 테이블에 삽입
        with connection.cursor() as cursor:
            for user_id, top_albums in user_recommendations.items():
                for album_id, _ in top_albums:
                    # album_id는 int로 변환
                    album_id = int(album_id)  # album_id를 정수로 변환

                    try:
                        print(f"Inserting into customized_rec: user_id={user_id}, album_id={album_id}")  # 로그 추가
                        cursor.execute(
                            "INSERT INTO customized_rec (user_id, album_id) VALUES (%s, %s)",
                            (user_id, album_id)  # user_id는 varchar로 그대로 사용
                        )
                        print(f"Successfully inserted: user_id={user_id}, album_id={album_id}")  # 삽입 성공 로그
                    except pymysql.IntegrityError:
                        print(f"Duplicate entry for user_id={user_id}, album_id={album_id}. Skipping...")  # 중복 로그
                    except Exception as e:
                        print(f"Error inserting user_id={user_id}, album_id={album_id}: {e}")  # 기타 오류 로그

            connection.commit()  # 변경사항을 데이터베이스에 반영

        print("Top 20 albums by cosine similarity have been inserted into customized_rec table.")

    except pymysql.MySQLError as e:
        print(f"Error: {e}")

    finally:
        connection.close()  # 데이터베이스 연결 닫기

if __name__ == "__main__":
    main()