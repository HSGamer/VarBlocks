package me.hsgamer.varblocks.task;

import io.github.projectunified.minelib.plugin.base.Loadable;
import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.common.task.Task;
import io.github.projectunified.minelib.scheduler.location.LocationScheduler;
import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.api.BlockEntry;
import me.hsgamer.varblocks.api.BlockUpdater;
import me.hsgamer.varblocks.manager.BlockManager;
import me.hsgamer.varblocks.manager.BlockUpdaterManager;
import me.hsgamer.varblocks.manager.TemplateManager;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;

public class BlockUpdateTask implements Loadable {
    private final VarBlocks plugin;
    private final Queue<BlockEntry> updateQueue = new LinkedList<>();
    private final Queue<Map.Entry<Location, Consumer<Block>>> setBlockQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean blockSetting = new AtomicBoolean(false);
    private Task updateTask;
    private Task setBlockTask;

    public BlockUpdateTask(VarBlocks plugin) {
        this.plugin = plugin;
    }

    private void onUpdate() {
        BlockEntry entry = updateQueue.poll();
        if (entry == null) {
            updateQueue.addAll(plugin.get(BlockManager.class).getBlockEntries());
            return;
        }

        if (!entry.valid()) return;
        Location location = entry.location();

        Optional<BlockUpdater> updaterOptional = plugin.get(BlockUpdaterManager.class).getBlockUpdater(entry.type);
        if (!updaterOptional.isPresent()) return;
        BlockUpdater updater = updaterOptional.get();

        List<String> args = plugin.get(TemplateManager.class).getParsedTemplate(entry.template, entry.args);

        Consumer<Block> consumer = updater.getUpdateTask(args);
        if (consumer != null) {
            setBlockQueue.add(new AbstractMap.SimpleEntry<>(location, consumer));
        }
    }

    private void onBlockSet() {
        if (blockSetting.get()) return;

        Map.Entry<Location, Consumer<Block>> entry = setBlockQueue.poll();
        if (entry == null) return;

        Location location = entry.getKey();

        blockSetting.set(true);
        LocationScheduler.get(plugin, location).run(() -> {
            try {
                Block block = location.getBlock();
                if (block.getChunk().isLoaded()) {
                    entry.getValue().accept(block);
                }
            } catch (Throwable throwable) {
                plugin.getLogger().log(Level.WARNING, "Error while updating block at " + location, throwable);
            } finally {
                blockSetting.set(false);
            }
        });
    }

    @Override
    public void enable() {
        updateTask = AsyncScheduler.get(plugin).runTimer(this::onUpdate, 0, 0);
        setBlockTask = AsyncScheduler.get(plugin).runTimer(this::onBlockSet, 0, 0);
    }

    @Override
    public void disable() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
        if (setBlockTask != null) {
            setBlockTask.cancel();
            setBlockTask = null;
        }
    }
}
