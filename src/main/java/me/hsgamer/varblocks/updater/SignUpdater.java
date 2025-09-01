package me.hsgamer.varblocks.updater;

import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.varblocks.api.BlockUpdater;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;

import java.util.List;

public class SignUpdater implements BlockUpdater {
    private static final boolean HAS_SIGN_SIDE;

    static {
        boolean hasSignSide;
        try {
            Class.forName("org.bukkit.block.sign.SignSide");
            hasSignSide = true;
        } catch (Exception e) {
            hasSignSide = false;
        }
        HAS_SIGN_SIDE = hasSignSide;
    }

    @Override
    public void updateBlock(Block block, List<String> args) {
        BlockState blockState = block.getState();
        if (!(blockState instanceof Sign)) return;
        Sign sign = (Sign) blockState;

        for (int i = 0; i < 4; i++) {
            String line = args.size() > i ? ColorUtils.colorize(args.get(i)) : "";
            sign.setLine(i, line);
        }

        if (HAS_SIGN_SIDE) {
            SignSide back = sign.getSide(Side.BACK);
            for (int i = 4; i < 8; i++) {
                String line = args.size() > i ? ColorUtils.colorize(args.get(i)) : "";
                back.setLine(i - 4, line);
            }
        }

        sign.update(false, false);
    }
}
