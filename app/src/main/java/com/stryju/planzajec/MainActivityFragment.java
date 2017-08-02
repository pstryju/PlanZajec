package com.stryju.planzajec;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

public class MainActivityFragment extends Fragment {

    TimetablePagerAdapter mTimetablePagerAdapter;
    ViewPager mDaysPager;
    static int actualDay;

    public MainActivityFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        mTimetablePagerAdapter = new TimetablePagerAdapter(getFragmentManager());
        mDaysPager = (ViewPager) view.findViewById(R.id.days_pager);
        mDaysPager.setAdapter(mTimetablePagerAdapter);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
        if(day < 0) day += 7;
        if(day > 4) day = 0;
        mDaysPager.setCurrentItem(day);


        mDaysPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                actualDay = position;
            }
            @Override
            public void onPageSelected(int position) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        return view;
    }

    static int getActualDay() {
        return actualDay;
    }


    public  class TimetablePagerAdapter extends FragmentPagerAdapter {
        TimetablePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SingleDay.newInstance(position);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + position;
        }
    }

}
