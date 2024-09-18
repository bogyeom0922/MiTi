import pymysql
import numpy as np
from scipy.spatial.distance import cosine

def fetch_album_features(connection, album_ids):
    with connection.cursor() as cursor:
        format_strings = ','.join(['%s'] * len(album_ids))
        query = f"SELECT id, music_acousticness, music_danceability, music_energy, music_liveness, music_loudness, music_tempo, music_valence FROM album WHERE id IN ({format_strings})"
        cursor.execute(query, tuple(album_ids))
        albums = cursor.fetchall()
    return {album[0]: np.array(album[1:], dtype=float) for album in albums}

def calculate_average_features(user_records, album_dict, feature_indices):
    feature_sums = np.zeros(len(feature_indices))
    count = 0

    for album_id in user_records:
        if album_id in album_dict:
            feature_vector = np.array(album_dict[album_id], dtype=float)
            selected_features = feature_vector[feature_indices]
            feature_sums += selected_features
            count += 1

    if count == 0:
        return np.zeros(len(feature_indices))
    else:
        return feature_sums / count

def main(album_ids):
    connection = pymysql.connect(
        host='mitidb.cvm64ss6y2xv.ap-northeast-2.rds.amazonaws.com',
        user='minseo',
        password='Alstj!!809',
        database='mitiDB'
    )

    features_group1_indices = [1, 2, 4, 5, 6]
    features_group2_indices = [0, 3]

    try:
        album_dict = fetch_album_features(connection, album_ids)

        user_records = album_ids  # 예시로 사용자 레코드를 앨범 ID 목록으로 설정
        average_features_group1 = calculate_average_features(user_records, album_dict, features_group1_indices)
        average_features_group2 = calculate_average_features(user_records, album_dict, features_group2_indices)

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

        sorted_group1 = sorted(similarities_group1, key=lambda x: x[1], reverse=True)
        sorted_group2 = sorted(similarities_group2, key=lambda x: x[1], reverse=True)

        top_albums = []
        seen_album_ids = set()

        for album_id, similarity in sorted_group1 + sorted_group2:
            if album_id in seen_album_ids:
                continue
            top_albums.append((album_id, similarity))
            seen_album_ids.add(album_id)
            if len(top_albums) == 20:
                break

        for album_id, similarity in top_albums:
            print(album_id, similarity)

    except pymysql.MySQLError as e:
        print(f"Error: {e}")

    finally:
        connection.close()

if __name__ == "__main__":
    import sys
    album_ids = list(map(int, sys.stdin.read().split()))
    main(album_ids)