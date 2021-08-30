package  com.inertiamobility.meechu;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DemoFragment extends Fragment {

    public DemoFragment() {
        // Required empty public constructor
    }

    TextView txt_display;
    Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate((R.layout.fragment_demo), container, false);
        context = view.getContext();
        txt_display = view.findViewById(R.id.txt_display);

        Bundle bundle = getArguments();
        txt_display.setText(bundle.getString("message"));

        return view;
    }

}
