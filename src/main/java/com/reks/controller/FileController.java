package com.reks.controller;

import com.reks.exception.FileStorageException;
import com.reks.model.EncryptedFile;
import com.reks.model.Role;
import com.reks.model.User;
import com.reks.service.FileService;
import com.reks.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;
    
    @Autowired
    private RoleService roleService;

    @PostMapping("/upload")
    public EncryptedFile uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long roleId,
            @AuthenticationPrincipal User user) throws IOException, FileStorageException {
        Role accessRole = roleService.getAllRoles().stream()
                .filter(role -> role.getId().equals(roleId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID"));
        
        return fileService.uploadFile(file, accessRole, user);
    }

    @GetMapping("/search")
    public List<EncryptedFile> searchFiles(
            @RequestParam String keyword,
            @AuthenticationPrincipal User user) throws IOException, FileStorageException {
        return fileService.searchFiles(keyword, user);
    }
}