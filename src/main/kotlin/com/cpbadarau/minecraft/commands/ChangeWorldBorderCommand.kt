package com.cpbadarau.minecraft.commands

import com.cpbadarau.minecraft.GameManager
import org.bukkit.Bukkit
import org.bukkit.WorldBorder
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ChangeWorldBorderCommand(gameManager: GameManager) : CommandExecutor {
    private val gameManager: GameManager = gameManager

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size != 2) {
            sender.sendMessage("Debes especificar el tamaño y el tiempo del muro!")
            return true
        }
        // get world border args
        val size = args[0].toInt()
        val seconds = args[1].toInt()
        if (seconds < 0 || size < 0) {
            sender.sendMessage("Los valores deben ser positivos!")
            return true
        }
        val wb: WorldBorder = Bukkit.getWorld("world")!!.worldBorder
        wb.setSize(size.toDouble(), seconds.toLong())
        gameManager.broadcastServerMessage("Se ha actualizado el muro!")
        return true
    }
}
