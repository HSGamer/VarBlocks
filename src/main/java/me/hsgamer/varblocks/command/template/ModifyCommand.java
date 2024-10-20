package me.hsgamer.varblocks.command.template;

import io.github.projectunified.minelib.util.subcommand.SubCommand;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.varblocks.Permissions;
import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.manager.TemplateManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ModifyCommand extends SubCommand {
    protected final VarBlocks plugin;

    public ModifyCommand(VarBlocks plugin, @NotNull String name, @NotNull String description, @NotNull String argUsage) {
        super(name, description, "<label> " + name + " <name> " + argUsage, Permissions.TEMPLATE.getName(), true);
        this.plugin = plugin;
    }

    protected abstract boolean modify(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> template, @NotNull String... args);

    protected boolean isProperArgUsage(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        return true;
    }

    protected @NotNull List<String> onModifyTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> template, @NotNull String... args) {
        return Collections.emptyList();
    }

    @Override
    public void onSubCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        String name = args[0];
        TemplateManager templateManager = plugin.get(TemplateManager.class);

        List<String> template = new ArrayList<>(templateManager.getTemplate(name));

        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);

        if (modify(sender, label, template, newArgs)) {
            templateManager.saveTemplate(name, template);
            MessageUtils.sendMessage(sender, "&aThe template &2" + name + " &ahas been modified");
        } else {
            MessageUtils.sendMessage(sender, "&cThe template &4" + name + " &chasn't be modified");
        }
    }

    @Override
    public boolean isProperUsage(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        if (args.length < 1) {
            return false;
        }
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);
        return isProperArgUsage(sender, label, newArgs);
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            return plugin.get(TemplateManager.class).getTemplateNames();
        }

        List<String> template = plugin.get(TemplateManager.class).getTemplate(args[0]);

        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);
        return onModifyTabComplete(sender, label, template, newArgs);
    }
}
