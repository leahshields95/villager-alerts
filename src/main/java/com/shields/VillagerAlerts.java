package com.shields;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

public class VillagerAlerts extends JavaPlugin implements Listener {
    private FileConfiguration config;

    public VillagerAlerts() {
        super();
    }

    protected VillagerAlerts(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        config = this.getConfig();
        config.addDefault("radius", 128.0);
        config.addDefault("show-location", false);
        config.addDefault("message-colour-code", "f");
        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("vareload")) {
            this.reloadConfig();
            config = this.getConfig();
            sender.sendMessage(ChatColor.RED + "Villager Alerts has been reloaded");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (this.villagerKilled(event.getEntity(), event.getFinalDamage())) {
            Villager villager = (Villager) event.getEntity();
            sendMessageToPlayersWithinConfiguredArea("Nearby " + getNameOfEntity(villager) + " was killed by " +
                    getNameOfEntity(event.getDamager()) + this.getLocationMessage(villager), villager);
        }
    }

    protected boolean villagerKilled(Entity victim, double damage) {
        if (victim instanceof Villager) {
            return ((Villager) victim).getHealth() - damage < 0;
        }
        return false;
    }

    protected String getNameOfEntity(Entity entity) {
        String name = entity.getType().toString().toLowerCase();
        if (entity instanceof Villager) {
            if (!((Villager) entity).getProfession().equals(Villager.Profession.NONE)) {
                name += " (" + ((Villager) entity).getProfession().toString().toLowerCase() + ")";
            }
        }

        if (entity instanceof Player) {
            name = ((Player) entity).getDisplayName();
        }
        return name;
    }

    protected void sendMessageToPlayersWithinConfiguredArea(String message, Villager villager) {
        double radius = config.getDouble("radius");
        for (Player player : getServer().getOnlinePlayers()) {
            player.getNearbyEntities(radius, radius, radius).forEach(entity -> {
                if (entity.equals(villager)) {
                    player.sendMessage(ChatColor.getByChar(config.getString("message-colour-code")) + message);
                }
            });
        }
    }

    protected String getLocationMessage(Villager villager) {
        return this.config.getBoolean("show-location") ?
                " at " + this.getLocationAsString(villager.getLocation()) : "";
    }

    private String getLocationAsString(Location location) {
        return (int) location.getX() + ", "
                + (int) location.getY() + ", "
                + (int) location.getZ();
    }
}
