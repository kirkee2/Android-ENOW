package com.example.lkj.enow;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by leegunjoon on 2016. 10. 31..
 */
public class MqttConnection {

    private MqttAndroidClient client;

    public void connect(final Context context, String url) {
        client = new MqttAndroidClient(context, "tcp://" + url, "enow");

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(context, "연결 성공.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(context, "연결 실패.", Toast.LENGTH_LONG).show();

                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(Context context,JSONObject json){
        if(client.isConnected()) {
            String topic = "order";
            String mes = json.toString();
            byte[] encodedPayload = new byte[0];

            try {
                encodedPayload = mes.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                message.setRetained(true);
                client.publish(topic, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
            Toast.makeText(context, "PUB 성공.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, "Connect가 안됨", Toast.LENGTH_LONG).show();
        }
    }

    public void close(){
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
