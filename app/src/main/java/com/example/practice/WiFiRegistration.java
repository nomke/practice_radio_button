package com.example.practice;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

public class WiFiRegistration extends AppCompatActivity {
    private Button btnGetIPAddress;
    private Button btnback;
    private Button btnShowSavedText;
    private TextView txtIPAddress;
    private TextView txtSavedText;
    private TextView txtMatchStatus;
    private Timer timer;

    private boolean isWifiConnected = false;
    private String savedIPAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifiregist);

        btnGetIPAddress = findViewById(R.id.btnGetIPAddress);
        btnback = findViewById(R.id.back_btn);
//        btnShowSavedText = findViewById(R.id.btnShowSavedText);
//        txtIPAddress = findViewById(R.id.txtIPAddress);
//        txtSavedText = findViewById(R.id.txtSavedText);
        txtMatchStatus = findViewById(R.id.txtMatchStatus);


        btnGetIPAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = getIPAddress();
//                txtIPAddress.setText(ipAddress);
                if (isWifiConnected) {
                    saveTextToFile(ipAddress);
                }
                checkIPAddressMatch();

                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
            }
        });

//        btnShowSavedText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String savedText = readTextFromFile();
//                txtSavedText.setText(savedText);
//            }
//        });

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!savedIPAddress.isEmpty()) {
                    checkIPAddressMatch();
                }
            }
        }, 0, 5000); // 5秒ごとに実行
    }

    private String getIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address.getAddress().length == 4) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void saveTextToFile(String text) {
        try {
            FileOutputStream outputStream = openFileOutput("saved_text.txt", Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readTextFromFile() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fileInputStream = openFileInput("saved_text.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void checkIPAddressMatch() {
        String ipAddress = getIPAddress();
        String savedIPAddress = readTextFromFile();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isWifiConnected && ipAddress.equals(savedIPAddress)) {
                    txtMatchStatus.setText("一致");
                } else {
                    txtMatchStatus.setText("不一致");
                }
            }
        });
    }

    private boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo != null && wifiNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isWifiConnected = isWifiConnected();
        savedIPAddress = readTextFromFile();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}

