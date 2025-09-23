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

import java.util.*;
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

            usersWithRoles.add(new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.isEnabled(), user.getCreatedTimestamp(), roles));
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
        try {
            UserResource userResource = keycloakProvider.getRealmResource()
                    .users()
                    .get(userId);

            return UserInfoDto.builder()
                    .id(userResource.toRepresentation().getId())
                    .username(userResource.toRepresentation().getUsername())
                    .firstName(userResource.toRepresentation().getFirstName())
                    .lastName(userResource.toRepresentation().getLastName())
                    .build();

        } catch (Exception e) {
            log.warn("No se pudo obtener el usuario con ID: {}", userId, e);
            return null;
        }
    }

    @Override
    public List<UserInfoDto> findUsersByIds(List<String> userIds) {
        return userIds.stream()
                .map(this::findUserById)
                .filter(Objects::nonNull)
                .toList();
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

        if (status == 201) {
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);

            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(OAuth2Constants.PASSWORD);
            credentialRepresentation.setValue(userDTO.getPassword());

            usersResource.get(userId).resetPassword(credentialRepresentation);

            RealmResource realmResource = keycloakProvider.getRealmResource();
            List<RoleRepresentation> rolesRepresentation = null;

            if (userDTO.getRoles() == null || userDTO.getRoles().isEmpty()) {
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
    public Long countUsers() {
        log.info("Contando total de usuarios registrados");
        List<UserRepresentation> users = keycloakProvider.getRealmResource()
                .users()
                .list();
        return (long) users.size();
    }

    @Override
    public Long countUsersByPeriod(int months) {
        log.info("Contando usuarios registrados en los últimos {} meses", months);
        List<UserRepresentation> allUsers = keycloakProvider.getRealmResource()
                .users()
                .list();

        long startTime = System.currentTimeMillis() - (long) months * 30 * 24 * 60 * 60 * 1000;

        return allUsers.stream()
                .filter(user -> {
                    Long createdTimestamp = user.getCreatedTimestamp();
                    return createdTimestamp != null && createdTimestamp >= startTime;
                })
                .count();
    }

    @Override
    public Long countUsersPreviousMonth() {
        log.info("Contando usuarios registrados en el mes anterior");
        List<UserRepresentation> allUsers = keycloakProvider.getRealmResource()
                .users()
                .list();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startOfPreviousMonth = cal.getTimeInMillis();

        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        long endOfPreviousMonth = cal.getTimeInMillis();

        return allUsers.stream()
                .filter(user -> {
                    Long createdTimestamp = user.getCreatedTimestamp();
                    return createdTimestamp != null &&
                            createdTimestamp >= startOfPreviousMonth &&
                            createdTimestamp <= endOfPreviousMonth;
                })
                .count();
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

        UserResource userResource = keycloakProvider.getUserResource().get(userId);
        UserRepresentation userRep = userResource.toRepresentation();

        userRep.setUsername(userDTO.getUsername());
        userRep.setEmail(userDTO.getEmail());
        userRep.setFirstName(userDTO.getFirstName());
        userRep.setLastName(userDTO.getLastName());
        userRep.setEmailVerified(true);

        userResource.update(userRep);

        if (userDTO.getRoles() != null) {
            RealmResource realmResource = keycloakProvider.getRealmResource();

            List<RoleRepresentation> allRoles = realmResource.roles().list();

            List<RoleRepresentation> currentRoles = userResource.roles().realmLevel().listAll();

            Set<String> targetRoles = new HashSet<>(userDTO.getRoles());
            Set<String> currentRoleNames = currentRoles.stream()
                    .map(RoleRepresentation::getName)
                    .collect(Collectors.toSet());

            List<RoleRepresentation> rolesToRemove = allRoles.stream()
                    .filter(role -> currentRoleNames.contains(role.getName())
                            && !targetRoles.contains(role.getName()))
                    .collect(Collectors.toList());

            List<RoleRepresentation> rolesToAdd = allRoles.stream()
                    .filter(role -> targetRoles.contains(role.getName())
                            && !currentRoleNames.contains(role.getName()))
                    .collect(Collectors.toList());

            if (!rolesToRemove.isEmpty()) {
                userResource.roles().realmLevel().remove(rolesToRemove);
            }
            if (!rolesToAdd.isEmpty()) {
                userResource.roles().realmLevel().add(rolesToAdd);
            }
        }
    }

}
