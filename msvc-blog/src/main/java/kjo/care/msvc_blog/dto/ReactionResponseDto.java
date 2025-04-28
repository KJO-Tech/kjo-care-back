package kjo.care.msvc_blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionResponseDto {
    private Long blogId;
    private UserInfoDto userId;
    private String type;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reactionDate;
}
