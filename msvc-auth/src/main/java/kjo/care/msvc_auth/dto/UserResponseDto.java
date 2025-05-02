package kjo.care.msvc_auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
@RequiredArgsConstructor
@Builder
@Data
public class UserResponseDto {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
}
