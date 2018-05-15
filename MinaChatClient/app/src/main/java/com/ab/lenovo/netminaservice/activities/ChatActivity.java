package com.ab.lenovo.netminaservice.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ab.lenovo.netminaservice.SessionManager;
import com.ab.lenovo.netminaservice.adapter.ChatContentAdapter;
import com.ab.lenovo.netminaservice.entity.MessageEntity;
import com.ab.lenovo.netminaservice.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private EditText editContent;
    private Button send;
    private RecyclerView chatContentLists;
    private ChatContentAdapter chatContentAdapter;
    private List<MessageEntity> messageEntities;


    public static Handler handler;

    private String name;
    private String username;

    private ReceiveMessageBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        init();

        registerBroadcast();


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        MessageEntity messageEntity = (MessageEntity) msg.obj;
                        messageEntities.add(messageEntity);
                        chatContentAdapter.notifyItemInserted(messageEntities.size() - 1);
                        chatContentLists.scrollToPosition(messageEntities.size() - 1);
                        break;
                }
            }
        };

        chatContentAdapter = new ChatContentAdapter(this, messageEntities);
        chatContentLists.setAdapter(chatContentAdapter);
        chatContentLists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        username = intent.getStringExtra("username");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editContent.getText().toString();
                editContent.setText("");
                SessionManager.getInstance().writeToServer("MSG:" + username + ":" + name + ":" + content);
            }
        });
    }

    private void registerBroadcast() {
        receiver = new ReceiveMessageBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.ab.lenovo.mina.receive");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(new Intent(this, MinaService.class));
        unregisterBroadcast();

    }

    private void init() {
        editContent = (EditText) findViewById(R.id.editContent);
        send = (Button) findViewById(R.id.send);
        chatContentLists = (RecyclerView) findViewById(R.id.chatContentLists);

        //chatContentLists.setItemAnimator(new DefaultItemAnimator());

        messageEntities = new ArrayList<>();

    }

    public class ReceiveMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String[] contents = intent.getStringExtra("message").split(":");
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessage(contents[3]);
            messageEntity.setUserName(contents[1]);
            Log.i("aaa",intent.getStringExtra("message"));
            if (contents[1].equals(username)) {
                messageEntity.setMyself(true);
            } else {
                messageEntity.setMyself(false);
            }
            messageEntities.add(messageEntity);
            chatContentAdapter.notifyItemInserted(messageEntities.size() - 1);
            chatContentLists.scrollToPosition(messageEntities.size() - 1);
        }
    }


}
