/*

const SpotifyPlayer = ({ musicInfo, providerId, onNextTrack, onPrevTrack }) => {
    const { musicName, musicUri, albumImage } = musicInfo;
    const { useState, useEffect } = React;
    const [paused, setPaused] = useState(true);
    const [player, setPlayer] = useState(null);
    const [accessToken, setAccessToken] = useState(null);
    const [deviceId, setDeviceId] = useState(null);

    // 엑세스 토큰 받아오기
    const fetchAccessToken = async () => {
        try {
            const response = await fetch('/api/get-token');
            const data = await response.json();
            if (data.access_token) {
                setAccessToken(data.access_token);
            } else {
                throw new Error('No access token found');
            }
        } catch (error) {
            console.error('Error fetching access token:', error);
        }
    };

    // Spotify SDK 초기화 함수
    const initializePlayer = () => {
        if (window.Spotify && accessToken) {
            const playerInstance = new window.Spotify.Player({
                name: 'Web Playback SDK',
                getOAuthToken: cb => { cb(accessToken); },
                volume: 0.5,
            });

            playerInstance.addListener('ready', ({ device_id }) => {
                console.log('Ready with Device ID', device_id);
                setDeviceId(device_id);
                if (musicUri) {
                    playTrack(device_id, musicUri);
                }
            });

            playerInstance.addListener('player_state_changed', (state) => {
                if (!state) return;
                setPaused(state.paused);

                // 트랙이 끝났을 때 자동으로 다음 곡 재생
                if (state.track_window.current_track && state.position === 0 && state.paused) {
                    console.log('Track ended, calling onNextTrack');
                    setTimeout(onNextTrack, 1000); // 1초 딜레이 후 다음 곡으로 넘어가도록 설정
                }
            });

            playerInstance.connect();
            setPlayer(playerInstance);
        } else {
            console.error('Spotify SDK is not loaded or access token is missing.');
        }
    };

    useEffect(() => {
        const loadSpotifySDK = () => {
            if (!window.Spotify) {
                const script = document.createElement('script');
                script.src = 'https://sdk.scdn.co/spotify-player.js';
                script.async = true;
                script.onload = () => {
                    window.onSpotifyWebPlaybackSDKReady = initializePlayer;
                };
                script.onerror = () => {
                    console.error('Spotify SDK failed to load.');
                };
                document.body.appendChild(script);
            } else {
                initializePlayer();
            }
        };

        // 엑세스 토큰을 가져온 후 SDK 로드
        if (accessToken && !player) {
            loadSpotifySDK();
        }

        // 컴포넌트 언마운트 시 플레이어 정리
        return () => {
            if (player) {
                player.disconnect();
                setPlayer(null);
            }
        };
    }, [accessToken, player]);

    useEffect(() => {
        fetchAccessToken();
    }, []);

    useEffect(() => {
        if (player && musicUri && deviceId) {
            player._options.getOAuthToken((token) => {
                const sanitizedUri = sanitizeUri(musicUri);
                console.log(`Attempting to play track with URI: ${sanitizedUri}`);
                fetch(`https://api.spotify.com/v1/me/player/play?device_id=${deviceId}`, {
                    method: 'PUT',
                    body: JSON.stringify({ uris: [sanitizedUri] }),
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${token}`,
                    },
                }).then(response => {
                    if (!response.ok) {
                        return response.json().then(errorData => {
                            throw new Error(`Spotify API error: ${errorData.error.message}`);
                        });
                    }
                    console.log('Track played successfully');
                    if (musicInfo.id) {
                        recordMusicPlay(providerId, musicInfo.id);
                    } else {
                        console.warn('albumId is null, not recording music play.');
                    }
                }).catch(error => {
                    console.error('Error playing track:', error.message);
                });
            });
        }
    }, [musicUri, deviceId]);

    // 음악 uri 공백문자 제거
    const sanitizeUri = (uri) => uri.trim().replace(/\r?\n|\r/g, '');

    // 음악 재생 위에랑 코드 중복인데 없애면 또 고장나서 일단 보류임
    const playTrack = (device_id, trackUri) => {
        const sanitizedTrackUri = sanitizeUri(trackUri);
        console.log(`Playing sanitized URI: ${sanitizedTrackUri}`);

        fetch(`https://api.spotify.com/v1/me/player/play?device_id=${device_id}`, {
            method: 'PUT',
            body: JSON.stringify({ uris: [sanitizedTrackUri] }),
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${accessToken}`,
            },
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errorData => {
                        throw new Error(`Spotify API error: ${errorData.error.message}`);
                    });
                }
                console.log('Track played successfully');
                if (musicInfo.id) {
                    recordMusicPlay(providerId, musicInfo.id);
                } else {
                    console.warn('albumId is null, not recording music play.');
                }
            })
            .catch(error => {
                console.error('Error playing track:', error.message);
            });
    };

    // 일시정지
    const togglePlayPause = () => {
        if (!player) return;
        if (paused) {
            player.resume().then(() => {
                setPaused(false);
            });
        } else {
            player.pause().then(() => setPaused(true));
        }
    };

    // 음악 기록
    const recordMusicPlay = async (providerId, albumId) => {
        try {
            const response = await fetch('/api/record', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ providerId, albumId }),
            });
            const responseText = await response.text();
            console.log('Server response:', responseText);

            if (!response.ok) {
                throw new Error(`Error recording music: ${responseText.message || response.statusText}`);
            }
            console.log('Music recorded successfully');
        } catch (error) {
            console.error('Error recording music:', error);
        }
    };

    return (
        <div className="player-container-1">
            <img src={albumImage} alt="Album cover" className="album-cover"/>
            <p>Now playing: {musicName}</p>
            <div className="navigation-buttons">
                <button onClick={onPrevTrack} className="nav-button">이전 곡</button>
                <button onClick={togglePlayPause} className="nav-button">
                    {paused ? '재생' : '일시정지'}
                </button>
                <button onClick={onNextTrack} className="nav-button">다음 곡</button>
                <button
                    onClick={() => window.location.href = `http://localhost:8080/index/${providerId}`}
                    className="nav-button"
                >
                    음악 목록
                </button>
            </div>
        </div>
    );
};

// App 컴포넌트 정의
// App 컴포넌트 정의
const App = () => {
    const { useState, useEffect } = React;
    const [currentTrackIndex, setCurrentTrackIndex] = useState(0); // 현재 플레이리스트에서의 트랙 인덱스
    const [musicInfo, setMusicInfo] = useState({ musicName: "", musicUri: "", albumImage: "", id: "" });
    const [playlist, setPlaylist] = useState([]); // 플레이리스트 상태

    // root 엘리먼트에서 userId와 mood 값을 추출
    const providerId = document.getElementById('root').getAttribute('data-user-id');
    const mood =  document.getElementById('root').getAttribute('data-mood');

    // 음악 정보 가져오기
    const fetchMusicInfo = async (id) => {
        try {
            const response = await fetch(`/api/music/${id}`);
            const data = await response.json();
            if (data) {
                setMusicInfo({
                    musicName: data.musicName,
                    musicUri: data.music_uri,
                    albumImage: data.album_image,
                    id: data.id,
                });
            } else {
                throw new Error('No music info found');
            }
        } catch (error) {
            console.error('Error fetching music info:', error);
        }
    };

    // 플레이리스트 데이터를 가져오는 함수
    async function fetchPlaylist() {
        try {
            const response = await fetch(`/api/playlist/${providerId}/${mood}`);
            const data = await response.json();
            if (response.ok && data.length > 0) {
                setPlaylist(data); // 가져온 데이터를 플레이리스트로 설정
            } else {
                console.error('플레이리스트를 찾을 수 없습니다.');
            }
        } catch (error) {
            console.error('플레이리스트 가져오기 실패:', error);
        }
    }

    // fetchPlaylist를 전역으로 노출
    window.fetchPlaylist = fetchPlaylist;

    // 현재 트랙의 정보를 플레이리스트에서 가져옴
    const updateCurrentTrack = (index) => {
        if (playlist.length > 0 && index >= 0 && index < playlist.length) {
            const track = playlist[index];
            setMusicInfo({
                musicName: track.musicName,
                musicUri: track.music_uri,
                albumImage: track.album_image,
                id: track.id,
            });
        }
    };

    useEffect(() => {
        fetchPlaylist(); // 컴포넌트가 처음 로드될 때 플레이리스트 가져옴
    }, []);

    useEffect(() => {
        if (playlist.length > 0 && currentTrackIndex >= 0 && currentTrackIndex < playlist.length) {
            updateCurrentTrack(currentTrackIndex); // 현재 트랙 업데이트
        }
    }, [currentTrackIndex, playlist]);

    // 다음 곡 재생
    const handleNextTrack = () => {
        setCurrentTrackIndex((prevIndex) => {
            const nextIndex = prevIndex + 1;
            return nextIndex < playlist.length ? nextIndex : 0; // 마지막 곡이면 첫 번째 곡으로 돌아감
        });
    };

    // 이전 곡 재생
    const handlePrevTrack = () => {
        setCurrentTrackIndex((prevIndex) => {
            const prevTrackIndex = prevIndex - 1;
            return prevTrackIndex >= 0 ? prevTrackIndex : playlist.length - 1; // 첫 번째 곡이면 마지막 곡으로 돌아감
        });
    };

    const handleMusicClick = (id) => {
        const trackIndex = playlist.findIndex(track => track.id === id);
        if (trackIndex !== -1) {
            setCurrentTrackIndex(trackIndex); // 선택한 곡으로 인덱스 변경
        }
    };

    // 전역 범위로 노출 (파일 나누면서 필요)
    window.handleMusicClick = handleMusicClick;

    return (
        <div>
            <SpotifyPlayer
                musicInfo={musicInfo}
                providerId={providerId}
                onNextTrack={handleNextTrack}
                onPrevTrack={handlePrevTrack}
            />
        </div>
    );
};

ReactDOM.render(<App />, document.getElementById('root'));

*/