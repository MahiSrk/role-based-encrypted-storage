package com.reks.controller;

import com.reks.model.Role;
import com.reks.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public Role createRole(@RequestBody Role role) {
        return roleService.createRole(role);
    }

    @PostMapping("/{roleId}/assign/{userId}")
    public void assignRole(@PathVariable Long roleId, @PathVariable Long userId) {
        roleService.assignRoleToUser(roleId, userId);  // Changed to match service method name
    }

    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }
}