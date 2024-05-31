import spotipy
import csv
from spotipy.oauth2 import SpotifyClientCredentials

client_id = '552a87573e13448e934f2c843c11c6a5'
client_secret = '3823736d49a94614912d2da790d0a808' 

client_credentials_manager = SpotifyClientCredentials(client_id=client_id, client_secret=client_secret)

sp = spotipy.Spotify(client_credentials_manager=client_credentials_manager)

csv_file_path = "spotify_tracks.csv"

chart_tracks = sp.playlist_tracks('37i9dQZEVXbNG2KDcFcKOF') # 트랙 인기차트 id

# 기존에 기록된 음악 이름들을 추적하기 위한 집합(set) 생성
recorded_music_names = set()

with open(csv_file_path, 'w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    # 헤더
    writer.writerow([
        "id", "music_name", "music_id", "music_popularity", "album_image", "album_detail",
        "music_artist_name", "music_artist_id", "music_artist_popular", "music_genre", 
        "music_artist_genres", "music_artist_followers", "music_analysis_url", 
        "music_key", "music_duration_ms", "music_instrumentalness", "music_acousticness", 
        "music_danceability", "music_energy", "music_liveness", "music_loudness", 
        "music_mode", "music_speechiness", "music_tempo", "music_time_signature", 
        "music_valence", "music_track_href", "music_type", "music_uri"
    ])
    
    # 트랙마다 음악 정보 작성
    track_id = 1

    for track in chart_tracks['items']:
        track_info = track['track']
        album_id = track_info['album']['id']
        album_tracks = sp.album_tracks(album_id)['items']

        for album_track in album_tracks:
            # 개별 트랙의 상세 정보
            track_details = sp.track(album_track['id'])

            # 이미 기록된 음악인지 확인
            music_name = album_track['name']
            if music_name in recorded_music_names:
                continue  # 이미 기록된 음악이면 건너뜁니다.

            # 새로운 음악이므로 기록합니다.
            recorded_music_names.add(music_name)

            # 오디오 특징
            audio_features = sp.audio_features(album_track['id'])[0]
            
            # 아티스트 정보
            artist_names = []
            artist_ids = []
            artist_popularities = []
            artist_genres = []
            artist_followers = []

            for artist in album_track['artists']:
                artist_id = artist['id']
                artist_info = sp.artist(artist_id)
                artist_names.append(artist_info['name'])
                artist_ids.append(artist_info['id'])
                artist_popularities.append(artist_info['popularity'])
                artist_genres.append(', '.join(artist_info['genres']))
                artist_followers.append(artist_info['followers']['total'])

            # 행 작성
            writer.writerow([
                track_id, music_name, album_track['id'], 
                track_details['popularity'],  # 개별 트랙의 인기도 가져오기
                track_info['album']['images'][0]['url'], track_info['album']['name'],
                ', '.join(artist_names), ', '.join(artist_ids), ', '.join(map(str, artist_popularities)),
                ', '.join(artist_names), ', '.join(artist_genres), ', '.join(map(str, artist_followers)),
                audio_features['analysis_url'], audio_features['key'], 
                audio_features['duration_ms'], audio_features['instrumentalness'],
                audio_features['acousticness'], audio_features['danceability'], 
                audio_features['energy'], audio_features['liveness'], 
                audio_features['loudness'], audio_features['mode'], 
                audio_features['speechiness'], audio_features['tempo'], 
                audio_features['time_signature'], audio_features['valence'], 
                audio_features['track_href'], audio_features['type'], album_track['uri']
            ])
            track_id += 1  # ID 값 증가
