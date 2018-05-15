package com.ab.lenovo.netminaservice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.lenovo.netminaservice.R;
import com.ab.lenovo.netminaservice.entity.MessageEntity;

import java.util.List;

public class ChatContentAdapter extends RecyclerView.Adapter<ChatContentAdapter.ChatContentViewHolder> {

    private List<MessageEntity> messageList;
    private Context context;
    private LayoutInflater inflater;

    public ChatContentAdapter(Context context, List<MessageEntity> messageList) {
        this.messageList = messageList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public ChatContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chat_content_item, parent, false);
        return new ChatContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatContentViewHolder holder, int position) {

        MessageEntity messageEntity=messageList.get(position);
        if(messageEntity.isMyself()){
            holder.textRight.setText(messageEntity.getMessage());
            holder.nameRight.setText(messageEntity.getUserName());

            holder.left.setVisibility(View.INVISIBLE);
            holder.right.setVisibility(View.VISIBLE);
        }else {

            String content=messageEntity.getMessage();
            String name=messageEntity.getUserName();

            if(name.equals("TOM")){
                holder.imageLeft.setImageResource(R.drawable.jinguanzhang);
                holder.nameLeft.setText("TOM");
            }

            else if(name.equals("JACK")){
                holder.imageLeft.setImageResource(R.drawable.erkang);
                holder.nameLeft.setText("JACK");
            }

            holder.textLeft.setText(content);

            holder.left.setVisibility(View.VISIBLE);
            holder.right.setVisibility(View.INVISIBLE);
        }



    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class ChatContentViewHolder extends RecyclerView.ViewHolder {

        private TextView textLeft;
        private TextView textRight;
        private LinearLayout left;
        private LinearLayout right;
        private ImageView imageLeft;
        private TextView nameRight;
        private TextView nameLeft;

        public ChatContentViewHolder(View itemView) {
            super(itemView);
            textLeft = (TextView) itemView.findViewById(R.id.text_left);
            textRight = (TextView) itemView.findViewById(R.id.text_right);
            left=(LinearLayout)itemView.findViewById(R.id.left);
            right=(LinearLayout)itemView.findViewById(R.id.right);
            imageLeft=(ImageView)itemView.findViewById(R.id.image_left);
            nameRight=(TextView)itemView.findViewById(R.id.nameRight);
            nameLeft=(TextView)itemView.findViewById(R.id.nameLeft);
        }
    }
}

