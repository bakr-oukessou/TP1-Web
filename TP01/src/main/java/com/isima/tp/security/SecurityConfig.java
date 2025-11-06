package com.isima.tp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // For @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/public/**", "/css/**", "/js/**", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/?error=true")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    /**
     * Maps Keycloak roles to Spring Security authorities.
     * This bean is automatically picked up by Spring Security.
     */
    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                // Keep existing authorities
                mappedAuthorities.add(authority);

                // Extract Keycloak roles from OidcUserAuthority
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    Map<String, Object> userInfo = oidcUserAuthority.getUserInfo().getClaims();

                    // Extract realm roles
                    Map<String, Object> realmAccess = (Map<String, Object>) userInfo.get("realm_access");
                    if (realmAccess != null && realmAccess.containsKey("roles")) {
                        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                        mappedAuthorities.addAll(roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                                .collect(Collectors.toList()));
                    }

                    // Extract client roles (optional)
                    Map<String, Object> resourceAccess = (Map<String, Object>) userInfo.get("resource_access");
                    if (resourceAccess != null) {
                        Map<String, Object> clientResource = (Map<String, Object>) resourceAccess.get("spring-app");
                        if (clientResource != null && clientResource.containsKey("roles")) {
                            Collection<String> clientRoles = (Collection<String>) clientResource.get("roles");
                            mappedAuthorities.addAll(clientRoles.stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                                    .collect(Collectors.toList()));
                        }
                    }
                }
            });

            return mappedAuthorities;
        };
    }
}
