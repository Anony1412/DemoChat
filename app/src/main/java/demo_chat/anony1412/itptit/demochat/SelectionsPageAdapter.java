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
                return new RequestsFragment();
            }
            case 1: {
                return new ChatsFragment();
            }
            case 2: {
                return new FriendsFragment();
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
                return "REQUESTS";
            }
            case 1: {
                return "CHAT";
            }
            case 2: {
                return "FRIENDS";
            }
            default: {
                return null;
            }
        }
    }
}
