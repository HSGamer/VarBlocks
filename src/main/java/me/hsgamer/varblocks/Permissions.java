package me.hsgamer.varblocks;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import io.github.projectunified.minelib.plugin.permission.PermissionComponent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Permissions extends PermissionComponent {
    public static final Permission TEMPLATE = new Permission("varblocks.template", PermissionDefault.OP);
    public static final Permission BLOCK = new Permission("varblocks.block", PermissionDefault.OP);

    public Permissions(BasePlugin plugin) {
        super(plugin);
    }
}
