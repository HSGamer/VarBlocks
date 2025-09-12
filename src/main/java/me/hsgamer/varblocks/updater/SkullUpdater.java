package me.hsgamer.varblocks.updater;

import com.cryptomorin.xseries.profiles.objects.ProfileContainer;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import me.hsgamer.varblocks.api.BlockUpdater;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

public class SkullUpdater implements BlockUpdater {
    private final LoadingCache<String, Profileable> cache = CacheBuilder.newBuilder().build(new CacheLoader<String, Profileable>() {
        @Override
        public @NotNull Profileable load(@NotNull String key) throws Exception {
            try {
                return Profileable.detect(key);
            } catch (Throwable e) {
                throw new Exception(e);
            }
        }
    });

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @Nullable Consumer<Block> getUpdateTask(List<String> args) {
        if (args.isEmpty()) return null;

        boolean showErrors = args.size() > 1 && args.get(1).equalsIgnoreCase("true");

        Plugin plugin = JavaPlugin.getProvidingPlugin(getClass());
        GameProfile gameProfile;
        try {
            Profileable profileable = cache.get(args.get(0));
            gameProfile = profileable.getProfile();
        } catch (Throwable e) {
            if (showErrors) {
                plugin.getLogger().log(Level.WARNING, "Error while trying to update skull profile", e);
            }
            return null;
        }

        return block -> {
            BlockState blockState = block.getState();
            if (!(blockState instanceof Skull)) return;
            Skull skull = (Skull) blockState;
            ProfileContainer.BlockStateProfileContainer profileContainer = new ProfileContainer.BlockStateProfileContainer(skull);
            try {
                profileContainer.setProfile(gameProfile);
            } catch (Throwable e) {
                if (showErrors) {
                    plugin.getLogger().log(Level.WARNING, "Error while trying to update skull profile", e);
                }
                return;
            }

            skull.update(true, false);
        };
    }
}
