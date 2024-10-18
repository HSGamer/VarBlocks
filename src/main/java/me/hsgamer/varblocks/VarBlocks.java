package me.hsgamer.varblocks;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import me.hsgamer.hscore.bukkit.variable.BukkitVariableBundle;
import me.hsgamer.hscore.variable.VariableBundle;
import me.hsgamer.varblocks.manager.BlockUpdaterManager;
import me.hsgamer.varblocks.manager.TemplateManager;

import java.util.Arrays;
import java.util.List;

public final class VarBlocks extends BasePlugin {
    private final VariableBundle variableBundle = new VariableBundle();

    @Override
    protected List<Object> getComponents() {
        return Arrays.asList(
                new TemplateManager(this),
                new BlockUpdaterManager()
        );
    }

    @Override
    public void load() {
        BukkitVariableBundle.registerVariables(variableBundle);
    }

    @Override
    public void disable() {
        variableBundle.unregisterAll();
    }
}
