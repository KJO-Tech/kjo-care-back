package kjo.care.msvc_emergency.client;

import kjo.care.msvc_emergency.dto.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-auth")
public interface UserClient {
    @GetMapping("/users/{userId}")
    UserInfoDto findUserById(@PathVariable("userId") String userId);
}
