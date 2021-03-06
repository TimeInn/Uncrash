package net.uncrash.authorization.api.web;


import net.uncrash.authorization.Permission;
import net.uncrash.authorization.Role;
import net.uncrash.authorization.User;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Sendya
 */
public interface Authentication extends Serializable {

    String getToken();

    User getUser();

    Role getRole();

    List<Permission> getPermissions();

    default Optional<Role> getRole(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    default Optional<Permission> getPermission(String id) {
        if (id == null) {
            Optional.empty();
        }
        return getPermissions().stream()
            .filter(permission -> permission.getId().equals(id))
            .findAny();
    }

    default boolean hasPermission(String permissionId, String... actions) {
        return getPermission(permissionId)
            .filter(permission -> actions.length == 0 ||
                permission.getActions().containsAll(Arrays.asList(actions)))
            .isPresent();
    }

    default boolean hasRole(String roleId) {
        return getRole().getId().equals(roleId);
    }

}
