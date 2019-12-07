package com.glesmannn.loancalculator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SecondActivity extends AppCompatActivity {

    TextView monthlyPaymentNumber;
    TextView totalInterestCostNumber;
    TextView totalLoanCostNumber;

    private String loanAmountString;
    private String loanTermString;
    private String interestRateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);
        monthlyPaymentNumber = findViewById(R.id.monthlyPaymentNumber);
        totalInterestCostNumber = findViewById(R.id.totalInterestCostNumber);
        totalLoanCostNumber = findViewById(R.id.totalLoanCostNumber);
    }

    @Override
    protected void onResume() {
        super.onResume();

        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        // pull data out of the intent from the main activity
        Intent intent = getIntent();

        loanAmountString = intent.getExtras().getString(MainActivity.LOAN_AMOUNT_STRING);
        loanTermString = intent.getExtras().getString(MainActivity.LOAN_TERM_STRING);
        interestRateString = intent.getExtras().getString(MainActivity.INTEREST_RATE_STRING);

        double monthlyPaymentAmount = intent.getExtras().getDouble(MainActivity.MONTHLY_PAYMENT_AMOUNT);
        double totalLoanAmount = intent.getExtras().getDouble(MainActivity.TOTAL_LOAN_AMOUNT);
        double totalInterestAmount = intent.getExtras().getDouble(MainActivity.TOTAL_INTEREST_AMOUNT);
        ArrayList<HashMap<String, String>> amortizationSchedule = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(MainActivity.AMORTIZATION_SCHEDULE);

        monthlyPaymentNumber.setText(formatter.format(monthlyPaymentAmount));
        totalInterestCostNumber.setText(formatter.format(totalInterestAmount));
        totalLoanCostNumber.setText(formatter.format(totalLoanAmount));

        // Create the adapter object
        SimpleAdapter adapter = new SimpleAdapter(this,
                amortizationSchedule,
                R.layout.amortization_schedule_layout,
                new String[]{MainActivity.MONTH_NUMBER,
                             MainActivity.MONTHLY_INTEREST_AMOUNT,
                             MainActivity.MONTHLY_PRINCIPAL_AMOUNT,
                             MainActivity.UPDATED_TOTAL_INTEREST_PAID,
                             MainActivity.UPDATED_REMAINING_LOAN_AMOUNT },
                new int[] {
                        R.id.monthNumberOnScheduleTextView,
                        R.id.monthlyInterestOnScheduleTextView,
                        R.id.monthlyPrincipalOnScheduleTextView,
                        R.id.updatedTotalInterestPaidOnScheduleTextView,
                        R.id.updatedRemainingLoanAmountOnScheduleTextView
                } );

        ListView mainListView = findViewById(R.id.amortizationScheduleListView);
        mainListView.setAdapter(adapter);
    }

    // catches the soft back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // returns the initial edit text values to the main activity
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(MainActivity.LOAN_AMOUNT_STRING, loanAmountString);
        intent.putExtra(MainActivity.LOAN_TERM_STRING, loanTermString);
        intent.putExtra(MainActivity.INTEREST_RATE_STRING, interestRateString);
        this.setResult(RESULT_OK, intent);
        finish();
    }
}
