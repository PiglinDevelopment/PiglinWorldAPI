/*
 * CraftBook Copyright (C) 2010, 2011 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 *
 * (https://github.com/EngineHub/CraftBook/blob/master/src/main/java/com/sk89q/craftbook/bukkit/MechanicListenerAdapter.java)
 */

package dev.piglin.piglinworldapi.block;

import dev.piglin.piglinworldapi.PiglinWorldAPI;
import dev.piglin.piglinworldapi.event.BlockRedstoneChangeEvent;
import dev.piglin.piglinworldapi.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RedstoneListener implements Listener {
    static HashMap<Block, HashMap<Block, Integer>> blocksRedstoneLevelBySource = new HashMap<>();
    private static Set<Material> isRedstoneBlock = new HashSet<>();

    static {
        isRedstoneBlock.add(Material.POWERED_RAIL);
        isRedstoneBlock.add(Material.DETECTOR_RAIL);
        isRedstoneBlock.add(Material.STICKY_PISTON);
        isRedstoneBlock.add(Material.PISTON);
        isRedstoneBlock.add(Material.LEVER);
        isRedstoneBlock.add(Material.STONE_PRESSURE_PLATE);
        isRedstoneBlock.addAll(Tag.WOODEN_PRESSURE_PLATES.getValues());
        isRedstoneBlock.add(Material.REDSTONE_TORCH);
        isRedstoneBlock.add(Material.REDSTONE_WALL_TORCH);
        isRedstoneBlock.add(Material.REDSTONE_WIRE);
        isRedstoneBlock.addAll(Tag.DOORS.getValues());
        isRedstoneBlock.add(Material.TNT);
        isRedstoneBlock.add(Material.DISPENSER);
        isRedstoneBlock.add(Material.NOTE_BLOCK);
        isRedstoneBlock.add(Material.REPEATER);
        isRedstoneBlock.add(Material.TRIPWIRE_HOOK);
        isRedstoneBlock.add(Material.COMMAND_BLOCK);
        isRedstoneBlock.addAll(Tag.BUTTONS.getValues());
        isRedstoneBlock.add(Material.TRAPPED_CHEST);
        isRedstoneBlock.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        isRedstoneBlock.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        isRedstoneBlock.add(Material.COMPARATOR);
        isRedstoneBlock.add(Material.REDSTONE_BLOCK);
        isRedstoneBlock.add(Material.HOPPER);
        isRedstoneBlock.add(Material.ACTIVATOR_RAIL);
        isRedstoneBlock.add(Material.DROPPER);
        isRedstoneBlock.add(Material.DAYLIGHT_DETECTOR);
        isRedstoneBlock = EnumSet.copyOf(isRedstoneBlock);
    }

    private static void checkBlockChange(Block block, boolean build, @Nullable Material oldMaterial) {
        if (oldMaterial == null) oldMaterial = block.getType();
        switch (oldMaterial) {
            case REDSTONE_TORCH:
            case REDSTONE_WALL_TORCH:
            case REDSTONE_BLOCK:
                handleRedstoneForBlock(block, build ? 0 : 15, build ? 15 : 0, oldMaterial);
                break;
            case ACACIA_BUTTON:
            case BIRCH_BUTTON:
            case DARK_OAK_BUTTON:
            case JUNGLE_BUTTON:
            case OAK_BUTTON:
            case SPRUCE_BUTTON:
            case STONE_BUTTON:
            case LEVER:
            case DETECTOR_RAIL:
            case STONE_PRESSURE_PLATE:
            case ACACIA_PRESSURE_PLATE:
            case BIRCH_PRESSURE_PLATE:
            case DARK_OAK_PRESSURE_PLATE:
            case JUNGLE_PRESSURE_PLATE:
            case OAK_PRESSURE_PLATE:
            case SPRUCE_PRESSURE_PLATE:
            case COMPARATOR:
            case REPEATER:
                Powerable powerable = (Powerable) block.getBlockData();
                if (powerable.isPowered())
                    handleRedstoneForBlock(block, build ? 0 : 15, build ? 15 : 0, oldMaterial);
                break;
            case HEAVY_WEIGHTED_PRESSURE_PLATE:
            case LIGHT_WEIGHTED_PRESSURE_PLATE:
            case REDSTONE_WIRE:
                AnaloguePowerable analoguePowerable = (AnaloguePowerable) block.getBlockData();
                if (analoguePowerable.getPower() > 0) {
                    handleRedstoneForBlock(block, build ? 0 : analoguePowerable.getPower(), build ? analoguePowerable.getPower() : 0, oldMaterial);
                }
                break;
            default:
                break;
        }
    }

    private static void handleRedstoneForBlock(Block block, int oldLevel, int newLevel, @Nullable Material oldMaterial) {
        World world = block.getWorld();

        // Give the method a BlockWorldVector instead of a Block
        boolean wasOn = oldLevel >= 1;
        boolean isOn = newLevel >= 1;
        boolean wasChange = wasOn != isOn;

        // For efficiency reasons, we're only going to consider changes between
        // off and on state, and ignore simple current changes (i.e. 15->13)
        if (!wasChange) return;

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        // When this hook has been called, the level in the world has not
        // yet been updated, so we're going to do this very ugly thing of
        // faking the value with the new one whenever the data value of this
        // block is requested -- it is quite ugly
        if (oldMaterial == null) oldMaterial = block.getType();
        switch (oldMaterial) {
            case REDSTONE_WIRE:

                Material above = world.getBlockAt(x, y + 1, z).getType();

                Material westSide = world.getBlockAt(x, y, z + 1).getType();
                Material westSideAbove = world.getBlockAt(x, y + 1, z + 1).getType();
                Material westSideBelow = world.getBlockAt(x, y - 1, z + 1).getType();
                Material eastSide = world.getBlockAt(x, y, z - 1).getType();
                Material eastSideAbove = world.getBlockAt(x, y + 1, z - 1).getType();
                Material eastSideBelow = world.getBlockAt(x, y - 1, z - 1).getType();

                Material northSide = world.getBlockAt(x - 1, y, z).getType();
                Material northSideAbove = world.getBlockAt(x - 1, y + 1, z).getType();
                Material northSideBelow = world.getBlockAt(x - 1, y - 1, z).getType();
                Material southSide = world.getBlockAt(x + 1, y, z).getType();
                Material southSideAbove = world.getBlockAt(x + 1, y + 1, z).getType();
                Material southSideBelow = world.getBlockAt(x + 1, y - 1, z).getType();

                // Make sure that the wire points to only this block
                if (!isRedstoneBlock(westSide) && !isRedstoneBlock(eastSide)
                        && (!isRedstoneBlock(westSideAbove) || westSide == Material.AIR || above != Material.AIR)
                        && (!isRedstoneBlock(eastSideAbove) || eastSide == Material.AIR || above != Material.AIR)
                        && (!isRedstoneBlock(westSideBelow) || westSide != Material.AIR)
                        && (!isRedstoneBlock(eastSideBelow) || eastSide != Material.AIR)) {
                    // Possible blocks north / south
                    handleDirectWireInput(x - 1, y, z, block, oldLevel, newLevel);
                    handleDirectWireInput(x + 1, y, z, block, oldLevel, newLevel);
                    handleDirectWireInput(x - 1, y - 1, z, block, oldLevel, newLevel);
                    handleDirectWireInput(x + 1, y - 1, z, block, oldLevel, newLevel);
                }

                if (!isRedstoneBlock(northSide) && !isRedstoneBlock(southSide)
                        && (!isRedstoneBlock(northSideAbove) || northSide == Material.AIR || above != Material.AIR)
                        && (!isRedstoneBlock(southSideAbove) || southSide == Material.AIR || above != Material.AIR)
                        && (!isRedstoneBlock(northSideBelow) || northSide != Material.AIR)
                        && (!isRedstoneBlock(southSideBelow) || southSide != Material.AIR)) {
                    // Possible blocks west / east
                    handleDirectWireInput(x, y, z - 1, block, oldLevel, newLevel);
                    handleDirectWireInput(x, y, z + 1, block, oldLevel, newLevel);
                    handleDirectWireInput(x, y - 1, z - 1, block, oldLevel, newLevel);
                    handleDirectWireInput(x, y - 1, z + 1, block, oldLevel, newLevel);
                }

                // Can be triggered from below
                handleDirectWireInput(x, y + 1, z, block, oldLevel, newLevel);

                // Can be triggered from above
                handleDirectWireInput(x, y - 1, z, block, oldLevel, newLevel);
                return;
            case REPEATER:
            case COMPARATOR:
                Directional diode = (Directional) block.getBlockData();
                BlockFace f = diode.getFacing();
                handleDirectWireInput(x - f.getModX(), y, z - f.getModZ(), block, oldLevel, newLevel);
                if (world.getBlockAt(x - f.getModX(), y, z - f.getModZ()).getType().isSolid()) {
                    handleDirectWireInput(x - f.getModX() * 2, y, z - f.getModZ() * 2, block, oldLevel, newLevel);
                }
                if (block.getRelative(f).getType() != Material.AIR) {
                    handleDirectWireInput(x + f.getModX(), y - 1, z + f.getModZ(), block, oldLevel, newLevel);
                    handleDirectWireInput(x + f.getModX(), y + 1, z + f.getModZ(), block, oldLevel, newLevel);
                    handleDirectWireInput(x + f.getModX() + 1, y - 1, z + f.getModZ(), block, oldLevel, newLevel);
                    handleDirectWireInput(x + f.getModX() - 1, y - 1, z + f.getModZ(), block, oldLevel, newLevel);
                    handleDirectWireInput(x + f.getModX() + 1, y - 1, z + f.getModZ() + 1, block, oldLevel, newLevel);
                    handleDirectWireInput(x + f.getModX() - 1, y - 1, z + f.getModZ() - 1, block, oldLevel, newLevel);
                }
                return;
            case ACACIA_BUTTON:
            case BIRCH_BUTTON:
            case DARK_OAK_BUTTON:
            case JUNGLE_BUTTON:
            case OAK_BUTTON:
            case SPRUCE_BUTTON:
            case STONE_BUTTON:
            case LEVER:
                Directional button = (Directional) block.getBlockData();
                BlockFace face = button.getFacing().getOppositeFace();
                handleDirectWireInput(x + face.getModX() * 2, y + face.getModY() * 2, z + face.getModZ() * 2, block, oldLevel, newLevel);
                break;
            case POWERED_RAIL:
            case ACTIVATOR_RAIL:
                return;
            case REDSTONE_TORCH:
            case REDSTONE_WALL_TORCH:
                if (y + 1 < world.getMaxHeight()) {
                    handleDirectWireInput(x, y + 1, z, block, oldLevel, newLevel);
                }
                if (y + 2 < world.getMaxHeight() && world.getBlockAt(x, y + 1, z).getType().isSolid()) {
                    handleDirectWireInput(x, y + 2, z, block, oldLevel, newLevel);
                }
                break;
        }

        // For redstone wires and repeaters, the code already exited this method
        // Non-wire blocks proceed
        handleDirectWireInput(x - 1, y, z, block, oldLevel, newLevel);
        handleDirectWireInput(x + 1, y, z, block, oldLevel, newLevel);
        handleDirectWireInput(x - 1, y - 1, z, block, oldLevel, newLevel);
        handleDirectWireInput(x + 1, y - 1, z, block, oldLevel, newLevel);
        handleDirectWireInput(x, y, z - 1, block, oldLevel, newLevel);
        handleDirectWireInput(x, y, z + 1, block, oldLevel, newLevel);
        handleDirectWireInput(x, y - 1, z - 1, block, oldLevel, newLevel);
        handleDirectWireInput(x, y - 1, z + 1, block, oldLevel, newLevel);

        // Can be triggered from below
        handleDirectWireInput(x, y + 1, z, block, oldLevel, newLevel);

        // Can be triggered from above
        handleDirectWireInput(x, y - 1, z, block, oldLevel, newLevel);
    }

    private static void handleDirectWireInput(int x, int y, int z, Block sourceBlock, int oldLevel, int newLevel) {
        Block block = sourceBlock.getWorld().getBlockAt(x, y, z);

        if (sourceBlock.equals(block)) //The same block, don't run.
            return;

        blocksRedstoneLevelBySource.putIfAbsent(block, new HashMap<>());
        var level = getRedstoneLevel(block);
        if (level == null) {
            blocksRedstoneLevelBySource.get(block).put(sourceBlock, newLevel);
            var event = new BlockRedstoneChangeEvent(sourceBlock, block, oldLevel, newLevel);
            Bukkit.getPluginManager().callEvent(event);
            return;
        }
        var oldLevelRegistered = level.getValue();
        blocksRedstoneLevelBySource.get(block).put(sourceBlock, newLevel);
        var newLevelRegistered = getRedstoneLevel(block).getValue();
        var event = new BlockRedstoneChangeEvent(sourceBlock, block, oldLevelRegistered, newLevelRegistered);
        Bukkit.getPluginManager().callEvent(event);
    }

    private static Pair<Block, Integer> getRedstoneLevel(Block block) {
        if (!blocksRedstoneLevelBySource.containsKey(block)) return null;
        return blocksRedstoneLevelBySource.get(block).entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Pair::of)
                .orElse(null);
    }

    /**
     * Returns true if a block uses Redstone in some way.
     *
     * @param id the type ID of the block
     * @return true if the block uses Redstone
     */
    public static boolean isRedstoneBlock(Material id) {
        return isRedstoneBlock.contains(id);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        checkBlockChange(event.getBlock(), false, null);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        checkBlockChange(event.getBlock(), true, null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPhysics(BlockPhysicsEvent event) {
        Bukkit.getScheduler().runTask(PiglinWorldAPI.getInstance(), () -> {
            if (event.getBlock().getType() == Material.AIR) {
                if (event.getChangedType() == Material.REDSTONE_TORCH || event.getChangedType() == Material.REDSTONE_WALL_TORCH) {
                    checkBlockChange(event.getBlock(), false, event.getChangedType());
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        handleRedstoneForBlock(event.getBlock(), event.getOldCurrent(), event.getNewCurrent(), null);
    }
}
