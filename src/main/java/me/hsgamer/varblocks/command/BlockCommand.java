package me.hsgamer.varblocks.command;

import io.github.projectunified.minelib.util.subcommand.SubCommand;
import io.github.projectunified.minelib.util.subcommand.SubCommandManager;
import me.hsgamer.varblocks.Permissions;
import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.command.block.TemplateCommand;
import me.hsgamer.varblocks.command.block.*;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockCommand extends SubCommand {
    private final SubCommandManager subCommandManager = new SubCommandManager();

    public BlockCommand(VarBlocks plugin) {
        super("block", "Block commands", "<label> block", Permissions.BLOCK.getName(), true);
        subCommandManager.registerSubcommand(new AddCommand(plugin));
        subCommandManager.registerSubcommand(new RemoveCommand(plugin));
        subCommandManager.registerSubcommand(new LocationCommand(plugin));
        subCommandManager.registerSubcommand(new TemplateCommand(plugin));
        subCommandManager.registerSubcommand(new TypeCommand(plugin));
        subCommandManager.registerSubcommand(new ArgumentCommand(plugin));
        subCommandManager.registerSubcommand(new InfoCommand(plugin));
    }

    @Override
    public void onSubCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        subCommandManager.onCommand(sender, label + " block", args);
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        return subCommandManager.onTabComplete(sender, label + " block", args);
    }
}
