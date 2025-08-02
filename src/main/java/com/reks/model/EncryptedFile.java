package com.reks.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class EncryptedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    
    @Lob
    private byte[] encryptedData;
    
    @Lob
    private byte[] encryptedIndex;
    
    @ManyToOne
    private User owner;
    
    @ManyToOne
    private Role accessRole;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}