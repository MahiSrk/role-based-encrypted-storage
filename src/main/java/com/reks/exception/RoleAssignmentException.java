package com.reks.exception;

public class RoleAssignmentException extends RuntimeException {
    public RoleAssignmentException(String message) {
        super(message);
    }
    
    public RoleAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
}