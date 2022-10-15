package jib.stack64.theboysplugin.ability;

import jib.stack64.theboysplugin.task.TaskManager;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// NON-PLAYABLE.
// Used by other abilities to implement flying.
// Matches methods in normal abilities for ease of use
public class FlightAbility {
    public static final float defaultFlySpeed = 0.5F;

    public static void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        // Change speed of flight over time, eventually stopping.
        if (player.isFlying()) {
            Ability.createParticles(player, Particle.CLOUD, 1);
            player.setFlySpeed(player.getFlySpeed()*0.996F);
            if (player.getFlySpeed() < 0.075) {
                player.setAllowFlight(false);
                TaskManager.delayedTask(() -> {
                    gain(player);
                }, 5);
            }
        }
    }

    public static void gain(Player player) {
        player.setAllowFlight(true);
        player.setFlySpeed(defaultFlySpeed);
    }

    public static void lose(Player player) {
        player.setAllowFlight(false);
    }

    public static boolean doesGainRefresh() {
        return false;
    }
}
