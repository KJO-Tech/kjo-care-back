package kjo.care.msvc_emergency.services;

import org.springframework.web.multipart.MultipartFile;

public interface IUploadImageService {
    String uploadFile(MultipartFile file, String folder, String resourceType);
    String DeleteImage(String image, String resourceType);
}
