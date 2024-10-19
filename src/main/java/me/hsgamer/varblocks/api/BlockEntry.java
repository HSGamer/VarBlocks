package me.hsgamer.varblocks.api;

import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BlockEntry {
    public final String world;
    public final int x;
    public final int y;
    public final int z;
    public final String type;
    public final String template;
    public final Map<String, String> args;

    public BlockEntry(String world, int x, int y, int z, String type, String template, Map<String, String> args) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.template = template;
        this.args = args;
    }

    public static BlockEntry fromMap(Map<String, Object> map) {
        String world = Optional.ofNullable(map.get("world")).map(Object::toString).orElse("");
        int x = Optional.ofNullable(map.get("x")).map(Object::toString).flatMap(Validate::getNumber).map(Number::intValue).orElse(0);
        int y = Optional.ofNullable(map.get("y")).map(Object::toString).flatMap(Validate::getNumber).map(Number::intValue).orElse(0);
        int z = Optional.ofNullable(map.get("z")).map(Object::toString).flatMap(Validate::getNumber).map(Number::intValue).orElse(0);
        String type = Optional.ofNullable(map.get("type")).map(Object::toString).orElse("");
        String template = Optional.ofNullable(map.get("template")).map(Object::toString).orElse("");
        Map<String, String> args = new LinkedHashMap<>();
        Optional.ofNullable(map.get("args"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .ifPresent(rawArgs -> rawArgs.forEach((key, value) -> args.put(key, value.toString())));
        return new BlockEntry(world, x, y, z, type, template, args);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("world", world);
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        map.put("type", type);
        map.put("template", template);
        map.put("args", args);
        return map;
    }

    public BlockEntry withLocation(Location location) {
        return new BlockEntry(
                Optional.ofNullable(location.getWorld()).map(World::getName).orElse(""),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                type,
                template,
                args
        );
    }

    public BlockEntry withType(String type) {
        return new BlockEntry(world, x, y, z, type, template, args);
    }

    public BlockEntry withTemplate(String template) {
        return new BlockEntry(world, x, y, z, type, template, args);
    }

    public BlockEntry withArguments(Map<String, String> args) {
        return new BlockEntry(world, x, y, z, type, template, args);
    }

    public BlockEntry withArgument(String key, String value) {
        Map<String, String> newArgs = new LinkedHashMap<>(args);
        newArgs.put(key, value);
        return withArguments(newArgs);
    }

    public boolean valid() {
        return Bukkit.getWorld(world) != null;
    }

    public Location location() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockEntry that = (BlockEntry) o;
        return x == that.x && y == that.y && z == that.z && Objects.equals(world, that.world) && Objects.equals(type, that.type) && Objects.equals(template, that.template) && Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z, type, template, args);
    }
}
