package pcl.lc.irc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.WhoisEvent;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.wolfram.alpha.WAEngine;

import pcl.lc.utils.Account;
import pcl.lc.utils.Account.ExpiringToken;
import pcl.lc.utils.Database;

public class IRCBot {

	private Connection connection = Database.getConnection();
	public static IRCBot instance;
	public static boolean isDebug;
	//public static TimedHashMap messages = new TimedHashMap(600000, null );
	private static final int MAX_MESSAGES = 150;
	public static LinkedHashMap<UUID, List<String>> messages = new LinkedHashMap<UUID, List<String>>(MAX_MESSAGES + 1, .75F, false) {
		private static final long serialVersionUID = 3558133365599892107L;
		@SuppressWarnings("rawtypes")
		protected boolean removeEldestEntry(Map.Entry eldest) {
			return size() > MAX_MESSAGES;
		}
	};
	
	//Keep a list of invites recieved
	public static HashMap<String, String> invites = new HashMap<String, String>();
	//Keep a list of users, and what server they're connected from
	public static HashMap<String, String> users = new HashMap<String, String>();
	//Keep a list of authed users, this list is cleared on a timer set in Permissions.java
	public static HashMap<String, String> authed = new HashMap<String,String>();
	//List of bot admins
	public static HashMap<String, Integer> admins = new HashMap<String,Integer>();
	private final List<String> ops = new ArrayList<>();
	//List of ignored users
	public static ArrayList<String> ignoredUsers = new ArrayList<String>();
	public final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
	public static String ournick = null;
	private final Scanner scanner;

	public static final Logger log = LoggerFactory.getLogger(IRCBot.class);

	public static PircBotX bot;
	public static boolean isIgnored(String nick) {
		if (IRCBot.admins.containsKey(nick)) {
			return false;
		} else if (ignoredUsers.contains(nick)){
			return true;
		} else {
			return false;
		}
	}

	public static String getOurNick() {
		return ournick;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IRCBot() {
		scanner = new Scanner(System.in);
		instance = this;
		Config.setConfig();	
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
			return;
		}
		try {
			if (!initDatabase()) {
				log.error("Database Failure!");
				return;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		loadOps();
		loadChannels();
		//Database.setDBVer(Database.DB_VER);
		Database.updateDatabase();
		try {
			Config.config.addListener(new LoogerHook());
			bot = new PircBotX(Config.config.buildConfiguration());
			bot.startBot();		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private boolean initDatabase() throws SQLException {
			Database.init();
			Database.addStatement("CREATE TABLE IF NOT EXISTS Channels(name)");
			Database.addStatement("CREATE TABLE IF NOT EXISTS Info(key PRIMARY KEY, data)");
			//Channels
			Database.addPreparedStatement("addChannel", "REPLACE INTO Channels (name) VALUES (?);");
			Database.addPreparedStatement("removeChannel","DELETE FROM Channels WHERE name = ?;");
			//Ops
			Database.addStatement("CREATE TABLE IF NOT EXISTS Ops(name, level)");
			Database.addPreparedStatement("removeOp","DELETE FROM Ops WHERE name = ?;");
			Database.addPreparedStatement("addOp","REPLACE INTO Ops (name) VALUES (?);");
			Database.addPreparedStatement("getOps", "SELECT name FROM ops;");
			return true;
	}

	@Deprecated
	public void sendMessage(String target, String message) {
		bot.sendIRC().message(target, message);
	}

	private void loadOps() {
		try {
			ResultSet readOps = Database.preparedStatements.get("getOps").executeQuery();
			int rowCount = 0;
			while (readOps.next()) {
				rowCount++;
				ops.add(readOps.getString("name"));
			}
			if (rowCount == 0) {
				log.info("Please enter the primary nickserv name of the first person with op privileges for the bot:\n> ");
				String op = scanner.nextLine();
				ops.add(op);
				Database.preparedStatements.get("addOp").setString(1, op);
				Database.preparedStatements.get("addOp").executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void loadChannels() {
		try {
			ResultSet readChannels = Database.getConnection().createStatement().executeQuery("SELECT name FROM channels;");
			int rowCount = 0;
			while (readChannels.next()) {
				rowCount++;
				log.info(readChannels.getString("name"));
				Config.config.addAutoJoinChannel(readChannels.getString("name"));
			}
			if (rowCount == 0) {
				log.info("Please enter the first channel the bot should join eg #channelname:\n> ");
				String channel = scanner.nextLine();
				Config.config.addAutoJoinChannel(channel);
				Database.preparedStatements.get("addChannel").setString(1, channel);
				Database.preparedStatements.get("addChannel").executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * use Database.getPreparedStatement
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public PreparedStatement getPreparedStatement(String statement) throws Exception {
		return Database.getPreparedStatement(statement);
	}

	public static IRCBot getInstance() {
		return instance;
	}

	public List<String> getOps() {
		return ops;
	}

	public static void setDebug(boolean b) {
		isDebug = b;
	}
	
	public static boolean getDebug() {
		return isDebug;
	}

	public static boolean isOp(PircBotX sourceBot, User user) {
		long startTime = System.currentTimeMillis();
		String nsRegistration = "";
		if (IRCBot.getDebug())
			System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() +"#"+ Thread.currentThread().getStackTrace()[2].getMethodName());
		if (Account.userCache.containsKey(user.getUserId()) && Account.userCache.get(user.getUserId()).getExpiration().after(Calendar.getInstance().getTime())) {
			nsRegistration = Account.userCache.get(user.getUserId()).getValue();
			Calendar future = Calendar.getInstance();
			if (!IRCBot.getDebug()) {
				System.out.println("Not Debugging setting cache to 10 hours");
				future.add(Calendar.HOUR,10);
			} else {
				System.out.println("Debugging setting cache to 30 seconds");
				future.add(Calendar.SECOND,30);
			}
			Account.userCache.put(user.getUserId(), new ExpiringToken(future.getTime(),nsRegistration));
			IRCBot.log.debug(user.getNick() + " is cached");
		} else {
			IRCBot.log.debug(user.getNick() + " is NOT cached");
			try {
				sourceBot.sendRaw().rawLine("WHOIS " + user.getNick() + " " + user.getNick());
				WaitForQueue waitForQueue = new WaitForQueue(sourceBot);
				WhoisEvent whoisEvent = waitForQueue.waitFor(WhoisEvent.class);
				waitForQueue.close();
				if (whoisEvent.getRegisteredAs() != null) {
					nsRegistration = whoisEvent.getRegisteredAs();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!nsRegistration.isEmpty()) {
				Calendar future = Calendar.getInstance();
				if (!IRCBot.getDebug())
					future.add(Calendar.MINUTE,10);
				else
					future.add(Calendar.SECOND,30);
				Account.userCache.put(user.getUserId(), new ExpiringToken(future.getTime(),nsRegistration));
				IRCBot.log.debug(user.getUserId().toString() + " added to cache: " + nsRegistration + " expires at " + future.getTime().toString());
			}
		}
		long endTime = System.currentTimeMillis();
		if (IRCBot.getDebug())
			System.out.println("That took " + (endTime - startTime) + " milliseconds");
		if (IRCBot.instance.getOps().contains(nsRegistration)) {
			return true;
		} else {
			return false;
		}
	}

    public static File getThisJarFile() throws UnsupportedEncodingException
    {
      //Gets the path of the currently running Jar file
        String path = IRCBot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");

        //This is code especially written for running and testing this program in an IDE that doesn't compile to .jar when running.
        if (!decodedPath.endsWith(".jar"))
        {
            return new File("LanteaBot.jar");
        }
        return new File(decodedPath);   //We use File so that when we send the path to the ProcessBuilder, we will be using the proper System path formatting.
    }
	
}
