package com.example.quickbook.ApiHelper;

public class RegisterResult {
    final private boolean success;
    final private String error;
    public RegisterResult(boolean success,String error) {
        this.success=success;
        this.error=error;
    }

    public boolean getSuccess() {
        return success;
    }
    public String getError() {
        return error;
    }
}
