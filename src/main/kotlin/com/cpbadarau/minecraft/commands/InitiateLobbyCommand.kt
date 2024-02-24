package com.cpbadarau.minecraft.commands

import com.cpbadarau.minecraft.GameManager
import com.cpbadarau.minecraft.models.GameState
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class InitiateLobbyCommand(gameManager: GameManager) : CommandExecutor {
    private val gameManager: GameManager = gameManager

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (gameManager.getGameState() === GameState.RUNNING) {
            sender.sendMessage("El juego ya está en marcha!")
            return true
        }
        // get player from sender
        val player: Player = sender as Player
        gameManager.startLobby(player)
        return true
    }
}
