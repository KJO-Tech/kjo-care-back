package kjo.care.msvc_notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogRejectedEventDto {
    private UUID blogId;
    private String blogTitle;
    private String authorId;
    private String sourceService;
}
