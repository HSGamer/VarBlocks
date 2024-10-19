package me.hsgamer.varblocks.command.block;

import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.api.BlockEntry;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LocationCommand extends ModifyCommand {
    public LocationCommand(VarBlocks plugin) {
        super(plugin, "location", "Set the location of the block", "", false);
    }

    @Override
    protected BlockEntry modify(@NotNull CommandSender sender, @NotNull String label, @NotNull BlockEntry blockEntry, @NotNull String... args) {
        Player player = (Player) sender;
        Block block = player.getTargetBlock(null, 5);
        if (!block.getType().isBlock()) {
            sender.sendMessage("&cYou need to look at a block");
            return blockEntry;
        }
        return blockEntry.withLocation(block.getLocation());
    }
}
