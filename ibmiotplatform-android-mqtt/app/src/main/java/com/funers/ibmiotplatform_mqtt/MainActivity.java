package com.funers.ibmiotplatform_mqtt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "IBMiotMqtt";

    final private String orgId = "wf2ir5";               //조직id
    final private String key = "a-wf2ir5-coftq4csoe";    //API key 변경하기
    final private String token = "w*hZkZeGO+_nW(TyAw";   //token 변경하기

    final private String name = "test";
    final private String appId = "a:"+orgId+":"+name;    //a:조직id:appId
    final private String url = "tcp://"+ orgId + ".messaging.internetofthings.ibmcloud.com:1883";


    protected MqttAndroidClient mqttAndroidClient;

    protected Button openButton;
    protected Button closeButton;
    protected RecyclerView event_log_view;
    protected RecyclerView.LayoutManager manager;
    protected SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionBar();



        ////////////////////////////////////////////////////////////////////////////////////////////
        //mqtt client 생성 및 callBack 함수 작성
        ////////////////////////////////////////////////////////////////////////////////////////////
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(key);
        mqttConnectOptions.setPassword(token.toCharArray());
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), url, appId);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i(TAG, "connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                JSONObject data = new JSONObject(new String(message.getPayload()));
                adapter.addLog("topic: " + topic + ", data: " + data.get("dist_cm"));


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i(TAG, "msg delivered");
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////
        //mqtt client로 ibm_iot_platform에 접속 및 event subscribe
        ////////////////////////////////////////////////////////////////////////////////////////////
        try {
            mqttAndroidClient.connect(mqttConnectOptions,null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "connect succeed");
                    subscribeEvent("Raspberry_Pi","test2_pi", "distance");//test_pi
//                    subscribeEvent("Raspberry_Pi","test_pi");
//                    subscribeEvent("Raspberry_Pi");
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "connect failed");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        //MainActivity의 View 동작설정
        ////////////////////////////////////////////////////////////////////////////////////////////
        event_log_view = (RecyclerView) findViewById(R.id.event_log_view);
        adapter = new SimpleAdapter();
        manager = new LinearLayoutManager(this);
        event_log_view.setLayoutManager(manager);
        event_log_view.setAdapter(adapter);

        //button을 누르면 command publish
        openButton = findViewById(R.id.open);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();
                try {
                    data.put("state" , "run");
                    publishCommand("Raspberry_Pi","test2_pi","door", data);//test_pi
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //button을 누르면 command publish
        closeButton = findViewById(R.id.close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();
                try {
                    data.put("state" , "stop");
                    publishCommand("Raspberry_Pi","test2_pi","door", data);//test_pi
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    //함수
    ////////////////////////////////////////////////////////////////////////////////////////////
    public void subscribeEvent(String deviceType, String deviceId, String eventId) {
        String topic = "iot-2/type/"+deviceType+"/id/"+deviceId+"/evt/"+eventId+"/fmt/json";
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "subscribed succeed");
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "subscribed failed");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void subscribeEvent(String deviceType, String deviceId) {
        String topic = "iot-2/type/"+deviceType+"/id/"+deviceId+"/evt/+/fmt/json";
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "subscribed succeed");
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "subscribed failed");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void subscribeEvent(String deviceType) {
        String topic = "iot-2/type/"+deviceType+"/id/+/evt/+/fmt/json";
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "subscribed succeed");
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "subscribed failed");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishCommand(String deviceType, String deviceId, String commandId, JSONObject data) {
        String Topic = "iot-2/type/"+deviceType+"/id/"+deviceId+"/cmd/"+commandId+"/fmt/json";
        try {
            if (mqttAndroidClient.isConnected() == false)
                mqttAndroidClient.connect();

            MqttMessage message = new MqttMessage();
            message.setPayload(data.toString().getBytes());
            message.setQos(0);
            mqttAndroidClient.publish(Topic, message,null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "publish succeed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "publish failed!");
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private void setActionBar(){
        CustomActionBar ca=new CustomActionBar(this, getSupportActionBar());
        ca.setActionBar();
    }
}




