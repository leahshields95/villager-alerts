package com.shields;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class VillagerAlerts extends JavaPlugin implements Listener {
    private FileConfiguration config;

    @Override
    public void onEnable() {
        config = this.getConfig();
        config.addDefault("radius", 128.0);
        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Villager) {
            Villager villager = (Villager) event.getEntity();
            if (villager.getHealth() - event.getFinalDamage() < 0) {
                sendMessageToPlayersWithinArea(villager.getProfession() + " was killed by " +
                        getNameOfEntity(event.getDamager()), villager);
            }
        }
    }

    private String getNameOfEntity(Entity entity) {
        if (entity instanceof Player) {
            return ((Player) entity).getDisplayName();
        } else {
            return entity.getType().toString();
        }
    }

    private void sendMessageToPlayersWithinArea(String message, Villager villager) {
        double radius = config.getDouble("radius");
        for (Player player : getServer().getOnlinePlayers()) {
            player.getNearbyEntities(radius, radius, radius).forEach(entity -> {
                if (entity.equals(villager)) {
                    player.sendMessage(message);
                }
            });
        }
    }
}
