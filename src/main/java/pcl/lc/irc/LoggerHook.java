package pcl.lc.irc;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.joda.time.LocalDate;
import org.pircbotx.Channel;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.ModeEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class LoggerHook extends ListenerAdapter {
	private ScheduledFuture<?> executor;

	private LocalDate lastCheck = null;

	public boolean isNewDay() {
	  LocalDate today = LocalDate.now();
	  boolean ret = lastCheck == null || today.isAfter(lastCheck);
	  lastCheck = today;
	  return ret;
	}
	
	public LoggerHook() {
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		executor = ses.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					if (!chanLines.isEmpty()) {
						for (String channelName : chanLines.keys()) {
							Collection<PreparedStatement> stmtItr = chanLines.get(channelName);
							Iterator<PreparedStatement> iter = stmtItr.iterator();
							while (iter.hasNext()) {
								Integer lineNum = 1;
								PreparedStatement stmt = iter.next();
								DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
								Date date = new Date();
								String select = "SELECT COUNT(`linenum`) as `total`,`date` FROM `logs` WHERE `channel`='"+channelName+"' AND `date`='"+dateFormat.format(date)+"' ORDER BY `linenum`;";
								// create the java statement
								Statement st;
									try {
										st = IRCBot.con.createStatement();
										// execute the query, and get a java resultset
										ResultSet rs = st.executeQuery(select);
										// iterate through the java resultset
										while (rs.next()) {
											if (rs.getString("date") != null && rs.getString("date").equals(dateFormat.format(date))) {
												lineNum = rs.getInt("total") + 1;
										} else {
												lineNum = 1;
											}
										}
									} catch (SQLException ex) {
										ex.printStackTrace();
										throw new RuntimeException(ex);  // maybe create a new exception type?
									}
								stmt.setInt    (4, lineNum);
								stmt.execute();
								if (chanLines.containsEntry(channelName, stmt))
									chanLines.remove(channelName, stmt);
							}
						}
					}	
				} catch (Exception e){
					//Just eat the error it's a java.util.ConcurrentModificationException
					//and frankly I don't care right now.
					//e.printStackTrace();
					//System.out.println("It's dead jim " + e.getClass());
				}
			}
		}, 0, 500, TimeUnit.MILLISECONDS);
	}
	public Multimap<String, PreparedStatement> chanLines = ArrayListMultimap.create();

	@Override
	public void onMessage(MessageEvent event) {
		updateDB(event.getChannel().getName(), "<"+event.getUser().getNick()+"> "+ event.getMessage().replaceAll("[\\p{Cf}]", ""));
	}

	@Override
	public void onAction(ActionEvent event) {
		updateDB(event.getChannel().getName(), "* "+event.getUser().getNick()+" "+ event.getMessage().replaceAll("[\\p{Cf}]", ""));
	}

	@Override
	public void onMode(ModeEvent event) {
		updateDB(event.getChannel().getName(), "*** "+event.getUser().getNick()+" sets mode: "+ event.getMode());
	}
	
	@Override
	public void onPart(PartEvent event) {
		String reason;
		String hostmask;
		String nick = event.getUserHostmask().getNick();
		hostmask = event.getUserHostmask().getHostmask();
		if (event.getReason() != null) {
			reason = event.getReason();
		} else {
			reason = "User Left";
		}
		System.out.println("*** Parts: "+nick+" ("+hostmask+") ("+reason+")");
		updateDB(event.getChannel().getName(), "*** Parts: "+nick+" ("+hostmask+") ("+reason+")");
	}

	@Override
	public void onNickChange(NickChangeEvent event) {
		String oldNick = event.getOldNick();
		String newNick = event.getNewNick();
		String nick = event.getOldNick();
		for (String channelName : IRCBot.channelNicks.keySet()) {
			if (IRCBot.channelNicks.get(channelName).contains(nick)) {			
				updateDB(channelName, "*** " + oldNick + " is now known as "+ newNick);
			}
			IRCBot.channelNicks.get(channelName).remove(nick);
		}
		updateNickList();
	}


	@Override
	public void onKick(KickEvent event) {
		updateDB(event.getChannel().getName(), "*** "+event.getRecipient().getNick()+" was kicked by " + event.getUser().getNick() + " ("+ event.getReason() + ")");
		updateNickList(event.getChannel());
	}

	@Override
	public void onJoin(JoinEvent event) {
		updateDB(event.getChannel().getName(), "*** Joins: " + event.getUser().getNick() + " ("+event.getUserHostmask().getHostmask()+")");
		updateNickList(event.getChannel());
	}

	@Override
	public void onQuit(QuitEvent event) {
		String reason;
		String hostmask;
		String nick = event.getUserHostmask().getNick();
		for (String channelName : IRCBot.channelNicks.keySet()) {
			if (IRCBot.channelNicks.get(channelName).contains(nick)) {
				hostmask = event.getUserHostmask().getHostmask();
				if (event.getReason() != null)
					reason = event.getReason();
				else
					reason = "Client Quit";

				updateDB(channelName, "*** Quits: " + nick + " ("+hostmask+") ("+reason+")");
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

	public void updateDB(String channelName, String message) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
		Date date2 = new Date();
		String query = " insert into logs (date, timestamp, channel, linenum, message)"
				+ " values (?, ?, ?, ?, ?)";
		try {
			PreparedStatement preparedStmt;
			preparedStmt = IRCBot.con.prepareStatement(query);
			preparedStmt.setString (1, dateFormat.format(date));
			preparedStmt.setString (2, dateFormat2.format(date2));
			preparedStmt.setString (3, channelName);
			//we get the line number in the runnable
			//to make sure we don't duplicate them..
			//preparedStmt.setInt    (4, lineNum);
			preparedStmt.setString (5, message);
			chanLines.put(channelName, preparedStmt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
