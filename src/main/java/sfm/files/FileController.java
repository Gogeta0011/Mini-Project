package sfm.files;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import sfm.files.FileController.FileItem;

import org.springframework.http.MediaType;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService files;

    public FileController(FileService files) {
        this.files = files;
    }

    public record FileItem(Long id, String originalName, long sizeBytes, String createdAt) {}
    public record UploadResponse(Long id, String originalName) {}
    public record DownloadResponse(String url) {}

   @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadResponse upload(Authentication auth,
                                @RequestParam("file") MultipartFile file) {
        String username = auth.getName();
        var saved = files.upload(username, file);
        return new UploadResponse(saved.getId(), saved.getOriginalName());
    }

    @GetMapping
    public List<FileItem> list(Authentication auth) {
        String username = auth.getName();
        return files.listMine(username).stream()
                .map(f -> new FileItem(f.getId(), f.getOriginalName(), f.getSizeBytes(), f.getCreatedAt().toString()))
                .toList();
    }

    @GetMapping("/{id}/download")
    public DownloadResponse download(Authentication auth, @PathVariable Long id) {
        String username = auth.getName();
        return new DownloadResponse(files.downloadUrl(username, id));
    }

    @DeleteMapping("/{id}")
    public void delete(Authentication auth, @PathVariable Long id) {
        String username = auth.getName();
        files.delete(username, id);
    }
}
