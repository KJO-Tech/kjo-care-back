package kjo.care.msvc_blog.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogController {

    @GetMapping("/blog")
    public String getBlog() {
        return "Hello Blog";
    }
}
