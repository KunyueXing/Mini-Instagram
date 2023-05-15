package com.example.miniinstagram.model;

public abstract class Person {
    private String name;
    private Account account;

    public Person(String name, Account account) {
        this.name = name;
        this.account = account;
    }

    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
