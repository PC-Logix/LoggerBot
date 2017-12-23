package pcl.lc.irc;

import java.sql.SQLException;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import pcl.lc.utils.Database;

public class Commands extends ListenerAdapter {
	@Override
	public void onMessage(MessageEvent event) {
		if (event.getMessage().startsWith(Config.commandprefix) || IRCBot.isOp(IRCBot.bot, event.getUser())) {
			if (event.getMessage().startsWith(Config.commandprefix + "join")) {
				IRCBot.bot.sendIRC().joinChannel(event.getMessage().split("\\s+")[1]);
				try {
					Database.preparedStatements.get("addChannel").setString(1, event.getMessage().split("\\s+")[1]);
					Database.preparedStatements.get("addChannel").executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (event.getMessage().startsWith(Config.commandprefix + "part")) {
				IRCBot.bot.sendRaw().rawLineNow("part " + event.getMessage().split("\\s+")[1]);
				try {
					Database.preparedStatements.get("removeChannel").setString(1, event.getMessage().split("\\s+")[1]);
					Database.preparedStatements.get("removeChannel").executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (event.getMessage().startsWith(Config.commandprefix + "chgnick")) {
				IRCBot.bot.sendIRC().changeNick(event.getMessage().split("\\s+")[1]);
				Config.nick = event.getMessage().split("\\s+")[1];
				Config.prop.setProperty("commandprefix", event.getMessage().split("\\s+")[1]);
				Config.saveProps();
			}
		}
	}
}
