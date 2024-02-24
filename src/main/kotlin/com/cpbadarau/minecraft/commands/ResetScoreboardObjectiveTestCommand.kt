package com.cpbadarau.minecraft.commands

import com.cpbadarau.minecraft.TeamManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ResetScoreboardObjectiveTestCommand(teamManager: TeamManager) : CommandExecutor {
    private val teamManager: TeamManager = teamManager

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        teamManager.resetScoreboardObjective()
        return true
    }
}
