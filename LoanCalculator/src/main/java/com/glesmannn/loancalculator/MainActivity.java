package com.glesmannn.loancalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


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

    private double monthlyPayment;
    private double totalLoanCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loanAmountEditText = findViewById(R.id.loanAmountEditText);
        loanTermEditText = findViewById(R.id.loanTermEditText);
        interestRateEditText = findViewById(R.id.interestRateEditText);
        calculateButton = findViewById(R.id.calculateButton);

        calculateButton.setOnClickListener(this);

        monthlyPayment = 0;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.calculateButton) {

            loanAmountString = loanAmountEditText.getText().toString();
            loanTermString = loanTermEditText.getText().toString();
            interestRateString = interestRateEditText.getText().toString();

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

            Toast.makeText(this, "Monthly Payment: " + monthlyPayment, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Total Loan Cost: " + totalLoanCost, Toast.LENGTH_SHORT).show();
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
        monthlyPayment = loanAmount / D;
    }

    public void calculateTotalLoanCost() {

        totalLoanCost = (r * loanAmount * n) / (1 - Math.pow((1 + r), (n * -1)));
    }
}
