package com.reks.repository;

import com.reks.model.Role;
import com.reks.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Authentication methods
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    // Role-based queries with improved performance
    @EntityGraph(attributePaths = {"roles", "userKey"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    @EntityGraph(attributePaths = "roles")
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId")
    List<User> findByRoleId(@Param("roleId") Long roleId);

    // Access control methods
    @EntityGraph(attributePaths = "roles")
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r IN :roles")
    List<User> findUsersWithAnyRole(@Param("roles") Set<Role> roles);

    // Performance optimized queries
    @Query("SELECT u.id, u.username FROM User u")
    List<Object[]> findAllUserBasicInfo();

    // Additional useful methods
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.roles r WHERE u.id = :userId AND r.id = :roleId")
    boolean userHasRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @EntityGraph(attributePaths = {"roles", "userKey"})
    @Query("SELECT u FROM User u WHERE u.id IN :userIds")
    List<User> findAllByIdsWithRoles(@Param("userIds") Set<Long> userIds);
}