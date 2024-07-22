import spotipy
import csv
#spotify api 인증을 위한 클래스
from spotipy.oauth2 import SpotifyClientCredentials

#spotify 계정 정보
client_id = '552a87573e13448e934f2c843c11c6a5'
client_secret = '3823736d49a94614912d2da790d0a808' 

#spotify api 인증을 위해 변수 값으로 클라이언트 인증 매니저를 생성
client_credentials_manager = SpotifyClientCredentials(client_id=client_id, client_secret=client_secret)

#spotify 라이브러리를 이용하여 클라이언트 초기화
sp = spotipy.Spotify(client_credentials_manager=client_credentials_manager)

#파일이 저장될 경로 지정
csv_file_path = "spotify_tracks.csv"

#인기차트 id를 이용해 트랙 정보 가져옴
chart_tracks = sp.playlist_tracks('37i9dQZEVXbNG2KDcFcKOF') 

#기록된 음악 이름들을 추적하기 위한 집합 생성
#set()자료형은 중복을 허용하지 않기 때문에 중복 검사를 위해 사용됨
recorded_music_names = set()

#csv파일 열기
with open(csv_file_path, 'w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    
    #csv 파일 헤더 (album테이블 필드이름)
    writer.writerow([
        "id", "music_name", "music_id", "music_popularity", "album_image", "album_detail",
        "music_artist_name", "music_artist_id", "music_artist_popular", "music_genre", 
        "music_artist_genres", "music_artist_followers", "music_analysis_url", 
        "music_key", "music_duration_ms", "music_instrumentalness", "music_acousticness", 
        "music_danceability", "music_energy", "music_liveness", "music_loudness", 
        "music_mode", "music_speechiness", "music_tempo", "music_time_signature", 
        "music_valence", "music_track_href", "music_type", "music_uri"
    ])
    
    #트랙 id 초기화
    track_id = 1

    #가져온 인기 차트의 트랙에서 트랙 정보와 해당 앨범의 모든 트랙 정보를 가져옴
    for track in chart_tracks['items']:
        track_info = track['track']
        album_id = track_info['album']['id']
        album_tracks = sp.album_tracks(album_id)['items']

        #각 앨범 트랙의 상세 정보 가져옴
        for album_track in album_tracks:
            track_details = sp.track(album_track['id'])

            #이미 기록된 음악인지 중복 확인
            music_name = album_track['name']
            if music_name in recorded_music_names:
                continue  #이미 기록된 음악이면 건너뛰기

            #새로운 음악인 경우 기록
            recorded_music_names.add(music_name)

            #각 트랙의 오디오 특징 가져옴
            audio_features = sp.audio_features(album_track['id'])[0]
            
            #아티스트 정보 저장할 리스트 초기화
            artist_names = []
            artist_ids = []
            artist_popularities = []
            artist_genres = []
            artist_followers = []

            #각 트랙마다 아티스트 정보를 가져온 후 리스트에 추가
            for artist in album_track['artists']:
                artist_id = artist['id']
                artist_info = sp.artist(artist_id)
                artist_names.append(artist_info['name'])
                artist_ids.append(artist_info['id'])
                artist_popularities.append(artist_info['popularity'])
                artist_genres.append(', '.join(artist_info['genres']))
                artist_followers.append(artist_info['followers']['total'])

            #각 트랙에서 가져온 정보를 모두 파일에 작성
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

            #다음 트랙으로 이동하기 위해 id값 +1
            track_id += 1
