package me.flail.SlashPlayer.Executables;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.Utilities.ExeHandler;
import me.flail.SlashPlayer.Utilities.Tools;

public class Executioner {

	private SlashPlayer plugin;

	public Executioner(SlashPlayer instance) {
		plugin = instance;
	}

	public boolean execute(Player target, Player operator, String executable, String configSection, boolean closeInv,
			boolean offline) {

		Tools chat = new Tools();
		ExeHandler handler = new ExeHandler();

		String command = "slashplayer";

		String pUuid = target.getUniqueId().toString();

		FileConfiguration pData = plugin.getPlayerData();
		FileConfiguration guiConfig = plugin.getGuiConfig();
		FileConfiguration config = plugin.getConfig();
		FileConfiguration messages = plugin.getMessages();

		if (!executable.isEmpty() && (executable != null)) {

			String cantUseExe = messages.getString("AccessDenied").toString();

			boolean verbose = config.getBoolean("ConsoleVerbose");

			if (operator.hasPermission("slashplayer." + handler.exeType(executable))) {

				int operatorRank = Tools.playerRank(operator);
				int playerRank = Tools.playerRank(target);

				boolean equalsCanExecute = config.getBoolean("EqualsCanExecute");

				if ((operatorRank > playerRank) || ((operatorRank >= playerRank) && equalsCanExecute)) {

					String exe = handler.exeType(executable);

					switch (exe) {

					case "teleport":
						operator.sendMessage(chat
								.m(messages.get("TeleportPlayer").toString().replace("%player%", target.getName())));
						operator.teleport(target);

						break;
					case "summon":
						operator.sendMessage(
								chat.m(messages.get("SummonPlayer").toString().replace("%player%", target.getName())));
						target.sendMessage(chat.m(messages.get("Summoned").toString()
								.replace("%operator%", operator.getName()).replace("%player%", target.getName())));

						target.teleport(operator);

						break;
					case "heal":
						operator.sendMessage(
								chat.m(messages.get("HealPlayer").toString().replace("%player%", target.getName())));
						target.sendMessage(chat.m(messages.get("Healed").toString()
								.replace("%player%", target.getName()).replace("%operator%", operator.getName())));

						for (PotionEffect eff : target.getActivePotionEffects()) {
							target.removePotionEffect(eff.getType());
						}
						target.setHealth(20);

						break;
					case "feed":
						operator.sendMessage(
								chat.m(messages.get("FeedPlayer").toString().replace("%player%", target.getName())));
						target.sendMessage(chat.m(messages.get("Fed").toString().replace("%player%", target.getName())
								.replace("%operator%", operator.getName())));

						target.setFoodLevel(26);
						break;
					case "fly":

						break;
					case "gamemode":

					case "kick":

					case "kill":

					case "clearinventory":

					case "whitelist":

					case "freeze":

					case "unfreeze":

					}

					plugin.logAction(
							operator.getName() + " ran executable: " + exe.toUpperCase() + " on " + target.getName());

					if (verbose) {
						plugin.console.sendMessage("%prefix% " + operator.getName() + " ran executable: "
								+ exe.toUpperCase() + " on " + target.getName());
					}

				} else {
					String lowRank = messages.getString("RankTooLow").toString();
					operator.sendMessage(chat.m(lowRank));
				}

			} else {
				operator.sendMessage(chat.m(cantUseExe.replace("%executable%", handler.exeType(executable))));
			}

		}

		return true;
	}

}
