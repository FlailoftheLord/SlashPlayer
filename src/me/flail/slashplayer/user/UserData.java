package me.flail.slashplayer.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.flail.slashplayer.SlashPlayer;
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
		values.put("Name", user.name());
		values.put("IP", user.ip());
		values.put("Gamemode", user.player().getGameMode().toString().toLowerCase());
		values.put("Frozen", Boolean.valueOf(false));
		values.put("Muted", Boolean.valueOf(false));
		values.put("Banned", Boolean.valueOf(false));
		/************* YOINK, tyvm! ******************/
		for (String key : values.keySet()) {
			if (!file.hasValue(key)) {
				file.setValue(key, values.get(key));
			}
		}

	}

}
