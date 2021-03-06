package net.uncrash.authorization.simple.service;

import lombok.RequiredArgsConstructor;
import net.uncrash.authorization.simple.dao.UserDao;
import net.uncrash.authorization.simple.domain.User;
import net.uncrash.authorization.simple.entity.JwtUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("defaultUserDetailsService")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userDao.findByUsername(s);
        return JwtUser.builder()
            .id(user.getId())
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.singleton(new SimpleGrantedAuthority("role")))
            .build();
    }
}
