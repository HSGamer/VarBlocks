package me.hsgamer.varblocks.hook;

import net.skinsrestorer.api.PropertyUtils;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.VersionProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public final class SkinsRestorerHook {
    private SkinsRestorerHook() {
        // EMPTY
    }

    public static boolean isAvailable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) {
            return false;
        }
        return VersionProvider.isCompatibleWith("15");
    }

    public static Optional<String> getUrl(String key) {
        PlayerStorage playerStorage = SkinsRestorerProvider.get().getPlayerStorage();
        Player player = Bukkit.getPlayer(key);
        if (player != null) {
            try {
                Optional<String> url = playerStorage.getSkinForPlayer(player.getUniqueId(), player.getName()).map(PropertyUtils::getSkinTextureUrl);
                if (url.isPresent()) return url;
            } catch (DataRequestException e) {
                return Optional.empty();
            }
        }

        try {
            UUID uuid = UUID.fromString(key);
            Optional<String> url = playerStorage.getSkinForPlayer(uuid, "").map(PropertyUtils::getSkinTextureUrl);
            if (url.isPresent()) return url;
        } catch (IllegalArgumentException ignored) {
            // IGNORED
        } catch (DataRequestException e) {
            return Optional.empty();
        }

        return SkinsRestorerProvider.get().getSkinStorage().findSkinData(key).map(InputDataResult::getProperty).map(PropertyUtils::getSkinTextureUrl);
    }
}
