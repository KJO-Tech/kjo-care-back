package kjo.care.msvc_emergency.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyResponseDto {
    private UUID id;
    private UserInfoDto user;
    private String name;
    private String description;
    private String resourceUrl;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate modifiedDate;
    private List<String> contacts;
    private List<String> links;
    private String status;
    private int accessCount;
}
