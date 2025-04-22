package kjo.care.msvc_user.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/users")
    public String getAllUsers() {
        return "Hello users";
    }
}
