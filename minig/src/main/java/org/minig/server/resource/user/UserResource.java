package org.minig.server.resource.user;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * @author Kamill Sokol
 */
@RestController
public class UserResource {

    @RequestMapping("1/me")
    public User me(Authentication authentication) {
        User user = new User();
        user.username = authentication.getPrincipal().toString();
        return user;
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    static class User {
        String username;
    }

}
