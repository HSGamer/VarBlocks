package me.hsgamer.varblocks.updater;

import me.hsgamer.varblocks.api.BlockUpdater;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.util.List;

public class SignUpdater implements BlockUpdater {
    @Override
    public void updateBlock(Block block, List<String> args) {
        BlockState blockState = block.getState();
        if (!(blockState instanceof Sign)) return;
        Sign sign = (Sign) blockState;

        for (int i = 0; i < 4; i++) {
            if (args.size() > i) {
                sign.setLine(i, args.get(i));
            } else {
                sign.setLine(i, "");
            }
        }

        sign.update(false, false);
    }
}
