package jib.stack64.theboysplugin;

import jib.stack64.theboysplugin.command.CommandAbility;
import jib.stack64.theboysplugin.command.CommandToggle;
import jib.stack64.theboysplugin.player.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TheBoysPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Add listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        // Register commands
        getCommand("ability").setExecutor(new CommandAbility());
        getCommand("toggle").setExecutor(new CommandToggle());
    }

    @Override
    public void onDisable() {

    }
}
