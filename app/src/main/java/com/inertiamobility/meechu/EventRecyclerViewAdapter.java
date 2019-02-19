package  com.inertiamobility.meechu;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "EventRecyclerViewAdapter";

    private List<Event> events;
    private Context mContext;

    SharedPreferenceConfig preferenceConfig;

    //Date-Time format
    DateFormat dateFormat;

    public EventRecyclerViewAdapter(Context mContext, List<Event> events) {
        this.events = events;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // Example using glide for user profile pictures

        // .load(path)
        //Glide.with(mContext).asBitmap().load(events.get(position).getUserId()).into(holder.image);

        holder.eventNameText.setText(events.get(position).getName());
        holder.venueNameText.setText(events.get(position).getVenueName());

        //Calc Date/Time
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
        try {
            holder.startTimeText.setText(DateUtils.getRelativeTimeSpanString(dateFormat.parse(events.get(position).getStartTime()).getTime(), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Calculate distance here
        preferenceConfig = new SharedPreferenceConfig(mContext);
        holder.distanceAwayText.setText(String.format("%.0f", haversine_mi(
                preferenceConfig.readUserLat(),
                preferenceConfig.readUserLng(), Double.valueOf(events.get(position).getLat()), Double.valueOf(events.get(position).getLng()))) + " Miles Away");

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, events.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // Images
        CircleImageView image;
        TextView eventNameText, venueNameText, startTimeText, distanceAwayText;
        RelativeLayout parentLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            // Profile pictures
            image = itemView.findViewById(R.id.image);

            eventNameText = itemView.findViewById(R.id.eventNameText);
            venueNameText = itemView.findViewById(R.id.venueNameText);
            startTimeText = itemView.findViewById(R.id.startTimeText);
            distanceAwayText = itemView.findViewById(R.id.distanceAwayText);

            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }

    double haversine_mi(double lat1, double long1, double lat2, double long2) {
        double d2r = (Math.PI / 180.0);
        double dlong = (long2 - long1) * d2r;
        double dlat = (lat2 - lat1) * d2r;
        double a = Math.pow(Math.sin(dlat/2.0), 2) + Math.cos(lat1*d2r) * Math.cos(lat2*d2r) * Math.pow(Math.sin(dlong/2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = 3956 * c;

        return d;
    }

}