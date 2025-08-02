package com.reks.controller;

import com.reks.model.EncryptedFile;
import com.reks.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public ResponseEntity<List<EncryptedFile>> secureSearch(
            @RequestParam String keyword,
            @AuthenticationPrincipal User user) {
        try {
            List<EncryptedFile> results = searchService.secureSearch(keyword, user.getUsername());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/advanced")
    public ResponseEntity<List<EncryptedFile>> advancedSearch(
            @RequestParam String query,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) String algorithm,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(
                searchService.advancedSearch(query, roleId, algorithm, user.getUsername())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
