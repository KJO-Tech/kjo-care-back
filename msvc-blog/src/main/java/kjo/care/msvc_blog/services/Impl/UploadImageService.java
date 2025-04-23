package kjo.care.msvc_blog.services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import kjo.care.msvc_blog.services.IUploadImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UploadImageService implements IUploadImageService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folder, String resourceType) {
        if(file.isEmpty()){
            throw new IllegalArgumentException("El archivo esta vacío");
        }
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "public_id", extractPublicId(file.getOriginalFilename()),
                            "resource_type", resourceType
                    )
            );
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Error al subir el archivo", e);
        }
    }

    public String DeleteImage(String imageUrl, String resourceType) {
        String publicId = extractPublicIdFromUrl(imageUrl);
        try {
            Map<?, ?> deleteResult = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", resourceType)
            );
            if (deleteResult.get("result").equals("ok")) {
                return "Recurso eliminado: " + publicId;
            }
            throw new RuntimeException("Error al eliminar: " + deleteResult.get("result"));
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar el recurso", e);
        }
    }

    private String extractPublicId(String fileName) {
        return fileName.split("\\.")[0];
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            String path = uri.getPath();

            int uploadIndex = path.indexOf("/upload/") + "/upload/".length();
            String afterUpload = path.substring(uploadIndex);

            String[] parts = afterUpload.split("/");
            int startIndex = parts[0].matches("v\\d+") ? 1 : 0;

            List<String> relevantParts = new ArrayList<>();
            for (int i = startIndex; i < parts.length; i++) {
                String part = parts[i];
                if (!part.isEmpty()) {
                    relevantParts.add(part);
                }
            }

            String publicIdWithExtension = String.join("/", relevantParts);
            return publicIdWithExtension.split("\\.")[0];
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL inválida: " + imageUrl, e);
        }
    }

}
