package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class ItemConfirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemconfirm);

        Button nextActivitySwitchButton = (Button) findViewById(R.id.next_button);
        nextActivitySwitchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplication(), ItemRegistration.class);
                startActivity(intent);
            }
        });

        Button backActivitySwitchButton = (Button) findViewById(R.id.back_button);
        backActivitySwitchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplication(), ItemRegistration.MainActivity.class);
                startActivity(intent);
            }
        });
    }
}