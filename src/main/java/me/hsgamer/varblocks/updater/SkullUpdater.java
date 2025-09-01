package me.hsgamer.varblocks.updater;

import com.cryptomorin.xseries.profiles.objects.ProfileContainer;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import me.hsgamer.varblocks.api.BlockUpdater;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

public class SkullUpdater implements BlockUpdater {
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void updateBlock(Block block, List<String> args) {
        if (args.isEmpty()) return;

        BlockState blockState = block.getState();
        if (!(blockState instanceof Skull)) return;
        Skull skull = (Skull) blockState;
        ProfileContainer.BlockStateProfileContainer profileContainer = new ProfileContainer.BlockStateProfileContainer(skull);

        boolean showErrors = args.size() > 1 && args.get(1).equalsIgnoreCase("true");

        Profileable profileable;
        try {
            profileable = Profileable.detect(args.get(0));
            profileContainer.setProfile(profileable.getProfile());
        } catch (Throwable e) {
            if (showErrors) {
                JavaPlugin.getProvidingPlugin(getClass()).getLogger().log(Level.WARNING, "Error while trying to update skull profile", e);
            }
            return;
        }

        skull.update(true, false);
    }
}
