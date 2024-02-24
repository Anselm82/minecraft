package com.cpbadarau.minecraft.listeners

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class InventoryClickEventListener : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.whoClicked is Player) {
            checkForPumpkin(event.whoClicked as Player)
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        if (event.player is Player) {
            checkForPumpkin(event.player as Player)
        }
    }

    private fun checkForPumpkin(player: Player) {
        val helmet = player.inventory.helmet

        if (helmet != null && helmet.type == Material.CARVED_PUMPKIN) {
            // If the player is wearing a carved pumpkin, give them night vision
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.NIGHT_VISION,
                    Int.MAX_VALUE, 0, false, false, false
                )
            )
        } else {
            // If the player is not wearing a carved pumpkin, remove night vision
            player.removePotionEffect(PotionEffectType.NIGHT_VISION)
        }
    }
}
