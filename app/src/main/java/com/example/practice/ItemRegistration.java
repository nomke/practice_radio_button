package com.example.practice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ItemRegistration extends AppCompatActivity {

    private EditText editText;
    private Button regist_button;
    private Button not_regist_button;
    private ScrollView scrollView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemregist);

        editText = findViewById(R.id.editText);
        regist_button = findViewById(R.id.regist_button);
        not_regist_button = findViewById(R.id.not_regist_button);

        regist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = editText.getText().toString();
                if (!fileName.isEmpty()) {
                    int randomNumber = generateRandomNumber();
                    saveToItemList(fileName, randomNumber);
                } else {
                    Toast.makeText(ItemRegistration.this, "Please enter a file name", Toast.LENGTH_SHORT).show();
                }
                Intent intent1 = new Intent(getApplication(), com.example.practice.MainActivity.class);
                startActivity(intent1);
            }
        });

        not_regist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplication(), com.example.practice.MainActivity.class);
                startActivity(intent2);
            }
        });

    }

    private int generateRandomNumber() {
        return (int) (Math.random() * 100) + 1;
    }

    private void saveToItemList(String fileName, int number) {
        // "items"フォルダーが存在しない場合は作成する
        File itemsFolder = new File(getFilesDir(), "items");
        if (!itemsFolder.exists()) {
            itemsFolder.mkdir();
        }

        // ファイルに数字を保存
        File itemFile = new File(itemsFolder, fileName + ".txt");
        try {
            FileWriter writer = new FileWriter(itemFile);
            writer.write(String.valueOf(number));
            writer.close();
            Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class MainActivity extends AppCompatActivity {

        private RadioGroup radioGroup;
        private TextView fileContentTextView;
        private SharedPreferences preferences;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            radioGroup = findViewById(R.id.radioGroup);
            fileContentTextView = findViewById(R.id.fileContentTextView);
            preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

            populateRadioGroup();
            restoreSelectedRadioButton();


            Button wifiActivitySwitchButton = (Button) findViewById(R.id.wifi_button);
            wifiActivitySwitchButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Intent intent = new Intent(getApplication(), WiFiRegistration.class);
                    startActivity(intent);
                }
            });

            Button itemConfirmActivitySwitchButton = (Button) findViewById(R.id.item_button);
            itemConfirmActivitySwitchButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Intent intent = new Intent(getApplication(), ItemConfirmation.class);
                    startActivity(intent);
                }
            });


            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    RadioButton radioButton = findViewById(checkedId);
                    String selectedFileName = radioButton.getText().toString();
                    String fileContent = readFileContent(selectedFileName);
                    fileContentTextView.setText(fileContent);

                    // 選択された項目をSharedPreferencesに保存
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("selectedButtonId", checkedId);
                    editor.apply();
                }
            });
        }

        private void populateRadioGroup() {
            File folder = new File(getFilesDir(), "items");
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            String fileName = file.getName();
                            addRadioButton(fileName);
                        }
                    }
                }
            }
        }


        private void addRadioButton(String fileName) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(fileName);

            radioGroup.addView(radioButton);
        }

        private void restoreSelectedRadioButton() {
            int selectedButtonId = preferences.getInt("selectedButtonId", -1);
            if (selectedButtonId != -1) {
                RadioButton selectedButton = findViewById(selectedButtonId);
                selectedButton.setChecked(true);
            }
        }

        private String readFileContent(String fileName) {
            StringBuilder content = new StringBuilder();
            try {
                File file = new File(getFilesDir() + "/items", fileName);
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content.toString();
        }
    }
}
