package jib.stack64.theboysplugin.ability;

import jib.stack64.theboysplugin.task.TaskManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

// Create a cooldown for toggle, otherwise players can spam
// ^ causes a LOT of lag
public class LaserAbility {
    static Map<UUID, Integer> lastPlayerTasks = new HashMap<>();
    public static void toggle(Player player, boolean v, Color col) {
        if (v) {
            AtomicInteger taskId = new AtomicInteger();
            AtomicInteger counter = new AtomicInteger(0);
            World world = player.getWorld();
            Particle.DustOptions dustOptions = new Particle.DustOptions(col, 1F);
            taskId.set(TaskManager.repeatingTask(() -> {
                if (counter.get() == 20) {
                    return;
                }

                // Fires a laser 6 blocks in the direction that the player is looking
                // 6 blocks because any longer than this can cause severe lag and crash the server
                Location originLoc = player.getLocation().add(0, 1.5, 0);
                Vector direction = originLoc.getDirection();
                int range = 6; // Number of blocks in front of the player
                for (int i = 0; i < range; i++) {
                    originLoc.add(direction);
                    world.spawnParticle(Particle.REDSTONE, originLoc, 2, dustOptions);
                    Block block = originLoc.getBlock();
                    Material mat = block.getType();
                    Collection<Entity> entities = world.getNearbyEntities(originLoc, 1.5, 1.5, 1.5);
                    if (!block.isPassable() && mat != Material.BEDROCK && !mat.isTransparent()) {
                        world.playSound(originLoc, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 1F, 1.5F);
                        block.breakNaturally();
                    }
                    for (int ii = 0; ii < entities.size(); ii++) {
                        Entity ent = (Entity) entities.toArray()[ii];
                        if (!ent.isDead()) {
                            ent = (LivingEntity)ent;
                            ((LivingEntity) ent).damage(6, player);
                        }
                    }
                }

                counter.getAndAdd(1);
            }, 0.25F, 0F));
            lastPlayerTasks.put(player.getUniqueId(), taskId.get());
        } else {
            if (lastPlayerTasks.containsKey(player.getUniqueId())) {
                TaskManager.haltTask(lastPlayerTasks.get(player.getUniqueId()));
                lastPlayerTasks.remove(player.getUniqueId());
            }
        }
    }

    public static void toggle(Player player, boolean v) {
        toggle(player, v, Color.RED);
    }
}
