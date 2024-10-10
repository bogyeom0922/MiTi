document.addEventListener("DOMContentLoaded", function () {
    fetch("/api/main") // 수정된 API 엔드포인트
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                // 추천 앨범 목록 추가
                const recommendedAlbumsContainer = document.getElementById("recommendedAlbums");
                for (const [key, value] of Object.entries(data.recommendedAlbumsMap)) {
                    const playlistDiv = document.createElement("div");
                    playlistDiv.className = "recommend_playlist";
                    const imageUrl = value[0].album_image; // 기본 이미지 설정
                    playlistDiv.innerHTML = `
                        <a href="/playlist/detail/${data.user ? data.user.providerId : ''}/${key}">
                            <img src="${imageUrl}" alt="Playlist Image" class="playlist_image">
                        </a>
                        <p class="playlist_name">${key} 때 듣는 음악</p>
                    `;
                    recommendedAlbumsContainer.appendChild(playlistDiv);
                }

                // 맞춤형 추천 곡들 추가
                const customizedSongsContainer = document.querySelector(".custom-songs-section .songs-container");
                data.customizedAlbums.forEach(song => {
                    const songItem = document.createElement("div");
                    songItem.className = "song-item";
                    songItem.onclick = () => handleMusicClick(song.id); // 클릭 시 함수 호출
                    const songImageUrl = song.album_image; // 기본 이미지 설정
                    songItem.innerHTML = `
                        <img src="${songImageUrl}" alt="앨범 이미지" class="album-cover-thumbnail">
                        <p class="song-title">${song.musicName}</p>
                        <p class="song-artist">${song.musicArtistName}</p>
                    `;
                    customizedSongsContainer.appendChild(songItem);
                });

                // 인기 차트 추가
                const popularChartContainer = document.getElementById("popularChart");
                data.popularAlbums.forEach((album, index) => {
                    if (index < 10) {
                        const chartItem = document.createElement("div");
                        chartItem.className = "chart_item";
                        chartItem.onclick = () => handleMusicClick(album.id); // 클릭 시 함수 호출
                        const chartImageUrl = album.album_image; // 기본 이미지 설정
                        chartItem.innerHTML = `
                            <img src="${chartImageUrl}" alt="Album Cover" class="chart_image">
                            <div class="chart_info">
                                <p class="chart_name">${album.musicName}</p>
                                <p>${album.musicArtistName}</p>
                            </div>
                        `;
                        popularChartContainer.appendChild(chartItem);
                    }
                });
            } else {
                console.error("Error loading data");
            }
        })
        .catch(error => console.error("Error fetching data:", error));
});

function handleMusicClick(albumId) {
    // 음악 클릭 시 처리할 로직을 여기에 추가합니다.
    console.log("Clicked album ID:", albumId);
}