package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class MinaServer {

	private static List<IoSession> sessions=new ArrayList<>();
	
	public static void main(String[] args) {

		IoAcceptor acceptor = new NioSocketAcceptor();

		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
		acceptor.setHandler(new ChatServerHandler());

		try {
			acceptor.bind(new InetSocketAddress(9123));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class ChatServerHandler extends IoHandlerAdapter {

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {

			UserDB userDB = new UserDB();
			String[] content = message.toString().split(":");
			if (content[0].equals("LOGIN")) {
				userDB.connect();
				if (userDB.isLoginSuccess(content[1], content[2])) {
					session.write("LOGIN_SUCCESS");
				} else {
					session.write("LOGIN_FAILED");
				}
				userDB.disconnect();
			} else if (content[0].equals("REGISTER")) {
				userDB.connect();
				if (userDB.isRegisterSuccess(content[1], content[2])) {
					session.write("REGISTER_SUCCESS");
				} else {
					session.write("REGISTER_FAILED");
				}
				userDB.disconnect();
			}else if(content[0].equals("MSG")){
				for (int i = 0; i < sessions.size(); i++) {
					sessions.get(i).write(message);
				}
			}
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			System.out.println("sessioncreated");
			sessions.add(session);
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			System.out.println("sessionclosed");
		}
	}

	private static class UserDB {
		private String url;
		private String name;
		private String password;

		private Connection connection = null;
		private PreparedStatement psm = null;

		public void connect() {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				url = "jdbc:mysql://localhost:3306/userinfo";
				name = "root";
				password = "1234";

				try {
					connection = (Connection) DriverManager.getConnection(url, name, password);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		public void disconnect() {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		public boolean isLoginSuccess(String username, String password) {

			String sql = "select * from userinfo where username=? and password=?";
			try {
				psm = (PreparedStatement) connection.prepareStatement(sql);
				psm.setString(1, username);
				psm.setString(2, password);
				ResultSet rs = psm.executeQuery();
				if (rs.next())
					return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;

		}

		public boolean isRegisterSuccess(String username, String password) {

			String sql = "select * from userinfo where username=?";
			try {
				psm = (PreparedStatement) connection.prepareStatement(sql);
				psm.setString(1, username);
				ResultSet rs = psm.executeQuery();
				if (rs.next()) {
					return false;
				} else {
					String sql2 = "insert into userinfo(username,password) values(?,?)";
					psm = (PreparedStatement) connection.prepareStatement(sql2);
					psm.setString(1, username);
					psm.setString(2, password);
					psm.execute();
					return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;

		}

	}

}
