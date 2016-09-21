package com.oozmakappa.oyeloans.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.oozmakappa.oyeloans.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by sankarnarayanan on 20/09/16.
 */
public class LoanDetailsHeaderFragment extends Fragment {

    String amount, paise;
    JSONArray totalArray;
    int index = 0;

    public void setValues(String amount, String paise, int index, JSONArray totalArray ){
        this.amount = amount;
        this.paise = paise;
        this.index = index;
        this.totalArray = totalArray;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.loaninfo_header_fragment, null);
        TextView amountText =  (TextView) v.findViewById(R.id.amountValue);
        TextView paiseText = (TextView) v.findViewById(R.id.paiseValue);
        if(this.amount != null){
            amountText.setText(this.amount);
        }
        if(this.paise != null){
            paiseText.setText(this.paise);
        }

        try {
            JSONArray currentArray = this.totalArray;
            JSONObject currentObj = currentArray.getJSONObject(index);
            TextView loanIdView = (TextView) v.findViewById(R.id.loanIdentifier);
            loanIdView.setText("Loan Id - ".concat(currentObj.getString("loan_id")));
        }catch (Exception e){
            e.printStackTrace();
        }

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }



    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}