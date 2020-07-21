package com.zpj.toolbar.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zpj.widget.toolbar.ZToolBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ZToolBar test = findViewById(R.id.test_bar);
        test.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test.setBackgroundColor(test.isLightStyle() ?  Color.WHITE : Color.BLACK);
            }
        });
        final ZToolBar test2 = findViewById(R.id.test_bar_2);
        test2.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test2.setLeftButtonTint(Color.BLUE);
                test2.setCenterTextColor(Color.RED);
                test2.setCenterSubTextColor(Color.GREEN);
            }
        });

        final ZToolBar test3 = findViewById(R.id.test_bar_3);
        test3.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test3.setLightStyle(!test3.isLightStyle());
            }
        });

        final ZToolBar test4 = findViewById(R.id.test4);
        test4.setBackgroundResource(R.drawable.shape_gradient, true);
    }

}
