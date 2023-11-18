package me.notlewx.privategames.listeners.bedwars2023;

import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.configuration.ConfigManager;
import com.tomkeuper.bedwars.api.events.gameplay.GeneratorUpgradeEvent;
import com.tomkeuper.bedwars.arena.OreGenerator;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.generator.GeneratorType;
import com.tomkeuper.bedwars.api.arena.generator.IGenerator;
import com.tomkeuper.bedwars.api.events.gameplay.GameEndEvent;
import com.tomkeuper.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.tomkeuper.bedwars.api.events.gameplay.NextEventChangeEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerKillEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerLeaveArenaEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerReSpawnEvent;
import com.tomkeuper.bedwars.arena.Arena;
import me.notlewx.privategames.api.arena.IPrivateArena;
import me.notlewx.privategames.api.player.IPrivatePlayer;
import me.notlewx.privategames.utils.GeneratorProperties;
import me.notlewx.privategames.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static me.notlewx.privategames.PrivateGames.api;
import static me.notlewx.privategames.config.bedwars1058.MessagesData.*;

public class PrivateArenaListener implements Listener {

    @EventHandler
    public void onGameStart(GameStateChangeEvent e) {
        if (e.getNewState() != GameState.playing) return;
        if (!api.getPrivateArenaUtil().isArenaPrivate(e.getArena().getArenaName())) return;
        IPrivatePlayer pp = api.getPrivateArenaUtil().getPrivateArenaByName(e.getArena().getArenaName()).getPrivateArenaHost();


        for (Player p : e.getArena().getPlayers()) {
            List<String> modifiers = new ArrayList<>();
            List<String> message = Utility.getList(p, PRIVATE_GAME_ENABLED_MODIFIERS);
            modifiers.add(message.get(0));
            modifiers.add(message.get(1).replace("{player}", pp.getPlayer().getDisplayName()));
            if (pp.getPlayerSettings().isOneHitOneKillEnabled()) {
                modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_FORMAT).replace("{modifier}", Utility.getMsg(p, ONE_HIT_ONE_KILL_MEANING)));
            }
            switch (pp.getPlayerSettings().getHealthBuffLevel()) {
                case 0:
                case 1:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, HEALTH_BUFF_MEANING))
                            .replace("{selected}", Utility.getMsg(p, NORMAL_HEALTH_MEANING)));
                    break;
                case 2:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, HEALTH_BUFF_MEANING))
                            .replace("{selected}", Utility.getMsg(p, DOUBLE_HEALTH_MEANING)));
                    break;
                case 3:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, HEALTH_BUFF_MEANING))
                            .replace("{selected}", Utility.getMsg(p, TRIPLE_HEALTH_MEANING)));
                    break;
            }
            if (pp.getPlayerSettings().isLowGravityEnabled()) {
                modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_FORMAT).replace("{modifier}", Utility.getMsg(p, ONE_HIT_ONE_KILL_MEANING)));
            }
            switch (pp.getPlayerSettings().getSpeedLevel()) {
                case 0:
                case 2:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, SPEED_MEANING))
                            .replace("{selected}", Utility.getMsg(p, NO_SPEED_MEANING)));
                    break;
                case 1:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, SPEED_MEANING))
                            .replace("{selected}", Utility.getMsg(p, SPEED_I_MEANING)));
                    break;
                case 3:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, SPEED_MEANING))
                            .replace("{selected}", Utility.getMsg(p, SPEED_II_MEANING)));
                    break;
                case 4:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, SPEED_MEANING))
                            .replace("{selected}", Utility.getMsg(p, SPEED_III_MEANING)));
                    break;
            }
            switch (pp.getPlayerSettings().getRespawnTimeLevel()) {
                case 0:
                case 2:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, RESPAWN_EVENT_TIME_MEANING))
                            .replace("{selected}", Utility.getMsg(p, RESPAWN_EVENT_TIME_II_MEANING)));
                    break;
                case 1:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, RESPAWN_EVENT_TIME_MEANING))
                            .replace("{selected}", Utility.getMsg(p, RESPAWN_EVENT_TIME_I_MEANING)));
                    break;
                case 3:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, RESPAWN_EVENT_TIME_MEANING))
                            .replace("{selected}", Utility.getMsg(p, RESPAWN_EVENT_TIME_III_MEANING)));
                    break;
            }
            switch (pp.getPlayerSettings().getEventsTimeLevel()) {
                case 0:
                case 2:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, EVENTS_TIME_MEANING))
                            .replace("{selected}", Utility.getMsg(p, EVENTS_TIME_NORMAL_MEANING)));
                    break;
                case 1:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, EVENTS_TIME_MEANING))
                            .replace("{selected}", Utility.getMsg(p, EVENTS_TIME_SLOWER_MEANING)));
                    break;
                case 3:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, EVENTS_TIME_MEANING))
                            .replace("{selected}", Utility.getMsg(p, EVENTS_TIME_FAST_MEANING)));
                    break;
                case 4:
                    modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_WITH_OPTION_FORMAT)
                            .replace("{modifier}", Utility.getMsg(p, EVENTS_TIME_MEANING))
                            .replace("{selected}", Utility.getMsg(p, EVENTS_TIME_FASTER_MEANING)));
                    break;
            }
            if (pp.getPlayerSettings().isNoEmeraldsEnabled()) {
                modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_FORMAT).replace("{modifier}", Utility.getMsg(p, NO_EMERALDS_MEANING)));
            }
            if (pp.getPlayerSettings().isNoDiamondsEnabled()) {
                modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_FORMAT).replace("{modifier}", Utility.getMsg(p, NO_DIAMONDS_MEANING)));
            }
            if (pp.getPlayerSettings().isAllowMapBreakEnabled()) {
                modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_FORMAT).replace("{modifier}", Utility.getMsg(p, ALLOW_MAP_BREAK_MEANING)));
            }
            if (pp.getPlayerSettings().isBedInstaBreakEnabled()) {
                modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_FORMAT).replace("{modifier}", Utility.getMsg(p, BED_INSTA_BREAK_MEANING)));
            }
            if (pp.getPlayerSettings().isMaxTeamUpgradesEnabled()) {
                modifiers.add(Utility.getMsg(p, PRIVATE_GAME_MODIFIERS_FORMAT).replace("{modifier}", Utility.getMsg(p, MAX_TEAM_UPGRADES_MEANING)));
            }
            modifiers.add(message.get(3));
            for (String m : modifiers) {
                p.sendMessage(m);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGeneratorUpgrade(GeneratorUpgradeEvent e) {
        /*
        if (!api.getPrivateArenaUtil().isArenaPrivate(e.getGenerator().getArena().getArenaName())) return;
        if (GeneratorProperties.getGenProps().get(e.getGenerator()) == null) return;
        GeneratorProperties.Properties props = GeneratorProperties.getGenProps().get(e.getGenerator());
        IGenerator gen = e.getGenerator();
        gen.setDelay(props.getDelay());
        gen.setAmount(props.getAmount());
        gen.setSpawnLimit(props.getSpawnLimit());
         */
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            IArena arena = api.getBedWars2023API().getArenaUtil().getArenaByPlayer((Player) e.getDamager());
            if (arena == null) {
                return;
            }
            IPrivateArena privateArena = api.getPrivateArenaUtil().getPrivateArenaByPlayer((Player) e.getDamager());

            if (privateArena == null) return;
            if (api.getPrivateArenaUtil().isArenaPrivate(arena.getArenaName())) {
                if (privateArena.getPrivateArenaHost().getPlayerSettings().isOneHitOneKillEnabled()) {
                    if (arena.getStatus() != GameState.playing) return;
                    if (arena.isSpectator((Player) e.getDamager())) return;
                    if (arena.isReSpawning((Player) e.getEntity())) return;
                    if (arena.isReSpawning((Player) e.getDamager())) return;
                    if (arena.getTeam((Player) e.getDamager()).getMembers().contains((Player) e.getEntity())) return;
                    e.setDamage(600.0D);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerReSpawnEvent e) {
        if (!api.getPrivateArenaUtil().isArenaPrivate(e.getArena().getArenaName())) return;
        IPrivatePlayer pp = api.getPrivatePlayer(e.getPlayer());

        Utility.giveLongJump(pp.getPlayer());
        Utility.giveHealthBuff(pp.getPlayer());
        Utility.giveSpeedLevel(pp.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerSpawn(GameStateChangeEvent e) {
        if (e.getNewState() != GameState.playing) return;
        if (!api.getPrivateArenaUtil().isArenaPrivate(e.getArena().getArenaName())) return;

        IPrivatePlayer pp = api.getPrivateArenaUtil().getPrivateArenaByName(e.getArena().getArenaName()).getPrivateArenaHost();
        IPrivateArena privateArena = api.getPrivateArenaUtil().getPrivateArenaByName(e.getArena().getArenaName());

        privateArena.getPlayers().forEach(Utility::giveLongJump);
        privateArena.getPlayers().forEach(Utility::giveHealthBuff);
        privateArena.getPlayers().forEach(Utility::giveSpeedLevel);

        if (pp.getPlayerSettings().isNoEmeraldsEnabled()) {
            for (IGenerator gen : e.getArena().getOreGenerators()) {
                if (gen.getType() != GeneratorType.EMERALD) continue;
                gen.destroyData();
            }
            e.getArena().getOreGenerators().removeIf(g -> g.getType() == GeneratorType.EMERALD);
            e.getArena().getNextEvents().remove("EMERALD_GENERATOR_TIER_II");
            e.getArena().getNextEvents().remove("EMERALD_GENERATOR_TIER_III");
        }

        if (pp.getPlayerSettings().isNoDiamondsEnabled()) {
            for (IGenerator gen : e.getArena().getOreGenerators()) {
                if (gen.getType() != GeneratorType.DIAMOND) continue;
                gen.destroyData();
            }
            e.getArena().getOreGenerators().removeIf(g -> g.getType() == GeneratorType.DIAMOND);
            e.getArena().getNextEvents().remove("DIAMOND_GENERATOR_TIER_II");
            e.getArena().getNextEvents().remove("DIAMOND_GENERATOR_TIER_III");
        }

        if (pp.getPlayerSettings().isMaxTeamUpgradesEnabled()) {
            upgradeTeams(e.getArena());
        }

        if (pp.getPlayerSettings().isAllowMapBreakEnabled()) {
            if (e.getArena().isAllowMapBreak()) {
                e.getArena().setAllowMapBreak(true);
            }
        } else {
            if (e.getArena().isAllowMapBreak()) {
                e.getArena().setAllowMapBreak(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGameEnd(GameEndEvent e) {
        if (!api.getPrivateArenaUtil().isArenaPrivate(e.getArena().getArenaName())) return;
        IArena a = e.getArena();
        IPrivateArena privateArena = api.getPrivateArenaUtil().getPrivateArenaByName(a.getArenaName());

        privateArena.getPlayers().forEach(p -> p.setMaxHealth(20.0));
        privateArena.getPlayers().forEach(p -> p.setHealth(20.0));
        privateArena.getPlayers().forEach(p -> p.setHealthScale(20.0));

        if (a.isAllowMapBreak()) {
            a.setAllowMapBreak(false);
        }
        privateArena.destroyData();
    }

    @EventHandler
    public void onPlayerDeath(PlayerKillEvent e) {
        if (api.getBedWars2023API().getArenaUtil().getArenaByPlayer(e.getVictim()) == null) return;
        if (!api.getPrivateArenaUtil().isArenaPrivate(e.getArena().getArenaName())) return;
        IPrivatePlayer pp = api.getPrivateArenaUtil().getPrivateArenaByName(e.getArena().getArenaName()).getPrivateArenaHost();
        switch (pp.getPlayerSettings().getRespawnTimeLevel()) {
            case 0:
            case 2:
                break;
            case 1:
                e.getArena().getRespawnSessions().put(e.getVictim(), 1);
                break;
            case 3:
                e.getArena().getRespawnSessions().put(e.getVictim(), 10);
                break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onArenaLeave(PlayerLeaveArenaEvent e) {
        Player p = e.getPlayer();

        if (api.getPrivateArenaUtil().isArenaPrivate(e.getArena().getArenaName())) {
            p.setMaxHealth(20.0);
            p.setHealth(20.0);
            p.setHealthScale(20.0);

            IPrivatePlayer pp = api.getPrivateArenaUtil().getPrivateArenaByPlayer(p).getPrivateArenaHost();
            if (e.getArena().getPlayers().size() <= 1) {
                if (pp.getPlayerSettings().isAllowMapBreakEnabled()) {
                    if (e.getArena().isAllowMapBreak()) {
                        e.getArena().setAllowMapBreak(false);
                    }
                }
                api.getPrivateArenaUtil().getPrivateArenaByName(e.getArena().getArenaName()).destroyData();
            }
        }
    }

    @EventHandler
    public void onNextEvent(NextEventChangeEvent e) {
        if (e.getArena() == null) return;
        if (!api.getPrivateArenaUtil().isArenaPrivate(e.getArena().getArenaName())) return;
        modifyEventTime((Arena) e.getArena());
    }

    @EventHandler
    public void onBedPunch(PlayerInteractEvent e) {
        try {
            if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;
            Player player = e.getPlayer();
            if (!api.getBedWars2023API().getArenaUtil().isPlaying(player)) return;
            IArena a = Arena.getArenaByPlayer(player);
            if (a == null) return;
            IPrivateArena pa = api.getPrivateArenaUtil().getPrivateArenaByName(a.getArenaName());
            if (pa == null) return;
            IPrivatePlayer pp = pa.getPrivateArenaHost();

            Block b = e.getClickedBlock();
            if (pp.getPlayerSettings().isAllowMapBreakEnabled() && !b.getType().toString().contains("BED")) {
                a.addPlacedBlock(b);
            }
            if (pp.getPlayerSettings().isBedInstaBreakEnabled() && b.getType().toString().contains("BED") && this.getBedLocations(b.getLocation()).stream().noneMatch(l -> l.getBlock().getLocation().equals((Object) a.getTeam(player).getBed().getBlock().getLocation()))) {
                Bukkit.getPluginManager().callEvent(new BlockBreakEvent(b, player));
                b.setType(Material.AIR);
            }
        } catch (Exception ex) {
            // PREVENT A USELESS ERROR
        }
    }

    private void modifyEventTime(Arena arena) {
        IPrivatePlayer pp = api.getPrivateArenaUtil().getPrivateArenaByName(arena.getArenaName()).getPrivateArenaHost();
        switch (pp.getPlayerSettings().getEventsTimeLevel()) {
            case 0:
            case 2:
                break;
            case 1:
                arena.upgradeDiamondsCount = (int) (arena.upgradeDiamondsCount * 2.0);
                arena.upgradeEmeraldsCount = (int) (arena.upgradeEmeraldsCount * 2.0);
                break;
            case 3:
                arena.upgradeDiamondsCount = (int) (arena.upgradeDiamondsCount * 0.5);
                arena.upgradeEmeraldsCount = (int) (arena.upgradeEmeraldsCount * 0.5);
                break;
            case 4:
                arena.upgradeDiamondsCount = (int) (arena.upgradeDiamondsCount * 0.25);
                arena.upgradeEmeraldsCount = (int) (arena.upgradeEmeraldsCount * 0.25);
                break;
        }
    }

    public List<Location> getBedLocations(Location loc) {
        List<Location> locationList = new ArrayList<>();
        if (!loc.getBlock().getType().toString().contains("BED")) return locationList;
        locationList.add(loc);
        if (loc.clone().add(0.0, 0.0, 1.0).getBlock().getType().toString().contains("BED")) {
            locationList.add(loc.clone().add(0.0, 0.0, 1.0));
        } else if (loc.clone().subtract(0.0, 0.0, 1.0).getBlock().getType().toString().contains("BED")) {
            locationList.add(loc.clone().subtract(0.0, 0.0, 1.0));
        } else if (loc.clone().add(1.0, 0.0, 0.0).getBlock().getType().toString().contains("BED")) {
            locationList.add(loc.clone().add(1.0, 0.0, 0.0));
        } else if (loc.clone().subtract(1.0, 0.0, 0.0).getBlock().getType().toString().contains("BED")) {
            locationList.add(loc.clone().subtract(1.0, 0.0, 0.0));
        }
        return locationList;
    }

    public void upgradeTeams(IArena a) {
        for (ITeam team : a.getTeams()) {
            ConfigManager upgradesConfig = api.getBedWars2023API().getConfigs().getUpgradesConfig();
            ConfigurationSection conf = upgradesConfig.getYml().getConfigurationSection(a.getGroup() + "-upgrades-settings");

            if (conf == null) {
                conf = api.getBedWars2023API().getConfigs().getUpgradesConfig().getYml().getConfigurationSection("default-upgrades-settings");
            }

            for (Object up : conf.getList("menu-content")) {
                String upgrade = ((String) up).split(",")[0];
                if (!upgrade.startsWith("upgrade-")) continue;
                Set<String> tiers = upgradesConfig.getYml().getConfigurationSection(upgrade).getKeys(false);
                for (String tier : tiers) {
                    int level = Integer.parseInt(tier.split("-")[1]);
                    if (level != tiers.size()) continue;
                    team.getTeamUpgradeTiers().put(upgrade, level-1);

                    for (String recieve : upgradesConfig.getYml().getStringList(upgrade + "." + tier + ".recieve")) {
                        String[] r = recieve.split(":");
                        switch (r[0]) {
                            case "generator-edit":
                                switch (r[1].replace(" ", "").split(",")[0]) {
                                    case "iron":
                                        team.getGenerators().stream().filter(g -> g.getType() == GeneratorType.IRON).forEach(g -> {
                                            g.setDelay(Integer.parseInt(r[1].replace(" ", "").split(",")[1]));
                                            g.setAmount(Integer.parseInt(r[1].replace(" ", "").split(",")[2]));
                                            g.setSpawnLimit(Integer.parseInt(r[1].replace(" ", "").split(",")[3]));
                                        });
                                        break;
                                    case "gold":
                                        team.getGenerators().stream().filter(g -> g.getType() == GeneratorType.GOLD).forEach(g -> {
                                            g.setDelay(Integer.parseInt(r[1].replace(" ", "").split(",")[1]));
                                            g.setAmount(Integer.parseInt(r[1].replace(" ", "").split(",")[2]));
                                            g.setSpawnLimit(Integer.parseInt(r[1].replace(" ", "").split(",")[3]));
                                        });
                                        break;
                                    case "emerald":
                                        IGenerator g = new OreGenerator(team.getGenerators().get(0).getLocation(), a, GeneratorType.CUSTOM, team);
                                        g.setDelay(Integer.parseInt(r[1].replace(" ", "").split(",")[1]));
                                        g.setAmount(Integer.parseInt(r[1].replace(" ", "").split(",")[2]));
                                        g.setSpawnLimit(Integer.parseInt(r[1].replace(" ", "").split(",")[3]));
                                        team.getGenerators().add(g);
                                        break;
                                }
                                break;
                            case "player-effect":
                                switch (r[1].replace(" ", "").split(",")[3]) {
                                    case "base":
                                        PotionEffectType type = PotionEffectType.getByName(r[1].replace(" ", "").split(",")[0]);
                                        team.addBaseEffect(type, Integer.parseInt(r[1].replace(" ", "").split(",")[1]), Integer.parseInt(r[1].replace(" ", "").split(",")[2]));
                                        break;
                                    case "team":
                                        PotionEffectType type2 = PotionEffectType.getByName(r[1].replace(" ", "").split(",")[0]);
                                        team.addTeamEffect(type2, Integer.parseInt(r[1].replace(" ", "").split(",")[1]), Integer.parseInt(r[1].replace(" ", "").split(",")[2]));
                                        break;
                                }
                                break;
                            case "enchant-item":
                               Enchantment ecnh =  Enchantment.getByName(r[1].split(",")[0].replace(" ", ""));
                                switch (r[1].split(",")[2]) {
                                    case "armor":
                                        team.addArmorEnchantment(ecnh, Integer.parseInt(r[1].split(",")[1]));
                                        break;
                                    case "sword":
                                        team.addSwordEnchantment(ecnh, Integer.parseInt(r[1].split(",")[1]));
                                        break;
                                }
                                break;
                            case "dragon":
                                team.setDragonAmount(Integer.parseInt(r[1]));
                                break;
                        }
                    }
                }
            }
        }
    }
}
