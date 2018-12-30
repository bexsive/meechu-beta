package  com.inertiamobility.meechu;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        // TODO: Switch statment for each tab/Fragment

        if (i == 0){
            EventsFragment eventsFragment = new EventsFragment();
            return eventsFragment;
        }

        DemoFragment demoFragment = new DemoFragment();
        i = i +1;
        Bundle bundle = new Bundle();
        bundle.putString("message", "Fragment :" + i);
        demoFragment.setArguments(bundle);

        return demoFragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position){
            case 0:
                title = "Events";
                break;
            case 1:
                title = "Alerts";
                break;

            case 2:
                title = "Messages";
                break;

            case 3:
                title = "Scoring";
                break;

            default:
                title = "Broken Naming";

        }
        return title;
    }
}
