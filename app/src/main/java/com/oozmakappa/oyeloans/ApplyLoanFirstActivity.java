package com.oozmakappa.oyeloans;

import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Range;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.channguyen.rsv.RangeSliderView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.oozmakappa.oyeloans.Adapters.EnterDetailsPagerAdapter;
import com.oozmakappa.oyeloans.Adapters.LoanStepsPagerAdapter;
import com.oozmakappa.oyeloans.DataExtraction.AppController;
import com.oozmakappa.oyeloans.fragments.ApplyLoanEmploymentInfo;
import com.oozmakappa.oyeloans.fragments.ApplyLoanFirstFragment;
import com.oozmakappa.oyeloans.fragments.ApplyLoanPersonalInfo;

import java.util.HashMap;


/**
 * Created by sankarnarayanan on 09/09/16.
 */
public class ApplyLoanFirstActivity extends AppCompatActivity implements ApplyLoanFirstFragment.OnProceedSelectedListener, ApplyLoanPersonalInfo.OnProceedSelectedListener{


    ViewPager viewPager;

    TabLayout tabLayout;


    @Override
    public void onLoanAmountSelected(HashMap<String,String> data){
        Log.v("data", data.toString());
        Fragment personalDetails = new ApplyLoanPersonalInfo();
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onStart() {
        super.onStart();
        Tracker t = ((AppController) this.getApplication()).getDefaultTracker();
        t.setScreenName("Loan application - Application Bank n Employment screen");
        t.send(new HitBuilders.ScreenViewBuilder().build());
        t.enableAutoActivityTracking(true);
    }

    @Override
    public void onPersonalDetailsEntered(HashMap<String,String> data){
        Fragment employmentDetails = new ApplyLoanEmploymentInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applyloan_first_activity);
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.NavigationMenuColor));
        }

        ImageView backButton = (ImageView) findViewById(R.id.menuIcon);
        backButton.setOnClickListener(clickListener);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new EnterDetailsPagerAdapter(getSupportFragmentManager(), this));
        viewPager.addOnPageChangeListener(pageChangeListener);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("Select Amount"));
        tabLayout.addTab(tabLayout.newTab().setText("Personal Info"));
        tabLayout.addTab(tabLayout.newTab().setText("Employment Info"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(tabSelectedListener);

        RangeSliderView sliderView = (RangeSliderView) findViewById(R.id.rsv_custom);
        sliderView.setEnabled(true);
        sliderView.setClickable(false);
        sliderView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        ImageView circleView = (ImageView) findViewById(R.id.collapseIcon);

        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideShowProgress(v);
            }
        });


    }


    public void hideShowProgress(View v){

        RangeSliderView sliderView = (RangeSliderView)findViewById(R.id.rsv_custom);
        TextView textView1 = (TextView)findViewById(R.id.textView5);
        TextView textView2 = (TextView)findViewById(R.id.textView6);
        TextView textView3 = (TextView)findViewById(R.id.textView7);
        TextView textView4 = (TextView)findViewById(R.id.textView8);

        if (sliderView.getVisibility() == View.GONE){
            sliderView.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            textView4.setVisibility(View.VISIBLE);
            ImageView currentImage = (ImageView) v;
            currentImage.setImageResource(R.drawable.ic_keyboard_arrow_up_white_48dp);
        }else{
            sliderView.setVisibility(View.GONE);
            textView1.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            textView4.setVisibility(View.GONE);
            ImageView currentImage = (ImageView) v;
            currentImage.setImageResource(R.drawable.ic_keyboard_arrow_down_white_48dp);
        }

    }



    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener(){
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };


    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            TabLayout.Tab tab = tabLayout.getTabAt(position);
            tab.select();

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private View.OnClickListener clickListener = new View.OnClickListener() {

        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        else if(getFragmentManager().getBackStackEntryCount() == 1) {
            moveTaskToBack(false);
        }
        else {
            getFragmentManager().popBackStack();
        }
    }


}
