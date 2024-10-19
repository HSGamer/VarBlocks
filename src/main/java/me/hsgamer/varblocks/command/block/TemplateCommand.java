package me.hsgamer.varblocks.command.block;

import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.api.BlockEntry;
import me.hsgamer.varblocks.manager.TemplateManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class TemplateCommand extends ModifyCommand {
    public TemplateCommand(VarBlocks plugin) {
        super(plugin, "template", "Set the template the block will use", "<template>", true);
    }

    @Override
    protected BlockEntry modify(@NotNull CommandSender sender, @NotNull String label, @NotNull BlockEntry blockEntry, @NotNull String... args) {
        return blockEntry.withTemplate(args[0]);
    }

    @Override
    protected boolean isProperArgUsage(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        return args.length >= 1;
    }

    @Override
    protected @NotNull List<String> onModifyTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        if (args.length == 1) {
            return plugin.get(TemplateManager.class).getTemplateNames();
        }
        return Collections.emptyList();
    }
}
