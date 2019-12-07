package com.glesmannn.loancalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOAN_AMOUNT_STRING = "loan_amount_string";
    public static final String LOAN_TERM_STRING = "loan_term_string";
    public static final String INTEREST_RATE_STRING = "interest_rate_string";

    public static final String MONTHLY_PAYMENT_AMOUNT = "monthly_payment_amount";
    public static final String TOTAL_LOAN_AMOUNT = "total_loan_amount";
    public static final String TOTAL_INTEREST_AMOUNT = "total_interest_amount";

    public static final String AMORTIZATION_SCHEDULE = "amortization_schedule";

    public static final String MONTH_NUMBER = "month_number";
    public static final String MONTHLY_INTEREST_AMOUNT = "monthly_interest_amount";
    public static final String MONTHLY_PRINCIPAL_AMOUNT = "monthly_principal_amount";
    public static final String UPDATED_REMAINING_LOAN_AMOUNT = "updated_remaining_loan_amount";
    public static final String UPDATED_TOTAL_INTEREST_PAID = "updated_total_interest_paid";
    private static final int REQUEST_CODE = 1;

    private EditText loanAmountEditText;
    private EditText loanTermEditText;
    private EditText interestRateEditText;
    private Button calculateButton;

    private String loanAmountString;
    private String loanTermString;
    private String interestRateString;

    private double loanAmount;
    private double loanTerm;
    private double interestRate;

    private double r;
    private double n;

    private double monthlyPaymentAmount;
    private double totalLoanAmount;
    private double totalInterestAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loanAmountEditText = findViewById(R.id.loanAmountEditText);
        loanTermEditText = findViewById(R.id.loanTermEditText);
        interestRateEditText = findViewById(R.id.interestRateEditText);
        calculateButton = findViewById(R.id.calculateButton);

        calculateButton.setOnClickListener(this);

        monthlyPaymentAmount = 0;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.calculateButton) {

            loanAmountString = loanAmountEditText.getText().toString();
            loanTermString = loanTermEditText.getText().toString();
            interestRateString = interestRateEditText.getText().toString();

            // Simple validation
            if(loanAmountString.equals("")) {
                Toast.makeText(this, "Loan Amount is Required!", Toast.LENGTH_SHORT).show();
                return;
            } else if (loanTermString.equals("")) {
                Toast.makeText(this, "Loan Term is Required!", Toast.LENGTH_SHORT).show();
                return;
            } else if (interestRateString.equals("")) {
                Toast.makeText(this, "Interest Rate is Required!", Toast.LENGTH_SHORT).show();
                return;
            }

            parseInput();
            calculateMonthlyPayment();
            calculateTotalLoanCost();
            calculateTotalInterestCost();

            Intent intent = new Intent(this, SecondActivity.class);

            // values to be replaced in the edit texts upon return from second activity
            intent.putExtra(LOAN_AMOUNT_STRING, loanAmountString);
            intent.putExtra(LOAN_TERM_STRING, loanTermString);
            intent.putExtra(INTEREST_RATE_STRING, interestRateString);

            // values to be rendered on the second activity
            intent.putExtra(MONTHLY_PAYMENT_AMOUNT, monthlyPaymentAmount);
            intent.putExtra(TOTAL_LOAN_AMOUNT, totalLoanAmount);
            intent.putExtra(TOTAL_INTEREST_AMOUNT, totalInterestAmount);
            intent.putExtra(AMORTIZATION_SCHEDULE, createAmortizationSchedule());

            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    // reload the initial edit text values upon return from the second activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            loanAmountEditText.setText(data.getStringExtra(LOAN_AMOUNT_STRING));
            loanTermEditText.setText(data.getStringExtra(LOAN_TERM_STRING));
            interestRateEditText.setText(data.getStringExtra(INTEREST_RATE_STRING));
        }
    }


    public void parseInput() {
        loanTerm = Double.parseDouble(loanTermString);
        loanAmount = Double.parseDouble(loanAmountString);
        interestRate = Double.parseDouble(interestRateString);
    }

    public void calculateMonthlyPayment() {

        n = loanTerm * 12;
        r = (interestRate * .01) / 12;
        double D = (Math.pow((1 + r), n) - 1) / (r * Math.pow((1 + r), n));
        monthlyPaymentAmount = loanAmount / D;

    }

    public void calculateTotalLoanCost() {

        totalLoanAmount = (r * loanAmount * n) / (1 - Math.pow((1 + r), (n * -1)));

    }

    private void calculateTotalInterestCost() {

        totalInterestAmount = totalLoanAmount - loanAmount;

    }

    private ArrayList<HashMap<String, String>> createAmortizationSchedule() {
        double updatedRemainingLoanAmount = loanAmount;
        double updatedTotalInterestPaid = 0;

        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        ArrayList<HashMap<String, String>> amortizationSchedule = new ArrayList<>();

        HashMap<String,String> initial = new HashMap<>();
        initial.put(MONTH_NUMBER, "Month Paid");
        initial.put(MONTHLY_INTEREST_AMOUNT, "Monthly Interest");
        initial.put(MONTHLY_PRINCIPAL_AMOUNT, "Monthly Principal");
        initial.put(UPDATED_REMAINING_LOAN_AMOUNT, "Total Balance");
        initial.put(UPDATED_TOTAL_INTEREST_PAID, "Total Interest");
        amortizationSchedule.add(initial);

        for(int i = 1; i <= loanTerm * 12; i++) {
            int monthNumber = i;
            double monthlyInterestAmount = ((interestRate * .01) / 12) * updatedRemainingLoanAmount;
            updatedTotalInterestPaid += monthlyInterestAmount;
            double monthlyPrincipalAmount = monthlyPaymentAmount - monthlyInterestAmount;
            updatedRemainingLoanAmount = updatedRemainingLoanAmount - monthlyPrincipalAmount;

            // fix for the last loan amount due to small rounding issues
            if(i == loanTerm * 12) {
                updatedRemainingLoanAmount = 0;
            }

            HashMap<String,String> map = new HashMap<>();
            map.put(MONTH_NUMBER, String.valueOf(monthNumber));
            map.put(MONTHLY_INTEREST_AMOUNT, formatter.format(monthlyInterestAmount));
            map.put(MONTHLY_PRINCIPAL_AMOUNT, formatter.format(monthlyPrincipalAmount));
            map.put(UPDATED_REMAINING_LOAN_AMOUNT, formatter.format(updatedRemainingLoanAmount));
            map.put(UPDATED_TOTAL_INTEREST_PAID, formatter.format(updatedTotalInterestPaid));
            amortizationSchedule.add(map);
        }

        return amortizationSchedule;
    }
}
