package kjo.care.msvc_blog.dto;

import kjo.care.msvc_blog.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionEventDto {
    private UUID reactionId;
    private UUID blogId;
    private String blogAuthorId;
    private String reactorUserId;
    private String reactorUsername;
    private ReactionType type;
    private LocalDate reactionDate;
    private String sourceService;
}
