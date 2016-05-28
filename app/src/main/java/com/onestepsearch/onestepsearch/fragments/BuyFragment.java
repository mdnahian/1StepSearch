package com.onestepsearch.onestepsearch.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.activities.ParentActivity;
import com.onestepsearch.onestepsearch.core.CrudInBackground;
import com.onestepsearch.onestepsearch.core.OnTaskCompleted;
import com.onestepsearch.onestepsearch.core.SavedSession;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mdislam on 12/26/15.
 */
public class BuyFragment extends Fragment {

    ParentActivity parentActivity;

    private SavedSession savedSession;

    private TextView buy_599;
    private TextView buy_499;
    private TextView buy_399;

    private String selectedPlan;
    private int selectedPlanSearches;
    private int numberOfMonths;

    private static PayPalConfiguration config = new PayPalConfiguration();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.buy_fragment, container, false);

        parentActivity = (ParentActivity) getActivity();

        savedSession = (SavedSession) getActivity().getIntent().getSerializableExtra("SavedSession");
        if(savedSession.getUsername().equals("")){
            parentActivity.logout();
        }

        buy_599 = (TextView) rootView.findViewById(R.id.buy_pkg_2);
        buy_499 = (TextView) rootView.findViewById(R.id.buy_pkg_3);
        buy_399 = (TextView) rootView.findViewById(R.id.buy_pkg_4);

        config.environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION);
        config.clientId(getString(R.string.paypalAPIKey));


        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);


        buy_599.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPlan = "$2.99 for 1 Month max 400 Searches";
                selectedPlanSearches = 400;
                numberOfMonths = 1;
                onBuyButtonPressed("2.99", selectedPlan);
            }
        });

        buy_499.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPlan = "$6.99 for 1 Year max 5000 Searches";
                selectedPlanSearches = 5000;
                numberOfMonths = 12;
                onBuyButtonPressed("6.99", selectedPlan);
            }
        });

        buy_399.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPlan = "$19.99 for 1 Year Unlimited Searches";
                selectedPlanSearches = 9999;
                numberOfMonths = 12;
                onBuyButtonPressed("19.99", selectedPlan);
            }
        });


        return rootView;
    }



    @Override
    public void onDestroy() {
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
    }


    public void onBuyButtonPressed(String amount, String item_title){

        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "USD", item_title, PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("Crash", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.

                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(new Date().getTime());
                    date.add(Calendar.MONTH, numberOfMonths);

                    CrudInBackground crudInBackground = new CrudInBackground(new OnTaskCompleted() {
                        @Override
                        public void onTaskComplete(String response) {

                        }
                    });

                    String sql = "UPDATE users set plan='"+selectedPlan+"', numOfSearches='"+selectedPlanSearches+"', currentNumOfSearches='0', plan_expiration='"+date.getTime().toString()+"' WHERE email='"+savedSession.getEmail()+"'";

                    crudInBackground.execute(sql, getString(R.string.crudURL), getString(R.string.crudApiKey));


                    new AlertDialog.Builder(getActivity())
                            .setTitle("Payment Successful")
                            .setMessage("Your new plan, "+selectedPlan+", is now active. Please login to start searching.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    parentActivity.logout();
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    parentActivity.logout();
                                }
                            })
                            .setIcon(R.drawable.logo)
                            .show();


                } catch (JSONException e) {
                    Log.e("Crash", "an extremely unlikely failure occurred: ", e);
                }
            }
        }

    }


}
