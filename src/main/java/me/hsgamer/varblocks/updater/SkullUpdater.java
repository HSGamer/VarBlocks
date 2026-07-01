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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

public class SkullUpdater implements BlockUpdater {
    private static final Map<String, MojangGameProfile> STATIC_PROFILE_CACHE = new ConcurrentHashMap<>();
    private static final LoadingCache<String, Optional<MojangGameProfile>> PROFILE_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<String, Optional<MojangGameProfile>>() {
        @SuppressWarnings("UnstableApiUsage")
        @Override
        public @NotNull Optional<MojangGameProfile> load(@NotNull String key) {
            if (SkinsRestorerHook.isAvailable()) {
                Optional<String> url = SkinsRestorerHook.getUrl(key);
                if (url.isPresent()) {
                    return Optional.ofNullable(ProfileInputType.TEXTURE_URL.getProfile(url.get()));
                }
            }

            Profileable profileable = resolveProfileable(key);
            MojangGameProfile gameProfile = profileable.getProfile();
            if (gameProfile != null) {
                return Optional.of(gameProfile);
            }

            return Optional.empty();
        }
    });

    private static @NotNull Profileable resolveProfileable(String key) {
        Player player = Bukkit.getPlayer(key);
        if (player != null) {
            return Profileable.of(player);
        }

        try {
            UUID uuid = UUID.fromString(key);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer.hasPlayedBefore()) {
                return Profileable.of(offlinePlayer);
            }
        } catch (IllegalArgumentException ignored) {
            // IGNORED
        }

        return Profileable.detect(key);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @Nullable Consumer<Block> getUpdateTask(List<String> args) {
        if (args.isEmpty()) return null;

        boolean showErrors = args.size() > 1 && args.get(1).equalsIgnoreCase("true");

        MojangGameProfile gameProfile;
        try {
            String key = args.get(0);
            if (STATIC_PROFILE_CACHE.containsKey(key)) {
                gameProfile = STATIC_PROFILE_CACHE.get(key);
            } else {
                ProfileInputType profileInputType = ProfileInputType.typeOf(key);
                if (profileInputType != null && profileInputType != ProfileInputType.UUID && profileInputType != ProfileInputType.USERNAME) {
                    gameProfile = profileInputType.getProfile(key);
                    STATIC_PROFILE_CACHE.put(key, gameProfile);
                } else {
                    gameProfile = PROFILE_CACHE.get(key).orElse(null);
                }
            }
        } catch (Throwable e) {
            if (showErrors) {
                JavaPlugin.getProvidingPlugin(getClass()).getLogger().log(Level.WARNING, "Error while trying to update skull profile", e);
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
                    JavaPlugin.getProvidingPlugin(getClass()).getLogger().log(Level.WARNING, "Error while trying to update skull profile", e);
                }
                profileContainer.setProfile(null);
                return;
            }

            skull.update(true, false);
        };
    }
}
