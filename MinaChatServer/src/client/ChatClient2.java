package client;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class ChatClient2 extends Frame {

	TextField tfTxt = new TextField();
	TextArea taContent = new TextArea();

	NioSocketConnector connector = null;
	IoSession session = null;

	public static void main(String[] args) {
		new ChatClient2().launchFrame();
	}

	public void launchFrame() {
		setLocation(400, 300);
		this.setSize(300, 300);
		this.setTitle("JACK");
		add(tfTxt, BorderLayout.SOUTH);
		add(taContent, BorderLayout.NORTH);
		pack();
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}
		});
		tfTxt.addActionListener(new TFListener());
		setVisible(true);
		connect();

	}

	public void connect() {

		connector = new NioSocketConnector();
		DefaultIoFilterChainBuilder chain = connector.getFilterChain();
		ProtocolCodecFilter filter = new ProtocolCodecFilter(new TextLineCodecFactory());
		chain.addLast("codec", filter);
		connector.setHandler(new MinaClientHanlder());
		ConnectFuture future = connector.connect(new InetSocketAddress("localhost", 9123));
		future.awaitUninterruptibly();
		session = future.getSession();

	}

	private class MinaClientHanlder extends IoHandlerAdapter {

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			System.out.println("sessioncreated");
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {

			String[] contents = message.toString().split(":");
			String name = contents[2];
			String content = contents[3];

			if(name.equals("JACK")||name.equals("ALL"))
			taContent.setText(taContent.getText() + content + '\n');
		}
	}

	public void disconnect() {
		connector.dispose();
	}

	private class TFListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String str = tfTxt.getText().trim();
			String content = "MSG:JACK:ALL:" + str;
			tfTxt.setText("");
			session.write(content);
		}

	}

}
