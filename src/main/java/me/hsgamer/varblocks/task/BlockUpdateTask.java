package me.hsgamer.varblocks.task;

import io.github.projectunified.minelib.plugin.base.Loadable;
import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.common.task.Task;
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

            @Override
            public void run() {
                if (queue.isEmpty()) {
                    queue.addAll(plugin.get(BlockManager.class).getBlockEntries());
                    return;
                }

                BlockEntry entry = queue.poll();
                if (entry == null || !entry.valid()) return;

                Location location = entry.location();
                Block block = location.getBlock();

                Optional<BlockUpdater> updaterOptional = plugin.get(BlockUpdaterManager.class).getBlockUpdater(entry.type);
                if (!updaterOptional.isPresent()) return;
                BlockUpdater updater = updaterOptional.get();

                List<String> args = plugin.get(TemplateManager.class).getParsedTemplate(entry.template, entry.args);

                updater.updateBlock(block, args);
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
