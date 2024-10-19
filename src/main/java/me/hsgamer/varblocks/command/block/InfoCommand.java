package me.hsgamer.varblocks.command.block;

import io.github.projectunified.minelib.util.subcommand.SubCommand;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.varblocks.Permissions;
import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.api.BlockEntry;
import me.hsgamer.varblocks.manager.BlockManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class InfoCommand extends SubCommand {
    private final VarBlocks plugin;

    public InfoCommand(VarBlocks plugin) {
        super("info", "Show the information of the block", "<label> info <name>", Permissions.BLOCK.getName(), true);
        this.plugin = plugin;
    }

    @Override
    public void onSubCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        String name = args[0];
        BlockManager blockManager = plugin.get(BlockManager.class);
        Optional<BlockEntry> optionalBlockEntry = blockManager.getBlockEntry(name);
        if (!optionalBlockEntry.isPresent()) {
            MessageUtils.sendMessage(sender, "&cThe block &e" + name + " &cdoesn't exist");
            return;
        }
        BlockEntry blockEntry = optionalBlockEntry.get();

        MessageUtils.sendMessage(sender, "&eName: &f" + name);
        MessageUtils.sendMessage(sender, "&eLocation: &f" + blockEntry.world + " " + blockEntry.x + " " + blockEntry.y + " " + blockEntry.z);
        MessageUtils.sendMessage(sender, "&eType: &f" + blockEntry.type);
        MessageUtils.sendMessage(sender, "&eTemplate: &f" + blockEntry.template);
        MessageUtils.sendMessage(sender, "&eArguments: ");
        blockEntry.args.forEach((key, value) -> MessageUtils.sendMessage(sender, "  &b" + key + ": &f" + value));
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
