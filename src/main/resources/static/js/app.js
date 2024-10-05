const SpotifyPlayer = ({ musicInfo, providerId, onNextTrack, onPrevTrack }) => {
    const { musicName, musicUri, albumImage } = musicInfo;
    const { useState, useEffect } = React;
    const [paused, setPaused] = useState(true);
    const [player, setPlayer] = useState(null);
    const [accessToken, setAccessToken] = useState(null);
    const [deviceId, setDeviceId] = useState(null);
    const [trackPosition, setTrackPosition] = useState(0); // 현재 재생 위치
    const [trackDuration, setTrackDuration] = useState(0); // 트랙 길이

    // 음악 재생 상태를 저장하는 함수
    const saveMusicState = (musicInfo, isPaused) => {
        const musicState = {
            ...musicInfo,
            paused: isPaused // 재생 중인지 여부도 저장
        };
        localStorage.setItem('musicState', JSON.stringify(musicState));
    };

    // 페이지 로드 시 저장된 상태를 불러오는 함수
    const loadMusicState = () => {
        const savedState = localStorage.getItem('musicState');
        return savedState ? JSON.parse(savedState) : { musicName: "", musicUri: "", albumImage: "", id: "", paused: true };
    };

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
                setTrackPosition(state.position); // 현재 위치
                setTrackDuration(state.track_window.current_track.duration_ms); // 트랙 길이

                if (state.track_window.current_track && state.position === 0 && state.paused) {
                    console.log('Track ended, calling onNextTrack');
                    setTimeout(onNextTrack, 1000); // 1초 딜레이 후 다음 곡으로
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
        const savedMusicInfo = loadMusicState();
        setPaused(savedMusicInfo.paused); // 저장된 재생 상태 복원
        setTrackPosition(savedMusicInfo.trackPosition || 0); // 저장된 위치 복원
        if (!savedMusicInfo.paused && player) {
            player.resume(); // 저장된 상태가 재생 중이라면 자동으로 재생
        }
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

    // 슬라이더로 위치 이동
    const handleSeek = (e) => {
        const position = (e.target.value / 100) * trackDuration;
        if (player) {
            player.seek(position).then(() => {
                setTrackPosition(position);
            });
        }
    };

    // 주기적으로 재생 위치 가져오기
    useEffect(() => {
        const interval = setInterval(() => {
            if (player) {
                player.getCurrentState().then(state => {
                    if (state) {
                        setTrackPosition(state.position);
                        setTrackDuration(state.track_window.current_track.duration_ms);
                    }
                });
            }
        }, 1000); // 1초마다 재생 위치 업데이트

        return () => clearInterval(interval);
    }, [player]);

    // 일시정지 및 재생
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

    // 시간을 mm:ss 형식으로 변환
    const formatTime = (milliseconds) => {
        const minutes = Math.floor(milliseconds / 60000);
        const seconds = ((milliseconds % 60000) / 1000).toFixed(0);
        return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
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

    // 음악 상태 저장
    useEffect(() => {
        saveMusicState(musicInfo, paused); // 재생 상태와 함께 저장
    }, [musicInfo, paused]);

    return (
        <div className="player-container-1">
            {/* 프로그레스 바 컨테이너를 상단으로 이동 */}
            <div className="progress-bar-container">
                <span>{formatTime(trackPosition)}</span>
                <input
                    type="range"
                    min="0"
                    max="100"
                    value={(trackPosition / trackDuration) * 100 || 0}
                    onChange={handleSeek} // 사용자가 슬라이더를 움직이면 위치 이동
                />
                <span>{formatTime(trackDuration)}</span>
            </div>

            {/* 앨범 이미지 및 음악 정보 */}
            <img src={albumImage} alt="Album cover" className="album-cover"/>
            <p>Now playing: {musicName}</p>

            {/* 네비게이션 버튼 */}
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
// App 컴포넌트 정의
const App = () => {
    const [musicInfo, setMusicInfo] = React.useState({
        musicName: "",
        musicUri: "",
        albumImage: "",
        id: "",
    });
    const [recommendedAlbums, setRecommendedAlbums] = React.useState([]); // 추천 앨범 목록 상태

    const providerId = document.getElementById('root').getAttribute('data-user-id');

    // 음악 정보 가져오기
    const fetchMusicInfo = async (id) => {
        try {
            const response = await fetch(`/api/music/${id}`);
            const data = await response.json();
            setMusicInfo({
                musicName: data.musicName,
                musicUri: data.music_uri,
                albumImage: data.albumImage,
                id: data.id,
            });
        } catch (error) {
            console.error("Error fetching music info: ", error);
        }
    };

    // 추천 앨범 목록 가져오기
    const fetchRecommendedAlbums = async (id) => {
        if (!id) {
            console.warn('Invalid albumId for fetching recommended albums');
            return;
        }

        try {
            const response = await fetch(`/api/recommend/${id}`);
            const data = await response.json();
            setRecommendedAlbums(data.recommendedAlbums); // 추천 앨범 목록 상태 업데이트
        } catch (error) {
            console.error('Error fetching recommended albums:', error);
        }
    };

    // 음악 클릭 시 처리: 음악 정보와 추천 앨범 목록을 동시에 가져오기
    const handleMusicClick = async (id) => {
        await fetchMusicInfo(id); // 선택한 음악의 정보 가져오기
        await fetchRecommendedAlbums(id); // 선택한 음악에 대한 추천 앨범 목록 가져오기
    };

    // 다음 곡 재생
    const onNextTrack = async () => {
        if (recommendedAlbums.length > 0) {
            const nextAlbumId = recommendedAlbums[0]; // 추천 앨범 목록에서 첫 번째 앨범
            await fetchMusicInfo(nextAlbumId); // 다음 곡 재생
            await fetchRecommendedAlbums(nextAlbumId); // 그 곡에 대한 새로운 추천 앨범 목록 가져오기
        } else {
            console.warn('No recommended albums found');
        }
    };

    // 재생 목록 UI 생성
    const renderPlaylist = () => {
        return recommendedAlbums.length > 0 ? (
            <ul>
                {recommendedAlbums.map((albumId, index) => (
                    <li key={index} onClick={() => handleMusicClick(albumId)}>
                        앨범 {albumId} 재생
                    </li>
                ))}
            </ul>
        ) : (
            <p>추천 앨범이 없습니다.</p>
        );
    };

    React.useEffect(() => {
        window.handleMusicClick = handleMusicClick; // 전역 함수로 노출
    }, []);

    return (

            <SpotifyPlayer
                musicInfo={musicInfo}
                providerId={providerId}
                onNextTrack={onNextTrack} // 자동으로 다음 트랙 재생
                onPrevTrack={() => {}} // 이전 트랙 핸들러는 생략
            />
    );
};

ReactDOM.render(<App />, document.getElementById('root'));