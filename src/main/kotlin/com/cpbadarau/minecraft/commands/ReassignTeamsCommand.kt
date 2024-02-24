package com.cpbadarau.minecraft.commands

import com.cpbadarau.minecraft.GameManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ReassignTeamsCommand(gameManager: GameManager) : CommandExecutor {
    private val gameManager: GameManager = gameManager

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        gameManager.reassignTeams()
        return true
    }
}
