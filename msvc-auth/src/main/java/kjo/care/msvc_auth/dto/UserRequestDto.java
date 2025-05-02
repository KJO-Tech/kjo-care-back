package kjo.care.msvc_auth.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@Builder
public class UserRequestDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
