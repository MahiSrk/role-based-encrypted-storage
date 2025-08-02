package com.reks.service;

import com.reks.exception.RoleAssignmentException;
import com.reks.exception.RoleHierarchyException;
import com.reks.model.Role;
import com.reks.model.User;
import com.reks.repository.RoleRepository;
import com.reks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;

    public Role createRole(Role role) {
        if (role.hasParent() && !roleRepository.existsById(role.getParentRole().getId())) {
            throw new RoleHierarchyException("Parent role with ID " + role.getParentRole().getId() + " does not exist");
        }
        return roleRepository.save(role);
    }

    public void assignRoleToUser(Long roleId, Long userId) {
        try {
            Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleAssignmentException("Role not found with ID: " + roleId));
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RoleAssignmentException("User not found with ID: " + userId));
            
            // Add the role to the user's roles
            if (user.getRoles() == null) {
                user.setRoles(new HashSet<>());
            }
            user.getRoles().add(role);
            
            userRepository.save(user);
        } catch (Exception e) {
            throw new RoleAssignmentException("Failed to assign role: " + e.getMessage(), e);
        }
    }

    public Set<Role> getAccessibleRoles(Long roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RoleHierarchyException("Role not found with ID: " + roleId));
        return getChildRolesRecursive(role);
    }

    private Set<Role> getChildRolesRecursive(Role parent) {
        Set<Role> accessibleRoles = new HashSet<>();
        accessibleRoles.add(parent);
        
        roleRepository.findByParentRoleId(parent.getId())
            .forEach(child -> accessibleRoles.addAll(getChildRolesRecursive(child)));
        
        return accessibleRoles;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}