// import React, { useEffect, useState } from 'react';
//
// const MyPageRecord = ({ providerId }) => {
//     const [recordList, setRecordList] = useState([]);
//
//     useEffect(() => {
//         const fetchRecordList = async () => {
//             try {
//                 const response = await fetch(`/api/record/${providerId}`);
//                 const data = await response.json();
//                 setRecordList(data.recordList);
//             } catch (error) {
//                 console.error('Failed to fetch record list:', error);
//             }
//         };
//
//         fetchRecordList();
//     }, [providerId]);
//
//     const handleMusicClick = (albumId) => {
//         // 음악 클릭 시 처리할 로직
//         console.log(`Album ID clicked: ${albumId}`);
//     };
//
//     return (
//         <div className="my_body">
//             <p className="my_body_title">재생했던 음악</p>
//             <table className="my_table_record">
//                 <tbody>
//                 {recordList.map((record) => (
//                     <tr key={record.albumId} onClick={() => handleMusicClick(record.albumId)}>
//                         <td style={{ width: '10%' }}>
//                             <img src={record.album_image} alt={record.music_name} />
//                         </td>
//                         <td style={{ width: '50%' }} className="my_table_padding my_table_body">
//                             <span>{record.music_name}</span>
//                         </td>
//                         <td style={{ width: '40%' }}>
//                             <span>{record.music_artist_name}</span>
//                         </td>
//                     </tr>
//                 ))}
//                 </tbody>
//             </table>
//         </div>
//     );
// };
//
// export default MyPageRecord;