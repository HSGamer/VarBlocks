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
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;

public class BlockUpdateTask implements Loadable {
    private final VarBlocks plugin;
    private final Queue<BlockEntry> updateQueue = new LinkedList<>();
    private final Map<Location, Map<Class<?>, Consumer<Block>>> setBlockMap = new HashMap<>();
    private final AtomicInteger pendingBlockSets = new AtomicInteger(0);
    private Task task;

    public BlockUpdateTask(VarBlocks plugin) {
        this.plugin = plugin;
    }

    private void onTick() {
        BlockEntry entry = updateQueue.poll();
        if (entry == null) {
            updateQueue.addAll(plugin.get(BlockManager.class).getBlockEntries());
        } else if (entry.valid()) {
            Location location = entry.location();

            Optional<BlockUpdater> updaterOptional = plugin.get(BlockUpdaterManager.class).getBlockUpdater(entry.type);
            if (updaterOptional.isPresent()) {
                BlockUpdater updater = updaterOptional.get();
                List<String> args = plugin.get(TemplateManager.class).getParsedTemplate(entry.template, entry.args);

                Consumer<Block> consumer = updater.getUpdateTask(args);
                if (consumer != null) {
                    setBlockMap.computeIfAbsent(location, k -> new HashMap<>()).put(consumer.getClass(), consumer);
                }
            }
        }

        if (pendingBlockSets.get() > 0) return;
        if (setBlockMap.isEmpty()) return;

        for (Map.Entry<Location, Map<Class<?>, Consumer<Block>>> blockEntry : setBlockMap.entrySet()) {
            Location location = blockEntry.getKey();
            World world = location.getWorld();
            if (world == null) continue;
            int chunkX = location.getBlockX() >> 4;
            int chunkZ = location.getBlockZ() >> 4;
            if (!world.isChunkLoaded(chunkX, chunkZ)) continue;

            pendingBlockSets.incrementAndGet();
            Collection<Consumer<Block>> consumers = blockEntry.getValue().values();
            LocationScheduler.get(plugin, location).run(() -> {
                try {
                    Block block = location.getBlock();
                    consumers.forEach(consumer -> consumer.accept(block));
                } catch (Throwable throwable) {
                    plugin.getLogger().log(Level.WARNING, "Error while updating block at " + location, throwable);
                } finally {
                    pendingBlockSets.decrementAndGet();
                }
            });
        }
        setBlockMap.clear();
    }

    @Override
    public void enable() {
        task = AsyncScheduler.get(plugin).runTimer(this::onTick, 0, 0);
    }

    @Override
    public void disable() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}

