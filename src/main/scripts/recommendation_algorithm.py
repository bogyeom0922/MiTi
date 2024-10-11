import pymysql
import numpy as np
from scipy.spatial.distance import cosine
import json
import sys

def fetch_album_features(connection, album_ids=None):
    with connection.cursor() as cursor:
        if album_ids:
            format_strings = ','.join(['%s'] * len(album_ids))
            query = f"""
            SELECT id, music_acousticness, music_danceability, music_energy, 
                   music_liveness, music_loudness, music_tempo, music_valence,
                   music_name, music_uri, album_image, music_artist_name, music_duration_ms
            FROM album WHERE id IN ({format_strings})
            """
            cursor.execute(query, tuple(album_ids))
        else:
            query = """
            SELECT id, music_acousticness, music_danceability, music_energy, 
                   music_liveness, music_loudness, music_tempo, music_valence,
                   music_name, music_uri, album_image, music_artist_name, music_duration_ms
            FROM album
            """
            cursor.execute(query)

        albums = cursor.fetchall()

    album_dict = {
        album[0]: {
            'features': np.array(album[1:8], dtype=float),
            'music_name': album[8],
            'music_uri': album[9],
            'album_image': album[10],
            'music_artist_name': album[11],
            'music_duration_ms': album[12]
        }
        for album in albums
    }

    return album_dict

def calculate_similarity(target_features, album_dict):
    similarities = []

    for album_id, album_data in album_dict.items():
        features = album_data['features']
        try:
            if np.all(target_features == 0) or np.all(features == 0):
                similarity = 0
            else:
                similarity = 1 - cosine(target_features, features)

            similarities.append((album_id, similarity))

        except Exception as e:
            print(f"Error processing album_id {album_id}: {e}")

    return similarities

def main(target_album_id):
    connection = pymysql.connect(
        host='mitidb.cvm64ss6y2xv.ap-northeast-2.rds.amazonaws.com',
        user='minseo',
        password='Alstj!!809',
        database='mitiDB'
    )

    try:
        album_dict = fetch_album_features(connection, [target_album_id])

        if target_album_id not in album_dict:
            print(f"Album ID {target_album_id} not found in database.")
            return

        target_features = album_dict[target_album_id]['features']

        all_other_albums = fetch_album_features(connection)

        similarities = calculate_similarity(target_features, all_other_albums)

        sorted_similarities = sorted(similarities, key=lambda x: x[1], reverse=True)

        top_albums = sorted_similarities[:20]

        result = []
        for album_id, similarity in top_albums:
            album_data = all_other_albums[album_id]
            result.append({
                'id': album_id,
                'musicName': album_data['music_name'],
                'music_uri': album_data['music_uri'],
                'albumImage': album_data['album_image'],
                'musicArtistName': album_data['music_artist_name'],
                'music_duration_ms': album_data['music_duration_ms']
            })

        print(json.dumps(result, indent=4, ensure_ascii=False))
        sys.stdout.flush()

    except pymysql.MySQLError as e:
        print(f"Error: {e}")
        sys.stdout.flush()

    finally:
        connection.close()

if __name__ == "__main__":
    import sys
    target_album_id = int(sys.argv[1])
    main(target_album_id)
    sys.stdout.flush()
