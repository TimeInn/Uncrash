package net.uncrash.authorization.basic.service;

import lombok.RequiredArgsConstructor;
import net.uncrash.authorization.*;
import net.uncrash.authorization.api.web.GeneratedToken;
import net.uncrash.authorization.basic.domain.DefaultRole;
import net.uncrash.authorization.basic.domain.DefaultUser;
import net.uncrash.authorization.basic.domain.PermissionRole;
import net.uncrash.authorization.basic.domain.RolePermission;
import net.uncrash.authorization.basic.repository.PermissionRepository;
import net.uncrash.authorization.basic.repository.PermissionRoleRepository;
import net.uncrash.authorization.basic.repository.RoleRepository;
import net.uncrash.authorization.basic.repository.UserRepository;
import net.uncrash.core.exception.NotFoundException;
import net.uncrash.core.utils.PasswordBuilder;
import net.uncrash.core.utils.id.IDGenerator;
import net.uncrash.data.api.AbstractJpaService;
import net.uncrash.data.dict.DataStatus;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.uncrash.core.utils.PasswordBuilder.check;

/**
 * @author Sendya
 */
@Service
@RequiredArgsConstructor
public class DefaultUserService extends AbstractJpaService<DefaultUser, String> implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionRoleRepository permissionRoleRepository;

    @Override
    public JpaRepository<DefaultUser, String> getRepository() {
        return this.userRepository;
    }


    @Override
    public User selectByUserNameAndPassword(String username, String password) {
        DefaultUser user = userRepository.findOne(
            Example.of(DefaultUser.builder().username(username).build())
        ).orElseThrow(() -> new NotFoundException("账户或密码错误"));

        boolean checked = check(password, user.getSalt(), user.getPassword());

        if (!checked) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus().equals(DataStatus.STATUS_DISABLED)) {
            throw new RuntimeException("用户被禁用");
        }

        DefaultRole role = roleRepository.findById(user.getRole())
               .orElse(DefaultRole.builder().build());
            // .orElseThrow(() -> new AuthorizeException("账户未分配权限，无法登陆"));

//        AuthenticationUser authentication = new AuthenticationUser();
//        authentication.setUser(user);
//        authentication.setRole(role);

        if (role != null) {
            List<RolePermission> permissions = roleRepository.findPermissionByRoleId(role.getId());
            role.setPermissions(permissions);
        }

        List<DefaultRole> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);
        // save to authentication manager
        // GeneratedToken generatedToken = AuthenticationHolder.save(authentication);
        return user;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public User register(String email, String username, String password) {
        final String salt = PasswordBuilder.createSalt();
        DefaultUser user = DefaultUser.builder()
            .id(IDGenerator.UUID2.generate())
            .email(email)
            .username(username)
            .password(PasswordBuilder.builder(password, salt))
            .salt(salt)
            .status(DataStatus.STATUS_ENABLED)
            .build();

        boolean checked = true;
        if (checked) {
            // required email verify
            user.setStatus(DataStatus.STATUS_LOCKED);
        }
        return userRepository.save(user);
    }
}
