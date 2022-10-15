package jib.stack64.theboysplugin.task;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class TaskManager {
    public static final Plugin plugin = Bukkit.getPluginManager().getPlugin("theboysplugin");
    public static final BukkitScheduler scheduler = Bukkit.getScheduler();

    // Starts a repeating task; runs every delay seconds, waits period seconds before first execution.
    // Returns the resulting task's identifier.
    public static int repeatingTask(Runnable runnable, float delay, float period) {
        AtomicInteger taskId = new AtomicInteger();
        taskId.set(scheduler.runTaskTimer(plugin, runnable, (long)(delay*20), (long)(period*20)).getTaskId());
        return taskId.get();
    }

    // Waits delay seconds before executing the given runnable.
    public static void delayedTask(Runnable runnable, float delay) {
        scheduler.runTaskLater(plugin, runnable, (long)(delay*20));
    }

    // Halts a task.
    public static void haltTask(int id) {
        scheduler.cancelTask(id);
    }
}
