package org.minig.server.resource.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static org.minig.MinigConstants.API;

@RestController
public class UserResource {

    @RequestMapping(API + "/me")
    public User me(Authentication authentication) {
        var user = new User();
        user.username = authentication.getPrincipal().toString();
        return user;
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    static class User {
        String username;
    }
}
