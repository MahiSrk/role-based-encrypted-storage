package com.reks.service;

import com.reks.model.EncryptedFile;
import com.reks.model.Role;
import com.reks.model.User;
import com.reks.repository.EncryptedFileRepository;
import com.reks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SearchService {

    private final EncryptedFileRepository encryptedFileRepository;
    private final UserRepository userRepository;
    private final CryptoService cryptoService;

    @Autowired
    public SearchService(EncryptedFileRepository encryptedFileRepository,
                         UserRepository userRepository,
                         CryptoService cryptoService) {
        this.encryptedFileRepository = encryptedFileRepository;
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
    }

    public List<EncryptedFile> search(String keyword, String username, String strategy) {
        try {
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String searchToken = generateSearchToken(keyword, strategy);
            String secretKey = cryptoService.getSearchSecretKey();

            List<Long> roleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());

            return encryptedFileRepository.searchByTokenAndRoles(searchToken, roleIds, secretKey);
        } catch (Exception e) {
            throw new SearchException("Search failed", e);
        }
    }

    public List<EncryptedFile> advancedSearch(String query, Long roleId, String algorithm, String username) {
        try {
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String searchToken = (algorithm != null)
                ? cryptoService.generateSearchToken(query, algorithm)
                : generateSearchToken(query, "EXACT");

            String secretKey = cryptoService.getSearchSecretKey();
            List<Long> roleIds;

            if (roleId != null) {
                roleIds = List.of(roleId); // Search by one specific role
            } else {
                roleIds = user.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toList());
            }

            return encryptedFileRepository.searchByTokenAndRoles(searchToken, roleIds, secretKey);
        } catch (Exception e) {
            throw new SearchException("Advanced search failed", e);
        }
    }

    public List<EncryptedFile> secureSearch(String keyword, String username) {
        try {
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String searchToken = cryptoService.generateSearchToken(user, keyword);
            String secretKey = cryptoService.getSearchSecretKey();

            List<Long> roleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());

            return encryptedFileRepository.searchByTokenAndRoles(searchToken, roleIds, secretKey);
        } catch (Exception e) {
            throw new SearchException("Secure search failed", e);
        }
    }

    private String generateSearchToken(String keyword, String strategy) {
        return switch (strategy.toUpperCase()) {
            case "FUZZY" -> "FUZZY:" + keyword.toLowerCase();
            default -> "EXACT:" + keyword;
        };
    }

    public static class SearchException extends RuntimeException {
        public SearchException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
