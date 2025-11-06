package com.isima.tp.security;

import com.isima.tp.Repositories.UserRepository;
import com.isima.tp.models.User;


import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Get user info from Keycloak
        OidcUser oidcUser = super.loadUser(userRequest);

        // 2. Extract email and username
        String email = oidcUser.getEmail();
        String pseudo = oidcUser.getPreferredUsername();

        // 3. Find existing user OR create new one
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setPseudo(pseudo);
                    return userRepository.save(newUser);
                });

        // 4. Update pseudo if changed
        if (!user.getPseudo().equals(pseudo)) {
            user.setPseudo(pseudo);
            userRepository.save(user);
        }

        // 5. Return wrapped user
        return new CustomOidcUser(oidcUser, user);
    }
}
