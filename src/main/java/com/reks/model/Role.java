package com.reks.model;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Set;

@Data
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_role_id")
    private Role parentRole;

    // Helper method to check if role has parent
    public boolean hasParent() {
        return this.parentRole != null;
    }
}