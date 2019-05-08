package me.flail.slashplayer.sp.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.flail.slashplayer.gui.GeneratedGui;
import me.flail.slashplayer.gui.Gui;
import me.flail.slashplayer.tools.DataFile;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class GuiGenerator extends Logger {

	/**
	 * idek... ;p
	 */
	public GuiGenerator() {
	}

	/**
	 * RUN IT Baby!
	 */
	public void run() {
		new Generator().runTaskLaterAsynchronously(plugin, 3L);
	}

	private class Generator extends BukkitRunnable {
		private String[] guiList;
		public Generator() {
			guiList = plugin.guiFiles.clone();
		}

		private Gui gui;
		private Map<Integer, ItemStack> items;

		@Override
		public void run() {
			for (String fileName : guiList) {
				DataFile file = new DataFile("GuiConfigurations/" + fileName);
				gui = new GuiControl(null);

				if (file.hasValue("Type")) {
					switch (file.getValue("Type").toLowerCase()) {

					case "list":
						loadList(file);
						break;
					case "plain":
						loadPlain(file);
						break;
					}
					continue;
				}
			}
			/** LOOP **/
		}

		private void loadList(DataFile file) {
			List<User> userList = new ArrayList<>(8);
			String source = file.getValue("Source");
			DataFile userListFile = null;
			if (source.contains("%") || source.contains("online-players")) {
				userList.addAll(plugin.players);
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

			int index = 1;

			if (file.hasValue("Format")) {
				for (User user : userList) {
					String name = chat(file.getValue("Format.Name").replace("%player%", user.name()));
					List<String> lore = file.getList("Format.Lore");

					List<String> actualLore = new ArrayList<>();
					actualLore.add(chat("&8" + user.id()));

					boolean completed = false;


					if (source.equals("ReportedPlayers.yml")) {
						for (String line : lore) {
							if (line.contains("%information%")) {

								lore.add(chat(line.replace("%information%", "")));
								String info = userListFile.getValue("Information");
								String color = getColor(info, "%information%");

								/** BEGIN lore oranization **/
								while (info.length() > 18) {
									int lastIndex = info.lastIndexOf(" ", 18);

									if (lastIndex > -1) {
										if (lastIndex >= 48) {
											actualLore.add(chat("  " + color + info.substring(0, 48)));
											info = info.substring(49, info.length());
											continue;
										}
										actualLore.add(chat("  " + color + info.substring(0, lastIndex)));
										info = info.substring(lastIndex + 1, info.length());

									} else {
										lore.add(chat("  " + color + info));
										break;
									}

								}
								/** END of lore organization **/

								continue;
							}

							actualLore.add(chat(line.replace("%reporter%",
									userListFile.getValue("Reporter").replace("%player%", user.name()))));
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

					ItemStack userHead = user.getSkull();
					ItemMeta meta = userHead.getItemMeta();

					meta.setDisplayName(name);
					meta.setLore(actualLore);
					userHead.setItemMeta(meta);

					items.put(Integer.valueOf(index), userHead);
				}
			}

			plugin.loadedGuis.add(new GeneratedGui(gui, items));
		}

		private void loadPlain(DataFile file) {

		}

		private String getColor(String string, String before) {
			String first = string.split(before)[0];
			char c = first.charAt(first.lastIndexOf("&") + 1);
			return "&" + c;
		}

	}

}
