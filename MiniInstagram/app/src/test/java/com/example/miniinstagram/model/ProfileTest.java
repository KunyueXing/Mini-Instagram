package com.example.miniinstagram.model;

import static com.example.miniinstagram.model.GenderChoice.GENDER_CHOICE_DEFAULT;
import static com.example.miniinstagram.model.GenderChoice.GENDER_CHOICE_NONE_BINARY;
import static org.junit.Assert.*;

//import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
//import static org.hamcrest.Matchers.equalTo;

import com.google.common.collect.Maps;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileTest {
    public Profile profile;
    @Before
    public void setup() {
        profile = new Profile("default", "I like swimming", GENDER_CHOICE_NONE_BINARY, 95, 5, 21, new ArrayList<Link>());
    }

    @Test
    public void getAndSetProfilePicUriStr() {
        String pic = "default";
        profile.setProfilePicUriStr(pic);
        assertEquals(pic, profile.getProfilePicUriStr());
    }

    @Test
    public void getAndSetBio() {
        String testStr = "dsjf;lajfio;washj";
        profile.setBio(testStr);
        assertEquals(testStr, profile.getBio());
    }

    @Test
    public void getAndSetPhone() {
        String phoneNum = "8888888888";
        profile.setPhone(phoneNum);
        assertEquals(phoneNum, profile.getPhone());
    }

    @Test
    public void getGender() {
        GenderChoice gender = GENDER_CHOICE_NONE_BINARY;
        profile.setGender(gender);
        assertEquals(gender, profile.getGender());
    }

    @Test
    public void getBirthday() {
        int year = 81;
        int month = 1;
        int day = 14;
        profile.setBirthday(year, month, day);


        Date birthday = profile.getBirthday();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birthday);
        assertEquals(year, calendar.get(Calendar.YEAR));
        assertEquals(month-1, calendar.get(Calendar.MONTH));
        assertEquals(day, calendar.get(Calendar.DAY_OF_MONTH));
    }

    /*
     * Test addLink(). When user add a link via this func, check if the links is added successfully.
     */
    @Test
    public void addLink() {
        Link testLink = new Link("https://www.google.com/", "google");
        profile.addLink(testLink);

        List<Link> links = new ArrayList<>();
        links.add(testLink);

        assertThat(profile.getLinks(), is(links));
    }

    @Test
    public void toMap() {
        String pic = "default";
        String bio = "I like swimming";
        GenderChoice gender = GENDER_CHOICE_DEFAULT;
        String phone = "111313131133";
        List<Link> links = new ArrayList<>();

        int year = 95;
        int month = 5;
        int day = 21;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        Date birthday = calendar.getTime();

        Profile user1 = new Profile(pic, bio, phone, gender, year, month, day, links);

        Map<String, Object> test = new HashMap<>();
        test.put("profile pic", pic);
        test.put("bio", bio);
        test.put("phone", phone);
        test.put("gender", gender);
        test.put("birthday", birthday);
        test.put("links", links);

        assertTrue(Maps.difference(test, user1.toMap()).areEqual());
    }
}