package me.dartanman.duels.stats.db.impl;

import me.dartanman.duels.stats.StatisticsManager;
import me.dartanman.duels.stats.db.StatisticsDatabase;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class StatisticsDatabaseYAML implements StatisticsDatabase
{

    private final StatisticsManager manager;

    public StatisticsDatabaseYAML(StatisticsManager manager)
    {
        this.manager = manager;
    }

    @Override
    public int getWins(UUID uuid)
    {
        return manager.getStatsConfig().getInt("Statistics." + uuid.toString() + ".Wins");
    }

    @Override
    public int getLosses(UUID uuid)
    {
        return manager.getStatsConfig().getInt("Statistics." + uuid.toString() + ".Losses");
    }

    @Override
    public int getKills(UUID uuid)
    {
        return manager.getStatsConfig().getInt("Statistics." + uuid.toString() + ".Kills");
    }

    @Override
    public int getDeaths(UUID uuid)
    {
        return manager.getStatsConfig().getInt("Statistics." + uuid.toString() + ".Deaths");
    }

    @Override
    public void setWins(UUID uuid, int wins)
    {
        manager.getStatsConfig().set("Statistics." + uuid.toString() + ".Wins", wins);
        manager.saveStatsConfig();
    }

    @Override
    public void setLosses(UUID uuid, int losses)
    {
        manager.getStatsConfig().set("Statistics." + uuid.toString() + ".Losses", losses);
        manager.saveStatsConfig();
    }

    @Override
    public void setKills(UUID uuid, int kills)
    {
        manager.getStatsConfig().set("Statistics." + uuid.toString() + ".Kills", kills);
        manager.saveStatsConfig();
    }

    @Override
    public void setDeaths(UUID uuid, int deaths)
    {
        manager.getStatsConfig().set("Statistics." + uuid.toString() + ".Deaths", deaths);
        manager.saveStatsConfig();
    }

    @Override
    public void registerNewPlayer(UUID uuid, String name)
    {
        manager.getStatsConfig().set("Statistics." + uuid.toString() + ".Last-Known-Name", name);
        manager.saveStatsConfig();
        setWins(uuid, 0);
        setLosses(uuid, 0);
        setKills(uuid, 0);
        setDeaths(uuid, 0);
    }

    @Override
    public boolean isRegistered(UUID uuid)
    {
        return manager.getStatsConfig().contains("Statistics." + uuid.toString());
    }

    @Override
    public UUID getUUID(String playerName)
    {
        for(String uuidStr : Objects.requireNonNull(manager.getStatsConfig().getConfigurationSection("Statistics")).getKeys(false))
        {
            if(Objects.requireNonNull(manager.getStatsConfig().getString("Statistics." + uuidStr + ".Last-Known-Name")).equalsIgnoreCase(playerName))
            {
                return UUID.fromString(uuidStr);
            }
        }
        return null;
    }

    @Override
    public String getLastKnownName(UUID uuid)
    {
        for(String uuidStr : Objects.requireNonNull(manager.getStatsConfig().getConfigurationSection("Statistics")).getKeys(false))
        {
            if(uuidStr.equals(uuid.toString()))
            {
                return manager.getStatsConfig().getString("Statistics." + uuidStr + ".Last-Known-Name");
            }
        }
        return null;
    }

    @Override
    public HashMap<UUID, Integer> getTopTenWins()
    {
        HashMap<UUID, Integer> allWins = new HashMap<>();
        HashMap<UUID, Integer> topTenWins = new HashMap<>();

        for(String uuidStr : Objects.requireNonNull(manager.getStatsConfig().getConfigurationSection("Statistics")).getKeys(false))
        {
            UUID uuid = UUID.fromString(uuidStr);
            allWins.put(uuid, getWins(uuid));
        }

        int topAmt = Math.min(10, allWins.size());
        for(int i = 0; i < topAmt; i++)
        {
            UUID maxUUID = null;
            int maxWins = -1;
            for(UUID uuid : allWins.keySet())
            {
                int curWins = allWins.get(uuid);
                if(curWins > maxWins)
                {
                    maxUUID = uuid;
                    maxWins = curWins;
                }
            }
            topTenWins.put(maxUUID, maxWins);
            allWins.remove(maxUUID);
        }
        return topTenWins;
    }

    @Override
    public HashMap<UUID, Integer> getTopTenKills()
    {
        HashMap<UUID, Integer> allKills = new HashMap<>();
        HashMap<UUID, Integer> topTenKills = new HashMap<>();

        for(String uuidStr : Objects.requireNonNull(manager.getStatsConfig().getConfigurationSection("Statistics")).getKeys(false))
        {
            UUID uuid = UUID.fromString(uuidStr);
            allKills.put(uuid, getKills(uuid));
        }

        int topAmt = Math.min(10, allKills.size());
        for(int i = 0; i < topAmt; i++)
        {
            UUID maxUUID = null;
            int maxKills = -1;
            for(UUID uuid : allKills.keySet())
            {
                int curKills = allKills.get(uuid);
                if(curKills > maxKills)
                {
                    maxUUID = uuid;
                    maxKills = curKills;
                }
            }
            topTenKills.put(maxUUID, maxKills);
            allKills.remove(maxUUID);
        }
        return topTenKills;
    }
}
