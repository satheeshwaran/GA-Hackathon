package com.oozmakappa.oyeloans;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.oozmakappa.oyeloans.Adapters.LoanStepsPagerAdapter;

/**
 * Created by sankarnarayanan on 9/10/16.
 */

public class LoanApplicationStepsActivity extends AppCompatActivity {

    ViewPager viewPager;
    RadioGroup radioGroup;
    LinearLayout getStartedButton;
    LinearLayout skipButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_applocation_steps);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        getStartedButton = (LinearLayout) findViewById(R.id.getStartedButton);
        skipButton = (LinearLayout) findViewById(R.id.skipButton);
        viewPager.setAdapter(new LoanStepsPagerAdapter(getSupportFragmentManager(), this));
        viewPager.addOnPageChangeListener(pageChangeListener);
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoanApplicationStepsActivity.this, ApplyLoanFirstActivity.class);
                startActivity(i);
                finish();
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoanApplicationStepsActivity.this, ApplyLoanFirstActivity.class);
                startActivity(i);
                finish();
            }
        });

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bgColorLightViolet));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            radioGroup.check(radioGroup.getChildAt(position).getId());
            if(position == 2){
                getStartedButton.setVisibility(View.VISIBLE);
            }else{
                getStartedButton.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageSelected(int position) {
            radioGroup.check(radioGroup.getChildAt(position).getId());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                switch (position) {
                    case 0:
                        getWindow().setStatusBarColor(getResources().getColor(R.color.bgColorLightViolet));
                        break;
                    case 1:
                        getWindow().setStatusBarColor(getResources().getColor(R.color.bgColorLightGreen));
                        break;
                    case 2:
                        getWindow().setStatusBarColor(getResources().getColor(R.color.NavBarColor));
                        break;
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}


