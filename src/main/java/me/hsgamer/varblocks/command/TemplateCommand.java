package me.hsgamer.varblocks.command;

import io.github.projectunified.minelib.util.subcommand.SubCommand;
import io.github.projectunified.minelib.util.subcommand.SubCommandManager;
import me.hsgamer.varblocks.Permissions;
import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.command.template.*;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TemplateCommand extends SubCommand {
    private final SubCommandManager subCommandManager = new SubCommandManager();

    public TemplateCommand(VarBlocks plugin) {
        super("template", "Template commands", "<label> template", Permissions.TEMPLATE.getName(), true);
        subCommandManager.registerSubcommand(new AddCommand(plugin));
        subCommandManager.registerSubcommand(new ClearCommand(plugin));
        subCommandManager.registerSubcommand(new InfoCommand(plugin));
        subCommandManager.registerSubcommand(new RemoveCommand(plugin));
        subCommandManager.registerSubcommand(new SetCommand(plugin));
    }

    @Override
    public void onSubCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        subCommandManager.onCommand(sender, label + " template", args);
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        return subCommandManager.onTabComplete(sender, label + " template", args);
    }
}
