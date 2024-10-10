const SpotifyPlayer = ({ initialMusicInfo= { musicName: "", musicUri: "", albumImage: "" }, providerId, playlist, onNextTrack, onPrevTrack }) => {
    const { useState, useEffect } = React;
    const [musicInfo, setMusicInfo] = useState(initialMusicInfo); // 초기 음악 정보를 설정합니다.
    const { musicName, musicUri, albumImage } = musicInfo;
    const [paused, setPaused] = useState(true);
    const [player, setPlayer] = useState(null);
    const [accessToken, setAccessToken] = useState(null);
    const [deviceId, setDeviceId] = useState(null);
    const [currentTrackIndex, setCurrentTrackIndex] = useState(0); // 현재 플레이리스트에서의 트랙 인덱스
    const [isFullscreen, setIsFullscreen] = useState(false); // 전체화면 상태
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
                    handleNextTrack(); // 다음 곡으로 넘어가기
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

    // musicInfo 업데이트 시 로그 출력
    useEffect(() => {
        console.log('SpotifyPlayer received new musicInfo:', musicInfo);
    }, [musicInfo]);


    useEffect(() => {
        if (player && musicInfo.musicUri && deviceId) {
            player._options.getOAuthToken((token) => {
                const sanitizedUri = sanitizeUri(musicInfo.musicUri);
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
    }, [musicInfo, deviceId]);



    // `currentTrackIndex`가 변경될 때 해당 트랙을 재생
    useEffect(() => {
        if (playlist.length > 0 && currentTrackIndex >= 0 && currentTrackIndex < playlist.length) {
            const track = playlist[currentTrackIndex];
            console.log('Track found:', track);
            setMusicInfo({
                musicName: track.musicName,
                musicUri: track.music_uri,
                albumImage: track.albumImage,
                musicArtistName: track.musicArtistName,
                id: track.id,
            });
        }
    }, [currentTrackIndex, playlist]);

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

    // 전체화면 토글 함수
    const toggleFullscreen = () => {
        setIsFullscreen(!isFullscreen);
    };

    // onNextTrack 호출 함수 정의
    const handleNextTrack = () => {
        setCurrentTrackIndex((prevIndex) => (prevIndex + 1) % playlist.length); // 다음 트랙으로 이동
    };

    const handlePrevTrack = () => {
        setCurrentTrackIndex((prevIndex) => (prevIndex - 1 + playlist.length) % playlist.length); // 이전 트랙으로 이동
    };


    // 클릭하면 재생
    const handlePlaylistClick = (id) => {
        console.log('Clicked track id: ', id);
        const trackIndex = playlist.findIndex(track => track.id === id);
        if (trackIndex !== -1) {
            setCurrentTrackIndex(trackIndex);
        }
    };

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
                <button onClick={handlePrevTrack} className="nav-button">이전 곡</button>
                <button onClick={togglePlayPause} className="nav-button">
                    {paused ? '재생' : '일시정지'}
                </button>
                <button onClick={handleNextTrack} className="nav-button">다음 곡</button>
                <button onClick={toggleFullscreen} className="nav-button">{isFullscreen ? '▼' : '▲'}</button>
            </div>

            {/* 전체화면 레이아웃 */}
            {isFullscreen && (
                <div className={`fullscreen-overlay ${isFullscreen ? 'fullscreen-active' : ''}`}>
                    <div className="fullscreen-content">
                        <div className="player-container-3">
                            <div className="album-container"> {/* 앨범 이미지와 이름을 감싸는 컨테이너 추가 */}
                                <div className="album-image-3">
                                    <img src={albumImage} alt="Album cover"/>
                                </div>
                                <p className="music-name-3">{musicName}</p> {/* 앨범 이름을 같은 컨테이너로 이동 */}
                            </div>
                            <div className="track-container">
                            <h3>다음 트랙</h3>
                            <div className="playlist-3">
                                <ul>
                                    {playlist.map((track, index) => (
                                        <li key={track.id} onClick={() => handlePlaylistClick(track.id)}>
                                            <img src={track.albumImage} alt={track.musicName}
                                                 style={{width: '50px', height: '50px'}}/>
                                            <div className="content-container">
                                                {track.musicName}|
                                                {track.musicArtistName}
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                            </div>
                        </div>


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
                                <button onClick={handlePrevTrack} className="nav-button">이전 곡</button>
                                <button onClick={togglePlayPause} className="nav-button">
                                    {paused ? '재생' : '일시정지'}
                                </button>
                                <button onClick={handleNextTrack} className="nav-button">다음 곡</button>
                                <button onClick={toggleFullscreen}
                                        className="nav-button">{isFullscreen ? '▼' : '▲'}</button>
                                {/* ▲ 클릭시 전체화면 */}

                            </div>
                        </div>
                    </div>
                </div>

            )}
        </div>
    );


};


// App 컴포넌트 정의
const App = () => {
    const {useState, useEffect} = React;
    const [currentTrackIndex, setCurrentTrackIndex] = useState(0); // 현재 플레이리스트에서의 트랙 인덱스
    const [musicInfo, setMusicInfo] = useState({musicName: "", musicUri: "", albumImage: "", id: ""});
    const [playlist, setPlaylist] = useState([]); // 플레이리스트 상태

    // root 엘리먼트에서 userId와 mood 값을 추출
    const providerId = document.getElementById('root').getAttribute('data-user-id');
    const genre = document.getElementById('root').getAttribute('data-genre');

    console.log(providerId, genre);

    // 음악 정보 가져오기 (외부 클릭 시 사용)
    const fetchMusicInfo = async (id) => {
        try {
            const response = await fetch(`/api/music/${id}`);
            const data = await response.json();
            if (data) {
                setMusicInfo({
                    musicName: data.musicName,
                    musicUri: data.music_uri,
                    albumImage: data.albumImage,
                    musicArtistName: data.musicArtistName,
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
            const response = await fetch(`/api/playlist/${providerId}/${genre}`);
            if (!response.ok) {
                throw new Error('플레이리스트를 찾을 수 없습니다.');
            }

            const data = await response.json();  // JSON으로 한 번에 파싱
            console.log('Fetched playlist:', data);  // 여기서 데이터가 잘 가져와지는지 확인

            if (data.length > 0) {
                setPlaylist(data);  // 가져온 데이터를 플레이리스트로 설정
            } else {
                console.error('플레이리스트가 비어 있습니다.');
            }
        } catch (error) {
            console.error('플레이리스트 가져오기 실패:', error);
        }
    }


    useEffect(() => {
        fetchPlaylist(); // 컴포넌트가 처음 로드될 때 플레이리스트 가져옴
    }, []);

    useEffect(() => {
        if (playlist.length > 0 && currentTrackIndex >= 0 && currentTrackIndex < playlist.length) {
            const track = playlist[currentTrackIndex];
            console.log('Track found:', track);
            setMusicInfo({
                musicName: track.musicName,
                musicUri: track.music_uri,
                albumImage: track.albumImage,
                musicArtistName : track.musicArtistName,
                id: track.id,
            });
        }
    }, [currentTrackIndex, playlist]);


    // 플레이리스트에서 음악 클릭 시 처리
    const handlePlaylistClick = (id) => {
        console.log('Clicked track id: ', id);
        const trackIndex = playlist.findIndex(track => track.id === id);
        if (trackIndex !== -1) {
            setCurrentTrackIndex(trackIndex);
        }
    };


    // 전역 범위로 노출 (파일 나누면서 필요)
    window.handlePlaylistClick = handlePlaylistClick;


    // 플레이리스트 외부에서 음악 클릭 시 처리
    const handleMusicClick = async (id) => {
        await fetchMusicInfo(id); // 다음 곡의 정보 가져오기
        const trackIndex = playlist.findIndex(track => track.id === id);
        if (trackIndex !== -1) {
            setCurrentTrackIndex(trackIndex); // 음악 정보 업데이트 후 현재 인덱스 설정
        }
    };


    // 전역 범위로 노출 (파일 나누면서 필요)
    window.handleMusicClick = handleMusicClick;


    return (
            <SpotifyPlayer
                key={musicInfo.id || musicInfo.musicUri}  // 음악 정보 변경 시 컴포넌트 리렌더링
                initialMusicInfo={musicInfo}
                providerId={providerId}
                playlist={playlist}  // playlist를 props로 전달
                onNextTrack={() => setCurrentTrackIndex((prev) => (prev + 1) % playlist.length)}  // 다음 곡으로 이동
                onPrevTrack={() => setCurrentTrackIndex((prev) => (prev - 1 + playlist.length) % playlist.length)}  // 이전 곡으로 이동
            />
    );


};


ReactDOM.render(<App />, document.getElementById('root'));