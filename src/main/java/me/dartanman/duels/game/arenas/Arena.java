package me.dartanman.duels.game.arenas;

import me.dartanman.duels.*;
import me.dartanman.duels.game.Countdown;
import me.dartanman.duels.game.Game;
import me.dartanman.duels.game.GameState;
import me.dartanman.duels.game.kits.KitManager;
import me.dartanman.duels.stats.db.StatisticsDatabase;
import me.dartanman.duels.utils.PlayerRestoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Arena
{

    private final Duels plugin;

    private final int id;
    private final String name;

    private GameState gameState;

    private final List<UUID> players;
    private final Location spawnOne;
    private final Location spawnTwo;
    private final Location lobby;
    private Game game;
    private Countdown countdown;
    private int countdownSeconds;

    public Arena(Duels plugin, int id, String name, Location spawnOne, Location spawnTwo, Location lobby, int countdownSeconds)
    {
        this.plugin = plugin;

        this.id = id;
        this.name = name;

        this.gameState = GameState.IDLE;

        this.spawnOne = spawnOne;
        this.spawnTwo = spawnTwo;
        this.lobby = lobby;
        this.players = new ArrayList<>();
        this.game = new Game(this);
        this.countdownSeconds = countdownSeconds;
        this.countdown = new Countdown(plugin, this, countdownSeconds);
    }

    public Arena(Duels plugin, ArenaConfig arenaConfig)
    {
        this.plugin = plugin;

        this.id = arenaConfig.getId();
        this.name = arenaConfig.getName();

        this.gameState = GameState.IDLE;

        this.spawnOne = arenaConfig.getSpawnOne();
        this.spawnTwo = arenaConfig.getSpawnTwo();
        this.lobby = arenaConfig.getLobby();
        this.players = new ArrayList<>();
        this.game = new Game(this);
        this.countdownSeconds = arenaConfig.getCountdownSeconds();
        this.countdown = new Countdown(plugin, this, arenaConfig.getCountdownSeconds());
    }

    /*
     * Gameplay
     */

    public void reset()
    {
        this.game = new Game(this);
        this.countdown = new Countdown(plugin, this, countdownSeconds);
        Player p1 = Bukkit.getPlayer(getPlayerOne());
        Player p2 = Bukkit.getPlayer(getPlayerTwo());
        if(p1 != null)
        {
            PlayerRestoration.restorePlayer(p1, false);
        }
        if(p2 != null)
        {
            PlayerRestoration.restorePlayer(p2, false);
        }

        players.clear();
        gameState = GameState.IDLE;

    }

    public void start()
    {
        gameState = GameState.COUNTDOWN;
        countdown.start();
    }

    /*
     * Arena Utilities
     */

    public void sendMessage(String message)
    {
        for(UUID uuid : players)
        {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null)
            {
                player.sendMessage(message);
            }
        }
    }

    public KitManager getKitManager()
    {
        return plugin.getKitManager();
    }

    public StatisticsDatabase getStatisticsDatabase()
    {
        return plugin.getStatisticsManager().getStatsDB();
    }

    /*
     * Players
     */

    public void addPlayer(Player player)
    {
        PlayerRestoration.savePlayer(player);
        players.add(player.getUniqueId());
        player.teleport(lobby);

        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);

        if(players.size() == 2)
        {
            start();
        }
    }

    public void removePlayer(Player player)
    {
        players.remove(player.getUniqueId());
        if(gameState == GameState.COUNTDOWN)
        {
            countdown.cancel();
            sendMessage(ChatColor.translateAlternateColorCodes('&',
                    Objects.requireNonNull(plugin.getConfig().getString("Messages.Player-Left-Cancelled"))));
            gameState = GameState.IDLE;
        }
        this.countdown = new Countdown(plugin, this, countdownSeconds);
    }

    public List<UUID> getPlayers()
    {
        return players;
    }

    public Location getSpawnOne()
    {
        return spawnOne;
    }

    public Location getSpawnTwo()
    {
        return spawnTwo;
    }

    public Location getLobby()
    {
        return lobby;
    }

    public UUID getPlayerOne()
    {
        return players.get(0);
    }

    public UUID getPlayerTwo()
    {
        return players.get(1);
    }

    /*
     * Arena Info
     */

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public Game getGame()
    {
        return game;
    }

    public GameState getGameState()
    {
        return gameState;
    }

    public void setGameState(GameState gameState)
    {
        this.gameState = gameState;
    }
}
