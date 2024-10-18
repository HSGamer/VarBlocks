package me.hsgamer.varblocks.manager;

import me.hsgamer.varblocks.api.BlockUpdater;
import me.hsgamer.varblocks.updater.SignUpdater;
import me.hsgamer.varblocks.updater.SkullUpdater;

import java.util.*;

public class BlockUpdaterManager {
    private final Map<String, BlockUpdater> blockUpdaterMap = new HashMap<>();

    public BlockUpdaterManager() {
        registerBlockUpdater("skull", new SkullUpdater());
        registerBlockUpdater("sign", new SignUpdater());
    }

    public void registerBlockUpdater(String name, BlockUpdater blockUpdater) {
        blockUpdaterMap.put(name, blockUpdater);
    }

    public Optional<BlockUpdater> getBlockUpdater(String name) {
        return Optional.ofNullable(blockUpdaterMap.get(name));
    }

    public List<String> getBlockUpdaters() {
        return new ArrayList<>(blockUpdaterMap.keySet());
    }
}
