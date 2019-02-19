package com.inertiamobility.meechu;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserList {
    @SerializedName("data")
    List<User> users;

    public List<User> getUsers() {
        return users;
    }
}