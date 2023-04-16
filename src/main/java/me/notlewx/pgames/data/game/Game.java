package me.notlewx.pgames.data.game;

import me.notlewx.pgames.api.PGamesAPI;
import me.notlewx.pgames.api.interfaces.IGame;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game implements IGame {
    private final HashMap<String, Boolean> privArena = new HashMap<>();
    private final HashMap<String, Player> arenaOwner = new HashMap<>();
    private final List<String> arenasInPrivMode = new ArrayList<>();
    @Override
    public boolean isArenaPrivate(String arena) {
        if (privArena.get(arena) == null) return false;
        return privArena.get(arena);
    }

    @Override
    public void setArenaPrivate(String arena, boolean value) {
        privArena.put(arena, value);
        if (value) {
            PGamesAPI.getBwApi().getArenaUtil().getArenas().remove(PGamesAPI.getBwApi().getArenaUtil().getArenaByName(arena));
            arenasInPrivMode.add(arena);
        } else {
            PGamesAPI.getBwApi().getArenaUtil().getArenas().add(PGamesAPI.getBwApi().getArenaUtil().getArenaByName(arena));
            arenasInPrivMode.remove(arena);
        }
    }

    @Override
    public void setPrivateArenaOwner(String arena, Player player) {
        arenaOwner.put(arena, player);
    }

    @Override
    public Player getOwnerOfPrivateArena(String arena) {
        return arenaOwner.get(arena);
    }

    @Override
    public boolean isOwnerOfArena(Player player, String arena) {
        return arenaOwner.get(arena) == player;
    }

    @Override
    public List<String> getPrivateGames() {
        return arenasInPrivMode;
    }
}
