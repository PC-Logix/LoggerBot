package pcl.lc.irc;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
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

	@Override
	public void onMessage(MessageEvent event) {
		int lineNum = 1;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String select = "SELECT `linenum`,`date` FROM `logs` WHERE `channel`='"+event.getChannel().getName()+"' AND `date`='"+dateFormat.format(date)+"' ORDER BY `linenum` DESC LIMIT 1;";

		// create the java statement
		Statement st;
		try {
			st = IRCBot.con.createStatement();

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
			preparedStmt = IRCBot.con.prepareStatement(query);
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
			st = IRCBot.con.createStatement();

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
			preparedStmt = IRCBot.con.prepareStatement(query);
			preparedStmt.setString (1, dateFormat.format(date));
			preparedStmt.setString (2, dateFormat2.format(date2));
			preparedStmt.setString (3, event.getChannel().getName());
			preparedStmt.setInt    (4, lineNum);
			preparedStmt.setString (5, "* "+event.getUser().getNick()+" "+ event.getMessage().replaceAll("[\\p{Cf}]", ""));

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
			st = IRCBot.con.createStatement();

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
			preparedStmt = IRCBot.con.prepareStatement(query);
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
		String oldNick = event.getOldNick();
		String newNick = event.getNewNick();
		String nick = event.getOldNick();
		for (String channelName : IRCBot.channelNicks.keySet()) {
			if (IRCBot.channelNicks.get(channelName).contains(nick)) {			String select = "SELECT `linenum`,`date` FROM `logs` WHERE `channel`='"+channelName+"' AND `date`='"+dateFormat.format(date)+"' ORDER BY `linenum` DESC LIMIT 1;";
			Statement st;
			try {
				st = IRCBot.con.createStatement();
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
				preparedStmt = IRCBot.con.prepareStatement(query);
				preparedStmt.setString (1, dateFormat.format(date));
				preparedStmt.setString (2, dateFormat2.format(date2));
				preparedStmt.setString (3, channelName);
				preparedStmt.setInt    (4, lineNum);
				preparedStmt.setString (5, "*** " + oldNick + " is now known as "+ newNick);

				// execute the preparedstatement
				preparedStmt.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			IRCBot.channelNicks.get(channelName).remove(nick);
		}
		updateNickList();
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
			st = IRCBot.con.createStatement();

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
			preparedStmt = IRCBot.con.prepareStatement(query);
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
		updateNickList(event.getChannel());
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
			st = IRCBot.con.createStatement();

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
			preparedStmt = IRCBot.con.prepareStatement(query);
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
		updateNickList(event.getChannel());
	}

	@Override
	public void onQuit(QuitEvent event) {
		int lineNum = 1;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String user;
		String reason;
		String hostmask;
		String nick = event.getUserHostmask().getNick();
		for (String channelName : IRCBot.channelNicks.keySet()) {
			if (IRCBot.channelNicks.get(channelName).contains(nick)) {
				hostmask = event.getUserHostmask().getHostmask();
				String select = "SELECT `linenum`,`date` FROM `logs` WHERE `channel`='"+channelName+"' AND `date`='"+dateFormat.format(date)+"' ORDER BY `linenum` DESC LIMIT 1;";
				Statement st;
				try {
					st = IRCBot.con.createStatement();
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
					preparedStmt = IRCBot.con.prepareStatement(query);
					preparedStmt.setString (1, dateFormat.format(date));
					preparedStmt.setString (2, dateFormat2.format(date2));
					preparedStmt.setString (3, channelName);
					preparedStmt.setInt    (4, lineNum);
					preparedStmt.setString (5, "*** Quits: " + nick + " ("+hostmask+") ("+reason+")");

					// execute the preparedstatement
					preparedStmt.execute();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		updateNickList();
	}

	public void updateNickList() {
		if (!IRCBot.bot.isConnected()) {
			return;
		}
		for (Channel channel : IRCBot.bot.getUserChannelDao().getAllChannels()) {
			this.updateNickList(channel);
		}
	}

	public void updateNickList(Channel channel) {
		if (!IRCBot.bot.isConnected()) {
			return;
		}
		// Build current list of names in channel
		ArrayList<String> users = new ArrayList<>();
		for (org.pircbotx.User user : channel.getUsers()) {
			//plugin.logDebug("N: " + user.getNick());
			users.add(user.getNick());
		}
		try {
			IRCBot.wl.tryLock(10, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ex) {
			return;
		}
		try {
			String channelName = channel.getName();
			IRCBot.channelNicks.put(channelName, users);
		} finally {
			IRCBot.wl.unlock();
		}
	}

}
