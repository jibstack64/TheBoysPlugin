package jib.stack64.theboysplugin.player;

import jib.stack64.theboysplugin.ability.Ability;
import jib.stack64.theboysplugin.ability.AbilityType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

// Contains useful utilities for managing players' abilities.
public class PlayerAbilityManager {
    // Used for NBT manipulation
    public static final NamespacedKey namespacedKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("theboysplugin"), "ability");

    // Sets the provided player's ability.
    public static void setAbility(Ability ability, Player player) {
        removeAbility(player);
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(namespacedKey, PersistentDataType.STRING, ability.id);
        ability.gain(player);
    }

    // Gets the given player's ability.
    public static Ability getAbility(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return AbilityType.getByIdentifier(data.get(namespacedKey, PersistentDataType.STRING));
    }

    // Removes the player's ability.
    public static void removeAbility(Player player) {
        Ability ab = getAbility(player);
        if (ab != null) {
            ab.lose(player);
            PersistentDataContainer data = player.getPersistentDataContainer();
            data.set(namespacedKey, PersistentDataType.STRING, "");
            ab.lose(player);
        }
    }
}
