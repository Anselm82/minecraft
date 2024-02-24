package com.cpbadarau.minecraft.commands

import com.cpbadarau.minecraft.GameManager
import com.cpbadarau.minecraft.models.GameState
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class StartGameCommand(gameManager: GameManager) : CommandExecutor {
    private val gameManager: GameManager = gameManager

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (gameManager.getGameState() === GameState.RUNNING) {
            sender.sendMessage("El juego ya está en marcha!")
            return true
        }
        gameManager.setTimeUntilGameStart(60)

        gameManager.startGame()

        Bukkit.broadcastMessage("El juego ha comenzado!")
        return true
    }
}
