package com.ruoxu.learndemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ruoxu.update.UpdateAgent;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        UpdateAgent.silentUpdate(this);





    }

    public void click(View view) {
        UpdateAgent.forceUpdate(this);
    }
}
