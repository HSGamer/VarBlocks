package me.hsgamer.varblocks.command.block;

import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.api.BlockEntry;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ArgumentCommand extends ModifyCommand {
    public ArgumentCommand(VarBlocks plugin) {
        super(plugin, "argument", "Set the value of the argument of the block", "<argument> <value>", true);
    }

    @Override
    protected BlockEntry modify(@NotNull CommandSender sender, @NotNull String label, @NotNull BlockEntry blockEntry, @NotNull String... args) {
        String argument = args[0];
        String[] value = Arrays.copyOfRange(args, 1, args.length);
        return blockEntry.withArgument(argument, String.join(" ", value));
    }

    @Override
    public boolean isProperUsage(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        return args.length >= 2;
    }
}
