package me.hsgamer.varblocks.api;

import org.bukkit.block.Block;

import java.util.List;

public interface BlockUpdater {
    void updateBlock(Block block, List<String> args);
}
