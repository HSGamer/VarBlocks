package me.hsgamer.varblocks.manager;

import io.github.projectunified.maptemplate.MapTemplate;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.variable.BukkitVariableBundle;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.hscore.variable.VariableManager;
import me.hsgamer.varblocks.VarBlocks;
import me.hsgamer.varblocks.hook.PlaceholderAPIHook;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateManager {
    private final VariableManager variableManager;
    private final BukkitConfig config;

    public TemplateManager(VarBlocks plugin) {
        this.config = new BukkitConfig(plugin, "templates.yml");
        this.config.setup();
        this.variableManager = new VariableManager();
        new BukkitVariableBundle(variableManager);
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

    private String transform(String text) {
        return PlaceholderAPIHook.replace(text);
    }

    public List<String> getParsedTemplate(String name, Map<String, Object> args) {
        List<String> template = getTemplate(name);
        if (template == null || template.isEmpty()) {
            return Collections.emptyList();
        }

        MapTemplate mapTemplate = MapTemplate.builder()
                .setVariableFunction(s -> {
                    if (args.containsKey(s)) {
                        return args.get(s);
                    }
                    return variableManager.replaceOrOriginal(s, null);
                })
                .build();
        //noinspection unchecked
        template = (List<String>) mapTemplate.apply(template);
        return template.stream().map(this::transform).collect(Collectors.toList());
    }
}
