package dev.piglin.piglinworldapi.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public abstract class GuiWidget {
    /**
     * This gui this widget is used in.
     */
    protected final CustomGui gui;

    /**
     * The opened inventory
     */
    protected final Inventory inventory;

    /**
     * The player who has this widget's menu opened
     */
    protected final Player player;

    /**
     * List of slots that a user is allowed to interact with
     * You will only receive events if the slot is here
     * Otherwise the event is passed to {@link CustomGui#onItemClick(InventoryClickEvent)} or other event methods.
     */
    protected List<Integer> slots;

    /**
     * Constructs the widget and handles inventory open.
     * In most cases you should fill your slots with items in this method
     *
     * @param gui       The gui that uses this widget
     * @param inventory The opened inventory
     * @param slots     See {@link #slots}
     */
    public GuiWidget(CustomGui gui, Inventory inventory, Player player, Integer... slots) {
        this.gui = gui;
        this.inventory = inventory;
        this.player = player;
        this.slots = Arrays.asList(slots);
    }

    /**
     * Checks if a slot belongs to this widget
     *
     * @param slot The slot
     * @return true if this slot belongs to this widget
     */
    public final boolean isWidgetSlot(int slot) {
        return slots.contains(slot);
    }

    /**
     * Handle clicks
     *
     * @param event The event
     * @return Result of this interaction
     */
    public abstract CustomGui.ClickResult onItemClick(@NotNull InventoryClickEvent event);

    /**
     * Handle clicks in player's inventory but when your inventory is open
     *
     * @param event The event
     * @return Result of this interaction
     */
    public abstract CustomGui.ClickResult onItemClickInInventory(@NotNull InventoryClickEvent event);

    /**
     * Handle item drag
     *
     * @param event The event
     */
    public abstract void onItemDrag(@NotNull InventoryDragEvent event);

    /**
     * Handle inventory close
     *
     * @param event The event
     */
    public abstract void onClose(@NotNull InventoryCloseEvent event);
}
