package mauricio.com.tutorgeometrico.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class TabsAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments;

    public TabsAdapter(FragmentManager fm, @NonNull List<Fragment> fragments) {

        super(fm);
        this.fragments = fragments;

    }

    @Override
    public Fragment getItem(int position) {

        return this.fragments.get(position);
    }

    @Override
    public int getCount() {

        return this.fragments.size();
    }
}
