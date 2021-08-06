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
        openedGuis.put(gui.open(player), gui);
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
                var result = openedGuis.get(event.getClickedInventory()).onItemClick(event);
                if (result instanceof CustomGui.ClickResultAnotherGui gui) {
                    var inventory = gui.gui().open((Player) event.getWhoClicked());
                    openedGuis.put(inventory, gui.gui());
                }
            }
        } else if (openedGuis.containsKey(event.getView().getTopInventory())) {
            var result = openedGuis.get(event.getView().getTopInventory()).onItemClickInInventory(event);
            if (result instanceof CustomGui.ClickResultAnotherGui gui) {
                var inventory = gui.gui().open((Player) event.getWhoClicked());
                openedGuis.put(inventory, gui.gui());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (openedGuis.containsKey(event.getInventory())) {
            openedGuis.get(event.getInventory()).onClose(event);
            openedGuis.remove(event.getInventory());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (openedGuis.containsKey(event.getInventory())) {
            if (event.getRawSlots().stream().allMatch(slot -> slot > 53 || openedGuis.get(event.getInventory()).checkSlot(slot, event))) {
                openedGuis.get(event.getInventory()).onItemDrag(event);
            }
        }
    }
}
