package me.notlewx.privategames.listeners.bedwars1058;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.events.player.PlayerJoinArenaEvent;
import me.notlewx.privategames.PrivateGames;
import me.notlewx.privategames.api.party.IParty;
import me.notlewx.privategames.api.player.IPlayerSettings;
import me.notlewx.privategames.api.player.IPrivatePlayer;
import me.notlewx.privategames.arena.PrivateArena;
import me.notlewx.privategames.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;
import static me.notlewx.privategames.PrivateGames.mainConfig;
import static me.notlewx.privategames.config.bedwars1058.MainConfig.MATERIAL;
import static me.notlewx.privategames.config.bedwars1058.MainConfig.POSITION;
import static me.notlewx.privategames.config.bedwars1058.MessagesData.PRIVATE_GAME_MENU_ITEM_LORE;
import static me.notlewx.privategames.config.bedwars1058.MessagesData.PRIVATE_GAME_MENU_ITEM_NAME;

public class ArenaJoin implements Listener {

    @EventHandler
    public void onArenaJoin(PlayerJoinArenaEvent e) {
        IPrivatePlayer pp = PrivateGames.api.getPrivatePlayer(e.getPlayer());
        IPlayerSettings p = pp.getPlayerSettings();
        IParty party = pp.getPlayerParty();

        ItemStack settings = new ItemStack(Material.valueOf(mainConfig.getString(MATERIAL)));
        ItemMeta settingsMeta = settings.getItemMeta();
        settingsMeta.setDisplayName(Utility.getMsg(pp.getPlayer(), PRIVATE_GAME_MENU_ITEM_NAME));
        settingsMeta.setLore(Utility.getList(pp.getPlayer(), PRIVATE_GAME_MENU_ITEM_LORE));
        settings.setItemMeta(settingsMeta);

        if (e.getArena().isSpectator(pp.getPlayer())) return;
        if (!e.getArena().getPlayers().isEmpty()) return;
        if (PrivateGames.api.getPrivateArenaUtil().isArenaPrivate(e.getArena().getArenaName()) && !party.hasMember(e.getPlayer())) e.setCancelled(true);

        if (party.hasParty() && party.isOwner()) {
            if (pp.hasPermission()) {
                if (p.isPrivateGameEnabled()) {
                    Bukkit.getScheduler().runTaskLater(PrivateGames.getPlugins(), () -> {
                        new PrivateArena(pp, party.getPartyMembers(), e.getArena().getArenaName());
                        for (Player member : party.getPartyMembers()) {
                            if (member != pp.getPlayer()) e.getArena().addPlayer(member, false);
                        }
                        pp.getPlayer().getInventory().setItem(mainConfig.getInt(POSITION), settings);
                    }, 35L);
                    e.getArena().setStatus(GameState.starting);
                    e.getArena().getStartingTask().setCountdown(PrivateGames.bw1058config.getInt("countdowns.game-start-regular"));
                }
            }
        } else if (pp.getPlayer().isOp() || pp.getPlayer().hasPermission("pg.admin")) {
                Bukkit.getScheduler().runTaskLater(PrivateGames.getPlugins(), () -> {
                    List<Player> players = new ArrayList<>();
                    players.add(pp.getPlayer());
                    new PrivateArena(pp, players, e.getArena().getArenaName());
                    pp.getPlayer().getInventory().setItem(mainConfig.getInt(POSITION), settings);
                }, 35L);
                if (e.getArena().getStatus() == GameState.playing || e.getArena().getStatus() == GameState.restarting) return;
                e.getArena().changeStatus(GameState.starting);
                e.getArena().getStartingTask().setCountdown(PrivateGames.bw1058config.getInt("countdowns.game-start-regular"));
        }
    }
}
