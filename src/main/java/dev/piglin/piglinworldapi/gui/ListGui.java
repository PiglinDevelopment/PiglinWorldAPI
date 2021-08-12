package dev.piglin.piglinworldapi.gui;

import dev.piglin.piglinworldapi.util.GuiUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * List gui with prev/next buttons and clickable elements
 *
 * @param <T> List element type
 */
public abstract class ListGui<T> extends CustomGui {
    protected final int rows;
    protected final String name;
    protected final List<Integer> listSlots;
    protected final int prevSlot;
    protected final int nextSlot;
    protected final ItemStack prevItem;
    protected final ItemStack nextItem;
    protected final ItemStack noPrevItem;
    protected final ItemStack noNextItem;
    protected final HashMap<Inventory, List<T>> lists = new HashMap<>();
    protected final HashMap<Inventory, Integer> pages = new HashMap<>();
    private final Plugin plugin;

    /**
     * @param listSlots  Slots for the items
     * @param prev       Slot for "previous page" button
     * @param next       Slot for "next page" button
     * @param prevItem   ItemStack for "previous page" button
     * @param nextItem   ItemStack for "next page" button
     * @param noPrevItem ItemStack for "previous page" button when it's first page
     * @param noNextItem ItemStack for "next page" button when it's last page
     * @param name       Gui title
     * @param plugin     Your plugin
     */
    public ListGui(List<Integer> listSlots,
                   int prev, int next,
                   ItemStack prevItem, ItemStack nextItem,
                   ItemStack noPrevItem, ItemStack noNextItem,
                   String name, Plugin plugin) {
        super(new ArrayList<>(listSlots.size() + 2) {{
            assert !listSlots.contains(prev) && !listSlots.contains(next);
            this.addAll(listSlots);
            this.add(prev);
            this.add(next);
        }});
        var max = Collections.max(this.allowedSlots);
        var rows = max / 9 + 1;
        assert rows > 6;
        this.rows = rows;
        this.name = name;
        this.listSlots = listSlots;
        this.prevSlot = prev;
        this.nextSlot = next;
        this.prevItem = prevItem;
        this.nextItem = nextItem;
        this.noPrevItem = noPrevItem;
        this.noNextItem = noNextItem;
        this.plugin = plugin;
    }

    @Override
    public ClickResult onItemClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        var pageChange = 0;
        if (listSlots.contains(event.getSlot()) && listSlots.indexOf(event.getSlot()) < lists.get(event.getInventory()).size()) {
            var item = lists.get(event.getInventory()).get(listSlots.indexOf(event.getSlot()));
            return onClick(item, (Player) event.getWhoClicked(), event.getInventory(), event.getClick());
        } else if (event.getSlot() == prevSlot && pages.get(event.getInventory()) > 0) {
            pageChange = -1;
        } else if (event.getSlot() == nextSlot && (pages.get(event.getInventory()) + 1) * listSlots.size() < lists.get(event.getInventory()).size()) {
            pageChange = 1;
        }
        if (pageChange != 0) {
            var inventory = event.getInventory();
            var items = getItems((Player) event.getWhoClicked());
            var page = pages.get(inventory) + pageChange;
            lists.put(inventory, items);
            pages.put(inventory, page);
            var i = 0;
            for (int slot : listSlots) {
                ItemStack item;
                if (i + page * listSlots.size() >= items.size()) {
                    item = new ItemStack(Material.AIR);
                } else {
                    item = display((Player) event.getWhoClicked(), items.get(i + page * listSlots.size()));
                }
                inventory.setItem(slot, item);
                i++;
            }
            var next = page > 0 ? prevItem : noPrevItem;
            var prev = items.size() > listSlots.size() ? nextItem : noNextItem;
            var prevMeta = prev.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(prevMeta.getDisplayName().replaceAll("\\{page}", String.valueOf(page)));
                prev.setItemMeta(prevMeta);
            }
            var nextMeta = next.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(nextMeta.getDisplayName().replaceAll("\\{page}", String.valueOf(page + 2)));
                next.setItemMeta(nextMeta);
            }
            inventory.setItem(prevSlot, next);
            inventory.setItem(nextSlot, prev);
        }
        return new ClickResultNone();
    }

    @Override
    public ClickResult onItemClickInInventory(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        return new ClickResultNone();
    }

    @Override
    public void onItemDrag(@NotNull InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    public InventoryView onOpen(@NotNull Player player) {
        var inventory = Bukkit.createInventory(null, rows * 9, name);
        var items = getItems(player);
        var page = 0;
        lists.put(inventory, items);
        pages.put(inventory, page);
        var i = 0;
        for (int slot : listSlots) {
            if (i + page * listSlots.size() >= items.size()) break;
            var item = display(player, items.get(i + page * listSlots.size()));
            inventory.setItem(slot, item);
            i++;
        }
        var next = page > 0 ? prevItem : noPrevItem;
        var prev = items.size() > listSlots.size() ? nextItem : noNextItem;
        var prevMeta = prev.getItemMeta();
        if (prevMeta != null) {
            prevMeta.setDisplayName(prevMeta.getDisplayName().replaceAll("\\{page}", String.valueOf(page)));
            prev.setItemMeta(prevMeta);
        }
        var nextMeta = next.getItemMeta();
        if (nextMeta != null) {
            nextMeta.setDisplayName(nextMeta.getDisplayName().replaceAll("\\{page}", String.valueOf(page + 2)));
            next.setItemMeta(nextMeta);
        }
        inventory.setItem(prevSlot, next);
        inventory.setItem(nextSlot, prev);
        GuiUtils.fillWithBarriers(plugin, inventory);
        return player.openInventory(inventory);
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
        lists.remove(event.getInventory());
        pages.remove(event.getInventory());
    }

    /**
     * Get all available items
     *
     * @param player The player
     * @return List of all available items
     */
    public abstract List<T> getItems(Player player);

    /**
     * Makes an ItemStack representation of an item for the player
     *
     * @param player The player
     * @param t      The item
     * @return ItemStack representation of the item
     */
    public abstract ItemStack display(Player player, T t);

    /**
     * Handle item click
     *
     * @param t         The item
     * @param player    The player who clicked
     * @param inventory The inventory
     * @param type      Click type
     * @return Result of this interaction
     */
    public abstract ClickResult onClick(T t, Player player, Inventory inventory, ClickType type);
}
