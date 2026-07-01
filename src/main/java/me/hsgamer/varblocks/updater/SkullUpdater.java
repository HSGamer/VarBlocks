package me.hsgamer.varblocks.updater;

import com.cryptomorin.xseries.profiles.gameprofile.MojangGameProfile;
import com.cryptomorin.xseries.profiles.objects.ProfileContainer;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.hsgamer.varblocks.api.BlockUpdater;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

public class SkullUpdater implements BlockUpdater {
    private final LoadingCache<String, MojangGameProfile> cache = CacheBuilder.newBuilder().build(new CacheLoader<String, MojangGameProfile>() {
        @SuppressWarnings("UnstableApiUsage")
        @Override
        public @NotNull MojangGameProfile load(@NotNull String key) {
            ProfileInputType profileInputType = ProfileInputType.typeOf(key);
            if (profileInputType != null && profileInputType != ProfileInputType.UUID && profileInputType != ProfileInputType.USERNAME) {
                return profileInputType.getProfile(key);
            }
            Profileable profileable = null;
            Player player = Bukkit.getPlayer(key);
            if (player != null) {
                profileable = Profileable.of(player);
            } else {
                OfflinePlayer offlinePlayer;
                try {
                    UUID uuid = UUID.fromString(key);
                    offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                } catch (Throwable i1) {
                    try {
                        //noinspection deprecation
                        offlinePlayer = Bukkit.getOfflinePlayer(key);
                    } catch (Throwable i2) {
                        offlinePlayer = null;
                    }
                }
                if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
                    profileable = Profileable.of(offlinePlayer);
                }
            }
            if (profileable == null) {
                profileable = Profileable.detect(key);
            }
            MojangGameProfile gameProfile = profileable.getProfile();
            if (gameProfile != null) {
                return gameProfile;
            }
            throw new IllegalStateException("Invalid profile key: " + key);
        }
    });

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @Nullable Consumer<Block> getUpdateTask(List<String> args) {
        if (args.isEmpty()) return null;

        boolean showErrors = args.size() > 1 && args.get(1).equalsIgnoreCase("true");

        Plugin plugin = JavaPlugin.getProvidingPlugin(getClass());
        MojangGameProfile gameProfile;
        try {
            gameProfile = cache.get(args.get(0));
        } catch (Throwable e) {
            if (showErrors) {
                plugin.getLogger().log(Level.WARNING, "Error while trying to update skull profile", e);
            }
            gameProfile = null;
        }

        MojangGameProfile finalGameProfile = gameProfile;
        return block -> {
            BlockState blockState = block.getState();
            if (!(blockState instanceof Skull)) return;
            Skull skull = (Skull) blockState;
            ProfileContainer.BlockStateProfileContainer profileContainer = new ProfileContainer.BlockStateProfileContainer(skull);
            try {
                profileContainer.setProfile(finalGameProfile);
            } catch (Throwable e) {
                if (showErrors) {
                    plugin.getLogger().log(Level.WARNING, "Error while trying to update skull profile", e);
                }
                profileContainer.setProfile(null);
                return;
            }

            skull.update(true, false);
        };
    }
}
