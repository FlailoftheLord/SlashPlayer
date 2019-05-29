package me.flail.slashplayer.sp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class Message extends Logger {

	private DataFile file = plugin.messages;
	private String prefix = chat(plugin.getConfig().getString("Prefix"));
	private static List<String> message = new ArrayList<>();
	String key;

	public Message(String key) {
		this.key = key;
		new Message(true, key);
	}

	public Message(boolean fromFile, String... key) {
		if ((key == null) || key.equals(null)) {
			return;
		}
		message.clear();

		if (fromFile) {
			this.key = key[0];
			if (file.hasList(this.key)) {
				message = file.getList(this.key);
				return;
			}
			message.add(file.getValue(this.key));
			return;
		}

		for (String s : key) {
			message.add(s);
		}

	}

	public static Message construct(String... value) {
		return new Message(false, value);
	}

	/**
	 * Sends this message to the specified <code>recipient</code>
	 * Will automatically translate all basic placeholders for this user, (including the
	 * <code>operator</code> user if, not null.
	 * 
	 * @param recipient
	 *                      the user to send this message to.
	 * @param operator
	 *                      (optional) the operator, usually the one executing a command towards the
	 *                      subject. Used for placeholders.
	 */
	public void send(User recipient, @Nullable User operator) {
		if (!message.isEmpty() && (message.get(0) != null)) {
			for (String msg : message) {
				msg = this.placeholders(msg, recipient.commonPlaceholders());
				if (operator != null) {
					msg = msg.replace("%operator%", operator.name()).replace("%reporter%", operator.name());
				}

				if (recipient.isOnline()) {
					recipient.player().sendMessage(chat(msg));
				}
			}
			return;
		}

		console("&cThe following message doesn't exist in your &7Messages.yml &cfile.  &f" + key);
		console("&cPlease be sure to add it to your Messages.yml file!");
		console("&cAdd this:  &7" + key + ": \"message goes inside these quotes\"");
	}

	public void broadcast(User recipient, @Nullable User operator) {
		if (!message.isEmpty() && (message.get(0) != null)) {
			for (String msg : message) {
				msg = this.placeholders(msg, recipient.commonPlaceholders());
				if (operator != null) {
					msg = msg.replace("%operator%", operator.name()).replace("%reporter%", operator.name());
				}

				plugin.server.broadcast(msg, "slashplayer.notify");
			}
			return;
		}

		console("&cThe following message doesn't exist in your &7Messages.yml &cfile.  &f" + key);
		console("&cPlease be sure to add it to your Messages.yml file!");
		console("&cAdd this:  &7" + key + ": \"message goes inside these quotes\"");
	}

	public void console(User recipient, @Nullable User operator) {
		if (!message.isEmpty() && (message.get(0) != null)) {
			for (String msg : message) {
				msg = this.placeholders(msg, recipient.commonPlaceholders());
				if (operator != null) {
					msg = msg.replace("%operator%", operator.name()).replace("%reporter%", operator.name());
				}

				console(msg);
			}
			return;
		}
	}

	public void log(User recipient, @Nullable User operator) {
		if (!message.isEmpty() && (message.get(0) != null)) {
			for (String msg : message) {
				msg = this.placeholders(msg, recipient.commonPlaceholders());
				if (operator != null) {
					msg = msg.replace("%operator%", operator.name()).replace("%reporter%", operator.name());
				}

				log(msg);
			}
			return;
		}

	}

	public Message replace(String value, String replacement) {
		Map<String, String> pl = new HashMap<>();
		pl.put(value, replacement);

		return this.placeholders(pl);
	}

	public DataFile getFile() {
		return file;
	}

	public String stringValue() {
		return chat(message.get(0));
	}

	public String msgPrefix() {
		return new String(prefix);
	}

	public String toSingleString() {
		String msg = "";
		for (String line : message) {
			msg = msg.concat(line + " ");
		}
		return msg;
	}

	public Message placeholders(Map<String, String> placeholders) {
		if (!message.isEmpty() && (message.get(0) != null)) {

			List<String> newMsg = new ArrayList<>();
			for (String line : message) {
				if (line != null) {
					line = this.placeholders(line, placeholders);
					newMsg.add(line);
				}
			}

			message.clear();
			message.addAll(newMsg);

		}
		return this;
	}

}

