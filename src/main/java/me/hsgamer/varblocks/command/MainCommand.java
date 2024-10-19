package me.hsgamer.varblocks.command;

import io.github.projectunified.minelib.util.subcommand.SubCommandManager;
import me.hsgamer.varblocks.VarBlocks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainCommand extends Command {
    private final SubCommandManager subCommandManager = new SubCommandManager();

    public MainCommand(VarBlocks plugin) {
        super("varblocks");
        subCommandManager.registerSubcommand(new TemplateCommand(plugin));
        subCommandManager.registerSubcommand(new BlockCommand(plugin));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        return subCommandManager.onCommand(sender, commandLabel, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return subCommandManager.onTabComplete(sender, alias, args);
    }
}
