package com.inertiamobility.meechu;

import com.google.gson.annotations.SerializedName;

public class ResponseEventParticipant {
    @SerializedName("data")
    EventParticipant eventParticipant;

    public EventParticipant getEventParticipant() {
            return eventParticipant;
        }

}
