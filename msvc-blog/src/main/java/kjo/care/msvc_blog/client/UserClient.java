package kjo.care.msvc_blog.client;

import kjo.care.msvc_blog.dto.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "msvc-auth", path = "/users")
public interface UserClient {
    @GetMapping("/{userId}")
    UserInfoDto findUserById(@PathVariable("userId") String userId);

    @PostMapping("/batch")
    List<UserInfoDto> findUsersByIds(@RequestBody List<String> userIds);

    @GetMapping("/role/{roleName}")
    List<UserInfoDto> findUsersByRole(@PathVariable("roleName") String roleName);
}
