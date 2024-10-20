package me.hsgamer.varblocks.manager;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateManager {
    private final BukkitConfig config;

    public TemplateManager(Plugin plugin) {
        this.config = new BukkitConfig(plugin, "templates.yml");
        this.config.setup();
    }

    public List<String> getTemplate(String name) {
        return CollectionUtils.createStringListFromObject(config.getNormalized(name), false);
    }

    public void saveTemplate(String name, List<String> template) {
        if (template.isEmpty()) {
            config.remove(name);
        } else {
            config.set(template, name);
        }
        config.save();
    }

    public List<String> getTemplateNames() {
        return config.getKeys(false).stream().map(PathString::joinDefault).collect(Collectors.toList());
    }

    public List<String> getParsedTemplate(String name, Map<String, String> args) {
        return getTemplate(name).stream().map(s -> {
            for (Map.Entry<String, String> entry : args.entrySet()) {
                s = s.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            s = VariableManager.GLOBAL.replaceOrOriginal(s, null);
            return s;
        }).collect(Collectors.toList());
    }
}
