package me.hsgamer.varblocks.command.block;

import io.github.projectunified.minelib.util.subcommand.SubCommand;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.varblocks.Permissions;
import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.api.BlockEntry;
import me.hsgamer.varblocks.manager.BlockManager;
import me.hsgamer.varblocks.manager.BlockUpdaterManager;
import me.hsgamer.varblocks.manager.TemplateManager;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class AddCommand extends SubCommand {
    private final VarBlocks plugin;

    public AddCommand(VarBlocks plugin) {
        super("add", "Add the looking block to the plugin", "<label> add <name> <type> <template>", Permissions.BLOCK.getName(), false);
        this.plugin = plugin;
    }

    @Override
    public void onSubCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        Player player = (Player) sender;

        Block block = player.getTargetBlock(null, 5);
        if (!block.getType().isBlock()) {
            sender.sendMessage("&cYou need to look at a block");
            return;
        }

        String name = args[0];
        String type = args[1];
        String template = args[2];

        BlockManager blockManager = plugin.get(BlockManager.class);
        if (blockManager.getBlockEntry(name).isPresent()) {
            MessageUtils.sendMessage(sender, "&cThe block with the name " + name + " already exists");
            return;
        }

        BlockEntry blockEntry = new BlockEntry(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, template, Collections.emptyMap());
        plugin.get(BlockManager.class).setBlockEntry(name, blockEntry);

        MessageUtils.sendMessage(sender, "&aThe block with the name " + name + " has been added");
    }

    @Override
    public boolean isProperUsage(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        return args.length >= 3;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        if (args.length == 2) {
            return plugin.get(BlockUpdaterManager.class).getBlockUpdaters();
        } else if (args.length == 3) {
            return plugin.get(TemplateManager.class).getTemplateNames();
        }
        return Collections.emptyList();
    }
}
