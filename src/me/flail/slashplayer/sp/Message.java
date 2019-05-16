package me.flail.slashplayer.sp;

import java.util.Map;

import javax.annotation.Nullable;

import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class Message extends Logger {
	private DataFile file;
	private String prefix;
	private String message;

	public Message(String key) {
		file = plugin.messages;
		message = file.getValue(key);
		prefix = chat(plugin.getConfig().getString("Prefix"));
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
		message = this.placeholders(message, subject.commonPlaceholders());
		if (operator != null) {
			message = this.placeholders(message, operator.commonPlaceholders());
		}
		subject.player().sendMessage(message);
	}

	public DataFile getFile() {
		return file;
	}

	public String stringValue() {
		return message.replace("%prefix%", msgPrefix());
	}

	public String msgPrefix() {
		return prefix;
	}

	public String placeholders(Map<String, String> placeholders) {
		return this.placeholders(message, placeholders);
	}
}

