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

@Repository
public interface EncryptedFileRepository extends JpaRepository<EncryptedFile, Long> {

    // ✅ Basic operations
    List<EncryptedFile> findTop10ByOwnerOrderByCreatedAtDesc(User owner);
    List<EncryptedFile> findByOwner(User owner);
    Page<EncryptedFile> findByOwner(User owner, Pageable pageable);
    List<EncryptedFile> findByEncryptedIndexAndOwner(byte[] token, User owner); // exact match only

    // ✅ Role-based access control
    @Query("SELECT f FROM EncryptedFile f WHERE f.accessRole IN :roles")
    List<EncryptedFile> findByAccessibleRoles(@Param("roles") List<Role> roles);

    @Query("SELECT f FROM EncryptedFile f WHERE f.accessRole.id = :roleId")
    List<EncryptedFile> findByRoleId(@Param("roleId") Long roleId);

    // ✅ Search by filename
    @Query("SELECT f FROM EncryptedFile f WHERE f.filename LIKE %:keyword%")
    List<EncryptedFile> searchByFilename(@Param("keyword") String keyword);

    // ❌ Removed methods that did LIKE on byte[]
    // Removed:
    // findByEncryptedIndexContaining
    // findByEncryptedIndexContainingAndAccessRoleId
    // findByEncryptedIndexContainingAndOwner

    // ✅ Secure encrypted search using native SQL
    @Query(value = "SELECT f.* FROM encrypted_files f " +
            "WHERE f.access_role_id IN :roleIds AND " +
            "AES_DECRYPT(f.encrypted_index, :secretKey) LIKE CONCAT('%', :token, '%')",
            nativeQuery = true)
    List<EncryptedFile> searchByTokenAndRoles(
            @Param("token") String token,
            @Param("roleIds") List<Long> roleIds,
            @Param("secretKey") String secretKey);

    // ✅ Metadata search
    @Query("SELECT f FROM EncryptedFile f WHERE " +
            "LOWER(f.filename) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(f.owner.username) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<EncryptedFile> searchMetadata(@Param("query") String query, Pageable pageable);

    // ✅ Statistics and analytics
    @Query("SELECT f.accessRole, COUNT(f) FROM EncryptedFile f GROUP BY f.accessRole")
    List<Object[]> countFilesPerRole();

    @Query("SELECT COUNT(f) FROM EncryptedFile f WHERE f.owner = :owner")
    Long countByOwner(@Param("owner") User owner);

    // ✅ Recent files
    @Query("SELECT f FROM EncryptedFile f WHERE f.owner = :owner ORDER BY f.createdAt DESC")
    List<EncryptedFile> findRecentByOwner(@Param("owner") User owner, Pageable pageable);

    @Query("SELECT f FROM EncryptedFile f ORDER BY f.createdAt DESC")
    List<EncryptedFile> findRecentFiles(Pageable pageable);
    
    @Query(value = "SELECT f.* FROM encrypted_files f " +
            "WHERE f.owner_id = :ownerId AND " +
            "AES_DECRYPT(f.encrypted_index, :secretKey) LIKE CONCAT('%', :token, '%')",
            nativeQuery = true)
    List<EncryptedFile> searchByTokenAndOwner(
            @Param("token") String token,
            @Param("ownerId") Long ownerId,
            @Param("secretKey") String secretKey);

}
