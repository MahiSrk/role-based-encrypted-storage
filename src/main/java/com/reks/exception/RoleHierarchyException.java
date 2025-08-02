package com.reks.exception;

public class RoleHierarchyException extends RuntimeException {
    public RoleHierarchyException(String message) {
        super(message);
    }
    
    public RoleHierarchyException(String message, Throwable cause) {
        super(message, cause);
    }
}