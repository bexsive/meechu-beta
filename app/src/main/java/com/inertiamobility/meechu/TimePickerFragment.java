package  com.inertiamobility.meechu;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    static final int START_TIME = 1;
    static final int END_TIME = 2;

    private int mChosenTime;

    int cur = 0;
    TheListener listener;

    public interface TheListener{
        public void returnStartTime(int hour, int min);
        public void returnEndTime(int hour, int min);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        listener = (TheListener) getActivity();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mChosenTime = bundle.getInt("TIME", 1);
        }

        switch (mChosenTime) {

            case START_TIME:
                cur = START_TIME;
                Calendar c = Calendar.getInstance();
                c.add(Calendar.HOUR_OF_DAY, 1);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                return new TimePickerDialog(getActivity(), this, hour, 0, false);

            case END_TIME:
                cur = END_TIME;
                Calendar c2 = Calendar.getInstance();
                c2.add(Calendar.HOUR_OF_DAY, 2);
                int hourEnd = c2.get(Calendar.HOUR_OF_DAY);
                return new TimePickerDialog(getActivity(), this, hourEnd, 0, false);

        }
        return null;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int min) {

        if (listener != null) {
            if (cur == START_TIME) {
                listener.returnStartTime(hour, min);
            } else {
                listener.returnEndTime(hour, min);
            }
        }
    }
}