package com.cpbadarau.minecraft

import org.bukkit.Bukkit
import org.bukkit.entity.Player

class PlayerManager(private val gameManager: GameManager, private val teamManager: TeamManager) {
    fun addPlayer(player: Player) {
        setPlayerScoreboard(player)
    }

    fun setPlayerScoreboard(player: Player) {
        player.scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard
    }
}
