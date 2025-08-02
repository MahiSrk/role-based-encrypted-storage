package com.reks.service;

import com.reks.model.User;
import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

@Service
public class CryptoService {

    private final Pairing pairing;
    private final Field<?> gtField;
    private final Field<?> g1Field;
    private final Field<?> zrField;
    private final Element generator;
    private final SecureRandom secureRandom;
    
    @Value("${app.search.secretKey:}")
    private String searchSecretKey;

    public CryptoService() {
        this.pairing = PairingFactory.getPairing("a.properties");
        this.gtField = pairing.getGT();
        this.g1Field = pairing.getG1();
        this.zrField = pairing.getZr();
        this.generator = pairing.getG1().newRandomElement();
        this.secureRandom = new SecureRandom();
    }

    @PostConstruct
    public void init() {
        if (this.searchSecretKey == null || this.searchSecretKey.isEmpty()) {
            this.searchSecretKey = generateSecureSearchKey();
        }
    }

    /* File Encryption/Decryption Methods */
    
    public byte[] encryptFile(byte[] data, String password) throws CryptoException {
        try {
            Element secretKey = generateSecretKey();
            byte[] symmetricKey = deriveSymmetricKey(secretKey, password);
            
            // AES encryption
            SecretKey key = new SecretKeySpec(symmetricKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] encryptedData = cipher.doFinal(data);
            
            // Combine with JPBC elements
            Element publicKey = generator.duplicate().powZn(secretKey);
            byte[] publicKeyBytes = publicKey.toBytes();
            
            byte[] result = new byte[iv.length + publicKeyBytes.length + encryptedData.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(publicKeyBytes, 0, result, iv.length, publicKeyBytes.length);
            System.arraycopy(encryptedData, 0, result, iv.length + publicKeyBytes.length, encryptedData.length);
            
            return result;
        } catch (Exception e) {
            throw new CryptoException("File encryption failed", e);
        }
    }

    public byte[] decryptFile(byte[] encryptedData, String password) throws CryptoException {
        try {
            int ivLength = 16;
            int publicKeyLength = g1Field.getLengthInBytes();
            
            byte[] iv = new byte[ivLength];
            byte[] publicKeyBytes = new byte[publicKeyLength];
            byte[] aesEncryptedData = new byte[encryptedData.length - ivLength - publicKeyLength];
            
            System.arraycopy(encryptedData, 0, iv, 0, ivLength);
            System.arraycopy(encryptedData, ivLength, publicKeyBytes, 0, publicKeyLength);
            System.arraycopy(encryptedData, ivLength + publicKeyLength, aesEncryptedData, 0, aesEncryptedData.length);
            
            Element publicKey = g1Field.newElement();
            publicKey.setFromBytes(publicKeyBytes);
            
            byte[] symmetricKey = deriveSymmetricKey(publicKey, password);
            SecretKey key = new SecretKeySpec(symmetricKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            
            return cipher.doFinal(aesEncryptedData);
        } catch (Exception e) {
            throw new CryptoException("File decryption failed", e);
        }
    }

    /* Search Token Generation Methods */
    
    public String generateSearchToken(User user, String keyword) {
        Element userElement = zrField.newElement()
            .setFromHash(user.getUsername().getBytes(StandardCharsets.UTF_8), 0, user.getUsername().length());
        Element keywordElement = zrField.newElement()
            .setFromHash(keyword.getBytes(StandardCharsets.UTF_8), 0, keyword.length());
        
        Element token = pairing.pairing(generator, generator)
            .powZn(userElement.mul(keywordElement));
        
        return Base64.getEncoder().encodeToString(token.toBytes());
    }

    public String generateSearchToken(String data, String algorithm) throws CryptoException {
        try {
            switch (algorithm.toUpperCase()) {
                case "AES256":
                    return generateAesSearchToken(data);
                case "SHA256":
                    return generateSha256SearchToken(data);
                default:
                    return generateDefaultSearchToken(data);
            }
        } catch (Exception e) {
            throw new CryptoException("Search token generation failed", e);
        }
    }

    public String getSearchSecretKey() {
        if (this.searchSecretKey == null || this.searchSecretKey.isEmpty()) {
            throw new IllegalStateException("Search secret key is not configured");
        }
        return this.searchSecretKey;
    }

    /* Key Management Methods */
    
    public KeyPair generateKeyPair() throws CryptoException {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            return kpg.generateKeyPair();
        } catch (Exception e) {
            throw new CryptoException("Key pair generation failed", e);
        }
    }

    public String hashPassword(String password) throws CryptoException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new CryptoException("Password hashing failed", e);
        }
    }

    /* Helper Methods */
    
    private Element generateSecretKey() {
        return zrField.newRandomElement();
    }

    private byte[] deriveSymmetricKey(Element element, String password) {
        Element hashedPassword = zrField.newElement()
            .setFromHash(password.getBytes(StandardCharsets.UTF_8), 0, password.length());
        return pairing.pairing(element, generator)
            .powZn(hashedPassword)
            .toBytes();
    }

    private String generateSecureSearchKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 256 bits
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private String generateAesSearchToken(String data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(searchSecretKey.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private String generateSha256SearchToken(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    private String generateDefaultSearchToken(String data) {
        return Base64.getEncoder().encodeToString(
            pairing.pairing(generator, generator)
                .powZn(zrField.newElement().setFromHash(data.getBytes(StandardCharsets.UTF_8), 0, data.length()))
                .toBytes()
        );
    }

    public static class CryptoException extends Exception {
        public CryptoException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}