package me.hsgamer.varblocks.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.varblocks.api.BlockEntry;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class BlockManager implements Loadable {
    private final BukkitConfig config;
    private final Map<String, BlockEntry> blockEntryMap = new LinkedHashMap<>();
    private final AtomicReference<Map<Location, BlockEntry>> blockEntryCache = new AtomicReference<>(Collections.emptyMap());

    public BlockManager(Plugin plugin) {
        this.config = new BukkitConfig(plugin, "blocks.yml");
        config.setup();
    }

    public List<BlockEntry> getBlockEntries() {
        return new ArrayList<>(blockEntryMap.values());
    }

    public Optional<BlockEntry> getBlockEntry(String name) {
        return Optional.ofNullable(blockEntryMap.get(name));
    }

    public Optional<BlockEntry> getBlockEntry(Location location) {
        return Optional.ofNullable(blockEntryCache.get().get(location));
    }

    public void addBlockEntry(String name, BlockEntry blockEntry) {
        blockEntryMap.put(name, blockEntry);
        saveConfig();
        updateCache();
    }

    public void removeBlockEntry(String name) {
        blockEntryMap.remove(name);
        saveConfig();
        updateCache();
    }

    private void updateCache() {
        Map<Location, BlockEntry> cache = new HashMap<>();
        blockEntryMap.forEach((name, blockEntry) -> cache.put(blockEntry.location(), blockEntry));
        blockEntryCache.set(cache);
    }

    private void loadConfig() {
        blockEntryMap.clear();
        Map<String[], Object> map = config.getNormalizedValues(false);
        map.forEach((key, value) ->
                MapUtils.castOptionalStringObjectMap(value)
                        .map(BlockEntry::fromMap)
                        .ifPresent(blockEntry -> blockEntryMap.put(PathString.joinDefault(key), blockEntry))
        );
    }

    private void saveConfig() {
        config.clear();
        for (Map.Entry<String, BlockEntry> entry : blockEntryMap.entrySet()) {
            config.set(entry.getValue().toMap(), entry.getKey());
        }
        config.save();
    }

    @Override
    public void enable() {
        loadConfig();
        updateCache();
    }

    @Override
    public void disable() {
        saveConfig();
        blockEntryCache.set(Collections.emptyMap());
    }
}
