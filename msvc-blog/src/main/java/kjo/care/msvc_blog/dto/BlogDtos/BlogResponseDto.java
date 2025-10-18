package kjo.care.msvc_blog.dto.BlogDtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import kjo.care.msvc_blog.dto.CategoryDtos.CategoryResponseDto;
import kjo.care.msvc_blog.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class BlogResponseDto {
    private UUID id;
    private UserInfoDto author;
    private CategoryResponseDto category;
    private String title;
    private String content;
    private String video;
    private String image;
    private String state;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishedDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate modifiedDate;
}
