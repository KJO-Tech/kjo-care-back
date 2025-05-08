package kjo.care.msvc_auth.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kjo.care.msvc_auth.dto.UserDTO;
import kjo.care.msvc_auth.dto.UserRequestDto;
import kjo.care.msvc_auth.service.IKeycloakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Log4j2
@SecurityRequirement(name = "securityToken")
@Tag(name = "Auth", description = "Operations for Auth")
public class AuthController {

    @Autowired
    private IKeycloakService keycloakService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<?> findAllUser() {
        return ResponseEntity.ok(keycloakService.findAllUser());
    }

    @GetMapping("/listAll")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<?> findAllUserRoles() {
        return ResponseEntity.ok(keycloakService.findAllUsersRoles());
    }

    @GetMapping("search/{username}")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<?> findAllUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(keycloakService.findAllUserByUsername(username));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> findUserById(@PathVariable String userId) {
        return ResponseEntity.ok(keycloakService.findUserById(userId));
    }

    @PostMapping("/batch")
    public ResponseEntity<?> findUsersByIds(@RequestBody List<String> userId) {
        return ResponseEntity.ok(keycloakService.findUsersByIds(userId));
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) throws URISyntaxException {
        String message = keycloakService.createUser(userDTO);

        var response = new HashMap<String, String>();
        response.put("message", message);
        return ResponseEntity.created(new URI("/keycloak/user/register")).body(response);
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasRole('user_client_role') or hasRole('admin_client_role')")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UserRequestDto userDTO) {
        keycloakService.updateUser(userId, userDTO);

        var response = new HashMap<String, String>();
        response.put("message", "User updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('user_client_role') or hasRole('admin_client_role')")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        keycloakService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
