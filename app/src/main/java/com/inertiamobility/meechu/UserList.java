package com.inertiamobility.meechu;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class UserList {
    @SerializedName("data")
    List<User> users;

    public UserList(User user){
        this.users = new ArrayList<User>();
        users.add(user);
    }
}