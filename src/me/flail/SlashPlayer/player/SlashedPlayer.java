package me.flail.SlashPlayer.player;

import java.util.UUID;

import org.bukkit.entity.Player;

public class SlashedPlayer {
	private Player player;
	private UUID uuid;

	public SlashedPlayer(Player player) {
		this.player = player;
		uuid = player.getUniqueId();
	}

}
