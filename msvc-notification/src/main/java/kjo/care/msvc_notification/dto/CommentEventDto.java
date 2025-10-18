package kjo.care.msvc_notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentEventDto {
    private UUID commentId;
    private UUID blogId;
    private String blogAuthorId;
    private String commenterUserId;
    private String commenterUsername;
    private UUID parentCommentId;
    private String parentCommentAuthorId;
    private String sourceService;
}
