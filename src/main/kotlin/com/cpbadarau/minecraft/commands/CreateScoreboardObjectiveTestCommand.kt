package com.cpbadarau.minecraft.commands

import com.cpbadarau.minecraft.TeamManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class CreateScoreboardObjectiveTestCommand(teamManager: TeamManager) : CommandExecutor {
    private val teamManager: TeamManager = teamManager

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        teamManager.createScoreboardObjective()
        Bukkit.broadcastMessage("Teams and objectives created!")
        return true
    }
}
