package me.hsgamer.varblocks.hook;

import net.skinsrestorer.api.PropertyUtils;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.VersionProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.InputDataResult;
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

    public static Optional<String> getTextureUrl(UUID uuid) {
        try {
            return SkinsRestorerProvider.get().getPlayerStorage().getSkinForPlayer(uuid, "").map(PropertyUtils::getSkinTextureUrl);
        } catch (DataRequestException e) {
            return Optional.empty();
        }
    }

    public static Optional<String> getTextureUrl(Player player) {
        try {
            return SkinsRestorerProvider.get().getPlayerStorage().getSkinForPlayer(player.getUniqueId(), player.getName()).map(PropertyUtils::getSkinTextureUrl);
        } catch (DataRequestException e) {
            return Optional.empty();
        }
    }

    public static Optional<String> getTextureUrl(String name) {
        return SkinsRestorerProvider.get().getSkinStorage().findSkinData(name).map(InputDataResult::getProperty).map(PropertyUtils::getSkinTextureUrl);
    }
}
