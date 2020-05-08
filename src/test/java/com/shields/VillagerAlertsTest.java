package com.shields;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class VillagerAlertsTest
{
    private ServerMock server;
    private VillagerAlerts villagerAlerts;

    @Before
    public void setUp() throws AssumptionViolatedException
    {
        server = MockBukkit.mock();
        villagerAlerts = MockBukkit.load(VillagerAlerts.class);
    }

    @After
    public void tearDown()
    {
        MockBukkit.unload();
    }

    @Test
    public void shouldReturnPlayerNameIfVillagerKilledByPlayer() {
        Villager villager = mock(Villager.class);
        Player player = mock(Player.class);
        EntityDamageByEntityEvent damageEvent = mock(EntityDamageByEntityEvent.class);
        when(player.getType()).thenReturn(EntityType.PLAYER);
        when(player.getDisplayName()).thenReturn("player1");
        when(damageEvent.getDamager()).thenReturn(player);
        when(villager.getLastDamageCause()).thenReturn(damageEvent);

        assertEquals("player1", villagerAlerts.getDeathCause(villager));
    }

    @Test
    public void shouldReturnEntityNameIfVillagerKilledByZombie() {
        Villager villager = mock(Villager.class);
        Zombie zombie = mock(Zombie.class);
        EntityDamageByEntityEvent damageEvent = mock(EntityDamageByEntityEvent.class);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);
        when(damageEvent.getDamager()).thenReturn(zombie);
        when(villager.getLastDamageCause()).thenReturn(damageEvent);

        assertEquals("zombie", villagerAlerts.getDeathCause(villager));
    }

    @Test
    public void shouldReturnDamageCauseIfVillagerKilledOtherCause() {
        Villager villager = mock(Villager.class);
        EntityDamageEvent damageEvent = mock(EntityDamageEvent.class);
        when(damageEvent.getCause()).thenReturn(EntityDamageEvent.DamageCause.LAVA);
        when(villager.getLastDamageCause()).thenReturn(damageEvent);

        assertEquals("lava", villagerAlerts.getDeathCause(villager));
    }

    @Test
    public void shouldReturnPlayerName() {
        Player player = mock(Player.class);
        when(player.getType()).thenReturn(EntityType.PLAYER);
        when(player.getDisplayName()).thenReturn("Leah");

        assertThat(villagerAlerts.getNameOfEntity(player), is(equalTo("Leah")));
    }

    @Test
    public void shouldReturnVillagerProfessionIfNotNone() {
        Villager villager = mock(Villager.class);
        when(villager.getType()).thenReturn(EntityType.VILLAGER);
        when(villager.getProfession()).thenReturn(Villager.Profession.FISHERMAN);

        assertThat(villagerAlerts.getNameOfEntity(villager), is(equalTo("villager (fisherman)")));
    }

    @Test
    public void shouldReturnVillagerEntityTypeIfProfessionNone() {
        Villager villager = mock(Villager.class);
        when(villager.getType()).thenReturn(EntityType.VILLAGER);
        when(villager.getProfession()).thenReturn(Villager.Profession.NONE);

        assertThat(villagerAlerts.getNameOfEntity(villager), is(equalTo("villager")));
    }

    @Test
    public void shouldReturnZombieEntityType() {
        Zombie zombie = mock(Zombie.class);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);

        assertThat(villagerAlerts.getNameOfEntity(zombie), is(equalTo("zombie")));
    }

    @Test
    public void shouldReturnZombieVillagerWithoutUnderscore() {
        Zombie zombie = mock(Zombie.class);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE_VILLAGER);

        assertThat(villagerAlerts.getNameOfEntity(zombie), is(equalTo("zombie villager")));
    }

    @Test
    public void shouldReturnLocationIfShowLocationConfigTrue() {
        Villager villager = mock(Villager.class);
        World world = mock(World.class);
        when(villager.getLocation()).thenReturn(new Location(world, 100, 64, 100));

        villagerAlerts.getConfig().set("show-location", true);

        assertThat(villagerAlerts.getLocationMessage(villager), is(equalTo(" at 100, 64, 100")));
    }

    @Test
    public void shouldReturnBlankStringIfShowLocationConfigFalse() {
        Villager villager = mock(Villager.class);
        World world = mock(World.class);
        when(villager.getLocation()).thenReturn(new Location(world, 100, 64, 100));

        villagerAlerts.getConfig().set("show-location", false);

        assertThat(villagerAlerts.getLocationMessage(villager), is(equalTo("")));
    }
}
