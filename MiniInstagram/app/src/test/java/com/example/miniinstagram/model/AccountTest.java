package com.example.miniinstagram.model;

import static org.junit.Assert.*;

import com.google.common.collect.Maps;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AccountTest {

    public Account account;
    @Before
    public void setUp() throws Exception {
        account = new Account();
    }

    @Test
    public void getAndSetEmail() {
        assertNull(account.getEmail());

        String email = "john@gmail.com";
        account.setEmail(email);
        assertEquals(email, account.getEmail());
    }

    @Test
    public void getAndSetUsername() {
        assertNull(account.getUsername());

        String username = "john";
        account.setUsername(username);
        assertEquals(username, account.getUsername());
    }

    @Test
    public void getAndSetStatus() {
        assertNull(account.getStatus());

        AccountStatus status = AccountStatus.ACCOUNT_STATUS_ACTIVE;
        account.setStatus(status);
        assertEquals(status, account.getStatus());
    }

    @Test
    public void toMap() {
        String email = "jenny@gmail.com";
        String username = "jenny";
        String userID = "123456";
        AccountStatus status = AccountStatus.ACCOUNT_STATUS_PRIVATE;

        Account user1 = new Account(email, username, userID, status);

        Map<String, Object> test = new HashMap<>();

        test.put("email", email);
        test.put("username", username);
        test.put("userID", userID);
        test.put("account status", status);

        assertTrue(Maps.difference(test, user1.toMap()).areEqual());
    }
}