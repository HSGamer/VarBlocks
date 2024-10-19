package me.hsgamer.varblocks.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.varblocks.api.BlockEntry;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class BlockManager implements Loadable {
    private final BukkitConfig config;
    private final Map<Location, BlockEntry> blockEntryMap = new LinkedHashMap<>();

    public BlockManager(Plugin plugin) {
        this.config = new BukkitConfig(plugin, "blocks.yml");
        config.setup();
    }

    public List<BlockEntry> getBlockEntries() {
        return new ArrayList<>(blockEntryMap.values());
    }

    public Optional<BlockEntry> getBlockEntry(Location location) {
        return Optional.ofNullable(blockEntryMap.get(location));
    }

    public void addBlockEntry(BlockEntry blockEntry) {
        blockEntryMap.put(blockEntry.location(), blockEntry);
        saveConfig();
    }

    public void removeBlockEntry(Location location) {
        blockEntryMap.remove(location);
        saveConfig();
    }

    private void loadConfig() {
        blockEntryMap.clear();
        Object configObject = config.getNormalized("blocks");
        if (configObject instanceof List) {
            List<?> list = (List<?>) configObject;
            for (Object o : list) {
                Optional<Map<String, Object>> mapOptional = MapUtils.castOptionalStringObjectMap(o);
                mapOptional.ifPresent(map -> addBlockEntry(BlockEntry.fromMap(map)));
            }
        }
    }

    private void saveConfig() {
        List<Map<String, Object>> list = new ArrayList<>();
        blockEntryMap.values().forEach(blockEntry -> list.add(blockEntry.toMap()));
        config.set(list, "blocks");
        config.save();
    }

    @Override
    public void enable() {
        loadConfig();
    }

    @Override
    public void disable() {
        saveConfig();
    }
}
