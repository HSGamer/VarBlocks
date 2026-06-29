package me.hsgamer.varblocks.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.hscore.bukkit.variable.BukkitVariableBundle;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.variable.VariableManager;

import java.util.ArrayList;
import java.util.List;

public class StringManager implements Loadable {
    private final VariableManager variableManager = new VariableManager();
    private final List<StringReplacer> replacers = new ArrayList<>();

    @Override
    public void enable() {
        new BukkitVariableBundle(variableManager);
    }

    public Runnable addReplacer(StringReplacer replacer) {
        replacers.add(replacer);
        return () -> replacers.remove(replacer);
    }

    public VariableManager getVariableManager() {
        return variableManager;
    }

    public StringReplacer getReplacer() {
        return StringReplacer.combine(replacers);
    }
}
