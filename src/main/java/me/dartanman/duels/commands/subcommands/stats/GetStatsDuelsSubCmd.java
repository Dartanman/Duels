package me.dartanman.duels.commands.subcommands.stats;

import me.dartanman.duels.Duels;
import me.dartanman.duels.commands.subcommands.DuelsSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GetStatsDuelsSubCmd extends DuelsSubCommand
{
    public GetStatsDuelsSubCmd(Duels plugin)
    {
        super(plugin, "stats");
    }

    private void sendStatsCard(CommandSender sender, UUID target, String name)
    {
        for(String line : plugin.getConfig().getStringList("Messages.Stats-Card"))
        {
            line = ChatColor.translateAlternateColorCodes('&', line
                    .replace("<wins>", plugin.getStatisticsManager().getStatsDB().getWins(target) + "")
                    .replace("<losses>", plugin.getStatisticsManager().getStatsDB().getLosses(target) + "")
                    .replace("<kills>", plugin.getStatisticsManager().getStatsDB().getKills(target) + "")
                    .replace("<deaths>", plugin.getStatisticsManager().getStatsDB().getDeaths(target) + "")
                    .replace("<name>", name));
            sender.sendMessage(line);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        if(!sender.hasPermission("duels.stats"))
        {
            noPerm(sender);
            return true;
        }
        if(args.length == 0)
        {
            if(sender instanceof Player player)
            {
                sendStatsCard(sender, player.getUniqueId(), player.getName());
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You must be a Player to do that!");
                return false;
            }
        }
        else if (args.length == 1)
        {
            String targetName = args[0];
            Player target = Bukkit.getPlayer(targetName);
            if(target != null)
            {
                UUID uuid = target.getUniqueId();
                sendStatsCard(sender, uuid, target.getName());
                return true;
            }
            else
            {
                UUID uuid = plugin.getStatisticsManager().getStatsDB().getUUID(targetName);
                if(uuid != null)
                {
                    sendStatsCard(sender, uuid, targetName);
                    return true;
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Cannot find statistics for " + ChatColor.YELLOW + targetName);
                    return true;
                }
            }
        }
        else
        {
            incorrectArgs(sender, "/duels stats [player]");
            return true;
        }
    }
}
