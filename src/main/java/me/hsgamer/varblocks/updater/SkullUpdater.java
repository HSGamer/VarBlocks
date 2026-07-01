package me.hsgamer.varblocks.updater;

import com.cryptomorin.xseries.profiles.gameprofile.MojangGameProfile;
import com.cryptomorin.xseries.profiles.objects.ProfileContainer;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.hsgamer.varblocks.api.BlockUpdater;
import me.hsgamer.varblocks.hook.SkinsRestorerHook;
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

public class SkullUpdater implements BlockUpdater {
    private static final LoadingCache<String, MojangGameProfile> PROFILE_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<String, MojangGameProfile>() {
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
                if (SkinsRestorerHook.isAvailable()) {
                    Optional<String> url = SkinsRestorerHook.getTextureUrl(player);
                    if (url.isPresent()) {
                        return ProfileInputType.TEXTURE_URL.getProfile(url.get());
                    }
                }
                profileable = Profileable.of(player);
            } else {
                try {
                    UUID uuid = UUID.fromString(key);
                    if (SkinsRestorerHook.isAvailable()) {
                        Optional<String> url = SkinsRestorerHook.getTextureUrl(uuid);
                        if (url.isPresent()) {
                            return ProfileInputType.TEXTURE_URL.getProfile(url.get());
                        }
                    }
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    if (offlinePlayer.hasPlayedBefore()) {
                        profileable = Profileable.of(offlinePlayer);
                    }
                } catch (Throwable ignored) {
                    // IGNORED
                }
            }
            if (profileable == null) {
                if (SkinsRestorerHook.isAvailable()) {
                    Optional<String> url = SkinsRestorerHook.getTextureUrl(key);
                    if (url.isPresent()) {
                        return ProfileInputType.TEXTURE_URL.getProfile(url.get());
                    }
                }
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
            gameProfile = PROFILE_CACHE.get(args.get(0));
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
