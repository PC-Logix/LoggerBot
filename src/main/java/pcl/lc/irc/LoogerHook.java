package pcl.lc.irc;

import java.sql.*;
import java.util.Date;
import java.util.Iterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.pircbotx.Channel;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.ModeEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.QuitEvent;

public class LoogerHook extends ListenerAdapter {
	private Connection con;
	public LoogerHook() {
		System.out.println(System.getProperty("java.class.path"));
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			con=DriverManager.getConnection(  
					Config.mysqlServer,Config.mysqlUser,Config.mysqlPass);   
		} catch(Exception e){ System.out.println(e); } 
	}

	@Override
	public void onMessage(MessageEvent event) {
		int lineNum = 1;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String select = "SELECT `linenum`,`date` FROM `logs` WHERE `channel`='"+event.getChannel().getName()+"' AND `date`='"+dateFormat.format(date)+"' ORDER BY `linenum` DESC LIMIT 1;";

		// create the java statement
		Statement st;
		try {
			st = con.createStatement();

			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(select);

			// iterate through the java resultset
			while (rs.next()) {
				if (rs.getString("date").equals(dateFormat.format(date)))
					lineNum = rs.getInt("linenum") + 1;
				else
					lineNum = 1;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
		Date date2 = new Date();
		String query = " insert into logs (date, timestamp, channel, linenum, message)"
				+ " values (?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = con.prepareStatement(query);
			preparedStmt.setString (1, dateFormat.format(date));
			preparedStmt.setString (2, dateFormat2.format(date2));
			preparedStmt.setString (3, event.getChannel().getName());
			preparedStmt.setInt    (4, lineNum);
			preparedStmt.setString (5, "<"+event.getUser().getNick()+"> "+ event.getMessage().replaceAll("[\\p{Cf}]", ""));

			// execute the preparedstatement
			preparedStmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onAction(ActionEvent event) {
		int lineNum = 1;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String select = "SELECT `linenum`,`date` FROM `logs` WHERE `channel`='"+event.getChannel().getName()+"' AND `date`='"+dateFormat.format(date)+"' ORDER BY `linenum` DESC LIMIT 1;";

		// create the java statement
		Statement st;
		try {
			st = con.createStatement();

			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(select);

			// iterate through the java resultset
			while (rs.next()) {
				if (rs.getString("date").equals(dateFormat.format(date)))
					lineNum = rs.getInt("linenum") + 1;
				else
					lineNum = 1;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
		Date date2 = new Date();
		String query = " insert into logs (date, timestamp, channel, linenum, message)"
				+ " values (?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = con.prepareStatement(query);
			preparedStmt.setString (1, dateFormat.format(date));
			preparedStmt.setString (2, dateFormat2.format(date2));
			preparedStmt.setString (3, event.getChannel().getName());
			preparedStmt.setInt    (4, lineNum);
			preparedStmt.setString (5, "* "+event.getUser().getNick()+" "+ event.getMessage());

			// execute the preparedstatement
			preparedStmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onMode(ModeEvent event) {
		int lineNum = 1;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String select = "SELECT `linenum`,`date` FROM `logs` WHERE `channel`='"+event.getChannel().getName()+"' AND `date`='"+dateFormat.format(date)+"' ORDER BY `linenum` DESC LIMIT 1;";

		// create the java statement
		Statement st;
		try {
			st = con.createStatement();

			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(select);

			// iterate through the java resultset
			while (rs.next()) {
				if (rs.getString("date").equals(dateFormat.format(date)))
					lineNum = rs.getInt("linenum") + 1;
				else
					lineNum = 1;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
		Date date2 = new Date();
		String query = " insert into logs (date, timestamp, channel, linenum, message)"
				+ " values (?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = con.prepareStatement(query);
			preparedStmt.setString (1, dateFormat.format(date));
			preparedStmt.setString (2, dateFormat2.format(date2));
			preparedStmt.setString (3, event.getChannel().getName());
			preparedStmt.setInt    (4, lineNum);
			preparedStmt.setString (5, "*** "+event.getUser().getNick()+" sets mode: "+ event.getMode());

			// execute the preparedstatement
			preparedStmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onNickChange(NickChangeEvent event) {
		int lineNum = 1;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String oldNick;
		String newNick;
		for (Channel channel : IRCBot.bot.getUserChannelDao().getAllChannels()) {
			oldNick = event.getOldNick();
			newNick = event.getNewNick();
			String select = "SELECT `linenum`,`date` FROM `logs` WHERE `channel`='"+channel.getName()+"' AND `date`='"+dateFormat.format(date)+"' ORDER BY `linenum` DESC LIMIT 1;";
			Statement st;
			try {
				st = con.createStatement();
				ResultSet rs = st.executeQuery(select);
				while (rs.next()) {
					if (rs.getString("date").equals(dateFormat.format(date)))
						lineNum = rs.getInt("linenum") + 1;
					else
						lineNum = 1;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
			Date date2 = new Date();
			String query = " insert into logs (date, timestamp, channel, linenum, message)"
					+ " values (?, ?, ?, ?, ?)";
			PreparedStatement preparedStmt;
			try {
				preparedStmt = con.prepareStatement(query);
				preparedStmt.setString (1, dateFormat.format(date));
				preparedStmt.setString (2, dateFormat2.format(date2));
				preparedStmt.setString (3, channel.getName());
				preparedStmt.setInt    (4, lineNum);
				preparedStmt.setString (5, "*** " + oldNick + " is now known as "+ newNick);

				// execute the preparedstatement
				preparedStmt.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

	
	@Override
	public void onKick(KickEvent event) {
		int lineNum = 1;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String select = "SELECT `linenum`,`date` FROM `logs` WHERE `channel`='"+event.getChannel().getName()+"' AND `date`='"+dateFormat.format(date)+"' ORDER BY `linenum` DESC LIMIT 1;";

		// create the java statement
		Statement st;
		try {
			st = con.createStatement();

			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(select);

			// iterate through the java resultset
			while (rs.next()) {
				if (rs.getString("date").equals(dateFormat.format(date)))
					lineNum = rs.getInt("linenum") + 1;
				else
					lineNum = 1;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
		Date date2 = new Date();
		String query = " insert into logs (date, timestamp, channel, linenum, message)"
				+ " values (?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = con.prepareStatement(query);
			preparedStmt.setString (1, dateFormat.format(date));
			preparedStmt.setString (2, dateFormat2.format(date2));
			preparedStmt.setString (3, event.getChannel().getName());
			preparedStmt.setInt    (4, lineNum);
			preparedStmt.setString (5, "*** "+event.getRecipient().getNick()+" was kicked by " + event.getUser().getNick() + " ("+ event.getReason() + ")");

			// execute the preparedstatement
			preparedStmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onJoin(JoinEvent event) {
		int lineNum = 1;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String select = "SELECT `linenum`,`date` FROM `logs` WHERE `channel`='"+event.getChannel().getName()+"' AND `date`='"+dateFormat.format(date)+"' ORDER BY `linenum` DESC LIMIT 1;";

		// create the java statement
		Statement st;
		try {
			st = con.createStatement();

			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(select);

			// iterate through the java resultset
			while (rs.next()) {
				if (rs.getString("date").equals(dateFormat.format(date)))
					lineNum = rs.getInt("linenum") + 1;
				else
					lineNum = 1;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
		Date date2 = new Date();
		String query = " insert into logs (date, timestamp, channel, linenum, message)"
				+ " values (?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = con.prepareStatement(query);
			preparedStmt.setString (1, dateFormat.format(date));
			preparedStmt.setString (2, dateFormat2.format(date2));
			preparedStmt.setString (3, event.getChannel().getName());
			preparedStmt.setInt    (4, lineNum);
			preparedStmt.setString (5, "*** Joins: " + event.getUser().getNick() + " ("+event.getUserHostmask().getHostmask()+")");

			// execute the preparedstatement
			preparedStmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onQuit(QuitEvent event) {
		int lineNum = 1;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String user;
		String reason;
		for (Channel channel : IRCBot.bot.getUserChannelDao().getAllChannels()) {
			user = event.getUserHostmask().getNick();
			String select = "SELECT `linenum`,`date` FROM `logs` WHERE `channel`='"+channel.getName()+"' AND `date`='"+dateFormat.format(date)+"' ORDER BY `linenum` DESC LIMIT 1;";
			Statement st;
			try {
				st = con.createStatement();
				ResultSet rs = st.executeQuery(select);
				while (rs.next()) {
					if (rs.getString("date").equals(dateFormat.format(date)))
						lineNum = rs.getInt("linenum") + 1;
					else
						lineNum = 1;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if (event.getReason() != null)
				reason = event.getReason();
			else
				reason = "Client Quit";
			DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
			Date date2 = new Date();
			String query = " insert into logs (date, timestamp, channel, linenum, message)"
					+ " values (?, ?, ?, ?, ?)";
			PreparedStatement preparedStmt;
			try {
				preparedStmt = con.prepareStatement(query);
				preparedStmt.setString (1, dateFormat.format(date));
				preparedStmt.setString (2, dateFormat2.format(date2));
				preparedStmt.setString (3, channel.getName());
				preparedStmt.setInt    (4, lineNum);
				preparedStmt.setString (5, "*** Quits: " + user + " ("+reason+")");

				// execute the preparedstatement
				preparedStmt.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

}
