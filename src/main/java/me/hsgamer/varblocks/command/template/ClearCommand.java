package me.hsgamer.varblocks.command.template;

import me.hsgamer.varblocks.VarBlocks;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClearCommand extends ModifyCommand {
    public ClearCommand(VarBlocks plugin) {
        super(plugin, "clear", "Clear all texts of the template", "");
    }

    @Override
    protected boolean modify(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> template, @NotNull String... args) {
        template.clear();
        return true;
    }
}
