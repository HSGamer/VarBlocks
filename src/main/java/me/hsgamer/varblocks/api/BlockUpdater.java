package me.hsgamer.varblocks.api;

import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public interface BlockUpdater {
    @Nullable Consumer<Block> getUpdateTask(List<String> args);
}
