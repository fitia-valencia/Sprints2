package com.monframework.security;

public class SecurityCheckResult {
    private boolean allowed;
    private int errorCode;
    private String errorMessage;
    
    public SecurityCheckResult() {
        this.allowed = true;
    }
    
    public SecurityCheckResult(boolean allowed, int errorCode, String errorMessage) {
        this.allowed = allowed;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    // Getters et setters
    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }
    
    public int getErrorCode() { return errorCode; }
    public void setErrorCode(int errorCode) { this.errorCode = errorCode; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}