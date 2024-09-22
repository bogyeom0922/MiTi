// SpotifyPlayer 컴포넌트 정의
const SpotifyPlayer = ({ musicInfo, userId, onNextTrack, onPrevTrack }) => {
    const { musicName, musicUri, albumImage } = musicInfo;
    const { useState, useEffect } = React;
    const [paused, setPaused] = useState(true);
    const [player, setPlayer] = useState(null);
    const [accessToken, setAccessToken] = useState(null);

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

    useEffect(() => {
        // 음악 재생
        const loadSpotifySDK = () => {
            if (!window.Spotify) {
                const script = document.createElement('script');
                script.src = 'https://sdk.scdn.co/spotify-player.js';
                script.async = true;
                script.onload = () => {
                    window.onSpotifyWebPlaybackSDKReady = () => {
                        initializePlayer();
                    };
                };
                script.onerror = () => {
                    console.error('Spotify SDK failed to load.');
                };
                document.body.appendChild(script);
            } else {
                initializePlayer();
            }
        };

        // 엑세스 토큰 확인을 통해 음악 스트리밍 구현
        const initializePlayer = () => {
            if (window.Spotify && accessToken) {
                const playerInstance = new window.Spotify.Player({
                    name: 'Web Playback SDK',
                    getOAuthToken: cb => { cb(accessToken); },
                    volume: 0.5,
                });

                setPlayer(playerInstance);

                playerInstance.addListener('ready', ({ device_id }) => {
                    console.log('Ready with Device ID', device_id);
                    if (musicUri) {
                        playTrack(device_id, musicUri);
                    }
                });

                playerInstance.addListener('player_state_changed', (state) => {
                    if (!state) return;
                    setPaused(state.paused);
                });

                playerInstance.connect();
            } else {
                console.error('Spotify SDK is not loaded or access token is missing.');
            }
        };

        fetchAccessToken().then(() => {
            loadSpotifySDK();
        });
    }, [musicUri, accessToken]);

    // 음악 uri 공백문자 제거
    const sanitizeUri = (uri) => uri.trim().replace(/\r?\n|\r/g, '');

    // 음악 재생
    const playTrack = (device_id, trackUri) => {
        const sanitizedTrackUri = sanitizeUri(trackUri);

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
                if (musicInfo.albumId) { // albumId가 있는 경우에만 기록
                    recordMusicPlay(userId, musicInfo.albumId);
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
    const recordMusicPlay = async (userId, albumId) => {
        try {
            const response = await fetch('/api/record', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ userId, albumId }),
            });
            const responseText = await response.text();
            console.log('Server response:', responseText);

            if (!response.ok) {
                throw new Error(`Error recording music: ${responseData.message || response.statusText}`);
            }
            console.log('Music recorded successfully');
        } catch (error) {
            console.error('Error recording music:', error);
        }
    };

    return (
        <div className="player-container">
            <h3>Spotify Player</h3>
            <img src={albumImage} alt="Album cover" className="album-cover"/>
            <p>Now playing: {musicName}</p>
            <div className="navigation-buttons">
                <button onClick={onPrevTrack} className="nav-button">이전 곡</button>
                <button onClick={togglePlayPause} className="nav-button">
                    {paused ? '재생' : '일시정지'}
                </button>
                <button onClick={onNextTrack} className="nav-button">다음 곡</button>
            </div>
        </div>
    );
};

// App 컴포넌트 정의
const App = () => {
    const { useState, useEffect } = React;
    const [musicId, setMusicId] = useState(1);
    const [musicInfo, setMusicInfo] = useState({ musicName: "", musicUri: "", albumImage: "", id: "" });

    // root 엘리먼트에서 userId 값을 추출
    const userId = document.getElementById('root').getAttribute('data-user-id');

    // 음악 정보 가져오기
    const fetchMusicInfo = async (id) => {
        try {
            const response = await fetch(`/api/music/${id}`);
            const data = await response.json();
            if (data) {
                setMusicInfo({
                    musicName: data.musicName,
                    musicUri: data.music_uri,
                    albumImage: data.albumImage,
                    id: data.id,
                });
            } else {
                throw new Error('No music info found');
            }
        } catch (error) {
            console.error('Error fetching music info:', error);
        }
    };

    useEffect(() => {
        if (userId && musicId) {
            fetchMusicInfo(musicId);
        }
    }, [musicId, userId]);

    const handleNextTrack = () => {
        setMusicId(prevId => prevId + 1);
    };

    const handlePrevTrack = () => {
        setMusicId(prevId => (prevId > 1 ? prevId - 1 : 1));
    };

    const handleMusicClick = (id) => {
        setMusicId(id);
    };

    // 전역 범위로 노출 (파일 나누면서 필요)
    window.handleMusicClick = handleMusicClick;


    return (
        <div>
            <SpotifyPlayer
                musicInfo={musicInfo}
                userId={userId}
                onNextTrack={handleNextTrack}
                onPrevTrack={handlePrevTrack}
            />
        </div>
    );
};

ReactDOM.render(<App />, document.getElementById('root'));
