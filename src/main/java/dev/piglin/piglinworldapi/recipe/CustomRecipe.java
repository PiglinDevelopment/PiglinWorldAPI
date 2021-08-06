package dev.piglin.piglinworldapi.recipe;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Optional;

public abstract class CustomRecipe {
    public static LinkedList<CustomRecipe> recipes = new LinkedList<>();

    /**
     * @param ingredients ItemStack array of size 9
     * @return The result of this craft if any
     */
    public abstract Optional<ItemStack> getResult3x3(@NotNull ItemStack[] ingredients);

    /**
     * @param ingredients ItemStack array of size 4
     * @return The result of this craft if any
     */
    public abstract Optional<ItemStack> getResult2x2(@NotNull ItemStack[] ingredients);
}
