package pcl.lc.irc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.pircbotx.Configuration;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.cap.SASLCapHandler;
import org.pircbotx.cap.TLSCapHandler;

import pcl.lc.utils.CommentedProperties;

public class Config {

	public static HashMap<String, Object> botConfig = new HashMap<String, Object>();

	public static String nick = null;
	public static String nspass = null;
	public static String nsaccount = null;
	public static String channels = null;
	public static String ignoredUsersProp = null;
	public static String commandprefix = null;
	public static String proxyhost = null;
	public static String proxyport = null;
	public static String enableTLS = null;
	public static String enableSSL = null;
	public static String mysqlServer = null;
	public static String mysqlUser = null;
	public static String mysqlPass = null;
	static String adminProps = null;
	@SuppressWarnings("rawtypes")
	public static Builder config = new Configuration.Builder();
	public static CommentedProperties prop = new CommentedProperties();

	public static void saveProps() {
		FileOutputStream output = null;
		try {
			output = new FileOutputStream("config.properties");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			prop.store(output, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setConfig() {
		InputStream input = null;

		try {

			File file = new File("config.properties");
			if (!file.exists()) {
				System.out.println("Config file missing, edit config.default, and rename to config.properties");
				System.exit(1);
			}
		    final Version VERSION = new Version(
		            "@versionMajor@",
		            "@versionMinor@",
		            "@versionRevision@",
		            "@versionBuild@"
		    );
		    if (VERSION.getMajor() == -1) {
		    	IRCBot.setDebug(true);
		    }
			Config.config.setVersion("Neo LoggerBot Build# " + VERSION);
			input = new FileInputStream(file);
			// load a properties file
			prop.load(input);
			botConfig.put("server", prop.getProperty("server", "irc.esper.net"));
			botConfig.put("serverport", prop.getProperty("serverport", "6667"));
			botConfig.put("serverpass", prop.getProperty("serverpass", ""));
			nick = prop.getProperty("nick","Neo");
			nspass = prop.getProperty("nspass", "");
			nsaccount = prop.getProperty("nsaccount", "");
			ignoredUsersProp = prop.getProperty("ignoredUsers", "");
			commandprefix = prop.getProperty("commandprefix", "@");
			proxyhost = prop.getProperty("proxyhost", "");
			proxyport = prop.getProperty("proxyport", "");
			adminProps = prop.getProperty("admins", "");
			enableTLS = prop.getProperty("enableTLS", "false");
			enableSSL = prop.getProperty("enableSSL", "false");
			mysqlServer = prop.getProperty("mysqlServer", "");
			mysqlUser = prop.getProperty("mysqlUser", "");
			mysqlPass = prop.getProperty("mysqlPass", "");
			
			saveProps();


			if (!Config.proxyhost.isEmpty()) {
				System.setProperty("socksProxyHost",Config.proxyhost);
				System.setProperty("socksProxyPort",Config.proxyport);
			}

			Config.config.setRealName(Config.nick).setName(Config.nick).setLogin(Config.nick);
			Config.config.setAutoNickChange(true);
			Config.config.setCapEnabled(true);
			Config.config.setAutoReconnect(true);
			Config.config.setAutoNickChange(true);
			Config.config.setAutoSplitMessage(true);
			if (!Config.nspass.isEmpty())
				Config.config.addCapHandler(new SASLCapHandler(Config.nsaccount, Config.nspass, true));
				//Config.config.setNickservPassword(Config.nspass);


			Config.config.addCapHandler(new EnableCapHandler("extended-join", true));
			Config.config.addCapHandler(new EnableCapHandler("account-notify", true));
			Config.config.setEncoding(Charset.forName("UTF-8"));
			if (Config.enableTLS.equals("true")) {
				Config.config.addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true));
			} 
			Config.config.setSnapshotsEnabled(false);
			if (Config.enableSSL.equals("true")) {
				Config.config.addServer(Config.botConfig.get("server").toString(), Integer.parseInt(Config.botConfig.get("serverport").toString()))
			    .setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates()).setServerPassword(Config.botConfig.get("serverpass").toString());
			} else {
				Config.config.addServer(Config.botConfig.get("server").toString(), Integer.parseInt(Config.botConfig.get("serverport").toString()))
			    .setServerPassword(Config.botConfig.get("serverpass").toString());
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
