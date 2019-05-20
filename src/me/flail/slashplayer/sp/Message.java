package me.flail.slashplayer.sp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class Message extends Logger {
	private DataFile file = plugin.messages;
	private String prefix = chat(plugin.getConfig().getString("Prefix"));
	private List<String> message = new ArrayList<>();

	public Message(String key) {
		message.clear();
		if (file.hasList(key)) {
			message = file.getList(key);
			return;
		}
		message.add(file.getValue(key));
	}

	/**
	 * Sends this message to the specified <code>subject</code>
	 * Will automatically translate all basic placeholders for this user, (including the
	 * <code>operator</code> user if, not null.
	 * 
	 * @param subject
	 *                     the user to send this message to.
	 * @param operator
	 *                     (optional) the operator, usually the one executing a command towards the
	 *                     subject. Used for placeholders.
	 */
	public void send(User subject, @Nullable User operator) {
		for (String msg : message) {
			msg = this.placeholders(msg, subject.commonPlaceholders());
			if (operator != null) {
				msg = msg.replace("%operator%", operator.name());
			}

			subject.player().sendMessage(chat(msg));
		}
	}

	public DataFile getFile() {
		return file;
	}

	public String stringValue() {
		return message.get(0).replace("%prefix%", msgPrefix());
	}

	public String msgPrefix() {
		return prefix;
	}

	public String toSingleString() {
		String msg = "";
		for (String line : message) {
			msg = msg.concat(line + " ");
		}
		return msg;
	}

	public Message placeholders(Map<String, String> placeholders) {
		List<String> newMsg = new ArrayList<>();
		for (String line : message) {
			line = this.placeholders(line, placeholders);
			newMsg.add(line);
		}

		message.clear();
		message.addAll(newMsg);

		return this;
	}

}

