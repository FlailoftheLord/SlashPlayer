package me.flail.slashplayer.sp.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.flail.slashplayer.gui.GeneratedGui;
import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class GuiGenerator extends Logger {
	private String guiName;
	private DataFile file;

	/**
	 * idek... ;p
	 */
	public GuiGenerator(String guiName) {
		this.guiName = guiName;
	}

	public GuiGenerator(DataFile file) {
		this.file = file;
		guiName = file.name();
	}

	public DataFile file() {
		file = new DataFile("GuiConfigurations/" + guiName);
		return file;
	}

	/**
	 * RUN IT Baby!
	 */
	public void run() {
		new Generator(guiName).runTask(plugin);
	}

	private class Generator extends BukkitRunnable {
		private Logger util = new Logger();
		String fileName;
		DataFile file;

		public Generator(String guiName) {
			fileName = guiName;
			file = new DataFile("GuiConfigurations/" + fileName);
		}

		private Map<Integer, ItemStack> items = new HashMap<>();

		@Override
		public void run() {

			if (file.hasValue("Type")) {
				switch (file.getValue("Type").toLowerCase()) {

				case "list":
					loadList(file);
					break;
				case "plain":
					loadPlain(file);
					break;
				}
			}
			return;
		}


		private void loadList(DataFile file) {
			List<User> userList = new ArrayList<>(8);
			String source = file.getValue("Source");
			if (source == null) {
				console("&cInvalid source path for the " + file.name() + " Gui file!");
				console("&cPlease specify a source for this List file. "
						+ "&7You can use &f%online-players% &7for a list of the players who are currently online.");

				return;
			}

			DataFile userListFile = null;
			if (source.contains("online-players")) {
				userList.clear();
				userList.addAll(plugin.players.values());
			} else if (source.contains("inventory-backup")) {
				new DataFile("GuiConfigurations/RestoreInvGui.yml");

				return;
			} else {
				if (!source.endsWith(".yml")) {
					source = source.concat(".yml");
				}
				userListFile = new DataFile(source);
				for (String s : userListFile.keySet()) {
					User user = new User(UUID.fromString(s));
					userList.add(user);
				}

			}

			int index = 0;

			if (file.hasValue("Format")) {
				for (User user : userList) {
					if ((user == null) || user.equals(null)) {
						continue;
					}

					String name = chat(file.getValue("Format.Name").replace("%player%", user.name()));
					List<String> lore = file.getList("Format.Lore");

					List<String> actualLore = new ArrayList<>();
					actualLore.add(chat("&8" + user.id()));

					boolean completed = false;


					if (source.equals("ReportedPlayers.yml")) {
						for (String line : lore.toArray(new String[] {})) {
							if (line.contains("%information%")) {

								String info = userListFile.getValue(user.id() + ".Reason");
								String color = getColor(info, "%information%");

								actualLore.add(chat(line.replace("%information%", "")));

								/** BEGIN lore oranization **/
								while (info.length() > 24) {
									int lastIndex = info.lastIndexOf(" ", 24);

									if (lastIndex > -1) {
										if (lastIndex >= 48) {
											actualLore.add(chat("  " + color + info.substring(0, 48)));
											info = info.substring(49, info.length());
											continue;
										}
										actualLore.add(chat("  " + color + info.substring(0, lastIndex)));
										info = info.substring(lastIndex + 1, info.length());

									} else {
										actualLore.add(chat("  " + color + info));
										break;
									}

								}
								/** END of lore organization **/

								if (info.length() < 25) {
									actualLore.add(chat("  " + color + info));
									continue;
								}

								continue;
							}

							User reporter = new User(UUID.fromString(userListFile.getValue(user.id() + ".Reporter")));

							actualLore.add(chat(line.replace("%reporter%", reporter.name())));
						}

						actualLore.add(chat("&8Shift Click to remove."));
						completed = true;
					}

					if (!completed) {
						for (String line : lore) {
							actualLore.add(chat(line).replace("%player%", user.name()));
						}

					}

					if (user.isOnline()) {
						name = name.concat(chat("  &a&l(Online)"));
					} else {
						name = name.concat(chat("  &c&l(Offline)"));
					}

					ItemStack item;

					if (!file.hasValue("Format.Item")) {
						item = user.getSkull();
					} else {
						item = new ItemStack(Material.matchMaterial(file.getValue("Foramt.Item")));
						item = addTag(item, "uuid", user.id());
					}
					ItemMeta meta = item.getItemMeta();

					meta.setDisplayName(name);
					meta.setLore(actualLore);
					item.setItemMeta(meta);

					if (source.equalsIgnoreCase("ReportedPlayers.yml")) {
						item = addTag(item, "shift-click-remove", "true");
					}

					if (index < 54) {
						items.put(Integer.valueOf(index), item);
					} else {
						console("&cThere are more than 54 players online... "
								+ "This may cause issues with some GUIs on SlashPlayer, "
								+ "as it doesn't currently support multiple pages for the Player list.");
						break;
					}
					index++;
				}
			}

			for (int i = 0; i < 54; i++) {
				if (!items.containsKey(Integer.valueOf(i))) {
					items.put(Integer.valueOf(i), fillerItem(file));
				}
			}

			new GeneratedGui(file, items).create(file.name());
		}

		private void loadPlain(DataFile file) {
			for (int i = 0; i < 54; i++) {
				String itemKey = "Format." + (i + 1) + "";
				if (file.hasValue(itemKey)) {
					items.put(Integer.valueOf(i), util.createItem(file, itemKey));
					continue;
				}

				items.put(Integer.valueOf(i), fillerItem(file));
			}

			new GeneratedGui(file, items).create(file.name());
		}

	}

}
