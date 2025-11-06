package com.isima.tp.controllers;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Authentication failed. Please try again.");
        }
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getPreferredUsername());
            model.addAttribute("email", principal.getEmail());
            model.addAttribute("firstName", principal.getGivenName());
            model.addAttribute("lastName", principal.getFamilyName());
            model.addAttribute("name", principal.getFullName());
            model.addAttribute("roles", principal.getAuthorities());
            model.addAttribute("claims", principal.getClaims());
        }
        return "home";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userDashboard(Model model, @AuthenticationPrincipal OidcUser principal) {
        model.addAttribute("username", principal.getPreferredUsername());
        model.addAttribute("email", principal.getEmail());
        return "user";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPanel(Model model, @AuthenticationPrincipal OidcUser principal) {
        model.addAttribute("username", principal.getPreferredUsername());
        model.addAttribute("roles", principal.getAuthorities());
        return "admin";
    }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal OidcUser principal) {
        model.addAttribute("username", principal.getPreferredUsername());
        model.addAttribute("email", principal.getEmail());
        model.addAttribute("name", principal.getFullName());
        model.addAttribute("attributes", principal.getAttributes());
        return "profile";
    }
}