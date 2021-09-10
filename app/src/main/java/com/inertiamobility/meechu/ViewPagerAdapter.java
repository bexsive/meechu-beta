package  com.inertiamobility.meechu;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new SearchFriendsFragment();

            case 2:
                DemoFragment demoFragment = new DemoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("message", "DM's coming in a future update!");
                demoFragment.setArguments(bundle);
                return demoFragment;

            case 3:
                DemoFragment demoFragment2 = new DemoFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString("message", " Achievement Locked:  Coming Soon");
                demoFragment2.setArguments(bundle2);
                return demoFragment2;

            default:
                return new EventsFragment();
        }
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
                title = "Friends";
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
