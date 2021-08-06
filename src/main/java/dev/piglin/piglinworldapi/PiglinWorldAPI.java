package dev.piglin.piglinworldapi;

import dev.piglin.piglinworldapi.block.BlockController;
import dev.piglin.piglinworldapi.block.BlockDataStorage;
import dev.piglin.piglinworldapi.block.RedstoneListener;
import dev.piglin.piglinworldapi.gui.GuiController;
import dev.piglin.piglinworldapi.recipe.RecipeController;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public final class PiglinWorldAPI extends JavaPlugin {
    private final File blockDataStorageFile = new File(getDataFolder(), "block");
    private BlockDataStorage blockDataStorage;
    private BlockController blockController;
    private RecipeController recipeController;
    private GuiController guiController;

    public static PiglinWorldAPI getInstance() {
        return getPlugin(PiglinWorldAPI.class);
    }

    @Override
    public void onEnable() {
        setupFileStorage();
        blockDataStorage = new BlockDataStorage(blockDataStorageFile);
        blockController = new BlockController();
        recipeController = new RecipeController();
        guiController = new GuiController();
        getCommand("checkblocktag").setExecutor(blockDataStorage);
        getCommand("addblocktag").setExecutor(blockDataStorage);
        getCommand("removeblocktag").setExecutor(blockDataStorage);
        getServer().getPluginManager().registerEvents(blockController, this);
        getServer().getPluginManager().registerEvents(recipeController, this);
        getServer().getPluginManager().registerEvents(guiController, this);
        getServer().getPluginManager().registerEvents(new RedstoneListener(), this);
        if (getServer().getWorlds().stream().anyMatch(world -> world.getLoadedChunks().length != 0)) {
            System.out.println("Searching for PiglinAPI blocks... It may take some time.");
            for (var world : getServer().getWorlds()) {
                for (var chunk : world.getLoadedChunks()) {
                    blockController.loadChunk(chunk.getChunkSnapshot(), world.getMinHeight(), world.getMaxHeight());
                }
            }
            var blocks = BlockController.getAllBlocks();
            System.out.println("Loaded " + blocks.size() + " different blocks (" + blocks.values().stream().map(Collection::size).reduce(Integer::sum).orElse(0) + " blocks)");
        }
    }

    /**
     * Get the block data storage
     * @return block data storage
     */
    public BlockDataStorage getBlockDataStorage() {
        return blockDataStorage;
    }

    /**
     * Get the block controller
     * @return block controller
     */
    public BlockController getBlockController() {
        return blockController;
    }

    /**
     * Get the recipe controller
     * @return recipe controller
     */
    public RecipeController getRecipeController() {
        return recipeController;
    }

    /**
     * Get the gui controller
     * @return gui controller
     */
    public GuiController getGuiController() {
        return guiController;
    }

    @Override
    public void onDisable() {
        blockDataStorage.save(blockDataStorageFile);
        BlockController.clearBlocks();
        guiController.closeAll();
    }

    private void setupFileStorage() {
        try {
            if (!getDataFolder().exists()) getDataFolder().mkdir();
            if (!blockDataStorageFile.exists()) {
                blockDataStorageFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
