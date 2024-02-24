package com.cpbadarau.minecraft.listeners

import com.cpbadarau.minecraft.GameManager
import com.cpbadarau.minecraft.TeamManager
import com.cpbadarau.minecraft.models.GameState
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Chest
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class PlayerDeathListener(gameManager: GameManager) : Listener {
    private val gameManager: GameManager = gameManager
    private val teamManager: TeamManager = gameManager.getTeamManager()!!

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (gameManager.getGameState() !== GameState.RUNNING) {
            return
        }
        val player: Player = event.getEntity()
        val killer: Player = player.getKiller()!!
        handlePlayerDeath(player, event)

        val damageEvent: EntityDamageEvent = player.getLastDamageCause()!!

        if (damageEvent is EntityDamageByEntityEvent) {
            handleEntityDamage(event, damageEvent as EntityDamageByEntityEvent, killer, player)
        } else if (damageEvent.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setDeathMessage(player.getName() + " se ha estampado contra el suelo.")
        }

        gameManager.checkIfPlayersAreAlive(player)

        getPlayerHead(player)
    }

    private fun handlePlayerDeath(player: Player, event: PlayerDeathEvent) {
        val location: Location = player.getLocation()!!
        //El siguiente método no lo reconoce, igual lo han quitado del api, he usado el que me ha parecido más similar.
        //player.setRespawnLocation(location)
        player.setBedSpawnLocation(location)
        event.getEntity().getWorld().strikeLightningEffect(location)
        player.setGameMode(GameMode.SPECTATOR)
        player.setFlySpeed(0.4f)
    }

    fun getPlayerHead(player: Player) {
        val skull: ItemStack = createSkull(player)
        createDeathChest(player, skull)
    }

    private fun createSkull(player: Player): ItemStack {
        val skull: ItemStack = ItemStack(Material.PLAYER_HEAD)
        val skullMeta: SkullMeta = skull.getItemMeta() as SkullMeta
        skullMeta.setOwningPlayer(player)
        skullMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 623, true)
        skullMeta.isUnbreakable()
        skullMeta.setDisplayName("Cabeza de " + player.getName())
        skullMeta.setLore(
            mutableListOf(
                "Se puede usar de florero",
                "También te la puedes equipar, pero cuidado que solo tiene 3 de durabilidad!",
                "Te la puedes comer. Esto te curará 3 corazones y 3 de hambre ",
                "pero el item desaparecerá de tu inventario asi que un trofeo menos!",
                "Si juntas 3 cabezas y 6 bloques de oro en una linea puedes hacer un totem.",
                "Con una cabeza y 8 bloques de oro puedes hacer una manzana de bloques de oro!",
                "PD: Si la pones en el suelo cagaste porque ya no tendrá ningun efecto especial",
                "ni te la podras comer."
            )
        )
        skull.setItemMeta(skullMeta)
        return skull
    }

    private fun createDeathChest(player: Player, skull: ItemStack) {
        // Create a chest at the player's location
        val location: Location = player.getLocation()
        location.block.type = Material.CHEST
        val block = location.block
        val chest = block.state as Chest
        // Add the player's head to the chest
        chest.blockInventory.addItem(skull)
        chest.customName = "Cabeza de " + player.getName()
        val spiralStartLocation = location.clone()
        spiralStartLocation.y = spiralStartLocation.y + 4
        for (i in 1..249) {
            val blockLocation = location.clone()
            blockLocation.y = blockLocation.y + i
            blockLocation.block.type = Material.AIR
        }
        // createSpiral(spiralStartLocation);
        createRandomLanternPattern(spiralStartLocation, Material.LANTERN, 40, 250)
    }

    private fun getMaterialForY(y: Int): Material {
        return when (y % 5) {
            0 -> Material.GLOWSTONE
            1 -> Material.REDSTONE_LAMP
            2 -> Material.JACK_O_LANTERN
            3 -> Material.SEA_LANTERN
            4 -> Material.CHERRY_WOOD
            else -> Material.CHERRY_LEAVES
        }
    }

    private fun createSpiral(start: Location) {
        val world: World? = start.world
        var radius = 0.0
        var angle = 0.0
        for (y in start.blockY until (world?.getMaxHeight() ?: 0)) {
            // Calculate the x and z coordinates for the current angle and radius
            val x = start.blockX + (radius * cos(angle)).toInt()
            val z = start.blockZ + (radius * sin(angle)).toInt()

            // Set the block at the calculated coordinates to the specified material
            val blockMaterial: Material = getMaterialForY(y)
            world?.getBlockAt(x, y, z)?.setType(blockMaterial)

            // Increase the angle and radius for the next step in the spiral
            angle += Math.PI / 16
            radius += 0.1
        }
    }

    private fun createRandomLanternPattern(start: Location, lanternMaterial: Material, width: Int, height: Int) {
        val world: World? = start.world
        val random = Random()

        for (i in 0 until height) {
            // Generate random x and z coordinates within the specified width
            val x = start.blockX + random.nextInt(width) - width / 2
            val z = start.blockZ + random.nextInt(width) - width / 2

            // Calculate the y coordinate for the current block
            val y = start.blockY + i

            world?.getBlockAt(x, y, z)?.setType(if (i % 2 == 0) lanternMaterial else Material.SOUL_LANTERN)
        }
    }

    companion object {
        private fun handleEntityDamage(
            event: PlayerDeathEvent, damageEvent: EntityDamageByEntityEvent,
            killer: Player?, player: Player
        ) {
            val entityDamageEvent: EntityDamageByEntityEvent = damageEvent
            val aggressor: Entity = entityDamageEvent.getDamager()
            if (aggressor is Player) {
                if (killer != null) {
                    event.setDeathMessage(
                        killer.getName() + " le ha abierto el culo a "
                                + player.getName() + ". Un aplauso por favor."
                    )
                }
            } else if (aggressor is Monster) {
            }
        }
    }
}
