package com.example.tobia.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class CustomTour extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_tour);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.homeMiniBtn:
                        Intent startIntend = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(startIntend);
                        break;
                    case R.id.suggestedTourMiniBtn:
                        Intent startIntendSug = new Intent(getApplicationContext(), SuggestedTour.class);
                        startActivity(startIntendSug);
                        break;
                    case R.id.customTourMiniBtn:
                        Intent startIntendCus = new Intent(getApplicationContext(), CustomTour.class);
                        startActivity(startIntendCus);
                        break;
                    case R.id.personMiniBtn:
                        Intent startIntendPer = new Intent(getApplicationContext(), Person.class);
                        startActivity(startIntendPer);
                        break;
                }
                return true;
            }
        });

        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntend = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startIntend);
            }
        });
    }
}
