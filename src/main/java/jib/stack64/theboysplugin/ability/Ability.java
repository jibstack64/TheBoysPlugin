package jib.stack64.theboysplugin.ability;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import javax.annotation.Nullable;
import javax.lang.model.type.NullType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

// A base superpower that all other abilities extend apon.
// When coding an ability, call super.funcName(...) in each any every function overrided by the class.
// ^ There are some exceptions, of course.
public class Ability {
    // The ability's identifier
    public final String id;
    // The display name for the ability
    public final String name;
    // The tip displayed in chat when the ability is gained
    public final String tip;

    public Ability(String id, String name, String tip) {
        this.id = id;
        this.name = name;
        this.tip = tip;
    }

    // Contains a map with players and their toggled status
    // If their toggled status is null, the ability in question is not one supporting toggling
    public Map<UUID, Boolean> playersToggleStatus = new HashMap<>();
    // Players and whether they are on cooldown or not
    public Map<UUID, Boolean> playersCooldownStatus = new HashMap<>();
    // Cooldown durations for each player
    public Map<UUID, Integer> playersCooldownDuration = new HashMap<>();
    // Cooldown schedule ids for each player
    public Map<UUID, Integer> playersCooldownShedule = new HashMap<>();

    // Called when the ability is activated.
    public void toggle(Player player) {
        // KEEP THIS IN MIND! If the player is on cooldown, the ability is not set to toggled
        if (!isOnCooldown(player)) {
            setToggled(player, !isToggled(player));
        } else {
            player.chat(ChatColor.BOLD + "You are on cooldown for another " + ChatColor.RESET + ChatColor.ITALIC + getCooldown(player) + ChatColor.RESET + ChatColor.BOLD + " seconds.");
        }
    }

    // Gives the user the 'buffs' and or semi-permanent effects of the ability.
    public void gain(Player player) {
        // Form all data
        UUID uuid = player.getUniqueId();
        playersToggleStatus.putIfAbsent(uuid, false);
        playersCooldownStatus.putIfAbsent(uuid, false);
        playersCooldownDuration.putIfAbsent(uuid, 0);
        playersCooldownShedule.putIfAbsent(uuid, null);
    }

    // Removes any effects of the ability from the player.
    public void lose(Player player) {
        // Clear from all data
        UUID uuid = player.getUniqueId();
        playersToggleStatus.remove(uuid);
        playersCooldownStatus.remove(uuid);
        playersCooldownDuration.remove(uuid);
        playersCooldownShedule.remove(uuid);
    }

    // Returns a player's remaining cooldown (s).
    public int getCooldown(Player player) {
        return playersCooldownDuration.get(player.getUniqueId());
    }

    // Returns true if the player is on cooldown.
    public boolean isOnCooldown(Player player) {
        return playersCooldownStatus.get(player.getUniqueId());
    }

    // Creates a player's cooldown (s).
    // Set the timer to 0 before running this more than once.
    public void createCooldown(Player player, int seconds) {
        if (seconds < 1) {
            return;
        }
        // Remove any previous cooldowns
        endCooldown(player);
        // Set the player's cooldown and mark them as on one
        playersCooldownDuration.put(player.getUniqueId(), seconds);
        playersCooldownStatus.put(player.getUniqueId(), true);
        // Start the counter schedule
        AtomicInteger x = new AtomicInteger(seconds);
        AtomicInteger cId = new AtomicInteger(); cId.set(Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("theboysplugin"), () -> {
            if (x.get() == seconds) {
                playersCooldownShedule.put(player.getUniqueId(), cId.get());
            } else if (x.get() == 0) {
                endCooldown(player);
            } else {
                x.decrementAndGet();
                playersCooldownDuration.put(player.getUniqueId(), x.get());
            }
        }, 20L, 20L).getTaskId());
    }

    // Sends the current cooldown counter task to a halt.
    public void endCooldown(Player player) {
        // Kill the schedule
        if (playersCooldownShedule.get(player.getUniqueId()) == null) { return; }
        Bukkit.getScheduler().cancelTask(playersCooldownShedule.get(player.getUniqueId()));
        // Reset variables
        playersCooldownDuration.put(player.getUniqueId(), 0);
        playersCooldownStatus.put(player.getUniqueId(), false);
        playersCooldownShedule.put(player.getUniqueId(), null);
    }

    // Returns true if the ability is toggled for the player.
    public boolean isToggled(Player player) {
        return playersToggleStatus.get(player.getUniqueId());
    }

    // Sets the player's toggled status to the boolean provided.
    public void setToggled(Player player, boolean state) {
        playersToggleStatus.put(player.getUniqueId(), state);
    }

    // A shortcut for creating and adding an infinite potion effect to a player.
    public static void createInfiniteEffect(Player player, PotionEffectType effectType, int amp) {
        player.addPotionEffect(new PotionEffect(effectType, 1000000, amp, false, false, false));
    }

    // A shortcut for removing an infinite effect.
    public static void removeInfiniteEffect(Player player, PotionEffectType effectType) {
        PotionEffect curr = player.getPotionEffect(effectType);
        if (curr != null) {
            if (!curr.hasParticles() || curr.getDuration() == 1000000) {
                player.removePotionEffect(effectType);
            }
        }
    }

    // A shortcut for creating particles on or around a player.
    public static void createParticles(Player player, Particle particle, int count) {
        player.spawnParticle(particle, player.getLocation(), count);
    }

    // A shortcut for playing a global sound on a player.
    public static void globalSound(Player player, Sound sound, float vol, float pit) {
        player.getWorld().playSound(player, sound, vol, pit);
    }

    // Does this ability toggle off on player leave?
    public boolean togglesOffWhenLeave() {
        return true;
    }

    // Does this ability toggle off on player join?
    public boolean togglesOffWhenJoin() {
        return true;
    }

    // Does this ability toggle off on respawn?
    public boolean togglesOffWhenRespawn() {
        return true;
    }

    // Does the player regain buffs on respawn?
    public boolean doesRegainWhenRespawn() {
        return true;
    }

    // Does this player regain their effects every few ticks?
    public boolean doesGainRefresh() {
        return true;
    }

    // Interact events

    // Called when the player interacts at all.
    public void onInteract(PlayerInteractEvent event) {}

    // Called when the player left clicks.
    public void onPunch(PlayerInteractEvent event) {}

    // Called when the player right clicks.
    public void onTap(PlayerInteractEvent event) {}

    // Movement events

    // Called when the player moves at all.
    public void onMove(PlayerMoveEvent event) {}

    // Called when the player jumps.
    public void onJump(PlayerMoveEvent event) {}

    // Called when the player toggles sprint.
    public void onToggleSprint(PlayerToggleSprintEvent event) {}

    // Called when the player toggles flight.
    public void onToggleFlight(PlayerToggleFlightEvent event) {}

    // Other events

    // Called when a player mounts an entity.
    public void onMount(EntityMountEvent event) {}

    // Called when the player is mounted.
    public void onMounted(EntityMountEvent event) {}

    // The inverse of onMount.
    public void onDismount(EntityDismountEvent event) {}

    // The inverse of onMounted.
    public void onDismounted(EntityDismountEvent event) {}
}
