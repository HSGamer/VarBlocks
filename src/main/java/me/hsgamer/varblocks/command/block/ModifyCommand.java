package me.hsgamer.varblocks.command.block;

import io.github.projectunified.minelib.util.subcommand.SubCommand;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.varblocks.Permissions;
import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.api.BlockEntry;
import me.hsgamer.varblocks.manager.BlockManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class ModifyCommand extends SubCommand {
    protected final VarBlocks plugin;

    protected ModifyCommand(VarBlocks plugin, @NotNull String name, @NotNull String description, @NotNull String argUsage, boolean consoleAllowed) {
        super(name, description, "<label> " + name + " <name> " + argUsage, Permissions.BLOCK.getName(), consoleAllowed);
        this.plugin = plugin;
    }

    protected abstract BlockEntry modify(@NotNull CommandSender sender, @NotNull String label, @NotNull BlockEntry blockEntry, @NotNull String... args);

    protected boolean isProperArgUsage(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        return true;
    }

    protected @NotNull List<String> onModifyTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        return Collections.emptyList();
    }

    @Override
    public void onSubCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        String name = args[0];
        BlockManager blockManager = plugin.get(BlockManager.class);

        Optional<BlockEntry> optionalBlockEntry = blockManager.getBlockEntry(name);
        if (!optionalBlockEntry.isPresent()) {
            MessageUtils.sendMessage(sender, "&cThe block &4" + name + " &cdoesn't exist");
            return;
        }
        BlockEntry blockEntry = optionalBlockEntry.get();

        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);

        BlockEntry newBlockEntry = modify(sender, label, blockEntry, newArgs);
        if (!Objects.equals(blockEntry, newBlockEntry)) {
            blockManager.setBlockEntry(name, newBlockEntry);
            MessageUtils.sendMessage(sender, "&aThe block &2" + name + " &ahas been modified");
        } else {
            MessageUtils.sendMessage(sender, "&cThe block &4" + name + " &chasn't been modified");
        }
    }

    @Override
    public boolean isProperUsage(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        if (args.length < 1) {
            return false;
        }
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        return isProperArgUsage(sender, label, newArgs);
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            return plugin.get(BlockManager.class).getBlockNames();
        }
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        return onModifyTabComplete(sender, label, newArgs);
    }
}
