package com.inertiamobility.meechu;

import com.google.gson.annotations.SerializedName;

public class ResponseUser {
    @SerializedName("data")
    User user;

    public User getUser() {
            return user;
        }

}