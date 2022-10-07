package jib.stack64.theboysplugin.ability;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ATrainAbility extends Ability {
    public ATrainAbility() {
        super(
                "atrain",
                "A-Train",
                ChatColor.BLUE + "You feel a deep connection to your African roots."
        );
    }

    @Override
    public void toggle(Player player) {
        super.toggle(player);
        if (isToggled(player)) {
            // Do nothing (onToggleSprint handles) but play sound
            globalSound(player, Sound.ENTITY_ALLAY_ITEM_GIVEN, 0.5F, 1.5F);
        } else {
            // Remove players effects
            globalSound(player, Sound.ENTITY_ALLAY_ITEM_TAKEN, 0.5F, 1.5F);
            if (player.isSprinting()) {
                globalSound(player, Sound.ITEM_TRIDENT_RETURN, 1.5F, 0.25F);
                removeInfiniteEffect(player, PotionEffectType.SPEED);
                removeInfiniteEffect(player, PotionEffectType.JUMP);
            }
        }
    }

    @Override
    public void onMove(PlayerMoveEvent event) {
        Player pl = event.getPlayer();
        // Goofy particles
        if (pl.isSprinting() && isToggled(pl)) {
            createParticles(pl, Particle.CLOUD, 2);
        }
    }

    @Override
    public void onToggleSprint(PlayerToggleSprintEvent event) {
        Player pl = event.getPlayer();
        if (isToggled(pl)) {
            if (event.isSprinting()) {
                // Create noise on player and give effects
                globalSound(pl, Sound.ITEM_TRIDENT_RIPTIDE_3, 1.5F, 0.25F);
                createInfiniteEffect(pl, PotionEffectType.SPEED, 15);
                createInfiniteEffect(pl, PotionEffectType.JUMP, 1);
            } else {
                // Remove players effects
                globalSound(pl, Sound.ITEM_TRIDENT_RETURN, 1.5F, 0.25F);
                removeInfiniteEffect(pl, PotionEffectType.SPEED);
                removeInfiniteEffect(pl, PotionEffectType.JUMP);
            }
        }

    }

    @Override
    public void gain(Player player) {
        super.gain(player);
        createInfiniteEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 1);
        createInfiniteEffect(player, PotionEffectType.INCREASE_DAMAGE, 0);
    }

    @Override
    public void lose(Player player) {
        super.lose(player);
        removeInfiniteEffect(player, PotionEffectType.DAMAGE_RESISTANCE);
        removeInfiniteEffect(player, PotionEffectType.INCREASE_DAMAGE);
    }
}
