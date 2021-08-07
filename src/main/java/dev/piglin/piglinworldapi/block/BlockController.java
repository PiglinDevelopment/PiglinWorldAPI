package dev.piglin.piglinworldapi.block;

import com.google.common.collect.ImmutableMap;
import dev.piglin.piglinworldapi.PiglinWorldAPI;
import dev.piglin.piglinworldapi.util.InventoryUtils;
import dev.piglin.piglinworldapi.util.Triple;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class BlockController implements Listener {
    private static final HashMap<Class<? extends CustomBlock>, List<Block>> blocks = new HashMap<>();
    private final NamespacedKey tagsKey = new NamespacedKey(PiglinWorldAPI.getInstance(), "tags");

    /**
     * @deprecated Should be refactored to be private or package-private
     */
    @Deprecated
    public BlockController() {

    }

    /**
     * @param block The block to register
     */
    public static void registerBlock(CustomBlock block) {
        blocks.put(block.getClass(), new LinkedList<>());
        CustomBlock.blocks.add(block);
    }

    /**
     * @param clazz Block type of blocks to return
     * @return The blocks
     */
    public static List<Block> getBlocks(Class<? extends CustomBlock> clazz) {
        return blocks.get(clazz);
    }

    /**
     * Clears blocks cache (don't do this unless you really know what you're doing)
     */
    public static void clearBlocks() {
        blocks.clear();
    }

    /**
     * @return Map of all block types and corresponding mushroom blocks that are in loaded chunks
     */
    public static ImmutableMap<Class<? extends CustomBlock>, List<Block>> getAllBlocks() {
        return ImmutableMap.copyOf(blocks);
    }

    /**
     * @param block The block
     * @return The block's Mushroom
     */
    public static Mushroom getMushroom(Block block) {
        return getMushroom(block.getState());
    }

    /**
     * @param block The blockState
     * @return The blockState's Mushroom
     */
    public static Mushroom getMushroom(BlockState block) {
        if (!Mushroom.MushroomType.isMushroom(block.getType())) return null;
        var data = (MultipleFacing) block.getBlockData();
        var b1 = data.hasFace(BlockFace.DOWN) ? 1 : 0;
        var b2 = data.hasFace(BlockFace.EAST) ? 1 : 0;
        var b3 = data.hasFace(BlockFace.NORTH) ? 1 : 0;
        var b4 = data.hasFace(BlockFace.SOUTH) ? 1 : 0;
        var b5 = data.hasFace(BlockFace.UP) ? 1 : 0;
        var b6 = data.hasFace(BlockFace.WEST) ? 1 : 0;
        return Mushroom.fromCustomModel((b1 << 5 | b2 << 4 | b3 << 3 | b4 << 2 | b5 << 1 | b6) + Mushroom.MushroomType.valueOf(block.getType()).ordinal() * 0b111111);
    }

    /**
     * @param block The block
     * @return CustomBlock of this block, or empty if it's not a registered mushroom or not a mushroom at all
     */
    public Optional<? extends CustomBlock> getCustomBlock(Block block) {
        return getCustomBlock(getMushroom(block));
    }

    /**
     * @param mushroom The mushroom
     * @return CustomBlock of this mushroom, or empty if it's not a registered mushroom
     */
    public Optional<? extends CustomBlock> getCustomBlock(Mushroom mushroom) {
        return mushroom == null ? Optional.empty() : CustomBlock.blocks.stream().filter(block -> block.mushroom == mushroom).findAny();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockExplodeEvent event) {
        var random = new Random();
        event.blockList().forEach(block -> {
            if (random.nextFloat() <= event.getYield()) {
                var item = new ItemStack(block.getType());
                if (PiglinWorldAPI.getInstance().getBlockDataStorage().data.containsKey(block)) {
                    var meta = item.getItemMeta();
                    assert meta != null;
                    meta.getPersistentDataContainer().set(tagsKey, PersistentDataType.STRING, String.join("\u001E", PiglinWorldAPI.getInstance().getBlockDataStorage().data.get(block)));
                    item.setItemMeta(meta);
                }
                getCustomBlock(getMushroom(block)).ifPresent(customBlock -> {
                    if (customBlock.breakBlock(block, new ItemStack(Material.AIR))) {
                        event.getBlock().getWorld().dropItemNaturally(block.getLocation(), item);
                    }
                });
            }
        });
        PiglinWorldAPI.getInstance().getBlockDataStorage().data.remove(event.getBlock());
        event.setYield(0.0f);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDrop(BlockDropItemEvent event) {
        if(Mushroom.MushroomType.isMushroom(event.getBlockState().getType()) || PiglinWorldAPI.getInstance().getBlockDataStorage().data.containsKey(event.getBlock())) {
            event.setCancelled(true);
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                var item = new ItemStack(event.getBlockState().getType());
                var meta = item.getItemMeta();
                assert meta != null;
                if (PiglinWorldAPI.getInstance().getBlockDataStorage().data.containsKey(event.getBlock())) {
                    meta.getPersistentDataContainer().set(tagsKey, PersistentDataType.STRING, String.join("\u001E", PiglinWorldAPI.getInstance().getBlockDataStorage().data.get(event.getBlock())));
                }
                if (Mushroom.MushroomType.isMushroom(event.getBlockState().getType())) {
                    meta.setCustomModelData(getMushroom(event.getBlockState()).customModelData());
                }
                item.setItemMeta(meta);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
            }
        }

        PiglinWorldAPI.getInstance().getBlockDataStorage().data.remove(event.getBlock());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDrop(BlockBreakEvent event) {
        if (!event.isDropItems()) PiglinWorldAPI.getInstance().getBlockDataStorage().data.remove(event.getBlock());
        if (Mushroom.MushroomType.isMushroom(event.getBlock().getType())) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                var tool = event.getPlayer().getInventory().getItemInMainHand();
                var block = getCustomBlock(getMushroom(event.getBlock()));
                block.ifPresent(customBlock -> event.setDropItems(customBlock.breakBlock(event.getBlock(), tool)));
            }
            blocks.forEach((customBlockClass, customBlocks) -> {
                customBlocks.remove(event.getBlock());
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        var meta = event.getItemInHand().getItemMeta();
        assert meta != null;
        if (meta.getPersistentDataContainer().has(tagsKey, PersistentDataType.STRING)) {
            var tags = meta.getPersistentDataContainer().get(this.tagsKey, PersistentDataType.STRING).split("\u001E");
            for (var tag : tags) {
                PiglinWorldAPI.getInstance().getBlockDataStorage().set(event.getBlock(), tag);
            }
        }
        if (Mushroom.MushroomType.isMushroom(event.getBlock().getType())) {
            var model = event.getItemInHand().getItemMeta().getCustomModelData();
            var b1 = (model & 0b100000) != 0;
            var b2 = (model & 0b010000) != 0;
            var b3 = (model & 0b001000) != 0;
            var b4 = (model & 0b000100) != 0;
            var b5 = (model & 0b000010) != 0;
            var b6 = (model & 0b000001) != 0;
            var block = (MultipleFacing) event.getBlock().getBlockData();
            block.setFace(BlockFace.DOWN, b1);
            block.setFace(BlockFace.EAST, b2);
            block.setFace(BlockFace.NORTH, b3);
            block.setFace(BlockFace.SOUTH, b4);
            block.setFace(BlockFace.UP, b5);
            block.setFace(BlockFace.WEST, b6);
            event.getBlock().setBlockData(block, false);
            getCustomBlock(getMushroom(event.getBlock())).ifPresent(customBlock -> {
                if (!customBlock.placeBlock(event.getBlock())) {
                    event.setBuild(false);
                } else {
                    blocks.get(customBlock.getClass()).add(event.getBlock());
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.hasBlock()) {
            assert event.getClickedBlock() != null;
            if (Mushroom.MushroomType.isMushroom(event.getClickedBlock().getType())) {
                getCustomBlock(getMushroom(event.getClickedBlock())).flatMap(block -> block.onInteract(event)).ifPresent(inventory -> {
                    PiglinWorldAPI.getInstance().getGuiController().open(inventory, event.getPlayer());
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMiddleClickBlock(InventoryCreativeEvent event) {
        if (event.getAction() == InventoryAction.PLACE_ALL) {
            var material = event.getCursor().getType();
            if (Mushroom.MushroomType.isMushroom(material) && !event.getCursor().getItemMeta().hasCustomModelData()) {
                if (event.getWhoClicked().getTargetBlock(null, 5).getType() == material) {
                    var mushroom = getMushroom(event.getWhoClicked().getTargetBlock(null, 5));
                    var meta = event.getCursor().getItemMeta();
                    meta.setCustomModelData(mushroom.customModelData());
                    event.getCursor().setItemMeta(meta);
                    var slot = InventoryUtils.getSimilarSlots(event.getWhoClicked().getInventory(), event.getCursor()).get(0);
                    var airSlot = event.getWhoClicked().getInventory().firstEmpty();
                    if (slot == event.getWhoClicked().getInventory().getHeldItemSlot()) {
                        event.setCancelled(true);
                        event.getWhoClicked().getInventory().setHeldItemSlot(slot); // It's changed on client side, updating
                        if (airSlot != -1) event.getWhoClicked().getInventory().setItem(airSlot, null);
                    } else if (slot == -1) {
                        if (airSlot != -1 && airSlot < 9) {
                            if (event.getWhoClicked().getInventory().getItemInMainHand().getType() == Material.AIR) {
                                airSlot = event.getWhoClicked().getInventory().getHeldItemSlot();
                            }
                            event.setCancelled(true);
                            event.getWhoClicked().getInventory().setHeldItemSlot(airSlot);
                            event.getWhoClicked().getInventory().setItem(airSlot, event.getCursor());
                        }
                    } else if (slot < 9) {
                        event.setCancelled(true);
                        event.getWhoClicked().getInventory().setHeldItemSlot(slot);
                        if (airSlot != -1) event.getWhoClicked().getInventory().setItem(airSlot, null);
                    } else {
                        event.setCancelled(true);
                        var itemMushroom = event.getWhoClicked().getInventory().getItem(slot);
                        if (airSlot < 9) {
                            event.getWhoClicked().getInventory().setHeldItemSlot(airSlot);
                            event.getWhoClicked().getInventory().setItem(airSlot, itemMushroom);
                            event.getWhoClicked().getInventory().setItem(slot, null);
                        } else {
                            var itemInHand = event.getWhoClicked().getInventory().getItemInMainHand();
                            event.getWhoClicked().getInventory().setItem(event.getWhoClicked().getInventory().getHeldItemSlot(), itemMushroom);
                            event.getWhoClicked().getInventory().setItem(slot, itemInHand);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        if (Mushroom.MushroomType.isMushroom(event.getBlock().getType())) {
            event.setCancelled(true);

            var mushroom = event.getBlock().getType();
            var list = new ArrayList<Block>(100);
            list.add(event.getBlock());
            for (var i = 0; i < list.size(); i++) {
                var block = list.get(i);
                for (var face : BlockFace.values()) {
                    if (face.isCartesian()) {
                        var neighbor = block.getRelative(face);
                        if (neighbor.getType() == mushroom && !list.contains(neighbor)) {
                            list.add(neighbor);
                        }
                    }
                }
            }
            for (var block : list) {
                var data = block.getBlockData().clone();
                block.setType(mushroom, false);
                block.setBlockData(data, false);
            }
        }
    }

    public void loadChunk(ChunkSnapshot snapshot, int minHeight, int maxHeight) {
        new Thread(() -> {
            var locations = new LinkedList<Triple<Integer, Integer, Integer>>();
            for (var x = 0; x < 16; x++) {
                for (var y = minHeight; y < maxHeight; y++) {
                    for (var z = 0; z < 16; z++) {
                        var block = snapshot.getBlockData(x, y, z);
                        if (Mushroom.MushroomType.isMushroom(block.getMaterial())) {
                            locations.add(new Triple<>(x, y, z));
                        }
                    }
                }
            }
            Bukkit.getScheduler().runTask(PiglinWorldAPI.getInstance(), () -> {
                var world = Bukkit.getWorld(snapshot.getWorldName());
                var chunk = world.getChunkAt(snapshot.getX(), snapshot.getZ());
                locations.forEach(loc -> {
                    var block = chunk.getBlock(loc.left(), loc.middle(), loc.right());
                    getCustomBlock(getMushroom(block)).ifPresent(customBlock -> {
                        blocks.get(customBlock.getClass()).add(block);
                        customBlock.onLoad(block);
                    });
                });
            });
        }).start();
    }

    public void unloadChunk(Chunk chunk) {
        for (var x = 0; x < 16; x++) {
            for (var y = chunk.getWorld().getMinHeight(); y < chunk.getWorld().getMaxHeight(); y++) {
                for (var z = 0; z < 16; z++) {
                    var block = chunk.getBlock(x, y, z);
                    if (Mushroom.MushroomType.isMushroom(block.getType())) {
                        getCustomBlock(getMushroom(block)).ifPresent(customBlock -> {
                            blocks.get(customBlock.getClass()).add(block);
                            customBlock.onUnload(block);
                        });
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        loadChunk(event.getChunk().getChunkSnapshot(), event.getWorld().getMinHeight(), event.getWorld().getMaxHeight());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        unloadChunk(event.getChunk());
    }

    @EventHandler
    public void onPistonMove(BlockPistonExtendEvent event) {
        if (Mushroom.MushroomType.isMushroom(event.getBlock().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonMove(BlockPistonRetractEvent event) {
        if (Mushroom.MushroomType.isMushroom(event.getBlock().getType())) {
            event.setCancelled(true);
        }
    }
}
