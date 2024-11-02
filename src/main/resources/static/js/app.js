const SpotifyPlayer = ({
                           initialMusicInfo = {musicName: "", musicUri: "", albumImage: "", detail:""},
                           providerId,
                           playlist,
                           onNextTrack,
                           onPrevTrack
                       }) => {
    const {useState, useEffect} = React;
    const [musicInfo, setMusicInfo] = useState(initialMusicInfo); // 초기 음악 정보를 설정합니다.
    const {musicName, musicUri, albumImage, detail} = musicInfo;
    const [paused, setPaused] = useState(true);
    const [player, setPlayer] = useState(null);
    const [accessToken, setAccessToken] = useState(null);
    const [deviceId, setDeviceId] = useState(null);
    const [currentTrackIndex, setCurrentTrackIndex] = useState(0); // 현재 플레이리스트에서의 트랙 인덱스
    const [isFullscreen, setIsFullscreen] = useState(false); // 전체화면 상태
    const [trackPosition, setTrackPosition] = useState(0); // 현재 재생 위치
    const [trackDuration, setTrackDuration] = useState(0); // 트랙 길이
    const [activeAccordion, setActiveAccordion] = useState(null);
    const [likedTracks, setLikedTracks] = useState({}); // 좋아요 상태를 저장하는 객체

    const toggleAccordion = (index) => {
        setActiveAccordion(activeAccordion === index ? null : index);
    };

    // 음악 재생 상태를 저장하는 함수
    const saveMusicState = (musicInfo, isPaused) => {
        const musicState = {
            ...musicInfo,
            paused: isPaused // 재생 중인지 여부도 저장
        };
        localStorage.setItem('musicState', JSON.stringify(musicState));
    };

    // const loadLikedTracks = () => {
    //     playlist.forEach(track => {
    //         const albumId = track.id;
    //         fetch(`/mypage/like/album/isLiked?albumId=${albumId}&providerId=${providerId}`, {
    //             method: 'GET',
    //             cache: 'no-cache'
    //         })
    //             .then(response => response.json())
    //             .then(result => {
    //                 setLikedTracks(prevState => ({
    //                     ...prevState,
    //                     [albumId]: result.isLiked
    //                 }));
    //             })
    //             .catch(error => {
    //                 console.error('좋아요 상태 로드 오류:', error);
    //             });
    //     });
    // };
    //
    // useEffect(() => {
    //     loadLikedTracks();
    // }, [playlist]);

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


    // 페이지 로드 시 저장된 상태를 불러오는 함수
    const loadMusicState = () => {
        const savedState = localStorage.getItem('musicState');
        return savedState ? JSON.parse(savedState) : {
            musicName: "",
            musicUri: "",
            albumImage: "",
            id: "",
            detail:"",
            paused: true
        };
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
                getOAuthToken: cb => {
                    cb(accessToken);
                },
                volume: 0.5,
            });

            playerInstance.addListener('ready', ({device_id}) => {
                setDeviceId(device_id);
                if (musicUri) {
                    //playTrack(device_id, musicUri);
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
    }, [musicInfo]);


    useEffect(() => {
        if (player && musicInfo.musicUri && deviceId) {
            player._options.getOAuthToken((token) => {
                const sanitizedUri = sanitizeUri(musicInfo.musicUri);
                fetch(`https://api.spotify.com/v1/me/player/play?device_id=${deviceId}`, {
                    method: 'PUT',
                    body: JSON.stringify({uris: [sanitizedUri]}),
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
            setMusicInfo({
                musicName: track.musicName,
                musicUri: track.music_uri,
                albumImage: track.albumImage,
                musicArtistName: track.musicArtistName,
                id: track.id,
                detail: track.detail
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
                body: JSON.stringify({providerId, albumId}),
            });
            const responseText = await response.text();

            if (!response.ok) {
                throw new Error(`Error recording music: ${responseText.message || response.statusText}`);
            }
            console.log('Music recorded successfully');
        } catch (error) {
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

    const handlePlaylistButtonClick = (event) => {
        event.stopPropagation();

        const button = event.currentTarget;
        if (button.disabled) return;

        button.disabled = true;

        const providerId = button.dataset.userId;
        const albumId = button.dataset.albumId;
        const userPlaylistName = button.innerText;

        console.log('User:', providerId);
        console.log('Album:', albumId);
        console.log('Playlist:', userPlaylistName);

        const playlistDto = {
            providerId: providerId,
            albumId: albumId,
            userPlaylistName: userPlaylistName
        };

        fetch('/playlist/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(playlistDto)
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(errorText => {
                        throw new Error('Error: ' + errorText);
                    });
                }
                return response.text();
            })
            .then(data => {
                console.log('Response data:', data);
                alert(data);
            })
            .catch(error => {
                console.error('Fetch error:', error);
                alert('오류 발생: ' + error.message);
            })
            .finally(() => {
                button.disabled = false;
            });
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
            <div className="track-info">
                <img src={albumImage} alt="Album cover" className="album-cover"/>
                <p>Now playing: {musicName}</p>
            </div>

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
                            <div className="album-container" style={{flex: 0.6}}>
                                <div className="album-image-3">
                                    <img src={albumImage} alt="Album cover"/>
                                </div>
                            </div>
                            <div className="track-container" style={{ flex: 0.4 }}>
                                <p className="next_track">다음 트랙</p>
                                <div className="playlist-3">
                                    <ul>
                                        {playlist.map((track, index) => (
                                            <li key={track.id}
                                                style={{display: 'flex', alignItems: 'center', marginBottom: '10px'}}>
                                                <div
                                                    onClick={() => handlePlaylistClick(track.id)}
                                                    style={{display: 'flex', alignItems: 'center', cursor: 'pointer'}}
                                                >
                                                    <img
                                                        src={track.albumImage}
                                                        alt={track.musicName}
                                                        style={{
                                                            width: '60px',
                                                            height: '60px',
                                                            marginRight: '20px',
                                                            marginLeft: '10px'
                                                        }}
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
                                                        style={{display: activeAccordion === index ? 'block' : 'none'}}
                                                    >
                                                        <button className="like-button"
                                                                onClick={() => toggleTrackLike(track.id)}>
                                                            <span>{likedTracks[track.id] ? '♥' : '♡'}</span>
                                                        </button>
                                                        <button className="accordion4">플레이리스트에 추가
                                                        </button>
                                                        <div className="panel3" style={{display: 'none'}}>
                                                            {playlists.map((playlist) => (
                                                                <button
                                                                    key={playlist.id}
                                                                    data-playlist-id={playlist.id}
                                                                    data-user-id={providerId}
                                                                    data-album-id={track.id}
                                                                    onClick={handlePlaylistButtonClick}
                                                                >
                                                                    {playlist.userPlaylistName}
                                                                </button>
                                                            ))}
                                                            {/* 새로운 플레이리스트 추가 입력 필드와 버튼 */}
                                                            <input type="text" id="newPlaylistName"
                                                                   placeholder="새로운 플레이리스트"/>
                                                            <button id="addPlaylistBtn"
                                                                    onClick={handleAddPlaylistClick}>추가
                                                            </button>
                                                        </div>

                                                        <button className="my_shortcuts"
                                                                data-url={`/album/${track.detail}/${providerId}`}
                                                                onClick={() => {
                                                                    window.location.href = `/album/${track.detail}/${providerId}`;
                                                                }}>
                                                            앨범 정보
                                                        </button>

                                                        <button className="accordion3">공유</button>
                                                        <div className="panel2" style={{display: 'none'}}>
                                                            <button><a href="#" onClick={() => { /* 클립보드 복사 로직 */
                                                            }}>링크 복사</a></button>
                                                            <button><a href="#" onClick={() => shareKakao(track.id)}>카카오톡에
                                                                공유</a></button>
                                                            <button><a href="#" onClick={() => shareTwitter(track.id)}>트위터에
                                                                공유</a></button>
                                                            <button><a href="#" onClick={() => shareFacebook(track.id)}>페이스북에
                                                                공유</a></button>
                                                            <button><a href="#" onClick={() => shareBand(track.id)}>밴드에
                                                                공유</a></button>
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
    const [musicInfo, setMusicInfo] = useState({musicName: "", musicUri: "", albumImage: "", id: "", detail: ""});
    const [playlist, setPlaylist] = useState([]); // 플레이리스트 상태

    // root 엘리먼트에서 userId와 genre 값을 추출
    const providerId = document.getElementById('root').getAttribute('data-user-id');
    const genre = document.getElementById('root').getAttribute('data-genre');


    // 장르 기반 플레이리스트 가져오기
    const fetchPlaylist = async () => {
        try {
            const response = await fetch(`/api/playlist/${providerId}/${genre}`);
            if (!response.ok) {
                throw new Error('플레이리스트를 찾을 수 없습니다.');
            }
            const data = await response.json();
            setPlaylist(data);
        } catch (error) {
        }
    };

    // 나의 플레이리스트 가져오기
    const fetchMyPlaylist = async () => {
        const userPlaylistName = event.currentTarget.getAttribute('data-myplaylist');
        console.log('User Playlist Name:', userPlaylistName); // 값 확인
        try {
            const response = await fetch(`/api/mypage/playlist/my/${providerId}/${userPlaylistName}`);
            if (!response.ok) {
                throw new Error('플레이리스트를 찾을 수 없습니다.');
            }
            const data = await response.json();
            setPlaylist(data);
        } catch (error) {
        }
    };

    // 나의 플레이리스트 가져오기
    const fetchAlbumlist = async () => {
        const detail = event.currentTarget.getAttribute('data-detail');
        try {
            const response = await fetch(`/api/album/${providerId}/${detail}`);
            if (!response.ok) {
                throw new Error('플레이리스트를 찾을 수 없습니다.');
            }
            const data = await response.json();
            setPlaylist(data);
        } catch (error) {
            console.error('플레이리스트 가져오기 실패:', error);
        }
    };

    // 스트리밍 정보 가져오기 (외부 클릭 시 사용)
    const fetchMusicInfo = async (id) => {
        try {
            const response = await fetch(`/api/streaming/${providerId}/${id}`);
            if (!response.ok) {
                throw new Error('스트리밍 리스트를 찾을 수 없습니다.');
            }
            const data = await response.json();

            if (data.length > 0) {
                setPlaylist(data);  // 가져온 스트리밍 리스트를 플레이리스트로 설정
                setCurrentTrackIndex(0); // 첫 번째 트랙부터 재생
            } else {
                console.error('스트리밍 리스트가 비어 있습니다.');
            }
        } catch (error) {
            console.error('스트리밍 리스트 가져오기 실패:', error);
        }
    };

     // 컴포넌트가 처음 로드될 때 플레이리스트 가져옴
     useEffect(() => {
         fetchPlaylist();
         handlePlaylistClick();
     }, []);

    // 현재 트랙 정보 업데이트
    useEffect(() => {
        if (playlist.length > 0 && currentTrackIndex >= 0 && currentTrackIndex < playlist.length) {
            const track = playlist[currentTrackIndex];
            setMusicInfo({
                musicName: track.musicName,
                musicUri: track.music_uri,
                albumImage: track.albumImage,
                musicArtistName: track.musicArtistName,
                id: track.id,
                detail: track.detail
            });
        }
    }, [currentTrackIndex, playlist]);

    // 플레이리스트에서 음악 클릭 시 처리
    const handlePlaylistClick =  async (id) => {
        await fetchPlaylist(id); // 스트리밍 정보 가져오기
        const trackIndex = playlist.findIndex(track => track.id === id);
        if (trackIndex !== -1) {
            setCurrentTrackIndex(trackIndex);
        }
    };

    //앨범 수록곡 클릭 시 처리
    const handleAlbumlistClick = async (id) => {
        await fetchAlbumlist(id); // 스트리밍 정보 가져오기
        const trackIndex = playlist.findIndex(track => track.id === id);
        if (trackIndex !== -1) {
            setCurrentTrackIndex(trackIndex);
        }
    };

    //마이페이지 플레이리스트 클릭 시 처리
    const handleMyPlaylistClick = async (id) => {
        await fetchMyPlaylist(id); // 스트리밍 정보 가져오기
        const trackIndex = playlist.findIndex(track => track.id === id);
        if (trackIndex !== -1) {
            setCurrentTrackIndex(trackIndex);
        }
    };

    // 외부 음악 클릭 시 처리
    const handleMusicClick = async (id) => {
        await fetchMusicInfo(id); // 스트리밍 정보 가져오기
        const trackIndex = playlist.findIndex(track => track.id === id);
        if (trackIndex !== -1) {
            setCurrentTrackIndex(trackIndex); // 음악 정보 업데이트 후 현재 인덱스 설정
        }
    };

    // 전역 범위로 노출 (파일 나누면서 필요)
    window.handlePlaylistClick = handlePlaylistClick;
    window.handleMusicClick = handleMusicClick;
    window.handleMyPlaylistClick = handleMyPlaylistClick;
    window.handleAlbumlistClick =handleAlbumlistClick;

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


ReactDOM.render(<App/>, document.getElementById('root'));