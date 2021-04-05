package com.example.capstonephoneapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.capstonearchitecture2.StreamFragment;
import com.example.capstonephoneapp.ConsoleLogFragment;
import com.example.capstonephoneapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SliderParent extends AppCompatActivity {

    FragmentStateAdapter fragmentStateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_views_slider);
        ViewPager2 vp2 = (ViewPager2) findViewById(R.id.view_pager2);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        fragmentStateAdapter = new ScreenSlidePagerAdapter(this);
        vp2.setAdapter(fragmentStateAdapter);

        new TabLayoutMediator(tabLayout, vp2,
                (tab, position) -> tab.setText("")).attach();

    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        private static final int NUM_PAGES = 2;

        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {

            switch (position) {
                case 0:
                    return ConsoleLogFragment.newInstance(0,"I'm the ConsoleLogFragment");
                case 1 :
                    return StreamFragment.newInstance(1, "I'm the StreamFragment");
                default: return null;

            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

}
