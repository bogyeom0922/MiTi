document.addEventListener('DOMContentLoaded', () => {


    // 섹션 애니메이션 처리
    const sections = document.querySelectorAll('.section');

    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                entry.target.classList.add('show'); // 애니메이션 시작
            } else {
                entry.target.classList.remove('show'); // 필요 시 초기화
            }
        });
    }, {threshold: 0.15});

    sections.forEach((section) => observer.observe(section));

    // 부드러운 스크롤 처리
    const moveButton = document.querySelector('.move');
    const main2Section = document.getElementById('main2');

    if (moveButton && main2Section) {
        moveButton.addEventListener('click', () => {
            main2Section.scrollIntoView({behavior: 'smooth'});
        });
    }

    // 마이페이지 링크 클릭 시 동적 페이지 로딩
    const contentElement = document.getElementById('content'); // 현재 페이지의 콘텐츠 영역

    const loadPage = (url) => {
        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(data => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(data, 'text/html');
                const newContent = doc.getElementById('content');

                if (newContent) {
                    contentElement.innerHTML = newContent.innerHTML;
                    history.pushState(null, null, url);
                } else {
                    console.error("Error: #content not found in the loaded page.");
                }
            })
            .catch(error => console.error('Error loading page:', error));
    };

    document.querySelectorAll('.my_shortcuts').forEach(button => {
        button.addEventListener('click', (event) => {
            event.preventDefault(); // 기본 링크 동작 방지
            const url = button.getAttribute('data-url'); // 링크의 URL 가져오기

            fetch(url)
                .then(response => response.text())
                .then(html => {
                    document.getElementById('content').innerHTML = html;
                });
        });
    });

    // 이벤트 위임을 통한 클릭 이벤트 추가
    document.getElementById('content').addEventListener('click', (event) => {
        const button = event.target.closest('.my_shortcuts');
        if (button) {
            event.preventDefault(); // 기본 링크 동작 방지
            const url = button.getAttribute('data-url');
            if (url) loadPage(url);
        }

        // 앨범에 대한 좋아요 버튼
        const albumHeartButton = document.querySelector('.albumHeartButton');
        if (albumHeartButton) {
            albumHeartButton.addEventListener('click', function () {
                const providerId = this.getAttribute('data-user-id');
                const albumDetail = this.getAttribute('data-album-detail');

                fetch(`/mypage/like/album/toggle?albumDetail=${albumDetail}&providerId=${providerId}`, {
                    method: 'POST',
                })
                    .then(response => response.text())
                    .then(result => {
                        const isLiked = result === "liked";
                        this.querySelector('span').innerText = isLiked ? '♥' : '♡';
                        alert(isLiked ? "앨범 전체 음악에 좋아요를 눌렀습니다!" : "좋아요를 취소했습니다!");

                        document.querySelectorAll('.like-button span').forEach(button => {
                            button.innerText = isLiked ? '♥' : '♡';
                        });
                    })
                    .catch(error => {
                        console.error('Error toggling like:', error);
                        alert("좋아요 처리 중 오류가 발생했습니다.");
                    });
            });
        }

        // 댓글 생성
        // 댓글 작성 기능
        window.submitComment = function () {
            const data = {
                providerId: document.getElementById('providerId').value,
                albumId: document.getElementById('albumId').value,
                comment: document.getElementById('comment').value
            };

            fetch('/submitComment', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(data)
            })
                .then(response => response.ok ? response.json() : Promise.reject('댓글 작성 실패'))
                .then(() => {
                    alert('댓글이 작성되었습니다.');

                    // 댓글 작성 후 특정 영역을 업데이트
                    const url = button.getAttribute('data-url');
                    fetch(url)
                        .then(response => response.text())
                        .then(html => {
                            document.getElementById('content').innerHTML = html;
                        });
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        };


        //마이페이지 좋아요 삭제 기능
        window.deleteLike = function (id, button) {
            console.log(`Deleting like for userId: ${id}`);
            fetch(`/mypage/like/${id}`, {
                method: 'DELETE'
            })
                .then(response => response.text())
                .then(result => {
                    console.log(`Response: ${result}`);
                    if (result === 'success') {
                        // 삭제된 항목을 DOM에서 제거
                        document.querySelector(`tr[data-id="${id}"]`).remove();

                        // 좋아요 목록 페이지로 이동 (my_shortcuts 기능과 통합)
                        const url = button.getAttribute('data-url');
                        fetch(url)
                            .then(response => response.text())
                            .then(html => {
                                document.getElementById('content').innerHTML = html;
                            });
                    } else {
                        alert('삭제 실패');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        };
        <!-- JavaScript에 필요한 값 전달 var providerId = /*[[${user.providerId}]]*/ 'defaultProviderId'; -->

        // 장르 삭제 기능
        window.deleteGenre = function (id, button) {
            fetch(`/mypage/genre/${id}`, {method: 'DELETE'})
                .then(response => response.text())
                .then(result => {
                    if (result === 'success') {
                        // 삭제된 항목을 DOM에서 제거
                        document.querySelector(`tr[data-id="${id}"]`).remove();

                        // 장르 목록 페이지로 이동 (my_shortcuts 기능과 통합)
                        const url = button.getAttribute('data-url');
                        fetch(url)
                            .then(response => response.text())
                            .then(html => {
                                document.getElementById('content').innerHTML = html;
                            });
                    } else {
                        alert('삭제 실패');
                    }
                })
                .catch(error => console.error('Error:', error));
        };

        // 장르 목록 표시 기능
        window.showAddGenreList = function (button) {
            const id = button.getAttribute('data-user-id');

            fetch(`/mypage/genre/non-selected/${id}`)
                .then(response => response.json())
                .then(nonSelectedGenres => {
                    const list = document.getElementById('nonSelectedGenreList');
                    list.innerHTML = '';
                    nonSelectedGenres.forEach(genre => {
                        const listItem = document.createElement('li');
                        listItem.innerHTML = `
                    <img src="${genre.genre_image}" width="50">
                    <span>${genre.genre}</span>
                    <button
                        onclick="window.addGenre('${genre.genre}', '${genre.genre_image}', this, '${id}')" 
                        data-url="/mypage/genre/${id}"> <!-- data-url을 직접 설정 -->
                        추가
                    </button>
                `;
                        list.appendChild(listItem);
                    });
                    document.getElementById('addGenreList').style.display = 'block';
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        };

        // 장르 추가 기능
        window.addGenre = function (genre, genre_image, button, id) {
            const genreDto = {providerId: id, genre, genre_image};
            fetch(`/mypage/genre/add`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(genreDto)
            })
                .then(response => response.text())
                .then(result => {
                    if (result === 'success') {
                        // 장르 목록 페이지로 이동 (my_shortcuts 기능과 통합)
                        const url = button.getAttribute('data-url'); // data-url을 직접 설정했으므로 이제 올바른 값을 가져옴
                        fetch(url)
                            .then(response => response.text())
                            .then(html => {
                                document.getElementById('content').innerHTML = html;
                            });
                    } else {
                        alert('추가 실패');
                    }
                })
                .catch(error => console.error('Error:', error));
        };


        //mypage_comment
        // 댓글 수정 모달 열기
        window.editComment = function (id) {
            const commentTextElement = document.getElementById(`comment-text-${id}`);
            const commentText = commentTextElement ? commentTextElement.innerText : '';
            document.getElementById('editCommentId').value = id;
            document.getElementById('editCommentText').value = commentText;
            document.getElementById('editModal').style.display = 'flex';
        };

        // 댓글 수정 모달 닫기
        window.closeEditModal = function () {
            document.getElementById('editModal').style.display = 'none';
        };

        // 댓글 저장 기능
        window.saveComment = function (event) {
            const id = document.getElementById('editCommentId').value;
            const comment = document.getElementById('editCommentText').value;
            const button = event.target; // 클릭한 버튼을 참조

            fetch(`/comment/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({comment}),
            })
                .then(response => {
                    if (response.ok) {
                        return response.text();
                    } else {
                        throw new Error('Failed to update comment');
                    }
                })
                .then(result => {
                    if (result === 'success') {
                        // 장르 목록 페이지로 이동
                        const url = button.getAttribute('data-url');
                        fetch(url)
                            .then(response => response.text())
                            .then(html => {
                                document.getElementById('content').innerHTML = html;
                            });
                    } else {
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        };


        // 댓글 삭제 기능
        window.deleteMyComment = function (id) {
            fetch(`/mypage/comment/${id}`, {
                method: 'DELETE'
            })
                .then(response => response.text())
                .then(result => {
                    if (result === 'success') {
                        // 삭제된 항목을 DOM에서 제거
                        document.querySelector(`tr[data-id="${id}"]`).remove();

                        // 좋아요 목록 페이지로 이동 (my_shortcuts 기능과 통합)
                        const url = button.getAttribute('data-url');
                        fetch(url)
                            .then(response => response.text())
                            .then(html => {
                                document.getElementById('content').innerHTML = html;
                            });
                    } else {
                        alert('삭제 실패');
                    }
                });
        };

        //mypage_playlist(playlist_album)
        // 마이페이지 플레이리스트 삭제 기능
        window.deleteFromPlaylist = function (albumId, button) {
            const playlistName = button.getAttribute('data-playlist-name'); // data-* 속성으로 문자열 값 가져오기
            const url = button.getAttribute('data-url');

            console.log(`Deleting albumId: ${albumId} from playlist: ${playlistName}`);

            fetch(`/playlist/delete`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({albumId: albumId, playlistName: playlistName})
            })
                .then(response => response.text())
                .then(result => {
                    console.log(`Response: ${result}`);
                    if (result === 'success') {
                        document.querySelector(`tr[data-album-id="${albumId}"]`).remove();

                        // 페이지 업데이트
                        fetch(url)
                            .then(response => response.text())
                            .then(html => {
                                document.getElementById('content').innerHTML = html;
                            });
                    } else {

                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('삭제 중 오류가 발생했습니다.');
                });
        };

        //album_detail js q부분

        // 전역 함수로 toggleAccordion 정의
        window.toggleAccordion = function (button) {
            console.log('Accordion button clicked');
            button.classList.toggle("active");

            const panel = button.nextElementSibling;
            panel.style.display = panel.style.display === "block" ? "none" : "block";
        };

        // 추가적인 필요 기능들을 전역 함수로 설정
        window.shareTwitter = function () {
            const text = '앨범 공유 텍스트';
            const url = window.location.href;
            const hashtags = '음악,앨범';
            const twitterUrl = `https://twitter.com/intent/tweet?text=${encodeURIComponent(text)}&url=${encodeURIComponent(url)}&hashtags=${encodeURIComponent(hashtags)}`;
            window.open(twitterUrl, '_blank');
        };

        window.shareFacebook = function () {
            const url = window.location.href;
            const facebookUrl = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}`;
            window.open(facebookUrl, '_blank');
        };

        window.shareBand = function () {
            const url = window.location.href;
            const bandUrl = `https://band.us/plugin/share?body=${encodeURIComponent(url)}&route=${encodeURIComponent(url)}`;
            window.open(bandUrl, '_blank');
        };

        window.clip = function () {
            const url = window.location.href;
            const textarea = document.createElement('textarea');
            document.body.appendChild(textarea);
            textarea.value = url;
            textarea.select();
            document.execCommand('copy');
            document.body.removeChild(textarea);
            alert('링크가 복사되었습니다.');
        };


        // 곡에 대한 개별 좋아요 처리
        document.querySelectorAll('.like-button').forEach(button => {
            button.removeEventListener('click', handleLikeClick); // 기존 이벤트 제거
            button.addEventListener('click', handleLikeClick); // 새로운 이벤트 바인딩
        });

        function handleLikeClick() {
            console.log('Track like button clicked');
            const button = event.currentTarget; // 현재 클릭된 버튼 참조
            if (button.disabled) return;

            button.disabled = true;

            const albumId = this.getAttribute('data-album-id');
            const providerId = this.getAttribute('data-user-id');

            fetch(`/mypage/like/album/toggleTrack?albumId=${albumId}&providerId=${providerId}`, {
                method: 'POST',
                cache: 'no-cache'
            })
                .then(response => response.text())
                .then(result => {
                    const isLiked = result === "liked";
                    this.querySelector('span').innerText = isLiked ? '♥' : '♡';
                })
                .catch(error => {
                    console.error('Error toggling like:', error);
                    alert("좋아요 처리 중 오류가 발생했습니다.");
                })
                .finally(() => {
                    button.disabled = false;
                });
        }

        // 플레이리스트 추가 버튼 클릭 이벤트 초기화
        document.querySelectorAll('.panel3 button[data-user-id]').forEach(button => {
            // 클릭 이벤트 리스너 제거
            button.removeEventListener('click', handlePlaylistButtonClick);
            // 새로운 이벤트 바인딩
            button.addEventListener('click', handlePlaylistButtonClick);
        });

        function handlePlaylistButtonClick(event) {
            event.stopPropagation();

            const button = event.currentTarget; // 현재 클릭된 버튼 참조
            if (button.disabled) return;

            button.disabled = true;

            const providerId = button.dataset.userId; // data-user-id 값 가져오기
            const albumId = button.dataset.albumId; // data-album-id 값 가져오기
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
                headers: {'Content-Type': 'application/json'},
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
        }

        // 새로운 플레이리스트 추가 버튼 클릭 이벤트 초기화
        const addPlaylistBtn = document.getElementById('addPlaylistBtn');
        if (addPlaylistBtn) {
            addPlaylistBtn.removeEventListener('click', handleAddPlaylistClick); // 기존 이벤트 제거
            addPlaylistBtn.addEventListener('click', handleAddPlaylistClick); // 새로운 이벤트 바인딩
        }

        function handleAddPlaylistClick(event) {
            event.stopPropagation();
            const button = event.currentTarget; // 현재 클릭된 버튼 참조
            if (button.disabled) return;

            button.disabled = true;
            const newPlaylistName = document.getElementById('newPlaylistName').value.trim();

            console.log('New Playlist Name:', newPlaylistName);

            const firstButton = document.querySelector('.panel3 button[data-user-id]');
            if (!firstButton) {
                alert('플레이리스트가 없습니다. 먼저 플레이리스트를 추가하세요.');
                return;
            }

            const providerId = firstButton.dataset.userId;
            const albumId = firstButton.dataset.albumId;

            const playlistDto = {
                providerId: providerId,
                albumId: albumId,
                userPlaylistName: newPlaylistName
            };

            fetch('/playlist/create', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
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
                    document.getElementById('newPlaylistName').value = '';
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    alert('오류 발생: ' + error.message);
                })
                .finally(() => {
                    button.disabled = false;
                });
        }
    });


});

