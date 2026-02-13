package sfm.files;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private final FileAssetRepository repo;
    private final S3StorageService storage;

    public FileService(FileAssetRepository repo, S3StorageService storage) {
        this.repo = repo;
        this.storage = storage;
    }

    public FileAsset upload(String ownerUsername, MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");
        if (file.getSize() > 25 * 1024 * 1024) throw new IllegalArgumentException("Max file size is 25MB");

        String key = ownerUsername + "/" + UUID.randomUUID();

        try {
            storage.putObject(key, file.getBytes(), file.getContentType());
        } catch (Exception e) {
            throw new IllegalArgumentException("Upload failed");
        }

        FileAsset fa = new FileAsset();
        fa.setOwnerUsername(ownerUsername);
        fa.setOriginalName(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        fa.setObjectKey(key);
        fa.setSizeBytes(file.getSize());
        return repo.save(fa);
    }

    public List<FileAsset> listMine(String ownerUsername) {
        return repo.findByOwnerUsernameOrderByCreatedAtDesc(ownerUsername);
    }

    public String downloadUrl(String ownerUsername, Long id) {
        FileAsset fa = repo.findByIdAndOwnerUsername(id, ownerUsername)
                .orElseThrow(() -> new SecurityException("Not allowed"));
        return storage.presignedGetUrl(fa.getObjectKey());
    }

    public void delete(String ownerUsername, Long id) {
        FileAsset fa = repo.findByIdAndOwnerUsername(id, ownerUsername)
                .orElseThrow(() -> new SecurityException("Not allowed"));
        storage.deleteObject(fa.getObjectKey());
        repo.delete(fa);
    }
}
