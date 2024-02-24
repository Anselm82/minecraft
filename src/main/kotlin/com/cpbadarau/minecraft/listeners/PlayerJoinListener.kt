package com.cpbadarau.minecraft.listeners

import com.cpbadarau.minecraft.PlayerManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(playerManager: PlayerManager) : Listener {
    private val playerManager: PlayerManager = playerManager

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        Bukkit.broadcastMessage("Bienvenido al servidor, " + event.getPlayer().getName() + "!")
        playerManager.addPlayer(event.getPlayer())
        playerManager.setPlayerScoreboard(event.getPlayer())
    }
}
