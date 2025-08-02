package com.reks.service;

import com.reks.exception.FileStorageException;
import com.reks.model.EncryptedFile;
import com.reks.model.Role;
import com.reks.model.User;
import com.reks.repository.EncryptedFileRepository;
import com.reks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileService {

    @Autowired
    private EncryptedFileRepository fileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CryptoService cryptoService;

    public EncryptedFile uploadFile(MultipartFile file, Role accessRole, User owner) throws FileStorageException {
        try {
            EncryptedFile encryptedFile = new EncryptedFile();
            encryptedFile.setFilename(file.getOriginalFilename());
            encryptedFile.setOwner(owner);
            encryptedFile.setAccessRole(accessRole);
            
            String encryptionKey = "user-key-" + owner.getId();
            byte[] encryptedData = cryptoService.encryptFile(file.getBytes(), encryptionKey);
            encryptedFile.setEncryptedData(encryptedData);
            
            String searchToken = cryptoService.generateSearchToken(owner, file.getOriginalFilename());
            encryptedFile.setEncryptedIndex(searchToken.getBytes());
            
            return fileRepository.save(encryptedFile);
        } catch (Exception e) {
            throw new FileStorageException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    public List<EncryptedFile> searchFiles(String keyword, User user) throws FileStorageException {
        try {
            String searchToken = cryptoService.generateSearchToken(user, keyword);
            String secretKey = cryptoService.getSearchSecretKey();

            return fileRepository.searchByTokenAndOwner(
                searchToken,
                user.getId(),
                secretKey
            );
        } catch (Exception e) {
            throw new FileStorageException("Search failed: " + e.getMessage(), e);
        }
    }

    public List<EncryptedFile> getRecentFiles(String username) throws FileStorageException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new FileStorageException("User not found: " + username));
        
        return fileRepository.findTop10ByOwnerOrderByCreatedAtDesc(user);
    }
}