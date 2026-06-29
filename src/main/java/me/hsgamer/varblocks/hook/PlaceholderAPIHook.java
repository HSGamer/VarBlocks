package me.hsgamer.varblocks.hook;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.manager.StringManager;
import org.bukkit.Bukkit;

public class PlaceholderAPIHook implements Loadable {
    private final VarBlocks plugin;
    private Runnable disableRunnable;

    public PlaceholderAPIHook(VarBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            disableRunnable = plugin.get(StringManager.class).addReplacer(StringReplacer.of(original -> PlaceholderAPI.setPlaceholders(null, original)));
        }
    }

    @Override
    public void disable() {
        if (disableRunnable != null) {
            disableRunnable.run();
        }
    }
}
