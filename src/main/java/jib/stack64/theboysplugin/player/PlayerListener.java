package jib.stack64.theboysplugin.player;

import jib.stack64.theboysplugin.ability.Ability;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Contains event bindings to the many abilities.
public class PlayerListener implements Listener {

    // Handles interact events.
    // Activate ability code contained here.
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player pl = event.getPlayer();
        Ability ab = PlayerAbilityManager.getAbility(pl);
        if (ab != null) {
            ab.onInteract(event);
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ab.onTap(event);
            } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                ab.onPunch(event);
                // Activate ability
                if (!pl.getLocation().add(0, -1, 0).getBlock().getType().isSolid() && pl.getInventory().getItemInMainHand().getType() == Material.AIR && pl.isSneaking()) {
                    ab.toggle(pl);
                }
            }
        }
    }

    // Handles movement events.
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player pl = event.getPlayer();
        Ability ab = PlayerAbilityManager.getAbility(pl);
        if (ab != null) {
            ab.onMove(event);
            if (event.getFrom().getY() + 1 == event.getTo().getY() || event.getFrom().getY() + 2 == event.getTo().getY()) { //Checking if the player is going up 1 or 2 blocks
                ab.onJump(event);
            }
        }
    }

    // Sprint events
    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        Player pl = event.getPlayer();
        Ability ab = PlayerAbilityManager.getAbility(pl);
        if (ab != null) {
            ab.onToggleSprint(event);
        }
    }

    // Flight events
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player pl = event.getPlayer();
        Ability ab = PlayerAbilityManager.getAbility(pl);
        if (ab != null) {
            ab.onToggleFlight(event);
        }
    }

    // Handles mounting events.
    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        Entity en1 = event.getEntity();
        Entity en2 = event.getMount();
        if (en1.getType() == EntityType.PLAYER) {
            Player pl = (Player)en1;
            Ability ab = PlayerAbilityManager.getAbility(pl);
            if (ab != null) {
                ab.onMount(event);
            }
        }
        if (en2.getType() == EntityType.PLAYER) {
            Player pl = (Player)en2;
            Ability ab = PlayerAbilityManager.getAbility(pl);
            if (ab != null) {
                ab.onMounted(event);
            }
        }
    }

    // Handles dismounting events.
    @EventHandler
    public void onEntityUnmount(EntityDismountEvent event) {
        Entity en1 = event.getEntity();
        Entity en2 = event.getDismounted();
        if (en1.getType() == EntityType.PLAYER) {
            Player pl = (Player)en1;
            Ability ab = PlayerAbilityManager.getAbility(pl);
            if (ab != null) {
                ab.onDismount(event);
            }
        }
        if (en2.getType() == EntityType.PLAYER) {
            Player pl = (Player)en2;
            Ability ab = PlayerAbilityManager.getAbility(pl);
            if (ab != null) {
                ab.onDismounted(event);
            }
        }
    }

    // Player gain refresh runners
    Map<UUID, Integer> gainRefreshTasks = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player pl = event.getPlayer();
        Ability ab = PlayerAbilityManager.getAbility(pl);
        if (ab != null) {
            if (ab.doesRegainWhenRespawn()) {
                ab.gain(pl);
            }
            if (ab.togglesOffWhenJoin()) {
                if (ab.isToggled(pl)) {
                    Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("theboysplugin"),
                            () -> {ab.toggle(pl);}, 6L);
                }
            }
        }
        gainRefreshTasks.put(pl.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("theboysplugin"),
                () -> {
                    Ability abb = PlayerAbilityManager.getAbility(pl);
                    if (abb != null) {
                        if (abb.doesGainRefresh()) {
                            abb.gain(pl);
                        }
                    }
                }, 5L, 20L).getTaskId());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player pl = event.getPlayer();
        Ability ab = PlayerAbilityManager.getAbility(pl);
        gainRefreshTasks.remove(pl.getUniqueId());
        if (ab != null) {
            if (ab.togglesOffWhenLeave()) {
                ab.toggle(pl);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player pl = event.getPlayer();
        Ability ab = PlayerAbilityManager.getAbility(pl);
        if (ab != null) {
            if (ab.togglesOffWhenRespawn()) {
                Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("theboysplugin"),
                        () -> {ab.toggle(pl);}, 5L);
            }
            if (ab.doesRegainWhenRespawn()) {
                Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("theboysplugin"),
                        () -> {ab.gain(pl);}, 5L);
            }
        }
    }

}
