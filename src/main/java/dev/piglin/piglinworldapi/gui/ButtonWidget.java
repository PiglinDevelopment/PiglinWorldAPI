package dev.piglin.piglinworldapi.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ButtonWidget extends GuiWidget {
    private final Function<InventoryView, CustomGui.ClickResult> handler;
    
    /**
     * Constructs the widget
     *
     * @param gui       The gui that uses this widget
     * @param inventory The opened inventory
     * @param slot      The button slot
     */
    public ButtonWidget(CustomGui gui, Inventory inventory, Player player, int slot, ItemStack item, Function<InventoryView, CustomGui.ClickResult> clickHandler) {
        super(gui, inventory, player, slot);
        inventory.setItem(slot, item);
        this.handler = clickHandler;
    }

    @Override
    public CustomGui.ClickResult onItemClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        return handler.apply(event.getView());
    }

    @Override
    public CustomGui.ClickResult onItemClickInInventory(@NotNull InventoryClickEvent event) {
        return new CustomGui.ClickResultNone();
    }

    @Override
    public void onItemDrag(@NotNull InventoryDragEvent event) {
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
    }
}
