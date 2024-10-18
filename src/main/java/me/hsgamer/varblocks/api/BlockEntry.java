package me.hsgamer.varblocks.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class BlockEntry {
    public final String world;
    public final int x;
    public final int y;
    public final int z;
    public final String type;
    public final Map<String, String> args;

    public BlockEntry(String world, int x, int y, int z, String type, Map<String, String> args) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.args = args;
    }

    public BlockEntry withLocation(Location location) {
        return new BlockEntry(
                Optional.ofNullable(location.getWorld()).map(World::getName).orElse(""),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                type,
                args
        );
    }

    public BlockEntry withType(String type) {
        return new BlockEntry(world, x, y, z, type, args);
    }

    public BlockEntry withArgs(Map<String, String> args) {
        return new BlockEntry(world, x, y, z, type, args);
    }

    public BlockEntry withArg(String key, String value) {
        Map<String, String> newArgs = new LinkedHashMap<>(args);
        newArgs.put(key, value);
        return new BlockEntry(world, x, y, z, type, newArgs);
    }

    public Optional<Location> location() {
        return Optional.ofNullable(Bukkit.getWorld(world)).map(world -> new Location(world, x, y, z));
    }
}
