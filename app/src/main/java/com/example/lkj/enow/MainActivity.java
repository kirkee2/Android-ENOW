package com.example.lkj.enow;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private MySQLiteOpenHelper helper;
    private String dbName = "st_file.db";
    private int dbVersion = 1; // 데이터베이스 버전

    Toolbar toolbar;
    private String[] navItems = {"Setting","home page"};
    private ListView navList;
    private FrameLayout container;
    private TextView textView;
    private Button button;
    private EditText editRoadMapId;
    private EditText editServerId;
    private EditText editDeviceId;
    private EditText editBrokerId;
    private EditText editCorporationName;
    private EditText editPayloadKey;
    private EditText editPayloadValue;
    private MqttAndroidClient client;
    private MqttConnection mqttConnection;


    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;


    @Override
    protected void onResume() {
        super.onResume();

        textView = (TextView)findViewById(R.id.textView11);
        textView.setText(select());

        mqttConnection.connect(getApplicationContext(),select());

        /*
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://"+select(), "enow");

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"연결 성공.",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"연결 실패.",Toast.LENGTH_LONG).show();

                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
        */


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editRoadMapId = (EditText)findViewById(R.id.editRoadMapId);
        editCorporationName = (EditText)findViewById(R.id.editCorporationName);
        editServerId = (EditText)findViewById(R.id.editServerId);
        editBrokerId = (EditText)findViewById(R.id.editBrokerId);
        editDeviceId = (EditText)findViewById(R.id.editDeviceId);
        editPayloadKey = (EditText)findViewById(R.id.editPayloadKey);
        editPayloadValue = (EditText)findViewById(R.id.editPayloadValue);
        mqttConnection = new MqttConnection();


        button = (Button)findViewById(R.id.button2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        textView = (TextView)findViewById(R.id.textView11);
        toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.open_drawer, R.string.close_drawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

        };

        drawer.setDrawerListener(toggle);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);

        navList = (ListView)findViewById(R.id.left_drawer);
        container = (FrameLayout)findViewById(R.id.content_frame);


        navList.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
        navList.setOnItemClickListener(new DrawerItemClickListener());

        navList.setClickable(false);


        helper = new MySQLiteOpenHelper(
                this,  // 현재 화면의 제어권자
                dbName,// db 이름
                null,  // 커서팩토리-null : 표준커서가 사용됨
                dbVersion);       // 버전

        try {
            db = helper.getWritableDatabase(); // 읽고 쓸수 있는 DB
            //db = helper.getReadableDatabase(); // 읽기 전용 DB select문
        } catch (SQLiteException e) {
            e.printStackTrace();
            finish(); // 액티비티 종료
        }

        //textView.setText(select());

        //mqttConnection.connect(getApplicationContext(),select());
        /*
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://"+select(),"enow");

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"1.",Toast.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this,"연결 성공.",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"연결 실패.",Toast.LENGTH_LONG).show();

                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
        */




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                JSONObject payload = new JSONObject();
                try {
                    json.put("roadMapId",editRoadMapId.getText().toString());
                    json.put("corporationName",editCorporationName.getText().toString());
                    json.put("serverId",editServerId.getText().toString());
                    json.put("brokerId",editBrokerId.getText().toString());
                    json.put("deviceId",editDeviceId.getText().toString());
                    payload.put(editPayloadKey.getText().toString(),editPayloadValue.getText().toString());
                    json.put("payload",payload);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mqttConnection.publish(getApplicationContext(),json);

                /*
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
                    Toast.makeText(MainActivity.this, "PUB 성공.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "Connect가 안됨", Toast.LENGTH_LONG).show();
                }
                */

            }

        });

        editPayloadValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    JSONObject json = new JSONObject();
                    JSONObject payload = new JSONObject();
                    try {
                        json.put("roadMapId",editRoadMapId.getText().toString());
                        json.put("corporationName",editCorporationName.getText().toString());
                        json.put("serverId",editServerId.getText().toString());
                        json.put("brokerId",editBrokerId.getText().toString());
                        json.put("deviceId",editDeviceId.getText().toString());
                        payload.put(editPayloadKey.getText().toString(),editPayloadValue.getText().toString());
                        json.put("payload",payload);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mqttConnection.publish(getApplicationContext(),json);

                    /*
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
                    Toast.makeText(MainActivity.this, "PUB 성공.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "Connect가 안됨", Toast.LENGTH_LONG).show();
                }
                */

                    handled = true;
                }
                return handled;
            }
        });
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position,
                                long id) {
            switch (position) {
                case 0:

                    mqttConnection.close();

                    startActivity(new Intent(MainActivity.this,SettingActivity.class));
                    drawer.closeDrawer(navList);
                    break;
                case 1:
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/ENOW-IJI"));
                    //intent.setPackage("com.android.chrome");
                    drawer.closeDrawer(navList);
                    startActivity(intent);
                    break;
            }
        }

    }


    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    String select() {
        Cursor c = db.rawQuery("select * from mytable;", null);

        String ip = null;
        String port =null ;

        while(c.moveToNext()) {
            ip = c.getString(0);
            port = c.getString(1);
        }

        if(ip.equals(null) || ip.equals(null)) {
            return "Mqtt broker";
        }else{
            return ip + ":" + port;
        }
    }

    public void onBackPressed() {
        //super.onBackPressed();
        mqttConnection.close();

        finish();
    }


}

