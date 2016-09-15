package com.oozmakappa.oyeloans.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.oozmakappa.oyeloans.Models.SuccessModel;
import com.oozmakappa.oyeloans.R;
import com.oozmakappa.oyeloans.helper.WebServiceCallHelper;
import com.oozmakappa.oyeloans.utils.FacebookHelperUtils;
import com.oozmakappa.oyeloans.utils.SharedDataManager;

import java.util.HashMap;
import java.util.Objects;
import com.oozmakappa.oyeloans.constants.Jsonconstants;

/**
 * Created by sankarnarayanan on 11/09/16.
 */
public class ApplyLoanFirstFragment extends Fragment {

    OnProceedSelectedListener mCallback;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnProceedSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onLoanAmountSelected(HashMap<String,String> data);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnProceedSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.appflow_landing_fragment, null);
        return v;
    }


    @Override
    public void onStart() {

        SeekBar amountSeekbar = (SeekBar) getActivity().findViewById(R.id.seekBar);
        final EditText amountEdit = (EditText) getActivity().findViewById(R.id.editText);
        amountSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub

                int value = (progress * 1000) + 10000;
                amountEdit.setText(Integer.toString(value));
                amountEdit.clearFocus();

            }
        });

        Button proceedButton = (Button) getActivity().findViewById(R.id.profileProceedButton);
        proceedButton.setOnClickListener(buttonClickListener);
        super.onStart();
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.profileProceedButton:
                    constructValidateReferenceRequest();
                    // TODO Auto-generated method stub
                    break;
                case View.NO_ID:
                default:
                    // TODO Auto-generated method stub
                    break;
            }
        }
    };

    private void constructValidateReferenceRequest(){
        WebServiceCallHelper webServiceHelper = new WebServiceCallHelper(new WebServiceCallHelper.OnWebServiceRequestCompletedListener(){
            @Override
            public void onRequestCompleted(SuccessModel model, String errorMessage){
                if (model.getStatus().equals("success")) {
                    HashMap<String,String> firstPageData = new HashMap<String,String>();
                    EditText textField = (EditText)getActivity().findViewById(R.id.editText);
                    firstPageData.put("Amount", textField.getText().toString());
                    mCallback.onLoanAmountSelected(firstPageData);
                }
            }
        });
        EditText referralCode = (EditText) getActivity().findViewById(R.id.editTextReferralCode);
        webServiceHelper.validateReferral(referralCode.getText().toString());
        //Make web service call here.

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
