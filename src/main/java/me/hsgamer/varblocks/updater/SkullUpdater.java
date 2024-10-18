package me.hsgamer.varblocks.updater;

import me.hsgamer.hscore.common.Validate;
import me.hsgamer.varblocks.api.BlockUpdater;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SkullUpdater implements BlockUpdater {
    private static final UUID skullUUID = UUID.fromString("832b9c2d-f6c2-4c5f-8e0d-e7e7c4e9f9c8");
    private static Method setOwningPlayerMethod;
    private static Method setOwnerMethod;

    static {
        try {
            setOwnerMethod = Skull.class.getDeclaredMethod("setOwner", String.class);
            setOwningPlayerMethod = Skull.class.getDeclaredMethod("setOwningPlayer", OfflinePlayer.class);
        } catch (NoSuchMethodException e) {
            // IGNORED
        }
    }

    private void setOwner(Skull skull, OfflinePlayer owner) {
        if (setOwningPlayerMethod != null) {
            try {
                setOwningPlayerMethod.invoke(skull, owner);
            } catch (IllegalAccessException | InvocationTargetException e) {
                JavaPlugin.getProvidingPlugin(getClass()).getLogger().log(Level.WARNING, "Error when setting owner for skulls", e);
            }
        } else {
            try {
                setOwnerMethod.invoke(skull, owner == null ? null : owner.getName());
            } catch (IllegalAccessException | InvocationTargetException e) {
                JavaPlugin.getProvidingPlugin(getClass()).getLogger().log(Level.WARNING, "Error when setting owner for skulls", e);
            }
        }
    }

    @Override
    public void updateBlock(Block block, List<String> args) {
        BlockState blockState = block.getState();
        if (!(blockState instanceof Skull)) return;
        Skull skull = (Skull) blockState;

        UUID uuid;
        if (args.isEmpty()) {
            uuid = skullUUID;
        } else {
            String arg = args.get(0);
            boolean isPlayerName = args.size() > 1 && Boolean.parseBoolean(args.get(1));
            if (isPlayerName) {
                @SuppressWarnings("deprecation") OfflinePlayer player = Bukkit.getOfflinePlayer(arg);
                if (player.hasPlayedBefore()) {
                    uuid = player.getUniqueId();
                } else {
                    uuid = skullUUID;
                }
            } else {
                uuid = Validate.getUUID(arg).orElse(skullUUID);
            }
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        setOwner(skull, player);
        skull.update(false, false);
    }
}
