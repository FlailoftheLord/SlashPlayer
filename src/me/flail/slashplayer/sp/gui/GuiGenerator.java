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

					if (index < 55) {
						items.put(Integer.valueOf(index), userHead);
					} else {
						console("&cThere are more than 54 players online... "
								+ "This may cause issues with some GUIs on SlashPlayer, "
								+ "as it doesn't currently support multiple pages for the Player list.");
					}
					index++;
				}
			}

			int i = 1;
			while (items.size() < 55) {
				if (!items.containsKey(Integer.valueOf(i))) {
					items.put(Integer.valueOf(i), fillerItem(file));
				}
				i++;
			}


			new GeneratedGui(file, items).create(file.name().toLowerCase().replace("gui.yml", ""));
		}

		private void loadPlain(DataFile file) {
			int headerSlot = file.getNumber("HeaderSlot");

			new GeneratedGui(file, items).create(file.name().toLowerCase().replace("gui.yml", ""));
		}



		/**
		 * Grabs the color code which modifies the substring {@linkplain before} in the string
		 * {@linkplain string}
		 * 
		 * @param string
		 * @param before
		 */
		private String getColor(String string, String before) {
			String first = string.split(before)[0];
			char c = first.charAt(first.lastIndexOf("&") + 1);
			return "&" + c;
		}

		private ItemStack fillerItem(DataFile file) {
			ItemStack item = new ItemStack(Material.AIR);
			String filler = file.getValue("Format.FillerItem").replaceAll("[0-9]", "").toUpperCase();
			if (Material.matchMaterial(filler) != null) {
				item.setType(Material.matchMaterial(filler));
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("");
				item.setItemMeta(meta);
			}

			return item;
		}

	}

}
