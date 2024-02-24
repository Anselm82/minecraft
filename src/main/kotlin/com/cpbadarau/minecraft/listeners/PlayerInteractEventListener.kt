package com.cpbadarau.minecraft.listeners

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.SkullMeta
import kotlin.math.min

class PlayerInteractEventListener : Listener {
    // this will simulate eating the head
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = event.item
        Bukkit.getLogger().info("PlayerInteractEvent1")

        if (item != null && item.type == Material.PLAYER_HEAD && item.itemMeta is SkullMeta) {
            Bukkit.getLogger().info("PlayerInteractEvent2")
            if (event.action.name.contains("RIGHT_CLICK")) {
                val localPlayer = event.player
                // just consume the head if the player is pointing at an entity or at air
                if (event.hasBlock()) {
                    return
                }
                Bukkit.getLogger().info("PlayerInteractEvent3")
                // Simulate eating the head
                localPlayer.foodLevel = event.player.foodLevel + 6
                localPlayer.health = min(localPlayer.health + 6, localPlayer.maxHealth)

                Bukkit.getLogger().info("PlayerInteractEvent3")
                // Remove one player head from the player's hand
                if (item.amount > 1) {
                    item.amount = item.amount - 1
                } else {
                    localPlayer.inventory.removeItem(item)
                }

                event.isCancelled = true
            }
        }
    }
}
