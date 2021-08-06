package dev.piglin.piglinworldapi.event;

import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when the block redstone level is changed (or not changed, just updated)
 */
public class BlockRedstoneChangeEvent extends BlockRedstoneEvent {

    private static final HandlerList handlers = new HandlerList();
    protected final Block source;

    public BlockRedstoneChangeEvent(Block source, Block block, int old, int n) {
        super(block, old, n);
        this.source = source;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return The initiator of this update
     */
    @NotNull
    public Block getSource() {
        return source;
    }

    /**
     * @return true if old and new redstone levels are different, otherwise false
     */
    public boolean hasChanged() {
        return getOldCurrent() != getNewCurrent();
    }

    /**
     * @return true if the status powered/unpowered wasn't changed, false otherwise
     */
    public boolean isMinor() {
        return !hasChanged() || wasOn() == isOn();
    }

    public boolean isOn() {
        return getNewCurrent() > 0;
    }

    public boolean wasOn() {
        return getOldCurrent() > 0;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }
}
