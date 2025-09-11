package kjo.care.msvc_emergency.client;

import kjo.care.msvc_emergency.dto.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "msvc-auth")
public interface UserClient {
    @GetMapping("/users/{userId}")
    UserInfoDto findUserById(@PathVariable("userId") String userId);

    @PostMapping("/users/batch")
    List<UserInfoDto> findUsersByIds(@RequestBody List<String> userIds);
}
