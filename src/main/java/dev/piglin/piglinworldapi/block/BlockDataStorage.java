package dev.piglin.piglinworldapi.block;

import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class BlockDataStorage implements TabExecutor {
    final HashMap<Block, List<String>> data = new HashMap<>();

    /**
     * @deprecated Should be refactored to be private or package-private
     * @param storage The file to load data from
     */
    @Deprecated
    public BlockDataStorage(File storage) {
        try {
            var stream = new FileInputStream(storage);
            var buf = Unpooled.wrappedBuffer(stream.readAllBytes());
            stream.close();
            if (!buf.isReadable()) return;

            var length = buf.readInt();
            for (var i = 0; i < length; i++) {
                var bytes = new byte[buf.readShort()];
                buf.readBytes(bytes);
                var tag = new String(bytes);
                var locations = buf.readInt();
                for (var j = 0; j < locations; j++) {
                    var bytes2 = new byte[buf.readShort()];
                    buf.readBytes(bytes2);
                    var world = new String(bytes2);
                    var x = buf.readInt();
                    var y = buf.readInt();
                    var z = buf.readInt();
                    set(new Location(Bukkit.getWorld(world), x, y, z).getBlock(), tag);
                }
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Error initializing BlockDataStorage", exception);
        }
    }

    /**
     * Checks if the block contains the tag
     * @param block The block
     * @param tag The tag
     * @param plugin The plugin
     * @return true if the block contains the plugin:tag pair
     */
    public boolean is(Block block, String tag, Plugin plugin) {
        Validate.notNull(block);
        Validate.notNull(tag);
        Validate.notNull(plugin);
        Validate.isTrue(!tag.isBlank());
        Validate.isTrue(tag.length() + plugin.getName().length() < 32767, "The tag's length + plugin's name length must be less than 32767");
        return is(block, plugin.getName() + ":" + tag);
    }

    boolean is(Block block, String tag) {
        var blockTags = data.get(block);
        return blockTags != null && blockTags.contains(tag);
    }

    /**
     * Sets the tag for the block
     * @param block The block
     * @param tag The tag
     * @param plugin The plugin
     * @return true if set successfully, false if already set
     */
    public boolean set(Block block, String tag, Plugin plugin) {
        Validate.notNull(block);
        Validate.notNull(tag);
        Validate.notNull(plugin);
        Validate.isTrue(!tag.isBlank());
        Validate.isTrue(tag.length() + plugin.getName().length() < 32767, "The tag's length + plugin's name length must be less than 32767");
        return set(block, plugin.getName() + ":" + tag);
    }

    boolean set(Block block, String tag) {
        if (is(block, tag)) {
            return false;
        }
        var tags = data.get(block);
        if (tags == null) {
            data.put(block, Lists.newArrayList(tag));
        } else {
            tags.add(tag);
        }
        return true;
    }

    /**
     * Removes the tag from the block
     * @param block The block
     * @param tag The tag
     * @param plugin The plugin
     * @return true if unset successfully, false if not set
     */
    public boolean unset(Block block, String tag, Plugin plugin) {
        Validate.notNull(block);
        Validate.notNull(tag);
        Validate.notNull(plugin);
        Validate.isTrue(!tag.isBlank());
        Validate.isTrue(tag.length() + plugin.getName().length() < 32767, "The tag's length + plugin's name length must be less than 32767");
        return unset(block, plugin.getName() + ":" + tag);
    }

    boolean unset(Block block, String tag) {
        if (!is(block, tag)) {
            return false;
        }
        var tags = data.get(block);
        tags.remove(tag);
        if (tags.isEmpty()) {
            data.remove(block);
        }
        return true;
    }

    /**
     * @param block The block
     * @param plugin The plugin
     * @return List of all your plugin's tags in this block
     */
    public @NotNull List<String> getTags(Block block, Plugin plugin) {
        return data.containsKey(block)
                ? getTags(block)
                .stream()
                .filter(tag -> tag.startsWith(plugin.getName() + ":"))
                .map(tag -> tag.substring(plugin.getName().length() + 1))
                .toList()
                : Collections.emptyList();
    }

    private List<String> getTags(Block block) {
        return data.get(block);
    }

    /**
     * Save the block data to the file
     * @param storage The file to save
     */
    public void save(File storage) {
        var map = new HashMap<String, List<Block>>();
        data.forEach((block, tags) -> tags.forEach(tag -> {
            if (!map.containsKey(tag)) {
                map.put(tag, Lists.newArrayList(block));
            } else {
                map.get(tag).add(block);
            }
        }));

        try {
            var buf = Unpooled.buffer();

            buf.writeInt(map.size());
            map.forEach((tag, blocks) -> {
                buf.writeShort(tag.getBytes().length)
                        .writeBytes(tag.getBytes())
                        .writeInt(blocks.size());
                blocks.forEach(block -> buf.writeShort(block.getWorld().getName().getBytes().length)
                        .writeBytes(block.getWorld().getName().getBytes())
                        .writeInt(block.getX())
                        .writeInt(block.getY())
                        .writeInt(block.getZ()));
            });

            var stream = new FileOutputStream(storage);
            stream.write(buf.array());
            stream.close();
        } catch (Exception exception) {
            throw new IllegalStateException("Error initializing CustomBlockDataStorage", exception);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length != 1) return false;
        switch (command.getName()) {
            case "checkblocktag" -> sender.sendMessage(is(((Player) sender).getTargetBlock(null, 5), args[0]) ? "yes" : "no");
            case "addblocktag" -> {
                set(((Player) sender).getTargetBlock(null, 5), args[0]);
                sender.sendMessage("Done.");
            }
            case "removeblocktag" -> {
                unset(((Player) sender).getTargetBlock(null, 5), args[0]);
                sender.sendMessage("Done.");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length != 1 && !(sender instanceof Player)) return Collections.emptyList();
        return data.get(((Player) sender).getTargetBlock(null, 5));
    }
}
