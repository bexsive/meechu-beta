package  com.inertiamobility.meechu;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventsFragment extends Fragment {
    private static final String TAG = "EventsFragment";

    FloatingActionButton fab;
    List<Event> events = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;

    private SharedPreferenceConfig preferenceConfig;
    String userID;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        preferenceConfig = new SharedPreferenceConfig(this.getActivity().getApplicationContext());

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        final EventRecyclerViewAdapter adapter = new EventRecyclerViewAdapter(getContext(), events);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        refreshLayout = view.findViewById(R.id.swipeRefresh);

        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });

        // Floating Action Button
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getContext(), BuildEventActivity.class), 1);
            }
        });

        // Refresh events list
        updateList();
        //This might not be the most elegant way to do this.  UI thread hard wait. But it works :/
        //TODO: Loading screen a-la twitter while this loads
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        }, 1000);
        return view;
    }

    public void updateList(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        userID = String.valueOf(preferenceConfig.readUserId());
        Call<EventList> call = api.getFeed(userID);

        call.enqueue(new Callback<EventList>() {
            @Override
            public void onResponse(Call<EventList> call, Response<EventList> response) {
                EventList eventList = response.body();
                events.clear();
                for (int i = 0; i < eventList.events.size(); i++){
                    events.add(eventList.events.get(i));
                }
            }
            @Override
            public void onFailure(Call<EventList> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode == Activity.RESULT_OK) {
            //TODO: Add new event to event list, as an insertion
        }
    }
}