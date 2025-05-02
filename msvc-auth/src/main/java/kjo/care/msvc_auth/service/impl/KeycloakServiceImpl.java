package kjo.care.msvc_auth.service.impl;


import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import kjo.care.msvc_auth.dto.UserDTO;
import kjo.care.msvc_auth.dto.UserInfoDto;
import kjo.care.msvc_auth.dto.UserRequestDto;
import kjo.care.msvc_auth.dto.UserResponseDto;
import kjo.care.msvc_auth.service.IKeycloakService;
import kjo.care.msvc_auth.util.KeycloakProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KeycloakServiceImpl implements IKeycloakService {

    @Autowired
    private KeycloakProvider keycloakProvider;

    @Override
    public List<UserRepresentation> findAllUser() {
        return keycloakProvider.getRealmResource()
                .users()
                .list();
    }

    @Override
    public List<UserResponseDto> findAllUsersRoles() {
        List<UserRepresentation> users = keycloakProvider.getRealmResource().users().list();
        List<UserResponseDto> usersWithRoles = new ArrayList<>();

        for (UserRepresentation user : users) {
            List<RoleRepresentation> roleRepresentations = keycloakProvider
                    .getRealmResource()
                    .users()
                    .get(user.getId())
                    .roles()
                    .realmLevel()
                    .listEffective();

            List<String> roles = roleRepresentations.stream()
                    .map(RoleRepresentation::getName)
                    .collect(Collectors.toList());

            usersWithRoles.add(new UserResponseDto(user.getId(),user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.isEnabled(), user.getCreatedTimestamp(), roles));
        }
        return usersWithRoles;
    }

    @Override
    public List<UserRepresentation> findAllUserByUsername(String username) {
        return keycloakProvider.getRealmResource()
                .users()
                .searchByUsername(username, true);
    }

    @Override
    public UserInfoDto findUserById(String userId) {
        UserResource userResource = keycloakProvider.getRealmResource()
                .users()
                .get(userId);

        return UserInfoDto.builder()
                .id(userResource.toRepresentation().getId())
                .username(userResource.toRepresentation().getUsername())
                .firstName(userResource.toRepresentation().getFirstName())
                .lastName(userResource.toRepresentation().getLastName())
                .build();
    }

    @Override
    public String createUser(@NonNull UserDTO userDTO) {

        int status = 0;
        UsersResource usersResource = keycloakProvider.getUserResource();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userDTO.getFirstName());
        userRepresentation.setLastName(userDTO.getLastName());
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.setUsername(userDTO.getUsername());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);

        Response response = usersResource.create(userRepresentation);
        status = response.getStatus();

        if(status == 201){
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);

            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(OAuth2Constants.PASSWORD);
            credentialRepresentation.setValue(userDTO.getPassword());

            usersResource.get(userId).resetPassword(credentialRepresentation);

            RealmResource realmResource = keycloakProvider.getRealmResource();
            List<RoleRepresentation> rolesRepresentation = null;

            if(userDTO.getRoles() == null || userDTO.getRoles().isEmpty()){
                rolesRepresentation = List.of(realmResource.roles().get("user").toRepresentation());
            } else {
                rolesRepresentation = realmResource.roles()
                        .list()
                        .stream()
                        .filter(role -> userDTO.getRoles()
                                .stream()
                                .anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName())))
                        .toList();
            }

            realmResource.users().get(userId).roles().realmLevel().add(rolesRepresentation);

            return "User created successfully!!";
        } else if (status == 409) {
            log.error("User already exists");
            return "User already exists";
        } else {
            log.error("Error creating user");
            return "Error creating user";
        }
    }

    @Override
    public void deleteUser(String userId) {
        keycloakProvider.getUserResource()
                .get(userId)
                .remove();
    }

    @Override
    public void updateUser(String userId, UserRequestDto userDTO) {

        List<UserRepresentation> existingUsers = keycloakProvider.getRealmResource()
                .users()
                .search(userDTO.getUsername(), true);

        if (!existingUsers.isEmpty() && !existingUsers.get(0).getId().equals(userId)) {
            throw new BadRequestException("El username ya está en uso");
        }

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);
        userRepresentation.setFirstName(userDTO.getFirstName());
        userRepresentation.setLastName(userDTO.getLastName());
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.setUsername(userDTO.getUsername());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);

        UserResource userResource = keycloakProvider.getUserResource().get(userId);
        UserRepresentation userRep = userResource.toRepresentation();
        userRep.setUsername(userDTO.getUsername());
        userResource.update(userRep);
    }
}
