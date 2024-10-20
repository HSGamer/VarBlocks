package me.hsgamer.varblocks.command.template;

import me.hsgamer.varblocks.VarBlocks;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddCommand extends ModifyCommand {
    public AddCommand(VarBlocks plugin) {
        super(plugin, "add", "Add a text to the template", "<text>");
    }

    @Override
    protected boolean modify(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> template, @NotNull String... args) {
        template.add(String.join(" ", args));
        return true;
    }
}
