const SpotifyPlayer = ({
                           initialMusicInfo = { musicName: "", musicUri: "", albumImage: "" },
                           providerId,
                           playlist,
                           onNextTrack,
                           onPrevTrack
                       }) => {
    const { useState, useEffect } = React;
    const [musicInfo, setMusicInfo] = useState(initialMusicInfo);
    const { musicName, musicUri, albumImage } = musicInfo;
    const [paused, setPaused] = useState(true);
    const [player, setPlayer] = useState(null);
    const [accessToken, setAccessToken] = useState(null);
    const [deviceId, setDeviceId] = useState(null);
    const [currentTrackIndex, setCurrentTrackIndex] = useState(0);
    const [isFullscreen, setIsFullscreen] = useState(false);
    const [trackPosition, setTrackPosition] = useState(0);
    const [trackDuration, setTrackDuration] = useState(0);
    const [activeAccordion, setActiveAccordion] = useState(null);
    const [likedTracks, setLikedTracks] = useState({}); // 좋아요 상태를 저장하는 객체

    const toggleAccordion = (index) => {
        setActiveAccordion(activeAccordion === index ? null : index);
    };

    // 음악 재생 상태를 저장하는 함수
    const saveMusicState = (musicInfo, isPaused) => {
        const musicState = { ...musicInfo, paused: isPaused };
        localStorage.setItem('musicState', JSON.stringify(musicState));
    };

    const loadLikedTracks = () => {
        playlist.forEach(track => {
            const albumId = track.id;
            fetch(`/mypage/like/album/isLiked?albumId=${albumId}&providerId=${providerId}`, {
                method: 'GET',
                cache: 'no-cache'
            })
                .then(response => response.json())
                .then(result => {
                    setLikedTracks(prevState => ({
                        ...prevState,
                        [albumId]: result.isLiked
                    }));
                })
                .catch(error => {
                    console.error('좋아요 상태 로드 오류:', error);
                });
        });
    };

    useEffect(() => {
        loadLikedTracks();
    }, [playlist]);

    const toggleTrackLike = (albumId) => {
        fetch(`/mypage/like/album/toggleTrack?albumId=${albumId}&providerId=${providerId}`, {
            method: 'POST',
            cache: 'no-cache'
        })
            .then(response => response.text())
            .then(result => {
                const isLiked = result === "liked";
                setLikedTracks(prevState => ({
                    ...prevState,
                    [albumId]: isLiked
                }));
            })
            .catch(error => {
                console.error('좋아요 토글 오류:', error);
                alert("좋아요 처리 중 오류가 발생했습니다.");
            });
    };

    const loadMusicState = () => {
        const savedState = localStorage.getItem('musicState');
        return savedState ? JSON.parse(savedState) : {
            musicName: "",
            musicUri: "",
            albumImage: "",
            id: "",
            paused: true
        };
    };

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

    const initializePlayer = () => {
        if (window.Spotify && accessToken) {
            const playerInstance = new window.Spotify.Player({
                name: 'Web Playback SDK',
                getOAuthToken: cb => {
                    cb(accessToken);
                },
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
                setTrackPosition(state.position);
                setTrackDuration(state.track_window.current_track.duration_ms);

                if (state.track_window.current_track && state.position === 0 && state.paused) {
                    console.log('Track ended, calling onNextTrack');
                    handleNextTrack();
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

        if (accessToken && !player) {
            loadSpotifySDK();
        }

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
        setPaused(savedMusicInfo.paused);
        setTrackPosition(savedMusicInfo.trackPosition || 0);
        if (!savedMusicInfo.paused && player) {
            player.resume();
        }
    }, []);

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

    useEffect(() => {
        if (playlist.length > 0 && currentTrackIndex >= 0 && currentTrackIndex < playlist.length) {
            const track = playlist[currentTrackIndex];
            setMusicInfo({
                musicName: track.musicName,
                musicUri: track.music_uri,
                albumImage: track.albumImage,
                musicArtistName: track.musicArtistName,
                id: track.id,
            });
        }
    }, [currentTrackIndex, playlist]);

    const sanitizeUri = (uri) => uri.trim().replace(/\r?\n|\r/g, '');

    const handleSeek = (e) => {
        const position = (e.target.value / 100) * trackDuration;
        if (player) {
            player.seek(position).then(() => {
                setTrackPosition(position);
            });
        }
    };

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
        }, 1000);

        return () => clearInterval(interval);
    }, [player]);

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

    const formatTime = (milliseconds) => {
        const minutes = Math.floor(milliseconds / 60000);
        const seconds = ((milliseconds % 60000) / 1000).toFixed(0);
        return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    };

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

    useEffect(() => {
        saveMusicState(musicInfo, paused);
    }, [musicInfo, paused]);

    const toggleFullscreen = () => {
        setIsFullscreen(!isFullscreen);
    };

    const handleNextTrack = () => {
        setCurrentTrackIndex((prevIndex) => (prevIndex + 1) % playlist.length);
    };

    const handlePrevTrack = () => {
        setCurrentTrackIndex((prevIndex) => (prevIndex - 1 + playlist.length) % playlist.length);
    };

    const handlePlaylistClick = (id) => {
        const trackIndex = playlist.findIndex(track => track.id === id);
        if (trackIndex !== -1) {
            setCurrentTrackIndex(trackIndex);
        }
    };

    return (
        <div className="player-container-1">
            <div className="progress-bar-container">
                <span>{formatTime(trackPosition)}</span>
                <input
                    type="range"
                    min="0"
                    max="100"
                    value={(trackPosition / trackDuration) * 100 || 0}
                    onChange={handleSeek}
                />
                <span>{formatTime(trackDuration)}</span>
            </div>

            <div className="track-info">
                <img src={albumImage} alt="Album cover" className="album-cover" />
                <p>Now playing: {musicName}</p>
            </div>

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
                            <div className="album-container" style={{ flex: 0.6 }}>
                                <div className="album-image-3">
                                    <img src={albumImage} alt="Album cover" />
                                </div>
                            </div>
                            <div className="track-container" style={{ flex: 0.4 }}>
                                <p className="next_track">다음 트랙</p>
                                <div className="playlist-3">
                                    <ul>
                                        {playlist.map((track, index) => (
                                            <li key={track.id} style={{ display: 'flex', alignItems: 'center', flexDirection: 'column', marginBottom: '10px' }}>
                                                <div
                                                    onClick={() => handlePlaylistClick(track.id)}
                                                    style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}
                                                >
                                                    <img
                                                        src={track.albumImage}
                                                        alt={track.musicName}
                                                        style={{ width: '60px', height: '60px', marginRight: '20px', marginLeft: '10px' }}
                                                    />
                                                    <div className="content-container">
                                                        <p className="content-container-music">{track.musicName}</p>
                                                        <p className="content-container-artist">{track.musicArtistName}</p>
                                                    </div>
                                                </div>

                                                {/* 아코디언 메뉴 */}
                                                <div className="accordion_container">
                                                    <button
                                                        className="accordion2"
                                                        onClick={() => toggleAccordion(index)}
                                                    >
                                                        :
                                                    </button>
                                                    <div
                                                        className="panel"
                                                        style={{ display: activeAccordion === index ? 'block' : 'none' }}
                                                    >
                                                        <button className="like-button" onClick={() => toggleTrackLike(track.id)}>
                                                            <span>{likedTracks[track.id] ? '♥' : '♡'}</span>
                                                        </button>
                                                        <button>재생 목록에 추가</button>
                                                        <button onClick={() => window.location.href = `/album/${track.detail}/${track.id}`}>
                                                            앨범 정보
                                                        </button>
                                                        <button className="accordion3">공유</button>
                                                        <div className="panel2" style={{ display: 'none' }}>
                                                            <button><a href="#" onClick={() => { /* 클립보드 복사 로직 */ }}>링크 복사</a></button>
                                                            <button><a href="#" onClick={() => shareKakao(track.id)}>카카오톡에 공유</a></button>
                                                            <button><a href="#" onClick={() => shareTwitter(track.id)}>트위터에 공유</a></button>
                                                            <button><a href="#" onClick={() => shareFacebook(track.id)}>페이스북에 공유</a></button>
                                                            <button><a href="#" onClick={() => shareBand(track.id)}>밴드에 공유</a></button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            </div>
                        </div>

                        <div className="player-container-1">
                            <div className="progress-bar-container">
                                <span>{formatTime(trackPosition)}</span>
                                <input
                                    type="range"
                                    min="0"
                                    max="100"
                                    value={(trackPosition / trackDuration) * 100 || 0}
                                    onChange={handleSeek}
                                />
                                <span>{formatTime(trackDuration)}</span>
                            </div>

                            <img src={albumImage} alt="Album cover" className="album-cover" />
                            <p>Now playing: {musicName}</p>

                            <div className="navigation-buttons">
                                <button onClick={handlePrevTrack} className="nav-button">이전 곡</button>
                                <button onClick={togglePlayPause} className="nav-button">
                                    {paused ? '재생' : '일시정지'}
                                </button>
                                <button onClick={handleNextTrack} className="nav-button">다음 곡</button>
                                <button onClick={toggleFullscreen} className="nav-button">{isFullscreen ? '▼' : '▲'}</button>
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
    const { useState, useEffect } = React;
    const [currentTrackIndex, setCurrentTrackIndex] = useState(0);
    const [musicInfo, setMusicInfo] = useState({ musicName: "", musicUri: "", albumImage: "", id: "" });
    const [playlist, setPlaylist] = useState([]);

    const providerId = document.getElementById('root').getAttribute('data-user-id');
    const genre = document.getElementById('root').getAttribute('data-genre');

    const fetchPlaylist = async () => {
        try {
            const response = await fetch(`/api/playlist/${providerId}/${genre}`);
            if (!response.ok) {
                throw new Error('플레이리스트를 찾을 수 없습니다.');
            }
            const data = await response.json();
            setPlaylist(data);
        } catch (error) {
            console.error('플레이리스트 가져오기 실패:', error);
        }
    };

    useEffect(() => {
        fetchPlaylist();
    }, []);

    useEffect(() => {
        if (playlist.length > 0 && currentTrackIndex >= 0 && currentTrackIndex < playlist.length) {
            const track = playlist[currentTrackIndex];
            setMusicInfo({
                musicName: track.musicName,
                musicUri: track.music_uri,
                albumImage: track.albumImage,
                musicArtistName: track.musicArtistName,
                id: track.id,
            });
        }
    }, [currentTrackIndex, playlist]);

    const handlePlaylistClick = (id) => {
        const trackIndex = playlist.findIndex(track => track.id === id);
        if (trackIndex !== -1) {
            setCurrentTrackIndex(trackIndex);
        }
    };

    return (
        <SpotifyPlayer
            key={musicInfo.id || musicInfo.musicUri}
            initialMusicInfo={musicInfo}
            providerId={providerId}
            playlist={playlist}
            onNextTrack={() => setCurrentTrackIndex((prev) => (prev + 1) % playlist.length)}
            onPrevTrack={() => setCurrentTrackIndex((prev) => (prev - 1 + playlist.length) % playlist.length)}
        />
    );
};

ReactDOM.render(<App />, document.getElementById('root'));
