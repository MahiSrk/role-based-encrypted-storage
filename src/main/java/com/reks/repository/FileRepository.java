package com.reks.repository;

import com.reks.model.EncryptedFile;
import com.reks.model.Role;
import com.reks.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FileRepository extends JpaRepository<EncryptedFile, Long> {

    // Owner-based operations
    List<EncryptedFile> findByOwner(User owner);
    Page<EncryptedFile> findByOwner(User owner, Pageable pageable);

    // Role-based access
    @Query("SELECT f FROM EncryptedFile f WHERE f.accessRole IN :roles")
    List<EncryptedFile> findByAccessibleRoles(@Param("roles") Set<Role> roles);

    // REKS token search
    @Query(value = "SELECT f.* FROM encrypted_files f " +
           "WHERE f.access_role_id IN :roleIds AND " +
           "AES_DECRYPT(f.encrypted_index, :secretKey) LIKE CONCAT('%', :token, '%')", 
           nativeQuery = true)
    List<EncryptedFile> searchByTokenAndRoles(
        @Param("token") String token,
        @Param("roleIds") List<Long> roleIds,
        @Param("secretKey") String secretKey
    );

    // Metadata search
    @Query("SELECT f FROM EncryptedFile f WHERE f.filename LIKE %:keyword%")
    List<EncryptedFile> searchByFilename(@Param("keyword") String keyword);

    // Statistics
    @Query("SELECT f.accessRole, COUNT(f) FROM EncryptedFile f GROUP BY f.accessRole")
    List<Object[]> countFilesPerRole();
}
