package com.inertiamobility.meechu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class EventResponse implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private String data;

    public String getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
    public String getData() {
        return data;
    }
}
