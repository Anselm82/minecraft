package com.cpbadarau.minecraft.listeners

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.SkullMeta

class EntityDamageEventListener : Listener {
    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val helmet = player.inventory.helmet
            if (helmet != null && helmet.type == Material.PLAYER_HEAD && helmet.itemMeta is SkullMeta) {
                val meta = helmet.itemMeta
                val damage = (meta as Damageable?)!!.damage + 1

                if (damage >= 3) {
                    // The helmet has taken 3 hits, so break it
                    player.inventory.helmet = null
                } else {
                    // The helmet has taken less than 3 hits, so increase its damage
                    meta!!.damage = damage
                    helmet.setItemMeta(meta)
                }
            }
        }
    }
}
