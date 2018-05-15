package com.ab.lenovo.netminaservice;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;

public class ConnectionManager {

    private static final String BROADCAST_ACTION = "com.ab.lenovo.mina.broadcast";
    private static final String RECEIVE_ACTION="com.ab.lenovo.mina.receive";
    private static final String MESSAGE = "message";
    private ConnectionConfig mConfig;
    private WeakReference<Context> mContext;

    private NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;

    public ConnectionManager(ConnectionConfig config){

        this.mConfig = config;
        this.mContext = new WeakReference<>(config.getContext());
        init();
    }

    private void init() {
        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        mConnection = new NioSocketConnector();
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());
        mConnection.getFilterChain().addLast("logging", new LoggingFilter());
        mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
        mConnection.setHandler(new DefaultHandler(mContext.get()));
        mConnection.setDefaultRemoteAddress(mAddress);
    }

    public boolean connect(){
        try{
            ConnectFuture future = mConnection.connect();
            future.awaitUninterruptibly();
            mSession = future.getSession();

            SessionManager.getInstance().setSession(mSession);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return mSession == null ? false : true;
    }

    public void disconnect(){
        mConnection.dispose();
        mConnection=null;
        mSession=null;
        mAddress=null;
        mContext = null;
    }

    private static class DefaultHandler extends IoHandlerAdapter{

        private Context mContext;
        private DefaultHandler(Context context){
            this.mContext = context;

        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            if(mContext!=null){
                String[] contents = message.toString().split(":");
                if(contents.length==1){
                    Intent intent = new Intent();
                    intent.setAction(BROADCAST_ACTION);
                    intent.putExtra(MESSAGE, message.toString());
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }else if(contents[0].equals("MSG")){
                    Intent intent2=new Intent();
                    intent2.setAction(RECEIVE_ACTION);
                    intent2.putExtra(MESSAGE,message.toString());
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent2);
                }

            }
        }
    }
}
