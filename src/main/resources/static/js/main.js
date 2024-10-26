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
    });
});
