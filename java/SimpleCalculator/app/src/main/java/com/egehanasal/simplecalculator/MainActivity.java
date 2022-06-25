package com.egehanasal.simplecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText number1Text;
    EditText number2Text;
    TextView resultText;

    int number1;
    int number2;
    int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number1Text = findViewById(R.id.number1);
        number2Text = findViewById(R.id.number2);
        resultText = findViewById(R.id.textView);
    }

    public void add(View view){

        if((number1Text.getText().toString().matches("")) || (number2Text.getText().toString().matches(""))){
            resultText.setText("enter a number");
            return;
        }

        this.number1 = Integer.parseInt(number1Text.getText().toString());
        this.number2 = Integer.parseInt(number2Text.getText().toString());

        this.result = number1 + number2;
        resultText.setText("Result: " + result);
    }

    public void subtract(View view){

        if((number1Text.getText().toString().matches("")) || (number2Text.getText().toString().matches(""))){
            resultText.setText("enter a number");
            return;
        }

        this.number1 = Integer.parseInt(number1Text.getText().toString());
        this.number2 = Integer.parseInt(number2Text.getText().toString());

        this.result = number1 - number2;
        resultText.setText("Result: " + result);
    }

    public void multiply(View view){

        if((number1Text.getText().toString().matches("")) || (number2Text.getText().toString().matches(""))){
            resultText.setText("enter a number");
            return;
        }

        this.number1 = Integer.parseInt(number1Text.getText().toString());
        this.number2 = Integer.parseInt(number2Text.getText().toString());

        this.result = number1 * number2;
        resultText.setText("Result: " + result);
    }

    public void divide(View view){

        if((number1Text.getText().toString().matches("")) || (number2Text.getText().toString().matches(""))){
            resultText.setText("enter a number");
            return;
        }

        this.number1 = Integer.parseInt(number1Text.getText().toString());
        this.number2 = Integer.parseInt(number2Text.getText().toString());
        if(number2 == 0){
            resultText.setText("Invalid operation");
            return;
        }

        double result = number1 / number2;
        resultText.setText("Result: " + result);
    }
}