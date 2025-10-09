package kjo.care.msvc_blog.dto.ReactionDtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import kjo.care.msvc_blog.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionResponseDto {
    private UUID blogId;
    private UserInfoDto userId;
    private String type;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reactionDate;
}
