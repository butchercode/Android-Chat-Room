package com.ab.lenovo.netminaservice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ab.lenovo.netminaservice.R;

public class ChatListActivity extends AppCompatActivity {

    private LinearLayout qunChat;
    private LinearLayout laoJing;
    private LinearLayout aKang;

    private String userName;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);

        init();

        qunChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = getUserName();
                Intent intent1 = new Intent(ChatListActivity.this, ChatActivity.class);
                intent1.putExtra("username", userName);
                intent1.putExtra("name", "ALL");
                startActivity(intent1);
            }
        });

        laoJing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = getUserName();
                Intent intent1 = new Intent(ChatListActivity.this, ChatActivity.class);
                intent1.putExtra("username", userName);
                intent1.putExtra("name", "TOM");
                startActivity(intent1);
            }
        });

        aKang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = getUserName();
                Intent intent1 = new Intent(ChatListActivity.this, ChatActivity.class);
                intent1.putExtra("username", userName);
                intent1.putExtra("name", "JACK");
                startActivity(intent1);
            }
        });


    }

    public void init() {
        qunChat = (LinearLayout) findViewById(R.id.qunChat);
        laoJing = (LinearLayout) findViewById(R.id.laoJing);
        aKang = (LinearLayout) findViewById(R.id.aKang);
    }

    public String getUserName() {
        intent = getIntent();
        return intent.getStringExtra("username");
    }

}
