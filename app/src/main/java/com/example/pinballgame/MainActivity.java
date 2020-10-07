package com.example.pinballgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import my_interface.GameOverInterface;
import my_interface.scoreInterface;
import my_view.PinballGameView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button lift,right;
    private PinballGameView pinballGameView;
    private TextView score_text_view;
    private boolean isbegin;
    private AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        score_text_view=findViewById(R.id.score);
        pinballGameView=findViewById(R.id.pinball_view);
        lift=findViewById(R.id.to_lift);
        right=findViewById(R.id.to_right);
        init();
        builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("开始游戏");
        builder.setPositiveButton("开始", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pinballGameView.gameStart();
            }
        });

        pinballGameView.setGameOverInterface(new GameOverInterface() {
            @Override
            public void gameOver() {
                builder.show();
            }
        });
        builder.show();
    }
    private void init(){
        isbegin=false;
        pinballGameView.setScoreInterface(new scoreInterface() {
            @Override
            public void setScore() {
                score_text_view.setText(pinballGameView.getScore()+"");
            }
        });

        lift.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d("my_test", "onTouch: 向左开始");
                        pinballGameView.toLiftBegin();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("my_test", "onTouch: 向左结束");
                        pinballGameView.toLiftEnd();
                        break;
                }
                return true;
            }
        });
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d("my_test", "onTouch: 向右开始");
                        pinballGameView.toRightBegin();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("my_test", "onTouch: 向右结束");
                        pinballGameView.toRightEnd();
                        break;
                }
                return true;
            }
        });
    }
}
