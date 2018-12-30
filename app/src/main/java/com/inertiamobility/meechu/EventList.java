package  com.inertiamobility.meechu;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventList {
    @SerializedName("data")
    List<Event> events;

    public List<Event> getEvents() {
        return events;
    }
}
