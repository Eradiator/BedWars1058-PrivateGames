package me.notlewx.privategames.arena;

import com.andrei1058.bedwars.api.arena.IArena;
import me.notlewx.privategames.PrivateGames;
import me.notlewx.privategames.api.arena.IPrivateArena;
import me.notlewx.privategames.api.events.PrivateGameJoinEvent;
import me.notlewx.privategames.api.player.IPrivatePlayer;
import me.notlewx.privategames.support.Support;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import static me.notlewx.privategames.PrivateGames.support;

public class PrivateArena implements IPrivateArena {
    private IPrivatePlayer host;
    private List<OfflinePlayer> players;
    private String arenaName;
    private String defaultGroup;
    public static final LinkedHashMap<String, IPrivateArena> privateArenaByIdentifier = new LinkedHashMap<>();
    public static final LinkedHashMap<OfflinePlayer, IPrivateArena> privateArenaByPlayer = new LinkedHashMap<>();
    public static final LinkedList<IPrivateArena> privateArenas = new LinkedList<>();

    public PrivateArena(IPrivatePlayer host, List<OfflinePlayer> players, String arenaIdentifier, String defaultGroup) {
        this.host = host;
        this.players = players;
        this.arenaName = arenaIdentifier;
        this.defaultGroup = defaultGroup;

        privateArenaByIdentifier.put(arenaIdentifier, this);
        privateArenaByPlayer.put(host.getPlayer(), this);
        for (OfflinePlayer p : players) {
            privateArenaByPlayer.put(p, this);
        }
        privateArenas.add(this);

        switch (support) {
            case BEDWARS1058:
                PrivateGames.getBw1058Api().getArenaUtil().getArenas().remove(PrivateGames.getBw1058Api().getArenaUtil().getArenaByName(arenaIdentifier));
                break;
            case BEDWARS2023:
                PrivateGames.getBw2023Api().getArenaUtil().getArenas().remove(PrivateGames.getBw2023Api().getArenaUtil().getArenaByName(arenaIdentifier));
                break;
        }

    }
    @Override
    public IPrivatePlayer getPrivateArenaHost() {
        return host;
    }

    @Override
    public List<OfflinePlayer> getPlayers() {
        return players;
    }

    @Override
    public String getArenaIdentifier() {
        return arenaName;
    }
    @Override
    public String getDefaultGroup() {
        return defaultGroup;
    }
    @Override
    public void addPlayer(Player p, boolean callEvent) {
        if (players.contains(p)) return;
        if (isFull()) return;

        if (callEvent) {
            PrivateGameJoinEvent event = new PrivateGameJoinEvent(p, this);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) return;
        }

        this.players.add(p);
        privateArenaByPlayer.put(p, this);
    }

    @Override
    public void removePlayer(Player p) {
        if (!players.contains(p)) return;

        this.players.remove(p);
        privateArenaByPlayer.remove(p);
    }

    @Override
    public void stopGame() {
        if (support == Support.BEDWARS1058) {
            for (Player player : PrivateGames.getBw1058Api().getArenaUtil().getArenaByName(arenaName).getPlayers()) {
                PrivateGames.getBw1058Api().getArenaUtil().getArenaByName(arenaName).abandonGame(player);
                PrivateGames.getBw1058Api().getArenaUtil().getArenaByName(arenaName).setGroup(defaultGroup);
            }
        } else if (support == Support.BEDWARS2023) {
            for (Player player : PrivateGames.getBw2023Api().getArenaUtil().getArenaByName(arenaName).getPlayers()) {
                PrivateGames.getBw2023Api().getArenaUtil().getArenaByName(arenaName).abandonGame(player);
                PrivateGames.getBw2023Api().getArenaUtil().getArenaByName(arenaName).setGroup(defaultGroup);
            }
        }
        privateArenaByIdentifier.remove(arenaName);
        for (OfflinePlayer p : players) {
            privateArenaByPlayer.remove(p);
        }
        privateArenas.remove(this);
    }

    @Override
    public boolean isFull() {
        if (support == Support.BEDWARS1058) {
            IArena arena = PrivateGames.getBw1058Api().getArenaUtil().getArenaByName(arenaName);
            return arena.getPlayers().size() == arena.getMaxPlayers();
        } else if (support == Support.BEDWARS2023) {
            com.tomkeuper.bedwars.api.arena.IArena arena = PrivateGames.getBw2023Api().getArenaUtil().getArenaByName(arenaName);
            return arena.getPlayers().size() == arena.getMaxPlayers();
        }
        return false;
    }

    @Override
    public void destroyData() {
        privateArenaByIdentifier.remove(arenaName);
        for (OfflinePlayer p : players) {
            privateArenaByPlayer.remove(p);
        }
        privateArenas.remove(this);

        defaultGroup = null;
        host = null;
        players = null;
        arenaName = null;
    }
}
