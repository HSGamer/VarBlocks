package me.hsgamer.varblocks.listener;

import io.github.projectunified.minelib.plugin.listener.ListenerComponent;
import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.manager.BlockManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BlockListener implements ListenerComponent {
    private final VarBlocks plugin;

    public BlockListener(VarBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public VarBlocks getPlugin() {
        return plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (plugin.get(BlockManager.class).getBlockEntry(location).isPresent()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        Location location = event.getBlock().getLocation();
        if (plugin.get(BlockManager.class).getBlockEntry(location).isPresent()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> plugin.get(BlockManager.class).getBlockEntry(block.getLocation()).isPresent());
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> plugin.get(BlockManager.class).getBlockEntry(block.getLocation()).isPresent());
    }
}
