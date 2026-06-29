package me.hsgamer.varblocks.manager;

import io.github.projectunified.maptemplate.MapTemplate;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.varblocks.VarBlocks;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateManager {
    private final VarBlocks plugin;
    private final BukkitConfig config;

    public TemplateManager(VarBlocks plugin) {
        this.config = new BukkitConfig(plugin, "templates.yml");
        this.plugin = plugin;
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

    public List<String> getParsedTemplate(String name, Map<String, Object> args) {
        List<String> template = getTemplate(name);
        if (template == null || template.isEmpty()) {
            return Collections.emptyList();
        }

        StringReplacer variableManager = plugin.get(StringManager.class).getVariableManager();
        StringReplacer replacer = plugin.get(StringManager.class).getReplacer();

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
        return template.stream().map(s -> replacer.replaceOrOriginal(s, null)).collect(Collectors.toList());
    }
}
