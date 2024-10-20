package me.hsgamer.varblocks.command.template;

import me.hsgamer.hscore.common.Validate;
import me.hsgamer.varblocks.VarBlocks;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SetCommand extends ModifyCommand {
    public SetCommand(VarBlocks plugin) {
        super(plugin, "set", "Set a text at the specified index of the template", "<index> <text>");
    }

    @Override
    protected boolean modify(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> template, @NotNull String... args) {
        int index = Integer.parseInt(args[0]);
        String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (index >= template.size()) {
            for (int i = template.size(); i < index; i++) {
                template.add("");
            }
            template.add(text);
        } else {
            template.set(index, text);
        }
        return true;
    }

    @Override
    protected boolean isProperArgUsage(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        return args.length >= 1 && Validate.getNumber(args[0]).filter(n -> n.compareTo(BigDecimal.ZERO) >= 0).isPresent();
    }

    @Override
    protected @NotNull List<String> onModifyTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> template, @NotNull String... args) {
        if (args.length == 1) {
            return IntStream.range(0, template.size()).mapToObj(String::valueOf).collect(Collectors.toList());
        }
        return super.onModifyTabComplete(sender, label, template, args);
    }
}
