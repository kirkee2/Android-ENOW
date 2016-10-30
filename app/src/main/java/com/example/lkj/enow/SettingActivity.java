package com.example.lkj.enow;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private MySQLiteOpenHelper helper;
    private String dbName = "st_file.db";
    private int dbVersion = 1; // 데이터베이스 버전

    private EditText editIp;
    private EditText editPort;
    private Button button;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        editIp = (EditText)findViewById(R.id.editText3);
        editPort = (EditText)findViewById(R.id.editText2);
        button = (Button)findViewById(R.id.button);
        textView = (TextView)findViewById(R.id.textView12);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


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

        textView.setText(select());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert(editIp.getText().toString(),editPort.getText().toString());
                textView.setText(select());
            }

        });

        editPort.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    insert(editIp.getText().toString(),editPort.getText().toString());
                    textView.setText(select());

                    handled = true;
                }
                return handled;
            }
        });
    }

    void delete() {
        db.execSQL("delete from mytable;");
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



    void insert (String ip,String port) {
        delete();
        db.execSQL("insert into mytable (ip,port) values('"+ ip +"','"+port+"');");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}

