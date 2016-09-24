package com.oozmakappa.oyeloans;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.messaging.FirebaseMessaging;
import com.oozmakappa.oyeloans.Adapters.LoanDashBoardListAdapter;
import com.oozmakappa.oyeloans.Adapters.LoanDetailsHeaderAdapter;
import com.oozmakappa.oyeloans.DataExtraction.AppController;
import com.oozmakappa.oyeloans.Models.Loan;
import com.oozmakappa.oyeloans.Models.LoanApplicationInfo;
import com.oozmakappa.oyeloans.Models.LoanDetailsInfo;
import com.oozmakappa.oyeloans.Models.LoanSummaryModel;
import com.oozmakappa.oyeloans.Models.SuccessModel;
import com.oozmakappa.oyeloans.ResideMenu.ResideMenu;
import com.oozmakappa.oyeloans.ResideMenu.ResideMenuItem;
import com.oozmakappa.oyeloans.helper.WebServiceCallHelper;
import com.oozmakappa.oyeloans.utils.FacebookHelperUtils;
import com.oozmakappa.oyeloans.utils.SharedDataManager;
import com.oozmakappa.oyeloans.utils.Utils;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sankarnarayanan on 20/09/16.
 */
public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    ViewPager viewPager;

    // Local Members Reference
    private AppController mController;

    String loanInfoData = "";

    //Loan info data - {"status": "success", "service_name": "loaninfoprovider","request_id": 1, "description": " Loan Schedule for given loanid", "schedule": [{"installment_type": "interest", "cycle_no": 1, "payment_amount": "2083.34", "paid_amount": "0.00", "scheduled_due_date": "2016-09-09"},{"installment_type": "interest", "cycle_no": 1, "payment_amount": "2083.34", "paid_amount": "0.00", "scheduled_due_date": "2016-09-09"},{"installment_type": "interest", "cycle_no": 1, "payment_amount": "2083.34", "paid_amount": "0.00", "scheduled_due_date": "2016-09-09"},{"installment_type": "interest", "cycle_no": 1, "payment_amount": "2083.34", "paid_amount": "0.00", "scheduled_due_date": "2016-09-09"}], "ob": 100.8}

    String loanHistoryData = "";

    //Loan history data - {"loan_status_history": [{"loan_id":1, "loan_status":"Closed"},{"loan_id":3, "loan_status":"Pre- Closed"},{"loan_id":107, "loan_status":"Closed"}]}

    HashMap<String, List<LoanSummaryModel>> listDataChild = new HashMap<String, List<LoanSummaryModel>>();
    public String appHistoryData = "{\"application_status_history\":[{ \"app_id\":2, \"app_status\":\"All verification completed\", \"application_start_time\": \"2016-08-23 19:49:32\", \"current_state\": \"page4\", \"loan_amount\": \"300.00\", \"ALA\":\"150.00\"},{ \"app_id\":8, \"app_status\":\"All verification completed\", \"application_start_time\": \"2016-08-23 19:49:32\", \"current_state\": \"page4\", \"loan_amount\": \"300.00\", \"ALA\":\"150.00\"},{\"app_id\":160, \"app_status\":\"All verification completed\"},{ \"app_id\":290, \"app_status\":\"\", \"application_start_time\": \"2016-08-23 19:49:32\", \"current_state\": \"page4\", \"loan_amount\": \"300.00\", \"ALA\":\"150.00\"}]}";

    private ResideMenu resideMenu;
    private FloatingActionMenu menuRed;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemSettings;
    private ResideMenuItem referFriend;
    private AccountSummaryActivity mContext;
    public ArrayList<LoanSummaryModel> loanArray = new ArrayList<LoanSummaryModel>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Initializes the controller, for data extraction.
        this.setUpBasicItems();
        WebServiceCallHelper webServiceHelper = new WebServiceCallHelper(new WebServiceCallHelper.OnWebServiceRequestCompletedListener(){
            @Override
            public void onRequestCompleted(SuccessModel model, String errorMessage) {
                try {
                    if (model != null && model.getStatus().equals("success")) {
                        model = (LoanApplicationInfo) model;
                        loanHistoryData = ((LoanApplicationInfo) model).getLoanHistory().toString();
                        appHistoryData = ((LoanApplicationInfo) model).getApplicationHistory().toString();
                        JSONArray loanArray = ((LoanApplicationInfo) model).getLoanHistory();
                        for (int i = 0; i < loanArray.length(); i++) {
                            JSONObject loanObject = loanArray.getJSONObject(i);
                            Loan loanObj = new Loan();
                            loanObj.loanID = loanObject.getInt("loan_id");
                            WebServiceCallHelper webServiceHelper = new WebServiceCallHelper(new WebServiceCallHelper.OnWebServiceRequestCompletedListener() {
                                @Override
                                public void onRequestCompleted(SuccessModel model, String errorMessage) {
                                    if (model != null && model.getStatus().equals("success")) {
                                        Utils.removeLoading();
                                        loanInfoData = ((LoanDetailsInfo) model).getResponse().toString();
                                        setUpDashboard();
                                    }else{
                                        enableNoLoanView();
                                    }
                                }
                            });
                            webServiceHelper.getLoanInfoService(loanObj);
                            //break;
                        }
                        FirebaseMessaging.getInstance().subscribeToTopic("loan_info");
                        Utils.removeLoading();
                    } else {
                        enableNoLoanView();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        webServiceHelper.getLoanHistory(SharedDataManager.getInstance().userObject.emailID);
        Utils.showLoading(this, "Fetching your loan Info");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        Tracker t = ((AppController) this.getApplication()).getDefaultTracker();
        t.setScreenName("Account dashboard");
        t.send(new HitBuilders.ScreenViewBuilder().build());
        t.enableAutoActivityTracking(true);
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


    public void enableNoLoanView(){
        RelativeLayout noLoanView = (RelativeLayout) findViewById(R.id.noLoansView);
        RelativeLayout loanPresentView = (RelativeLayout)findViewById(R.id.loanPresentView);
        loanPresentView.setVisibility(View.GONE);
        noLoanView.setVisibility(View.VISIBLE);
        Button applyLoan = (Button) findViewById(R.id.applyLoanButtonNoLoanView);
        applyLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToApplyLoanPage();
                //Toast.makeText(getApplicationContext(),"Apply Loan button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUpDashboard(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new LoanDetailsHeaderAdapter(getSupportFragmentManager(), this, loanHistoryData, loanInfoData));
        viewPager.addOnPageChangeListener(pageChangeListener);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.NavBarColor));
        }

        CirclePageIndicator titleIndicator = (CirclePageIndicator)findViewById(R.id.titlesDashboard);
        titleIndicator.setFillColor(R.color.deep_orange_500);
        titleIndicator.setViewPager(viewPager);

        try {
            ListView listView = (ListView) findViewById(R.id.scheduleContainer);
            JSONObject jsonLoan = new JSONObject(loanInfoData);
            listView.setAdapter(new LoanDashBoardListAdapter(this, jsonLoan.getJSONArray("schedule")));
        }catch (Exception e){
            e.printStackTrace();
        }
        prepareListData();
    }

    public void setUpBasicItems(){
        setContentView(R.layout.activity_loan_info_service);
        this.setUpMenu();
        FloatingActionButton makePaymentBtn = (FloatingActionButton) findViewById(R.id.fab2);
        FloatingActionButton applyLoanBtn = (FloatingActionButton) findViewById(R.id.fab1);
        FloatingActionButton termsButton = (FloatingActionButton) findViewById(R.id.termsAndConditions);
        makePaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Make payment button Clicked", Toast.LENGTH_SHORT).show();
                SharedDataManager.getInstance().activeLoans = loanArray;
                Intent makePaymentIntent = new Intent(DashboardActivity.this,MakePayment.class);
                makePaymentIntent.putExtra("MultiLoanPayment",true);
                startActivity(makePaymentIntent);
            }
        });
        applyLoanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToApplyLoanPage();
                Toast.makeText(getApplicationContext(),"Apply Loan button Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makePaymentIntent = new Intent(DashboardActivity.this,TermsAndConditionsActivity.class);
                startActivity(makePaymentIntent);
            }
        });
    }



    private void setUpMenu() {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        //resideMenu.setUse3D(true);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.4f);
        itemHome     = new ResideMenuItem(this, R.drawable.icon_home,     "Home");
        itemSettings = new ResideMenuItem(this, R.drawable.icon_settings, "Settings");
        referFriend = new ResideMenuItem(this, R.drawable.command, "Refer A Friend");
        ResideMenuItem itemFAQ = new ResideMenuItem(this,R.drawable.question, "FAQ");
        ResideMenuItem chatWithUS = new ResideMenuItem(this,R.drawable.messenger, "Chat");
        ResideMenuItem rateUs = new ResideMenuItem(this,R.drawable.star, "Rate Us");
        ResideMenuItem itemLogout = new ResideMenuItem(this,R.drawable.logout, "Logout");
        chatWithUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://messaging/1162709597152181")));
                }catch (Exception ex){
                    ex.printStackTrace();
                    FirebaseCrash.log(ex.getLocalizedMessage());
                    Toast.makeText(getApplicationContext(), "Chat option not available right now!", Toast.LENGTH_SHORT).show();
                    resideMenu.closeMenu();
                }
            }
        });
        itemLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent loginIntent = new Intent(DashboardActivity.this,FBLoginActivty.class);
                startActivity(loginIntent);
                DashboardActivity.this.finish();
            }
        });
        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardActivity.this.ShowDialog();
            }
        });
        itemFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent faqIntent = new Intent(DashboardActivity.this,FAQActivity.class);
                startActivity(faqIntent);
            }
        });
        itemHome.setOnClickListener(this);
        itemSettings.setOnClickListener(this);
        referFriend.setOnClickListener(this);
        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(referFriend,ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemFAQ, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(chatWithUS, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(rateUs, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemLogout, ResideMenu.DIRECTION_LEFT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        ImageView image = (ImageView) findViewById(R.id.menuIcon);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        referFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent referFriendPage = new Intent(DashboardActivity.this,ReferFriendActivity.class);
                startActivity(referFriendPage);
            }
        });

        itemHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    resideMenu.closeMenu();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

    }


    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            //Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            // Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };


    public void ShowDialog()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final RatingBar rating = new RatingBar(this);
        rating.setMax(4);
        rating.setNumStars(0);
        rating.setStepSize(1);
        rating.setRating(5);
        popDialog.setTitle("Please rate us!! ");
        popDialog.setView(rating);
        popDialog.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //txtView.setText(String.valueOf(rating.getProgress()));
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        popDialog.create();
        popDialog.show();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        return true;
    }

    @Override
    public void onClick(View v) {

    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void goToApplyLoanPage(){
        Intent goToApplyLoanFirstScreenIntent = new Intent(this,LoanApplicationStepsActivity.class);
        startActivity(goToApplyLoanFirstScreenIntent);
    }




    private void prepareListData() {

        try {
            //listDataHeader.add("Loans");
            //listDataHeader.add("Applications");

            JSONObject jsonLoan = new JSONObject(loanHistoryData);
            JSONObject jsonApplication = new JSONObject(appHistoryData);
            JSONArray loanArrayList = jsonLoan.getJSONArray("loan_status_history");
            JSONArray ApplicationArrayList = jsonApplication.getJSONArray("application_status_history");

            for (int i = 0; i < loanArrayList.length(); i++) {
                JSONObject currentObj = loanArrayList.getJSONObject(i);
                final LoanSummaryModel loanModel = new LoanSummaryModel();
                loanModel.setLoanStatus(currentObj.getString("loan_status"));
                loanModel.setLoanId("Loan Id : "+currentObj.getString("loan_id"));
                loanArray.add(loanModel);
            }

            /*for (int i = 0; i < ApplicationArrayList.length(); i++) {
                JSONObject currentObj = ApplicationArrayList.getJSONObject(i);
                final LoanSummaryModel loanModel = new LoanSummaryModel();
                if (currentObj.has("app_status")) {
                    loanModel.setLoanStatus(currentObj.getString("app_status"));
                }
                if(currentObj.has("loan_amount")) {
                    loanModel.setLoanAmount("Rs."+currentObj.getString("loan_amount"));
                    loanModel.setLoanId("App. Id : " +currentObj.getString("app_id"));
                }else{
                    loanModel.setLoanAmount("");
                }
                applicationArray.add(loanModel);
            }*/

            listDataChild.put("Loans", loanArray);
            //listDataChild.put("Applications", applicationArray);

        } catch (Exception e) {
            Log.v("exception", e.getMessage());
        }

    }
}
