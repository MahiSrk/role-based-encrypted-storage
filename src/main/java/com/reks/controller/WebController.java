package com.reks.controller;

import com.reks.exception.FileStorageException;
import com.reks.model.EncryptedFile;
import com.reks.model.Role;
import com.reks.service.FileService;
import com.reks.service.RoleService;
import com.reks.service.SearchService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/")
public class WebController {

    private final FileService fileService;
    private final RoleService roleService;
    private final SearchService searchService;

    public WebController(FileService fileService, RoleService roleService, SearchService searchService) {
        this.fileService = fileService;
        this.roleService = roleService;
        this.searchService = searchService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) throws IOException, FileStorageException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Ensure the user is authenticated
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String username = auth.getName();  // This gets the username (e.g., from JWT)

        List<EncryptedFile> files = fileService.getRecentFiles(username);
        List<Role> roles = roleService.getAllRoles();

        model.addAttribute("files", files);
        model.addAttribute("roles", roles);

        return "dashboard";
    }
    
    @GetMapping("/files")
    public String files(Model model) throws IOException, FileStorageException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String username = auth.getName();

        List<EncryptedFile> files = fileService.getRecentFiles(username);
        List<Role> roles = roleService.getAllRoles();

        model.addAttribute("files", files);
        model.addAttribute("roles", roles);

        return "files";
    }



    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String username = auth.getName();

        if (keyword != null && !keyword.isEmpty()) {
            List<EncryptedFile> results = searchService.secureSearch(keyword, username);
            model.addAttribute("results", results);
        }

        return "search";
    }


}