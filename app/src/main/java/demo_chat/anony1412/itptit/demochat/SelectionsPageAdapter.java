package demo_chat.anony1412.itptit.demochat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SelectionsPageAdapter extends FragmentPagerAdapter{

    public SelectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return new ChatsFragment();
            }
            case 1: {
                return new FriendsFragment();
            }
            case 2: {
                return new RequestsFragment();
            }
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: {
                return "CHAT";
            }
            case 1: {
                return "FRIENDS";
            }
            case 2: {
                return "REQUESTS";
            }
            default: {
                return null;
            }
        }
    }
}
