package me.hsgamer.varblocks.command.template;

import io.github.projectunified.minelib.util.subcommand.SubCommand;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.varblocks.Permissions;
import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.manager.TemplateManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfoCommand extends SubCommand {
    private final VarBlocks plugin;

    public InfoCommand(VarBlocks plugin) {
        super("info", "Show the texts of the template", "<label> info <name>", Permissions.TEMPLATE.getName(), true);
        this.plugin = plugin;
    }

    @Override
    public void onSubCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        String name = args[0];
        TemplateManager templateManager = plugin.get(TemplateManager.class);
        List<String> template = templateManager.getTemplate(name);
        MessageUtils.sendMessage(sender, "&eTemplate: &f" + name);
        MessageUtils.sendMessage(sender, "&eTexts:");
        for (int i = 0; i < template.size(); i++) {
            MessageUtils.sendMessage(sender, "&7" + i + ": &f" + template.get(i));
        }
    }

    @Override
    public boolean isProperUsage(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        return args.length >= 1;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        if (args.length == 1) {
            return plugin.get(TemplateManager.class).getTemplateNames();
        }
        return super.onTabComplete(sender, label, args);
    }
}
