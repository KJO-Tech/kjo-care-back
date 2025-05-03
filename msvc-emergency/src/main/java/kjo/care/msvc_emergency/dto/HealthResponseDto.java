package kjo.care.msvc_emergency.dto;

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
public class HealthResponseDto {
    private Long id;
    private UserInfoDto user;
    private String name;
    private String address;
    private String phone;
    private Double latitude;
    private Double longitude;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate modifiedDate;
}
