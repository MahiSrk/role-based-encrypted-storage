package com.reks.repository;

import com.reks.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Basic role operations
    Role findByName(String name);
    boolean existsByName(String name);

    // ✅ Fixed field name to match entity: parentRole
    @Query("SELECT r FROM Role r WHERE r.parentRole.id = :parentId")
    List<Role> findByParentRoleId(@Param("parentId") Long parentId);

    // ✅ Corrected from 'parent' to 'parentRole'
    @Query("SELECT r FROM Role r WHERE r.parentRole.id = :parentId")
    List<Role> findDirectChildren(@Param("parentId") Long parentId);

    @Query("SELECT r FROM Role r WHERE r.parentRole IS NULL")
    List<Role> findRootRoles();

    // ✅ Corrected 'parent' to 'parentRole'
    @Query("SELECT r FROM Role r WHERE r IN :roles OR r.parentRole IN :roles")
    Set<Role> findAllAccessibleRoles(@Param("roles") Set<Role> roles);

    @Query("SELECT r.id, r.name, r.parentRole.id FROM Role r")
    List<Object[]> findAllRoleMappings();
}
