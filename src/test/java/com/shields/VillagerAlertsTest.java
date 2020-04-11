package com.shields;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
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
    public void shouldReturnTrueIfVillagerKilled() {
        Villager villager = mock(Villager.class);
        when(villager.getType()).thenReturn(EntityType.VILLAGER);
        when(villager.getHealth()).thenReturn(10.0);

        assertTrue(villagerAlerts.villagerKilled(villager, 20));
    }

    @Test
    public void shouldReturnFalseIfVillagerNotKilled() {
        Villager villager = mock(Villager.class);
        when(villager.getType()).thenReturn(EntityType.VILLAGER);
        when(villager.getHealth()).thenReturn(10.0);

        assertFalse(villagerAlerts.villagerKilled(villager, 5));
    }

    @Test
    public void shouldReturnFalseIfEntityNotAVillager() {
        Zombie zombie = mock(Zombie.class);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);
        when(zombie.getHealth()).thenReturn(10.0);

        assertFalse(villagerAlerts.villagerKilled(zombie, 20));
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

        assertThat(villagerAlerts.getNameOfEntity(villager), is(equalTo("FISHERMAN")));
    }

    @Test
    public void shouldReturnVillagerEntityTypeIfProfessionNone() {
        Villager villager = mock(Villager.class);
        when(villager.getType()).thenReturn(EntityType.VILLAGER);
        when(villager.getProfession()).thenReturn(Villager.Profession.NONE);

        assertThat(villagerAlerts.getNameOfEntity(villager), is(equalTo("VILLAGER")));
    }

    @Test
    public void shouldReturnVillagerIfProfessionNone() {
        Zombie zombie = mock(Zombie.class);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);

        assertThat(villagerAlerts.getNameOfEntity(zombie), is(equalTo("ZOMBIE")));
    }
}
