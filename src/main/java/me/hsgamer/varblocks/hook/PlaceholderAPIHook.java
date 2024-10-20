package me.hsgamer.varblocks.hook;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.Bukkit;

public class PlaceholderAPIHook implements Loadable {
    private StringReplacer replacer;

    @Override
    public void load() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            replacer = StringReplacer.of(original -> PlaceholderAPI.setPlaceholders(null, original));
            VariableManager.GLOBAL.addExternalReplacer(replacer);
        }
    }

    @Override
    public void disable() {
        if (replacer != null) {
            VariableManager.GLOBAL.removeExternalReplacer(replacer);
        }
    }
}
