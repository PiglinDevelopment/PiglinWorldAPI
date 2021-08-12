package dev.piglin.piglinworldapi.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;

public class GuiController implements Listener {
    private final HashMap<Inventory, CustomGui> openedGuis = new HashMap<>();

    /**
     * @deprecated Should be refactored to be private or package-private
     */
    @Deprecated
    public GuiController() {

    }

    /**
     * Opens the gui to the player and registers listeners for it
     *
     * @param gui    The gui
     * @param player The player
     */
    public void open(CustomGui gui, Player player) {
        var view = gui.onOpen(player);
        gui.inventory = view;
        openedGuis.put(view.getTopInventory(), gui);
    }

    /**
     * Closes all the registered guis
     */
    public void closeAll() {
        openedGuis.keySet().forEach(inventory -> {
            var viewers = inventory.getViewers();
            new ArrayList<>(viewers).forEach(HumanEntity::closeInventory);
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().equals(event.getClickedInventory())
                && openedGuis.containsKey(event.getView().getTopInventory())) {
            if (openedGuis.get(event.getClickedInventory()).checkSlot(event.getSlot(), event)) {
                var result = openedGuis.get(event.getClickedInventory()).onClick(event);
                if (result instanceof CustomGui.ClickResultAnotherGui gui) {
                    open(gui.gui(), (Player) event.getWhoClicked());
                }
            }
        } else if (openedGuis.containsKey(event.getView().getTopInventory())) {
            var result = openedGuis.get(event.getView().getTopInventory()).onClickInInventory(event);
            if (result instanceof CustomGui.ClickResultAnotherGui gui) {
                open(gui.gui(), (Player) event.getWhoClicked());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (openedGuis.containsKey(event.getInventory())) {
            openedGuis.get(event.getInventory()).getWidgets().forEach(w -> w.onClose(event));
            openedGuis.get(event.getInventory()).onClose(event);
            openedGuis.remove(event.getInventory());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (openedGuis.containsKey(event.getInventory())) {
            if (event.getRawSlots().stream().allMatch(slot -> slot >= event.getInventory().getSize() || openedGuis.get(event.getInventory()).checkSlot(slot, event))) {
                openedGuis.get(event.getInventory()).onDrag(event);
            }
        }
    }
}
