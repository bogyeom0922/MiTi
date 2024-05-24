import spotipy
import csv
from spotipy.oauth2 import SpotifyClientCredentials

client_id = '552a87573e13448e934f2c843c11c6a5'
client_secret = '3823736d49a94614912d2da790d0a808' 

client_credentials_manager = SpotifyClientCredentials(client_id=client_id, client_secret=client_secret)

sp = spotipy.Spotify(client_credentials_manager=client_credentials_manager)

csv_file_path = "spotify_tracks.csv"