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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class BlockUpdateTask implements Loadable {
    private final VarBlocks plugin;
    private Task task;

    public BlockUpdateTask(VarBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        task = AsyncScheduler.get(plugin).runTimer(new Runnable() {
            private final Queue<BlockEntry> queue = new LinkedList<>();
            private final AtomicBoolean updating = new AtomicBoolean(false);

            @Override
            public void run() {
                if (updating.get()) return;

                if (queue.isEmpty()) {
                    queue.addAll(plugin.get(BlockManager.class).getBlockEntries());
                    return;
                }

                BlockEntry entry = queue.poll();
                if (entry == null || !entry.valid()) return;

                Location location = entry.location();

                Optional<BlockUpdater> updaterOptional = plugin.get(BlockUpdaterManager.class).getBlockUpdater(entry.type);
                if (!updaterOptional.isPresent()) return;
                BlockUpdater updater = updaterOptional.get();

                List<String> args = plugin.get(TemplateManager.class).getParsedTemplate(entry.template, entry.args);

                updating.set(true);
                LocationScheduler.get(plugin, location).run(() -> {
                    try {
                        Block block = location.getBlock();
                        if (block.getChunk().isLoaded()) {
                            updater.updateBlock(location.getBlock(), args);
                        }
                    } catch (Throwable throwable) {
                        plugin.getLogger().log(Level.WARNING, "Error while updating block at " + location, throwable);
                    } finally {
                        updating.set(false);
                    }
                });
            }
        }, 0, 0);
    }

    @Override
    public void disable() {
        if (task != null) {
            task.cancel();
        }
    }
}
