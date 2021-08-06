package dev.piglin.piglinworldapi.block;

import dev.piglin.piglinworldapi.gui.CustomGui;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Your custom block. Extend this class to make a block.
 * <p>
 * How this works:
 * There are mushrooms. Many mushrooms. 192 in total (3 types * 64=2^6 boolean fields in BlockState).
 * Some of them are used (deprecated in {@link Mushroom}), some are not.
 * You can override one {@link CustomBlock#mushroom} to be your custom block.
 * We'll take care of any mutations done to the mushroom block (or at least we try to do this)
 */
public abstract class CustomBlock {
    static LinkedList<CustomBlock> blocks = new LinkedList<>();
    public final Mushroom mushroom;

    /**
     * @param mushroom The mushroom to use
     */
    public CustomBlock(Mushroom mushroom) {
        this.mushroom = mushroom;
    }

    /**
     * Handle interaction at the block
     *
     * @param event The event
     * @return gui to open if any
     */
    public abstract Optional<CustomGui> onInteract(PlayerInteractEvent event);

    /**
     * Handle block break
     *
     * @param block The block
     * @param tool  The tool
     * @return true if the block should spawn as an item
     */
    public abstract boolean breakBlock(Block block, ItemStack tool);

    /**
     * Handle block placement
     *
     * @param block The block
     * @return false to cancel
     */
    public abstract boolean placeBlock(Block block);

    /**
     * Handle the block load (when the chunk is being loaded)
     *
     * @param block The block
     */
    public abstract void onLoad(Block block);

    /**
     * Handle the block unload (when the chunk is being unloaded)
     *
     * @param block The block
     */
    public abstract void onUnload(Block block);

    /**
     * @return Loaded blocks of this type
     */
    public List<Block> getBlocks() {
        return BlockController.getBlocks(this.getClass());
    }
}
