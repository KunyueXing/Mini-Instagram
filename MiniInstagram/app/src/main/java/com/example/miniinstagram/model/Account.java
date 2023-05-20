package com.example.miniinstagram.model;

import java.util.HashMap;
import java.util.Map;

public class Account {
    private String email;
    private String username;
    private String password;
    private String accountID;
    private AccountStatus status;

    public Account() {
    }

    public Account(String email, String username, String accountID) {
        this.email = email;
        this.username = username;
        this.accountID = accountID;

        status = AccountStatus.ACCOUNT_STATUS_PUBLIC;
    }

    public Account(String email, String username, String accountID, AccountStatus status) {
        this.email = email;
        this.username = username;
        this.accountID = accountID;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountID() {
        return accountID;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("email", email);
        result.put("username", username);
        result.put("userID", accountID);
        result.put("account status", status);

        return result;
    }
}
