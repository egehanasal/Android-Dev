package com.egehanasal.runnablehandler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    TextView timeTracker;
    Button button;
    int number;
    int countTurn;
    Runnable runnable;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        timeTracker = findViewById(R.id.timeTracker);
        button = findViewById(R.id.startButton);
        this.number = 0;
        this.countTurn = 0;
    }

    public void buttonPressed(View view) {
        if (this.countTurn %2 == 0) {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    textView.setText(""+number);
                    number++;
                    handler.postDelayed(runnable, 1000);
                }
            };
            handler.post(runnable);
            button.setText("Stop");
        }
        else {
            handler.removeCallbacks(runnable);
            button.setText("Start");
            timeTracker.setText("Last time: " + number);
            this.number = 0;
            textView.setText(""+number);
        }
        this.countTurn++;
    }
}