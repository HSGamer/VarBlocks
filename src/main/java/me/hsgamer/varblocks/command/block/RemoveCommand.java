package me.hsgamer.varblocks.command.block;

import io.github.projectunified.minelib.util.subcommand.SubCommand;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.varblocks.Permissions;
import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.manager.BlockManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class RemoveCommand extends SubCommand {
    private final VarBlocks plugin;

    public RemoveCommand(VarBlocks plugin) {
        super("remove", "Remove a block from the plugin", "<label> remove <name>", Permissions.BLOCK.getName(), true);
        this.plugin = plugin;
    }

    @Override
    public void onSubCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        String name = args[0];
        BlockManager blockManager = plugin.get(BlockManager.class);
        if (!blockManager.getBlockEntry(name).isPresent()) {
            MessageUtils.sendMessage(sender, "&cThe block &e" + name + " &cdoesn't exist");
            return;
        }
        plugin.get(BlockManager.class).removeBlockEntry(name);
        MessageUtils.sendMessage(sender, "&aThe block &e" + name + " &ahas been removed");
    }

    @Override
    public boolean isProperUsage(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        return args.length >= 1;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        if (args.length == 1) {
            return plugin.get(BlockManager.class).getBlockNames();
        }
        return Collections.emptyList();
    }
}
