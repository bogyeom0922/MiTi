// React 컴포넌트 정의
const MyPageRecord = ({ providerId }) => {
    const [recordList, setRecordList] = useState([]);
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchRecordList = async () => {
            try {
                const response = await fetch(`/api/record/${providerId}`);
                const data = await response.json();
                setRecordList(data.recordList);
                setIsLoading(false); // 데이터 로딩 완료
            } catch (error) {
                console.error('Failed to fetch record list:', error);
                setError('음악 목록을 가져오는 데 실패했습니다.');
                setIsLoading(false); // 에러 발생 시 로딩 완료
            }
        };

        fetchRecordList();
    }, [providerId]);

    const handleMusicClick = (albumId) => {
        console.log(`Album ID clicked: ${albumId}`);
    };

    if (isLoading) {
        return <p>로딩 중...</p>;
    }

    if (error) {
        return <p>{error}</p>;
    }

    return (
        <div className="my_body">
            <p className="my_body_title">재생했던 음악</p>
            <table className="my_table_record">
                <tbody>
                {recordList.map((record) => (
                    <tr key={record.albumId} onClick={() => handleMusicClick(record.albumId)}>
                        <td style={{ width: '10%' }}>
                            <img src={record.album_image} alt={record.music_name} />
                        </td>
                        <td style={{ width: '50%' }} className="my_table_padding my_table_body">
                            <span>{record.music_name}</span>
                        </td>
                        <td style={{ width: '40%' }}>
                            <span>{record.music_artist_name}</span>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

// 전역에 MyPageRecord 컴포넌트를 등록
window.MyPageRecord = MyPageRecord;