package dev.piglin.piglinworldapi.recipe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class RecipeController implements Listener {
    /**
     * @deprecated Should be refactored to be private or package-private
     */
    @Deprecated
    public RecipeController() {
    }

    /**
     * @param ingredients The ingredients array of size 4 (2x2) or 9 (3x3)
     * @return The result of this craft if any
     */
    public static Optional<ItemStack> craft(ItemStack[] ingredients) {
        if (Arrays.stream(ingredients).allMatch(Objects::isNull)) return Optional.empty();
        ingredients = Arrays.stream(ingredients).map(i -> i == null ? new ItemStack(Material.AIR) : i).toArray(ItemStack[]::new);
        for (var recipe : CustomRecipe.recipes) {
            if (ingredients.length == 9) {
                var res = recipe.getResult3x3(ingredients);
                if (res.isPresent()) {
                    return res;
                }
            } else if (ingredients.length == 4) {
                var res = recipe.getResult2x2(ingredients);
                if (res.isPresent()) {
                    return res;
                }
            }
        }

        if (ingredients.length == 4) ingredients = new ItemStack[]{
                ingredients[0], ingredients[1], null,
                ingredients[2], ingredients[3], null,
                null, null, null
        };
        ingredients = Arrays.stream(ingredients).map(item -> item.getType() == Material.AIR ? null : item).toArray(ItemStack[]::new);

        var iter = Bukkit.recipeIterator();
        recipes:
        while (iter.hasNext()) {
            var r = iter.next();
            if (r instanceof ShapedRecipe recipe) {
                var columms = 1;
                for (var row : recipe.getShape()) {
                    if (row.length() > columms) columms = row.length();
                }
                var rows = recipe.getShape().length;
                var baseX = 2;
                var baseY = 2;
                for (var y = 0; y < 3; y++) {
                    for (var x = 0; x < 3; x++) {
                        if (ingredients[x + y * 3] == null) continue;
                        if (x < baseX) baseX = x;
                        if (y < baseY) baseY = y;
                    }
                }

                var deltaX = columms + baseX;
                var deltaY = rows + baseY;
                for (int y = baseY, shapeY = 0; y < deltaY; y++, shapeY++) {
                    for (int x = baseX, shapeX = 0; x < deltaX; x++, shapeX++) {
                        if (x >= 3 || y >= 3) continue recipes;
                        var item = ingredients[x + y * 3];
                        var currentlyKey = recipe.getShape()[shapeY].charAt(shapeX);
                        var ingredient = recipe.getChoiceMap().get(currentlyKey);

                        if (item == null || ingredient == null || !ingredient.test(item)) {
                            continue recipes;
                        }
                    }
                }
                for (var x = 0; x < 3; x++) {
                    for (var y = 0; y < 3; y++) {
                        if (x >= deltaX || y >= deltaY) {
                            var item = ingredients[x + y * 3];
                            if (item != null) continue recipes;
                        }
                    }
                }
                return Optional.of(recipe.getResult());
            } else if (r instanceof ShapelessRecipe recipe) {
                var ingredientsPassed = new ArrayList<>(Arrays.asList(ingredients));
                var choiceList = new ArrayList<RecipeChoice>(9);
                choiceList.addAll(recipe.getChoiceList());
                while (choiceList.size() < 9) choiceList.add(null);

                if (choiceList.stream().allMatch(ingredientTester -> {
                    if (ingredientTester == null) return ingredientsPassed.remove(null);
                    var ingredient = ingredientsPassed
                            .stream()
                            .filter(Objects::nonNull).filter(ingredientTester)
                            .findAny();
                    return ingredient.map(ingredientsPassed::remove).orElse(false);
                })) {
                    return Optional.of(recipe.getResult());
                }
            }
        }
        return Optional.empty();
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) {
            var ingredients = Arrays.stream(event.getInventory().getMatrix())
                    .map(item -> item == null ? new ItemStack(Material.AIR) : item)
                    .toArray(ItemStack[]::new);
            ItemStack result = null;
            for (var recipe : CustomRecipe.recipes) {
                if (ingredients.length == 9) {
                    var res = recipe.getResult3x3(ingredients);
                    if (res.isPresent()) {
                        result = res.get();
                        break;
                    }
                } else if (ingredients.length == 4) {
                    var res = recipe.getResult2x2(ingredients);
                    if (res.isPresent()) {
                        result = res.get();
                        break;
                    }
                }
            }
            if (result != null) {
                event.getInventory().setResult(result.clone());
            }
        }
    }
}
