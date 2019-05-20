package me.flail.slashplayer.user;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.flail.slashplayer.SlashPlayer;
import me.flail.slashplayer.sp.Message;
import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.Logger;

public class UserData extends Logger {
	protected SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);
	protected UUID playerUuid;
	private DataFile file;

	protected UserData(UUID playerUuid) {
		this.playerUuid = playerUuid;
		file = new DataFile("/PlayerData/" + playerUuid + ".yml");
	}

	protected DataFile getDataFile() {
		return file;
	}

	protected void loadDefaultValues(User user) {
		Map<String, Object> values = new HashMap<>();
		values.put("UUID", user.uuid().toString());
		values.put("Name", new String[] {user.name()});
		values.put("Online", Boolean.valueOf(true));
		values.put("IP", new String[] {user.ip()});
		values.put("Gamemode", user.gamemode());
		values.put("Frozen", Boolean.valueOf(false));
		values.put("Muted", Boolean.valueOf(false));
		values.put("Banned", Boolean.valueOf(false));
		/************* YOINK, tyvm! ******************/
		for (String key : values.keySet()) {
			if (!file.hasValue(key)) {
				file.setValue(key, values.get(key));
				continue;
			}
			String[] list = file.getArray(key);
			List<String> newList = new ArrayList<>();
			switch (key) {
			case "Name":
				boolean newName = false;
				for (String s : list) {
					newList.add(s);
					if (!s.equalsIgnoreCase(user.name())) {
						newName = true;
						continue;
					}
					newName = false;
				}

				if (newName) {
					newList.add(user.name());
					file.setValue(key, newList.toArray(new String[] {}));
				}
				break;
			case "IP":
				boolean newIp = false;
				for (String s : list) {
					newList.add(s);
					if (!s.equalsIgnoreCase(user.ip())) {
						newIp = true;
						continue;
					}
					newIp = false;
				}

				if (newIp) {
					newList.add(user.ip());
					file.setValue(key, newList.toArray(new String[] {}));
				}
				break;
			}

		}

	}

	public String banExpiry() {
		Instant instant = (Instant) getDataFile().getObj("UnbanTime");
		return new SimpleDateFormat("MM/dd/yyyy 'at' HH:mm.ss").format(Date.from(instant));
	}

	public Long banDuration() {
		return Long.valueOf(getDataFile().getValue("BanDuration").replaceAll("[^0-9]", ""));
	}

	public Message getBanMessage() {
		if (file.getBoolean("Banned")) {
			Map<String, String> placeholders = new HashMap<>();
			placeholders.put("%ban-duration%", banDuration() + "");

			Message banMsg = new Message("Banned");
			return banMsg.placeholders(placeholders);
		}
		return null;
	}

}
