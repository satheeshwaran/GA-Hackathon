package com.oozmakappa.oyeloans;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.oozmakappa.oyeloans.Adapters.LoanSummaryExpandableAdapter;
import com.oozmakappa.oyeloans.DataExtraction.AppController;
import com.oozmakappa.oyeloans.Models.LoanSummaryModel;
import com.oozmakappa.oyeloans.ResideMenu.ResideMenu;
import com.oozmakappa.oyeloans.ResideMenu.ResideMenuItem;
import com.oozmakappa.oyeloans.utils.SharedDataManager;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountSummaryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ResideMenu resideMenu;
    private AccountSummaryActivity mContext;
    public ArrayList<LoanSummaryModel> loanArray = new ArrayList<LoanSummaryModel>();
    public ArrayList<LoanSummaryModel> applicationArray = new ArrayList<LoanSummaryModel>();
    ArrayList<String> listDataHeader = new ArrayList<String>();
    HashMap<String, List<LoanSummaryModel>> listDataChild = new HashMap<String, List<LoanSummaryModel>>();
    public String loanHistoryData = "{\"loan_status_history\": [{\"loan_id\":1, \"loan_status\":\"Closed\"},{\"loan_id\":3, \"loan_status\":\"Pre- Closed\"},{\"loan_id\":107, \"loan_status\":\"Closed\"}]}";
    public String appHistoryData = "{\"application_status_history\":[{ \"app_id\":2, \"app_status\":\"All verification completed\", \"application_start_time\": \"2016-08-23 19:49:32\", \"current_state\": \"page4\", \"loan_amount\": \"300.00\", \"ALA\":\"150.00\"},{ \"app_id\":8, \"app_status\":\"All verification completed\", \"application_start_time\": \"2016-08-23 19:49:32\", \"current_state\": \"page4\", \"loan_amount\": \"300.00\", \"ALA\":\"150.00\"},{\"app_id\":160, \"app_status\":\"All verification completed\"},{ \"app_id\":290, \"app_status\":\"\", \"application_start_time\": \"2016-08-23 19:49:32\", \"current_state\": \"page4\", \"loan_amount\": \"300.00\", \"ALA\":\"150.00\"}]}";
    private FloatingActionMenu menuRed;
    private ResideMenuItem itemHome;
    private ResideMenuItem referFriend;
    private ResideMenuItem itemFAQ;
    /*
 * Preparing the list data
 */
    private void prepareListData() {

        try {
            listDataHeader.add("Loans");
            listDataHeader.add("Applications");

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

            for (int i = 0; i < ApplicationArrayList.length(); i++) {
                JSONObject currentObj = ApplicationArrayList.getJSONObject(i);
                final LoanSummaryModel loanModel = new LoanSummaryModel();
                if (currentObj.has("app_status")) {
                    loanModel.setLoanStatus(currentObj.getString("app_status"));
                }
                if(currentObj.has("loan_amount")) {
                    loanModel.setLoanAmount("₹."+currentObj.getString("loan_amount"));
                    loanModel.setLoanId("App. Id : " +currentObj.getString("app_id"));
                }else{
                    loanModel.setLoanAmount("");
                }
                applicationArray.add(loanModel);
            }

            listDataChild.put("Loans", loanArray);
            listDataChild.put("Applications", applicationArray);

        } catch (Exception e) {
            Log.v("exception", e.getMessage());
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        Tracker t = ((AppController) this.getApplication()).getDefaultTracker();
        t.setScreenName("Loan application - Enter bank info screen");
        t.send(new HitBuilders.ScreenViewBuilder().build());
        t.enableAutoActivityTracking(true);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_summary);
        Window window = this.getWindow();

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(this.getResources().getColor(R.color.NavigationMenuColor));
        }

        mContext = this;
        setUpMenu();
        //setListData(responseData);
        Resources res =getResources();
        // get the listview
        final ExpandableListView expListView = (ExpandableListView) findViewById(R.id.expandableListView);
        // preparing list data
        prepareListData();
        LoanSummaryExpandableAdapter listAdapter = new LoanSummaryExpandableAdapter(this, listDataHeader, listDataChild, res);
        // setting list adapter
        expListView.setAdapter(listAdapter);
        // Listview on child click listener
        expListView.expandGroup(0);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                return false;
            }
        });
        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = 0;
            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup)
                    expListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });
        ImageView image = (ImageView) findViewById(R.id.menuIcon);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

        menuRed = (FloatingActionMenu) findViewById(R.id.menu_red);

        FloatingActionButton makePaymentBtn = (FloatingActionButton) findViewById(R.id.fab2);
        FloatingActionButton applyLoanBtn = (FloatingActionButton) findViewById(R.id.fab1);
        FloatingActionButton termsButton = (FloatingActionButton) findViewById(R.id.termsAndConditions);

        makePaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Make payment button Clicked", Toast.LENGTH_SHORT).show();
                SharedDataManager.getInstance().activeLoans = loanArray;
                Intent makePaymentIntent = new Intent(AccountSummaryActivity.this,MakePayment.class);
                makePaymentIntent.putExtra("MultiLoanPayment",true);
                startActivity(makePaymentIntent);
            }
        });

        applyLoanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToApplyLoanPage();
                //Toast.makeText(getApplicationContext(),"Apply Loan button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makePaymentIntent = new Intent(AccountSummaryActivity.this,TermsAndConditionsActivity.class);
                startActivity(makePaymentIntent);
            }
        });

        /*itemProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilePage = new Intent(AccountSummaryActivity.this,MyProfilePage.class);
                startActivity(profilePage);
            }
        });*/

        referFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent referFriendPage = new Intent(AccountSummaryActivity.this,ReferFriendActivity.class);
                startActivity(referFriendPage);
            }
        });

        String welcomeUserText = "Welcome " + SharedDataManager.getInstance().userObject.firstName + " " + SharedDataManager.getInstance().userObject.lastName;

        ((TextView)findViewById(R.id.welcome_user_textview)).setText(welcomeUserText);

        Tracker t = ((AppController) this.getApplication()).getDefaultTracker();
        t.setScreenName("Account Summary");
        t.send(new HitBuilders.ScreenViewBuilder().build());
        t.enableAutoActivityTracking(true);
    }

    public void goToApplyLoanPage(){
        Intent goToApplyLoanFirstScreenIntent = new Intent(this,LoanApplicationStepsActivity.class);
        startActivity(goToApplyLoanFirstScreenIntent);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        return true;
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
        referFriend = new ResideMenuItem(this, R.drawable.command, "Refer A Friend");
        itemFAQ = new ResideMenuItem(this,R.drawable.question, "FAQ");
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
                }
            }
        });

        itemLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent loginIntent = new Intent(AccountSummaryActivity.this,FBLoginActivty.class);
                startActivity(loginIntent);
                AccountSummaryActivity.this.finish();
            }
        });


        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountSummaryActivity.this.ShowDialog();
            }
        });

        itemFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent faqIntent = new Intent(AccountSummaryActivity.this,FAQActivity.class);
                startActivity(faqIntent);
            }
        });

        itemHome.setOnClickListener(this);
        referFriend.setOnClickListener(this);
        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(referFriend,ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemFAQ, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(chatWithUS, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(rateUs, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemLogout, ResideMenu.DIRECTION_LEFT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

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


    @Override
    public void onClick(View v) {

    }
}
