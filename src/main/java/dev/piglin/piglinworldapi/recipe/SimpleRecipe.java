package dev.piglin.piglinworldapi.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class SimpleRecipe extends CustomRecipe {
    private final SimpleRecipeData data;

    /**
     * @param data The recipe ingredients and data
     */
    public SimpleRecipe(SimpleRecipeData data) {
        this.data = data;
    }

    @Override
    public Optional<ItemStack> getResult3x3(@NotNull ItemStack[] ingredients) {
        if (data.shaped) {
            // Shaped
            if (data.needCraftingTable()) {
                if (
                        ingredients[0].isSimilar(data.ingredient_1_1)
                                && ingredients[1].isSimilar(data.ingredient_1_2)
                                && ingredients[2].isSimilar(data.ingredient_1_3)
                                && ingredients[3].isSimilar(data.ingredient_2_1)
                                && ingredients[4].isSimilar(data.ingredient_2_2)
                                && ingredients[5].isSimilar(data.ingredient_2_3)
                                && ingredients[6].isSimilar(data.ingredient_3_1)
                                && ingredients[7].isSimilar(data.ingredient_3_2)
                                && ingredients[8].isSimilar(data.ingredient_3_3)
                ) {
                    return Optional.of(data.result);
                }
            } else {
                if ((
                        ingredients[0].isSimilar(data.ingredient_1_1)
                                && ingredients[1].isSimilar(data.ingredient_1_2)
                                && ingredients[3].isSimilar(data.ingredient_2_1)
                                && ingredients[4].isSimilar(data.ingredient_2_2)
                )) {
                    return Optional.of(data.result);
                }
            }
            return Optional.empty();
        } else {
            // Shapeless
            var items = new ArrayList<>(Arrays.asList(ingredients));
            for (var ingredient : new ItemStack[]{
                    data.ingredient_1_1, data.ingredient_1_2, data.ingredient_1_3,
                    data.ingredient_2_1, data.ingredient_2_2, data.ingredient_2_3,
                    data.ingredient_3_1, data.ingredient_3_2, data.ingredient_3_3
            }) {
                if (ingredient == null) ingredient = new ItemStack(Material.AIR);
                ItemStack finalIngredient = ingredient;
                Optional<ItemStack> i = items.stream().filter(item -> item.isSimilar(finalIngredient)).findAny();
                if (i.isPresent()) items.remove(i.get());
                else return Optional.empty();
            }
            return Optional.of(data.result);
        }
    }

    @Override
    public Optional<ItemStack> getResult2x2(@NotNull ItemStack[] ingredients) {
        if (data.shaped) {
            // Shaped
            if (data.needCraftingTable) return Optional.empty();
            if (ingredients[0].isSimilar(data.ingredient_1_1) && ingredients[1].isSimilar(data.ingredient_1_2)
                    && ingredients[3].isSimilar(data.ingredient_2_1) && ingredients[4].isSimilar(data.ingredient_2_2)) {
                return Optional.of(data.result);
            }
            return Optional.empty();
        } else {
            // Shapeless
            var items = new ArrayList<>(Arrays.asList(ingredients));
            for (var ingredient : new ItemStack[]{
                    data.ingredient_1_1, data.ingredient_1_2,
                    data.ingredient_2_1, data.ingredient_2_2,
            }) {
                if (ingredient == null) ingredient = new ItemStack(Material.AIR);
                ItemStack finalIngredient = ingredient;
                Optional<ItemStack> i = items.stream().filter(item -> item.isSimilar(finalIngredient)).findAny();
                if (i.isPresent()) items.remove(i.get());
                else return Optional.empty();
            }
            return Optional.of(data.result);
        }
    }

    /**
     * Recipe ingredients and data
     */
    public record SimpleRecipeData(@Nullable ItemStack ingredient_1_1, @Nullable ItemStack ingredient_1_2,
                                   @Nullable ItemStack ingredient_1_3,
                                   @Nullable ItemStack ingredient_2_1, @Nullable ItemStack ingredient_2_2,
                                   @Nullable ItemStack ingredient_2_3,
                                   @Nullable ItemStack ingredient_3_1, @Nullable ItemStack ingredient_3_2,
                                   @Nullable ItemStack ingredient_3_3,
                                   @NotNull ItemStack result, boolean shaped, boolean needCraftingTable) {

        /**
         * If the recipe is 2x2, the ingredients must be placed in ingredient_1_1, ingredient_1_2, ingredient_2_1, ingredient_2_2, and other ingredients must be null
         *
         * @param ingredient_1_1 Ingredient at left top corner
         * @param ingredient_1_2 Ingredient at top corner
         * @param ingredient_1_3 Ingredient at right top corner
         * @param ingredient_2_1 Ingredient at left corner
         * @param ingredient_2_2 Ingredient at the middle
         * @param ingredient_2_3 Ingredient at right corner
         * @param ingredient_3_1 Ingredient at left bottom corner
         * @param ingredient_3_2 Ingredient at bottom corner
         * @param ingredient_3_3 Ingredient at right bottom corner
         * @param result         The result
         * @param shaped         True if shaped, false if the shape and order don't matter
         */
        public SimpleRecipeData(@Nullable ItemStack ingredient_1_1, @Nullable ItemStack ingredient_1_2, @Nullable ItemStack ingredient_1_3,
                                @Nullable ItemStack ingredient_2_1, @Nullable ItemStack ingredient_2_2, @Nullable ItemStack ingredient_2_3,
                                @Nullable ItemStack ingredient_3_1, @Nullable ItemStack ingredient_3_2, @Nullable ItemStack ingredient_3_3,
                                @NotNull ItemStack result, boolean shaped) {
            this(ingredient_1_1, ingredient_1_2, ingredient_1_3,
                    ingredient_2_1, ingredient_2_2, ingredient_2_3,
                    ingredient_3_1, ingredient_3_2, ingredient_3_3,
                    result, shaped,
                    !(ingredient_3_1 == null && ingredient_3_2 == null && ingredient_3_3 == null && ingredient_1_3 == null && ingredient_2_3 == null));
        }
    }
}
