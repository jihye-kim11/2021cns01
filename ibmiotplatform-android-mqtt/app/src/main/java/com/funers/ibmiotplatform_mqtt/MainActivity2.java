package com.funers.ibmiotplatform_mqtt;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity2 extends AppCompatActivity {

    private final String TAG = "IBMiotMqtt";

    final private String orgId = "wf2ir5";               //조직id
    final private String key = "a-wf2ir5-coftq4csoe";    //API key 변경하기
    final private String token = "w*hZkZeGO+_nW(TyAw";   //token 변경하기

    final private String name = "test";
    final private String appId = "a:"+orgId+":"+name;    //a:조직id:appId
    final private String url = "tcp://"+ orgId + ".messaging.internetofthings.ibmcloud.com:1883";


    protected MqttAndroidClient mqttAndroidClient;

    protected Button startButton;
    protected Button backButton,updateButton;
    protected RecyclerView event_log_view;
    protected RecyclerView.LayoutManager manager;
    protected SimpleAdapter adapter;
    TextView total,yellow,green,blue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        setActionBar();

        total = (TextView)findViewById(R.id.t_cnt);
        yellow = (TextView)findViewById(R.id.y_cnt);
        green = (TextView)findViewById(R.id.g_cnt);
        blue = (TextView)findViewById(R.id.b_cnt);






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
                String input=data.get("command").toString();
                if (input.equals("ready")){
                    total.setText( "READY");
                }
                else {
                    adapter.addLog("     " + data.get("id").toString() + "      " + data.get("colortype").toString() +
                            "      " + data.get("starttime").toString() + "      " + data.get("endtime").toString());//event_id
                    total.setText("TRANS COMPLETE");
                    yellow.setText(data.get("yellow").toString());
                    green.setText(data.get("green").toString());
                    blue.setText(data.get("blue").toString());
                }

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
                    subscribeEvent("Raspberry_Pi","OK_rasp");//test_piOK_rasp
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
        event_log_view = (RecyclerView) findViewById(R.id.event_log_view1);
        adapter = new SimpleAdapter();
        manager = new LinearLayoutManager(this);
        event_log_view.setLayoutManager(manager);
        event_log_view.setAdapter(adapter);
/*
        adapter.addLog("    "+"001"+"        "+"yellow"+"           "+"16:22:33"+"            "+"16:24:22");//event_id
        adapter.addLog("    "+"002"+"        "+"yellow"+"          "+"16:25:33"+"            "+"16:27:22");//event_id
        adapter.addLog("    "+"003"+"        "+"green"+"           "+"16:28:33"+"            "+"16:30:22");//event_id
        adapter.addLog("    "+"004"+"        "+"blue"+"             "+"16:31:33"+"            "+"16:33:22");//event_id
        adapter.addLog("    "+"005"+"        "+"blue"+"             "+"16:34:33"+"            "+"16:36:22");//event_id
        adapter.addLog("    "+"006"+"        "+"blue"+"             "+"16:37:33"+"            "+"16:39:22");//event_id
        adapter.addLog("    "+"007"+"        "+"blue"+"             "+"16:40:33"+"            "+"16:41:22");//event_id
        adapter.addLog("    "+"008"+"        "+"blue"+"             "+"16:43:33"+"            "+"16:45:22");//event_id
        adapter.addLog("    "+"009"+"        "+"blue"+"             "+"16:46:33"+"            "+"16:48:22");//event_id
        adapter.addLog("    "+"010"+"        "+"blue"+"             "+"16:49:33"+"            "+"16:51:22");//event_id
        adapter.addLog("    "+"011"+"        "+"blue"+"             "+"16:52:33"+"            "+"16:54:22");//event_id
        adapter.addLog("    "+"012"+"        "+"blue"+"             "+"16:55:33"+"            "+"16:57:22");//event_id
        adapter.addLog("    "+"013"+"        "+"blue"+"             "+"16:58:33"+"            "+"17:00:22");//event_id
        adapter.addLog("    "+"014"+"        "+"blue"+"             "+"17:01:33"+"            "+"17:03:22");//event_id
*/
        //button을 누르면 command publish

        updateButton = findViewById(R.id.update_btn);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        JSONObject data = new JSONObject();
        try {
            data.put("state" , "request");
            publishCommand("Raspberry_Pi","OK_rasp","update", data);//test_pi
        } catch (JSONException e) {
            e.printStackTrace();
        }
            }});


        startButton = findViewById(R.id.start_btn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();
                try {
                    data.put("state" , "start");
                    total.setText( "START");
                    publishCommand("Raspberry_Pi","OK_rasp","robot", data);//test_pi
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //button을 누르면 command publish
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();
                try {
                    data.put("state" , "back");
                    total.setText( "BACK");

                    publishCommand("Raspberry_Pi","OK_rasp","robot", data);//test_pi
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




