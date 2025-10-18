package kjo.care.msvc_blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewBlogEventDto {
    private String recipientId;
    private UUID blogId;
    private String blogTitle;
    private String authorId;
    private String authorUsername;
    private String sourceService;
}
