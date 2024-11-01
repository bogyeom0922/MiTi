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
            console.log("Button clicked:", button);
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


        function toggleAccordion(event, accordionClass) {
            event.stopPropagation();
            const button = event.currentTarget;
            button.classList.toggle("active");
            const panel = button.nextElementSibling;
            panel.style.display = panel.style.display === "block" ? "none" : "block";
        }

        document.querySelectorAll('.accordion2, .accordion3, .accordion4').forEach(button => {
            button.addEventListener('click', (event) => toggleAccordion(event, button.className));
        });

        document.addEventListener("click", function(event) {
            document.querySelectorAll('.accordion2, .accordion3, .accordion4').forEach(button => {
                const panel = button.nextElementSibling;
                if (panel.style.display === "block" && !button.contains(event.target) && !panel.contains(event.target)) {
                    panel.style.display = "none";
                    button.classList.remove("active");
                }
            });
        });

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
        function submitComment() {
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
                    location.reload();
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('댓글 작성 중 오류가 발생했습니다.');
                });
        }
    });



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


    // 이벤트 위임을 사용하여 동적 콘텐츠 로딩 문제 해결
    document.querySelector('.music-click-2').addEventListener('click', function (event) {
        const item = event.target.closest('tr'); // 클릭된 요소 중 가장 가까운 tr 찾기
        if (item && !event.target.closest('.accordion_container') && !event.target.closest('.accordion_container2')) {
            const songId = item.getAttribute('data-song-id'); // songId 추출
            handlePlaylistClick(songId);
        }
    });


});

