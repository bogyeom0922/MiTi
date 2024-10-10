import React, { useEffect, useState } from 'react';
import $ from 'jquery'; // jQuery 사용

const Header = ({ user }) => {
    const [query, setQuery] = useState('');
    const [autocompleteResults, setAutocompleteResults] = useState([]);
    const [isAccordionOpen, setIsAccordionOpen] = useState(false);

    useEffect(() => {
        if (query.length > 1) {
            $('#autocomplete-results').css('display', 'block');
            $.ajax({
                url: '/find/autocomplete',
                type: 'GET',
                data: { query },
                success: (data) => {
                    setAutocompleteResults(data);
                },
                error: () => {
                    setAutocompleteResults(['자동완성 결과를 불러오는 중 오류가 발생했습니다.']);
                }
            });
        } else {
            $('#autocomplete-results').css('display', 'none');
        }
    }, [query]);

    const handleSearchChange = (e) => {
        setQuery(e.target.value);
    };

    const toggleAccordion = () => {
        setIsAccordionOpen(!isAccordionOpen);
    };

    const redirectToMain = () => {
        window.location.href = '/main';
    };

    return (
        <div className="header">
            {user && (
                <p className="logo" onClick={redirectToMain}>
                    MITI
                </p>
            )}

            <input
                type="text"
                className="search_input"
                id="search"
                placeholder="Search for music or artist"
                value={query}
                onChange={handleSearchChange}
                autoComplete="off"
            />
            <div id="autocomplete-results" style={{ display: 'none' }}>
                {autocompleteResults.map((result, index) => (
                    <div key={index} className="autocomplete-item">
                        {result}
                    </div>
                ))}
            </div>

            <div className="my_accordion-container">
                <span onClick={toggleAccordion} className="user">
                    {user ? user.name : '사용자'}
                </span>

                {isAccordionOpen && (
                    <div className="my_accordion-content">
                        <a href="/logout">로그아웃</a>
                        <a href={`/mypage/record/${user.providerId}`}>마이페이지</a>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Header;