import spotipy
import csv
import time
from spotipy.oauth2 import SpotifyClientCredentials
from requests.exceptions import ReadTimeout

# Spotify 계정 정보
client_id = 'acd4545842e24ce09389488d38a4456e'
client_secret = '5511b82814894eda8fa1617f764444e3'

# Spotify API 인증을 위해 클라이언트 인증 매니저를 생성
client_credentials_manager = SpotifyClientCredentials(client_id=client_id, client_secret=client_secret)

# Spotify 라이브러리를 이용하여 클라이언트 초기화
sp = spotipy.Spotify(client_credentials_manager=client_credentials_manager)

# API 호출 제한 대비 대기 시간 설정
REQUEST_DELAY = 0.5  # 0.5초 대기 후 다음 요청

# 파일이 저장될 경로 지정
csv_file_path = "음악.csv"

# 기록된 음악 이름들을 추적하기 위한 집합 생성
recorded_music_names = set()

# 카테고리별로 플레이리스트에서 트랙을 가져오는 함수
def fetch_tracks_from_playlists():
    try:
        # CSV 파일 열기
        with open(csv_file_path, 'w', newline='', encoding='utf-8') as file:
            writer = csv.writer(file)

            # CSV 파일 헤더 (필드 이름)
            writer.writerow([
                "id", "music_name", "music_id", "music_popularity", "album_image", "album_detail",
                "music_artist_name", "music_artist_id", "music_artist_popular", "music_genre", 
                "music_artist_genres", "music_artist_followers", "music_analysis_url", 
                "music_key", "music_duration_ms", "music_instrumentalness", "music_acousticness", 
                "music_danceability", "music_energy", "music_liveness", "music_loudness", 
                "music_mode", "music_speechiness", "music_tempo", "music_time_signature", 
                "music_valence", "music_track_href", "music_type", "music_uri"
            ])
            
            # 트랙 ID 초기화
            track_id = 1

            # Spotify에서 제공하는 카테고리 가져오기
            categories = sp.categories(limit=50)  # 최대 50개의 카테고리 가져오기
            for category in categories['categories']['items']:
                category_id = category['id']
                print(f"Fetching playlists from category: {category['name']}")

                # 카테고리별 플레이리스트 가져오기 (최대 20개)
                playlists = sp.category_playlists(category_id=category_id, limit=20)  # 각 카테고리당 20개의 플레이리스트
                for playlist in playlists['playlists']['items']:
                    playlist_id = playlist['id']
                    playlist_name = playlist['name']
                    print(f"Fetching tracks from playlist: {playlist_name}")

                    # 플레이리스트에서 트랙 가져오기
                    offset = 0
                    limit = 50  # 트랙을 50개씩 가져오기
                    total_tracks = sp.playlist_tracks(playlist_id, limit=1)['total']  # 전체 트랙 수 확인

                    while offset < total_tracks:
                        try:
                            tracks = sp.playlist_tracks(playlist_id, limit=limit, offset=offset)['items']
                        except ReadTimeout:
                            print("Timeout occurred, retrying after delay...")
                            time.sleep(REQUEST_DELAY)
                            tracks = sp.playlist_tracks(playlist_id, limit=limit, offset=offset)['items']

                        # 각 트랙의 정보 가져오기
                        for track_item in tracks:
                            track_info = track_item['track']
                            
                            # 트랙이 한국에서 스트리밍 가능한지 확인
                            if track_info.get('is_playable', True) and 'KR' in track_info.get('available_markets', []):
                                music_name = track_info['name']

                                # 중복된 트랙은 건너뛰기
                                if music_name in recorded_music_names:
                                    continue

                                # 트랙의 앨범 및 아티스트 정보 가져오기
                                album_info = track_info['album']
                                album_id = album_info['id']
                                album_name = album_info['name']
                                album_image_url = album_info['images'][0]['url']

                                # 아티스트 정보 가져오기
                                artist_names = []
                                artist_ids = []
                                artist_popularities = []
                                artist_genres = []
                                artist_followers = []

                                for artist in track_info['artists']:
                                    artist_id = artist['id']
                                    artist_info = sp.artist(artist_id)
                                    artist_names.append(artist_info['name'])
                                    artist_ids.append(artist_info['id'])
                                    artist_popularities.append(str(artist_info['popularity']))
                                    artist_genres.append(', '.join(artist_info['genres']))
                                    artist_followers.append(str(artist_info['followers']['total']))

                                # 트랙의 오디오 특징 가져오기
                                audio_features = sp.audio_features(track_info['id'])[0] if sp.audio_features(track_info['id']) else {}
                                normalized_loudness = (audio_features.get('loudness', -60) + 60) / 60
                                normalized_tempo = audio_features.get('tempo', 0) / 200

                                # 트랙 정보 CSV에 작성
                                writer.writerow([
                                    track_id, music_name, track_info['id'], track_info['popularity'], 
                                    album_image_url, album_name, ', '.join(artist_names), 
                                    ', '.join(artist_ids), ', '.join(artist_popularities), 
                                    ', '.join(artist_names), ', '.join(artist_genres), 
                                    ', '.join(artist_followers), audio_features.get('analysis_url', ''), 
                                    audio_features.get('key', ''), audio_features.get('duration_ms', ''), 
                                    audio_features.get('instrumentalness', ''), audio_features.get('acousticness', ''), 
                                    audio_features.get('danceability', ''), audio_features.get('energy', ''), 
                                    audio_features.get('liveness', ''), normalized_loudness, audio_features.get('mode', ''), 
                                    audio_features.get('speechiness', ''), normalized_tempo, 
                                    audio_features.get('time_signature', ''), audio_features.get('valence', ''), 
                                    audio_features.get('track_href', ''), audio_features.get('type', ''), 
                                    track_info['uri']
                                ])

                                # 중복 방지를 위해 트랙 이름 기록
                                recorded_music_names.add(music_name)

                                # 다음 트랙으로 이동하기 위해 트랙 ID 증가
                                track_id += 1

                        offset += limit  # 다음 50개의 트랙 가져오기
                        time.sleep(REQUEST_DELAY)  # API 호출 간 딜레이

        # 파일 작성 완료
        print("파일 작성이 완료되었습니다.")
    
    except Exception as e:
        print(f"오류 발생: {e}")

# 데이터 수집 실행
fetch_tracks_from_playlists()
