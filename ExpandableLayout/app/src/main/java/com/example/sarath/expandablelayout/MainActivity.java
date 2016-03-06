package com.example.sarath.expandablelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExpandablePanel expandablePanel = (ExpandablePanel) findViewById(R.id.main_parent);
        expandablePanel.setCollapseExpandListener(new ExpandablePanel.CollapseExpandListener() {
            @Override
            public void onCollapseStarted() {
                Toast.makeText(getApplicationContext(), "onCollapseStarted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCollapseFinished() {
                Toast.makeText(getApplicationContext(), "onCollapseFinished", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExpandStarted() {
                Toast.makeText(getApplicationContext(), "onExpandStarted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExpandFinished() {
                Toast.makeText(getApplicationContext(), "onExpandFinished", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
