package me.flail.SlashPlayer.Utilities;

public class ExeHandler {

	public String exeType(String raw) {

		String name = raw.toLowerCase().trim();

		if (name.contains("gamemode")) {
			return "gamemode";
		} else if (name.contains("teleport")) {
			return "teleport";
		} else if (name.contains("summon") || name.contains("tphere")) {
			return "summon";
		} else if (name.contains("kick")) {
			return "kick";
		} else if (name.contains("kill")) {
			return "kill";
		} else if (name.contains("unmute")) {
			return "unmute";
		} else if (name.contains("mute")) {
			return "mute";
		} else if (name.contains("unban")) {
			return "unban";
		} else if (name.contains("ban")) {
			return "ban";
		} else if (name.contains("unfreeze")) {
			return "unfreeze";
		} else if (name.contains("freeze")) {
			return "freeze";
		} else if (name.contains("inv") || name.contains("clear")) {
			return "clearinventory";
		} else if (name.contains("heal")) {
			return "heal";
		} else if (name.contains("feed") || name.contains("food")) {
			return "feed";
		} else if (name.contains("whitelist")) {
			return "whitelist";
		} else {
			return "nothing";
		}

	}

}
