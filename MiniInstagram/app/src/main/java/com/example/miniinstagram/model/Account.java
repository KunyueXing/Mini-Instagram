package com.example.miniinstagram.model;

public class Account {
    private String email;
    private String username;
    private String password;
    private String accountID;
    private AccountStatus status;

    public Account() {
    }

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
        status = AccountStatus.ACCOUNT_STATUS_PUBLIC;
    }

    public Account(String email, String username, String password, AccountStatus status) {
        this.email = email;
        this.username = username;
        this.password = password;
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
}
