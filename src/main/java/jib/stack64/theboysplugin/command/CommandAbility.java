package jib.stack64.theboysplugin.command;

import jib.stack64.theboysplugin.ability.Ability;
import jib.stack64.theboysplugin.ability.AbilityType;
import jib.stack64.theboysplugin.player.PlayerAbilityManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAbility implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player pl = (Player)sender;
            if (args.length > 0) {
                Ability ab = AbilityType.getByIdentifier(args[0]);
                if (ab != null) {
                    PlayerAbilityManager.setAbility(ab, pl);
                }
                return true;
            }
        }
        return false;
    }
}
