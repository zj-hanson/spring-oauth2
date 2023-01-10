package com.lightshell.oauth2.config;

import com.lightshell.oauth2.entity.User;
import com.lightshell.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author kevindong
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("账号不存在");
        }
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(),
                AuthorityUtils.createAuthorityList("admin","USER","ROLE_system"));
    }
}
