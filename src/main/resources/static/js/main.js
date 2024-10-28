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
    }, { threshold: 0.15 });

    sections.forEach((section) => observer.observe(section));

    // 부드러운 스크롤 처리
    const moveButton = document.querySelector('.move');
    const main2Section = document.getElementById('main2');

    if (moveButton && main2Section) {
        moveButton.addEventListener('click', () => {
            main2Section.scrollIntoView({ behavior: 'smooth' });
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
                console.log("Loaded document:", doc); // 로드된 전체 페이지 로그
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
            console.log("URL to load:", url);
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
            console.log("Button clicked:", button);
            console.log("URL to load:", url);
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

        // 개별 곡 좋아요 버튼
        document.querySelectorAll('.like-button').forEach(button => {
            button.addEventListener('click', function () {
                const albumId = this.getAttribute('data-album-id');
                const providerId = this.getAttribute('data-user-id');

                fetch(`/mypage/like/album/toggleTrack?albumId=${albumId}&providerId=${providerId}`, {
                    method: 'POST',
                    cache: 'no-cache'
                })
                    .then(response => response.text())
                    .then(result => {
                        this.querySelector('span').innerText = result === "liked" ? '♥' : '♡';
                    })
                    .catch(error => {
                        console.error('Error toggling like:', error);
                        alert("좋아요 처리 중 오류가 발생했습니다.");
                    });
            });
        });

        // 댓글 생성
        function submitComment() {
            const data = {
                providerId: document.getElementById('providerId').value,
                albumId: document.getElementById('albumId').value,
                comment: document.getElementById('comment').value
            };

            fetch('/submitComment', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
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

        // 아코디언 메뉴 초기화
        function initAccordion(className) {
            document.querySelectorAll(className).forEach(button => {
                button.addEventListener('click', function (event) {
                    event.stopPropagation();
                    this.classList.toggle('active');
                    const panel = this.nextElementSibling;
                    panel.style.display = panel.style.display === "block" ? "none" : "block";
                });
            });
        }
        initAccordion('.accordion2');
        initAccordion('.accordion3');
        initAccordion('.accordion4');

        document.addEventListener('click', event => {
            document.querySelectorAll('.accordion2').forEach(button => {
                const panel = button.nextElementSibling;
                if (panel.style.display === "block" && !button.contains(event.target)) {
                    panel.style.display = "none";
                    button.classList.remove("active");
                }
            });
        });

        // 플레이리스트 추가
        document.getElementById('addPlaylistBtn')?.addEventListener('click', event => {
            event.stopPropagation();
            const newPlaylistName = document.getElementById('newPlaylistName').value.trim();
            const firstButton = document.querySelector('.panel3 button[data-user-id]');

            if (!firstButton) return alert('플레이리스트가 없습니다.');

            const playlistDto = {
                providerId: firstButton.dataset.userId,
                albumId: firstButton.dataset.albumId,
                userPlaylistName: newPlaylistName
            };

            fetch('/playlist/create', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(playlistDto)
            })
                .then(response => response.ok ? response.text() : Promise.reject('플레이리스트 생성 실패'))
                .then(alert)
                .catch(error => {
                    console.error('Fetch error:', error);
                    alert('오류 발생: ' + error);
                });
        });

        document.querySelectorAll('.panel3 button[data-user-id]').forEach(button => {
            button.addEventListener('click', event => {
                event.stopPropagation();

                const playlistDto = {
                    providerId: button.dataset.userId,
                    albumId: button.dataset.albumId,
                    userPlaylistName: button.innerText
                };

                fetch('/playlist/add', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(playlistDto)
                })
                    .then(response => response.ok ? response.text() : Promise.reject('추가 실패'))
                    .then(alert)
                    .catch(error => {
                        console.error('Fetch error:', error);
                        alert('오류 발생: ' + error);
                    });
            });
        });

        // 카카오톡 공유
        Kakao.init('YOUR_KAKAO_API_KEY');
        document.getElementById('btnKakao')?.addEventListener('click', () => {
            Kakao.Link.createDefaultButton({
                container: '#btnKakao',
                objectType: 'feed',
                content: {
                    title: '앨범 공유 제목',
                    description: '앨범 공유 설명',
                    imageUrl: `${window.location.origin}/images/album_image.jpg`,
                    link: { mobileWebUrl: window.location.href, webUrl: window.location.href }
                }
            });
        });

        // SNS 공유 함수들
        const share = (url) => window.open(url, '_blank');
        document.getElementById('btnTwitter')?.addEventListener('click', () => {
            share(`https://twitter.com/intent/tweet?text=${encodeURIComponent('앨범 공유 텍스트')}&url=${encodeURIComponent(window.location.href)}&hashtags=${encodeURIComponent('음악,앨범')}`);
        });
        document.getElementById('btnFacebook')?.addEventListener('click', () => {
            share(`https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(window.location.href)}`);
        });
        document.getElementById('btnBand')?.addEventListener('click', () => {
            share(`https://band.us/plugin/share?body=${encodeURIComponent(window.location.href)}&route=${encodeURIComponent(window.location.href)}`);
        });

        // 링크 복사 기능
        document.getElementById('btnCopyLink')?.addEventListener('click', () => {
            const textarea = document.createElement('textarea');
            document.body.appendChild(textarea);
            textarea.value = window.location.href;
            textarea.select();
            document.execCommand('copy');
            document.body.removeChild(textarea);
            alert('링크가 복사되었습니다.');
        });
    });

    //마이페이지 좋아요 삭제 기능
    window.deleteLike = function(id, button) {
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
    <!-- JavaScript에 필요한 값 전달 -->
    var providerId = /*[[${user.providerId}]]*/ 'defaultProviderId';

    //마이페이지 선호장르
    // 장르 목록 표시 기능
    window.showAddGenreList = function() {
        fetch(`/mypage/genre/non-selected/${providerId}`)
            .then(response => response.json())
            .then(nonSelectedGenres => {
                const list = document.getElementById('nonSelectedGenreList');
                list.innerHTML = '';
                nonSelectedGenres.forEach(genre => {
                    const listItem = document.createElement('li');
                    listItem.innerHTML = `
                    <img src="${genre.genre_image}" width="50">
                    <span>${genre.genre}</span>
                    <button onclick="window.addGenre('${genre.genre}', '${genre.genre_image}', this)">추가</button>
                `;
                    list.appendChild(listItem);
                });
                document.getElementById('addGenreList').style.display = 'block';
            })
            .catch(error => console.error('Error:', error));
    };


    // 장르 삭제 기능
    window.deleteGenre = function(id, button) {
        fetch(`/mypage/genre/${id}`, { method: 'DELETE' })
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

// 장르 추가 기능
    window.addGenre = function(genre, genre_image, button) {
        const genreDto = { providerId, genre, genre_image };
        fetch(`/mypage/genre/add`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(genreDto)
        })
            .then(response => response.text())
            .then(result => {
                if (result === 'success') {
                    // 장르 목록 페이지로 이동 (my_shortcuts 기능과 통합)
                    const url = button.getAttribute('data-url');
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

});

