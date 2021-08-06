package dev.piglin.piglinworldapi.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

//TODO buttons
/**
 * Extend this class to make a custom gui.
 */
public abstract class CustomGui {
    /**
     * List of slots that a user is allowed to interact with.
     * You will only receive events if the slot is here.
     * Otherwise the event is silently cancelled.
     */
    final List<Integer> allowedSlots;

    /**
     * @param allowedSlots See ${@link CustomGui#allowedSlots}
     */
    public CustomGui(List<Integer> allowedSlots) {
        this.allowedSlots = allowedSlots;
    }

    /**
     * Handle clicks
     * @param event The event
     * @return Result of this interaction
     */
    public abstract ClickResult onItemClick(InventoryClickEvent event);

    /**
     * Handle clicks in player's inventory but when your inventory is open
     * @param event The event
     * @return Result of this interaction
     */
    public abstract ClickResult onItemClickInInventory(InventoryClickEvent event);

    /**
     * Handle item drag
     * @param event The event
     */
    public abstract void onItemDrag(InventoryDragEvent event);

    /**
     * Handle inventory close
     * @param event The event
     */
    public abstract void onClose(InventoryCloseEvent event);

    /**
     * Create an inventory and open it.
     * Usually this method's implementation looks like this:
     * <pre>
     * {@code
     * var inventory = Bukkit.createInventory(null, inventorySize, "Name");
     * // Some Inventory#setItem calls
     * // Maybe GuiUtils.fillWithBarriers(plugin, inventory);
     * player.openInventory(inventory);
     * return inventory;
     * }
     * </pre>
     * @param player The player
     * @return The inventory
     */
    public abstract Inventory open(Player player);

    /**
     * Checks if the slot is in {@link CustomGui#allowedSlots}.
     * If not, cancels the event.
     * @param slot The slot
     * @param event The event
     * @return true if {@link CustomGui#allowedSlots} contains the slot, false otherwise
     */
    boolean checkSlot(int slot, Cancellable event) {
        if (!allowedSlots.contains(slot)) {
            event.setCancelled(true);
            return false;
        } else return true;
    }

    public interface ClickResult {
    }

    /**
     * Do nothing
     */
    public record ClickResultNone() implements ClickResult {
    }

    /**
     * Closes the current gui and opens the new gui.
     * @param gui The new gui
     */
    public record ClickResultAnotherGui(CustomGui gui) implements ClickResult {
    }
}
