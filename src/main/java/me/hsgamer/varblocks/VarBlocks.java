package me.hsgamer.varblocks;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import io.github.projectunified.minelib.plugin.command.CommandComponent;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.bukkit.variable.BukkitVariableBundle;
import me.hsgamer.hscore.variable.VariableBundle;
import me.hsgamer.varblocks.command.MainCommand;
import me.hsgamer.varblocks.hook.PlaceholderAPIHook;
import me.hsgamer.varblocks.listener.BlockListener;
import me.hsgamer.varblocks.manager.BlockManager;
import me.hsgamer.varblocks.manager.BlockUpdaterManager;
import me.hsgamer.varblocks.manager.TemplateManager;
import me.hsgamer.varblocks.task.BlockUpdateTask;

import java.util.Arrays;
import java.util.List;

public final class VarBlocks extends BasePlugin {
    private final VariableBundle variableBundle = new VariableBundle();

    @Override
    protected List<Object> getComponents() {
        return Arrays.asList(
                new TemplateManager(this),
                new BlockUpdaterManager(),
                new BlockManager(this),

                new BlockUpdateTask(this),

                new Permissions(this),
                new CommandComponent(this, new MainCommand(this)),
                new BlockListener(this),

                new PlaceholderAPIHook()
        );
    }

    @Override
    public void load() {
        BukkitVariableBundle.registerVariables(variableBundle);
        MessageUtils.setPrefix("&8[&6VarBlocks&8] ");
    }

    @Override
    public void disable() {
        variableBundle.unregisterAll();
    }
}
