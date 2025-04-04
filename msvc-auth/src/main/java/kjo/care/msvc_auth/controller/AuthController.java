package kjo.care.msvc_auth.controller;


import kjo.care.msvc_auth.dto.UserDTO;
import kjo.care.msvc_auth.service.IKeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class AuthController {

    @Autowired
    private IKeycloakService keycloakService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<?> findAllUser(){
        return ResponseEntity.ok(keycloakService.findAllUser());
    }

    @GetMapping("search/{username}")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<?> findAllUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(keycloakService.findAllUserByUsername(username));
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) throws URISyntaxException {
        String response = keycloakService.createUser(userDTO);
        return ResponseEntity.created(new URI("/keycloak/user/register")).body(response);
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasRole('user_client_role') or hasRole('admin_client_role')")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UserDTO userDTO){
        keycloakService.updateUser(userId, userDTO);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('user_client_role') or hasRole('admin_client_role')")
    public ResponseEntity<?> deleteUser(@PathVariable String userId){
        keycloakService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
