package com.cpbadarau.minecraft.listeners

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EntityDamageByEntityEventListener : Listener {
    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        Bukkit.getLogger().info("EntityDamageByEntityEvent")
        if (event.damager is Player) {
            val player = event.damager as Player
            val item = player.inventory.itemInMainHand
            if (isInventoryEmpty(player) && item.type == Material.FEATHER) {
                event.damage = 10.0
            }
        }
    }

    private fun isInventoryEmpty(player: Player): Boolean {
        // Check if player has any items in their inventory but a feather
        for (item in player.inventory.contents) {
            if (item != null && item.type != Material.AIR && item.type != Material.FEATHER) {
                return false
            }
        }

        // Check if player has any armor equipped
        for (item in player.inventory.armorContents) {
            if (item != null && item.type != Material.AIR) {
                return false
            }
        }

        return true
    }
}
