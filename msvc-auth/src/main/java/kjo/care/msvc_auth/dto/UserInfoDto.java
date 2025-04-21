package kjo.care.msvc_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
}
