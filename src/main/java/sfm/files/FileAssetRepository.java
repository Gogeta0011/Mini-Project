package sfm.files;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FileAssetRepository extends JpaRepository<FileAsset, Long> {
    List<FileAsset> findByOwnerUsernameOrderByCreatedAtDesc(String ownerUsername);
    Optional<FileAsset> findByIdAndOwnerUsername(Long id, String ownerUsername);
}
