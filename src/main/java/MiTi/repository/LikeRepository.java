
package MiTi.repository;
import com.MiTi.entity.Like;
import com.MiTi.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, LikeId> {

    @Query("SELECT l FROM Like l WHERE l.user_id = :userId AND l.album_id = :albumId")
    Like findByUserIdAndAlbumId(@Param("userId") Integer userId, @Param("albumId") Integer albumId);
}
