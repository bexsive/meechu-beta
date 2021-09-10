package  com.inertiamobility.meechu;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    static final int START_DATE = 1;
    static final int END_DATE = 2;

    private int mChosenDate;

    int cur = 0;
    TheListener listener;

    public interface TheListener{
        void returnStartDate(int year, int month, int day);
        void returnEndDate(int year, int month, int day);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        listener = (TheListener) getActivity();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mChosenDate = bundle.getInt("DATE", 1);
        }

        switch (mChosenDate) {
            case START_DATE:
                cur = START_DATE;
                return new DatePickerDialog(getActivity(), this, year, month, day);

            case END_DATE:
                cur = END_DATE;
                return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        return null;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (listener != null) {
            if (cur == START_DATE) {
                listener.returnStartDate(year, month, day);
            } else {
                listener.returnEndDate(year, month, day);
            }
        }
    }
}