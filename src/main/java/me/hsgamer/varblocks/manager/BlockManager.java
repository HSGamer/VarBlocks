package me.hsgamer.varblocks.manager;

import me.hsgamer.varblocks.api.BlockEntry;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class BlockManager {
    private final Plugin plugin;
    private final Map<Location, BlockEntry> blockEntryMap = new LinkedHashMap<>();

    public BlockManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public List<BlockEntry> getBlockEntries() {
        return new ArrayList<>(blockEntryMap.values());
    }

    public Optional<BlockEntry> getBlockEntry(Location location) {
        return Optional.ofNullable(blockEntryMap.get(location));
    }

    public void addBlockEntry(BlockEntry blockEntry) {
        blockEntryMap.put(blockEntry.location(), blockEntry);
    }

    public void removeBlockEntry(Location location) {
        blockEntryMap.remove(location);
    }
}
