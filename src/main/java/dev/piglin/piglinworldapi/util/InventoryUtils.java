package dev.piglin.piglinworldapi.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InventoryUtils {
    /**
     * Checks if you can safely add the item to the specified inventory
     *
     * @param inventory The inventory
     * @param item      The item
     * @return true if there is enough free space in the inventory, false otherwise
     */
    public static boolean checkSpaceForItem(@NotNull Inventory inventory, @Nullable ItemStack item) {
        if (item == null) return true;
        else return inventory.firstEmpty() != -1
                || Arrays.stream(inventory.getContents())
                .filter(item::isSimilar)
                .map(i -> i.getMaxStackSize() - i.getAmount())
                .reduce(Integer::sum).orElse(0) >= item.getAmount();
    }

    /**
     * Adds the item to the inventory, but don't add it if it can't be added fully (e.g. 24 items can be added, 64 can't)
     *
     * @param inventory The inventory
     * @param item      The item
     * @return true if the item was added to the inventory, false otherwise
     */
    public static boolean addEverythingOrNothing(@NotNull Inventory inventory, @Nullable ItemStack item) {
        if (item == null) return true;
        if (!checkSpaceForItem(inventory, item)) return false;
        inventory.addItem(item);
        return true;
    }

    /**
     * Get slots in the inventory with items similar to the specified item
     *
     * @param inventory The inventory
     * @param item      The item
     * @return Slots with similar item
     */
    public static List<Integer> getSimilarSlots(Inventory inventory, ItemStack item) {
        if (item != null) {
            var list = new ArrayList<Integer>();
            var items = inventory.getStorageContents();
            for (var i = 0; i < items.length; i++) {
                if (items[i] != null && item.isSimilar(items[i])) {
                    list.add(i);
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
