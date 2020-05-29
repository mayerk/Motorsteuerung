package com.example.motorsteuerung;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout dirRight, dirLeft;

    private Spinner speedDropdown;

    String serverURL = "tcp://farmer.cloudmqtt.com:13524";
    String username = "fnxrzwib";
    String password = "HvyXuds-iXr2";

    String clientID = MqttClient.generateClientId();
    MqttAndroidClient client;

    String dirMessage, enableMessage, motionSpeed;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dirRight = findViewById(R.id.directionRight);
        dirLeft = findViewById(R.id.directionLeft);

        speedDropdown = findViewById(R.id.speedDropdown);

        client = new MqttAndroidClient(MainActivity.this, serverURL, clientID);

        final MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());


        if (!client.isConnected()) {
            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(MainActivity.this, "failed to connect", Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (MqttException e) {
                e.printStackTrace();
            }

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Toast.makeText(MainActivity.this, "ESP is connected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

        } else {
            Toast.makeText(MainActivity.this, "already connected", Toast.LENGTH_SHORT).show();
        }

        dirLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dirMessage = "CCW";
                        enableMessage = "on";
                        motionSpeed = speedDropdown.getSelectedItem().toString();
                        if (client.isConnected()) {
                            try {
                                client.publish("clock", String.valueOf(motionSpeed).getBytes(), 0, false);
                                client.publish("enable", enableMessage.getBytes(), 0, false);
                                client.publish("direction", dirMessage.getBytes(), 0, false);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        enableMessage = "off";
                        if (client.isConnected()) {
                            try {
                                client.publish("enable", enableMessage.getBytes(), 0, false);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                }
                return false;
            }
        });

        dirRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dirMessage = "CW";
                        enableMessage = "on";
                        motionSpeed = speedDropdown.getSelectedItem().toString();
                        if (client.isConnected()) {
                            try {
                                client.publish("clock", String.valueOf(motionSpeed).getBytes(), 0, false);
                                client.publish("enable", enableMessage.getBytes(), 0, false);
                                client.publish("direction", dirMessage.getBytes(), 0, false);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        enableMessage = "off";
                        if (client.isConnected()) {
                            try {
                                client.publish("enable", enableMessage.getBytes(), 0, false);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                }
                return false;
            }
        });
    }
}