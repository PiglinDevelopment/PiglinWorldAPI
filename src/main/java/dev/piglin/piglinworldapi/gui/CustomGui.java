package dev.piglin.piglinworldapi.gui;

import dev.piglin.piglinworldapi.PiglinWorldAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    private final List<GuiWidget> widgets;

    /**
     *
     */
    protected InventoryView inventory;

    /**
     * @param allowedSlots See ${@link CustomGui#allowedSlots}
     */
    public CustomGui(List<Integer> allowedSlots) {
        this.allowedSlots = allowedSlots;
        this.widgets = new ArrayList<>();
    }

    /**
     * Get this gui's widgets
     *
     * @return list of widgets
     */
    public List<GuiWidget> getWidgets() {
        return widgets;
    }

    /**
     * Handle clicks
     *
     * @param event The event
     * @return Result of this interaction
     */
    public abstract ClickResult onItemClick(@NotNull InventoryClickEvent event);

    /**
     * Handle clicks in player's inventory but when your inventory is open.
     * Bypasses {@link #checkSlot(int, Cancellable)}
     *
     * @param event The event
     * @return Result of this interaction
     */
    public abstract ClickResult onItemClickInInventory(@NotNull InventoryClickEvent event);

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

    /**
     * Create an inventory and open it.
     * Usually this method's implementation looks like this:
     * <pre>
     * {@code
     * var inventory = Bukkit.createInventory(null, inventorySize, "Name");
     * // Some Inventory#setItem calls
     * // Maybe GuiUtils.fillWithBarriers(plugin, inventory);
     * this.widgets.add(new MyWidget(this, inventory, player, ...));
     * return player.openInventory(inventory);
     * }
     * </pre>
     *
     * @param player The player
     * @return The inventory
     */
    public abstract InventoryView onOpen(@NotNull Player player);

    /**
     * Checks if the slot is in {@link CustomGui#allowedSlots}.
     * If not, cancels the event.
     *
     * @param slot  The slot
     * @param event The event
     * @return true if {@link CustomGui#allowedSlots} contains the slot, false otherwise
     */
    public final boolean checkSlot(int slot, @NotNull Cancellable event) {
        if (!allowedSlots.contains(slot) && widgets.stream().noneMatch(w -> w.isWidgetSlot(slot))) {
            event.setCancelled(true);
            return false;
        } else return true;
    }

    final ClickResult onClick(InventoryClickEvent event) {
        for (var widget : getWidgets()) {
            if (widget.isWidgetSlot(event.getSlot())) {
                return widget.onItemClick(event);
            }
        }
        return onItemClick(event);
    }

    final ClickResult onClickInInventory(InventoryClickEvent event) {
        for (var widget : getWidgets()) {
            if (widget.isWidgetSlot(event.getSlot())) {
                widget.onItemClickInInventory(event);
            }
        }
        return onItemClickInInventory(event);
    }

    final void onDrag(InventoryDragEvent event) {
        for (var widget : getWidgets()) {
            if (event.getRawSlots().stream().allMatch(slot -> slot >= inventory.getTopInventory().getSize() || widget.isWidgetSlot(slot))) {
                widget.onItemDrag(event);
                return;
            }
        }
        onItemDrag(event);
    }

    /**
     * Add a widget to this gui
     *
     * @param widget The widget
     */
    protected void addWidget(GuiWidget widget) {
        getWidgets().add(widget);
    }

    /**
     * Open the gui to the player
     *
     * @param player The player
     */
    public final void open(Player player) {
        PiglinWorldAPI.getInstance().getGuiController().open(this, player);
    }

    public interface ClickResult {
    }

    /**
     * Do nothing
     */
    public static class ClickResultNone implements ClickResult {
    }

    /**
     * Closes the current gui and opens the new gui.
     *
     * @param gui The new gui
     */
    public record ClickResultAnotherGui(CustomGui gui) implements ClickResult {
    }
}
