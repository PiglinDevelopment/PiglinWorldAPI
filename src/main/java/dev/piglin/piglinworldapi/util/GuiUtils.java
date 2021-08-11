package dev.piglin.piglinworldapi.util;

import dev.piglin.piglinworldapi.PiglinWorldAPI;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GuiUtils {
    // TODO maybe make a single barrier for all plugins?
    private static Map<Plugin, ItemStack> barriers = new HashMap<>();

    /**
     * Loads "gui.barrier" item in the plugin's configuration
     *
     * @param plugin The plugin
     */
    public static void loadBarrier(Plugin plugin) {
        ItemStack barrier;
        try {
            barrier = read(plugin.getConfig().getConfigurationSection("gui.barrier"), null, null);
        } catch (Exception exception) {
            PiglinWorldAPI.getInstance().getLogger().warning("No gui.barrier in config.yml or I have a problem deserializing it.");
            barrier = new ItemStack(Material.AIR);
        }
        barriers.put(plugin, barrier);
    }

    /**
     * Reads the ItemStack from ConfigurationSection.
     * The section should be in following format:
     * <pre>
     * item:
     *  # Optional
     *  base: {bukkit serialized itemstack or none}
     *  # If base is not specified, the next line must be specified, and the second line is optional
     *  material: MATERIAL with %placeholders% and {replacements} (for {color}_BANNER or {item}_SLAB)
     *  amount: optional, default is 1
     *
     *  # Replacements
     *  name: "Item name with %placeholders% and {replacements}"
     *  lore: [ "Array of lines with %placeholders% and {replacements}" ]
     *  custom model data: 42
     * </pre>
     *
     * @param section      The section
     * @param replacements Replacements ({@link StringUtils#replace(String, Map, OfflinePlayer)})
     * @param player       The player
     * @return The ItemStack
     */
    public static ItemStack read(ConfigurationSection section, HashMap<String, String> replacements, OfflinePlayer player) {
        ItemStack base = null;
        if (section.contains("base")) {
            base = section.getItemStack("base");
        }
        if (base == null) {
            var material = Material.matchMaterial(StringUtils.replace(section.getString("material"), replacements, player));
            base = new ItemStack(material == null ? Material.BARRIER : material, section.getInt("amount", 1));
        }
        return replace(base, section, replacements, player);
    }

    /**
     * @param base         The base ItemStack to replace
     * @param section      replacements
     *                     If the section contains any of these fields, they replace default fields in base
     * <pre>{@code
     * name: "Item name with %placeholders% and {replacements}"
     * lore: [ "Array of lines with %placeholders% and {replacements}" ]
     * custom model data: 42
     * }</pre>
     * @param replacements Replacements ({@link StringUtils#replace(String, Map, OfflinePlayer)})
     * @param player       The player
     * @return The ItemStack with replaced data
     */
    public static ItemStack replace(ItemStack base, ConfigurationSection section, HashMap<String, String> replacements, OfflinePlayer player) {
        try {
            var meta = base.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(StringUtils.replace("&f" + section.getString("name"), replacements, player));
                meta.setLore(Arrays.asList(section.getStringList("lore").stream().map(line -> StringUtils.replace("&7" + line, replacements, player)).collect(Collectors.joining("\n")).split("\n")));
                meta.setCustomModelData(section.getInt("custom model data", 0));
                meta.addItemFlags(ItemFlag.values());
                base.setItemMeta(meta);
            }
            return base;
        } catch (Exception e) {
            System.err.println("Ошибка в конфиге предмета: " + section.getCurrentPath());
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }
    }

    /**
     * Replaces Material.AIR with your plugin's barrier (which is AIR by default, see {@link #loadBarrier(Plugin)})
     *
     * @param plugin    Your plugin
     * @param inventory The inventory
     */
    public static void fillWithBarriers(@Nullable Plugin plugin, Inventory inventory) {
        fillWith(getBarrier(plugin), inventory);
    }

    /**
     * Replaces Material.AIR with the specified item
     *
     * @param item      The item
     * @param inventory The inventory
     */
    public static void fillWith(@Nullable ItemStack item, Inventory inventory) {
        for (var i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)
                inventory.setItem(i, item);
        }
    }

    /**
     * Tries to get your plugin's barrier item. Loads the item if not found ({@link #loadBarrier(Plugin)})
     *
     * @param plugin Your plugin
     * @return The barrier item
     */
    public static ItemStack getBarrier(Plugin plugin) {
        if (!barriers.containsKey(plugin)) loadBarrier(plugin);
        return barriers.get(plugin);
    }
}
