package sfm.files;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "file_assets")
public class FileAsset {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String ownerUsername;

    @Column(nullable=false, length=255)
    private String originalName;

    @Column(nullable=false, length=255, unique=true)
    private String objectKey; // S3 key

    @Column(nullable=false)
    private long sizeBytes;

    @Column(nullable=false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public String getOwnerUsername() { return ownerUsername; }
    public String getOriginalName() { return originalName; }
    public String getObjectKey() { return objectKey; }
    public long getSizeBytes() { return sizeBytes; }
    public Instant getCreatedAt() { return createdAt; }

    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }
}
