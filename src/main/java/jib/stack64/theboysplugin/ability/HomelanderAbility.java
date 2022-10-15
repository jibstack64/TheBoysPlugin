package jib.stack64.theboysplugin.ability;

import jib.stack64.theboysplugin.task.TaskManager;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffectType;

public class HomelanderAbility extends Ability {
    public HomelanderAbility() {
        super(
                "homelander",
                "Homelander",
                ChatColor.GOLD + "You feel the squirming need for the taste of breast milk."
        );
    }

    @Override
    public void toggle(Player player) {
        super.toggle(player);
        if (isToggled(player)) {
            globalSound(player, Sound.BLOCK_BEACON_ACTIVATE, 0.5F, 1.5F);
            LaserAbility.toggle(player, true);
            // If the player hasn't deactivated ability themselves, do it for them.
            TaskManager.delayedTask(() -> {
                if (isToggled(player)) {
                    toggle(player);
                }
            }, 5);
            createCooldown(player, 10);
        } else {
            globalSound(player, Sound.BLOCK_BEACON_DEACTIVATE, 0.5F, 1.5F);
            LaserAbility.toggle(player, false);
        }
    }

    @Override
    public void onMove(PlayerMoveEvent event) {
        super.onMove(event);
        FlightAbility.onMove(event);
    }

    @Override
    public void gain(Player player) {
        super.gain(player);
        FlightAbility.gain(player);
        createInfiniteEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 2);
        createInfiniteEffect(player, PotionEffectType.INCREASE_DAMAGE, 1);
    }

    @Override
    public void lose(Player player) {
        super.lose(player);
        FlightAbility.lose(player);
        removeInfiniteEffect(player, PotionEffectType.DAMAGE_RESISTANCE);
        removeInfiniteEffect(player, PotionEffectType.INCREASE_DAMAGE);
    }

    @Override
    public boolean doesGainRefresh() {
        return FlightAbility.doesGainRefresh();
    }
}
