package com.cpbadarau.minecraft

import com.cpbadarau.minecraft.models.GameStage
import com.cpbadarau.minecraft.models.GameState
import com.cpbadarau.minecraft.models.GameType
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scoreboard.Team
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.math.sqrt

class GameManager(cdlfUhc: CDLF_UHC, teamManager: TeamManager) {
    private val plugin: CDLF_UHC = cdlfUhc
    private val teamManager: TeamManager = teamManager
    private var gameState: GameState = GameState.LOBBY
    private val gameType: GameType = GameType.TEAM
    var gameStage: GameStage = GameStage.NONE


    var isPvPEnabled: Boolean = false

    fun setGameState(gameState: GameState) {
        this.gameState = gameState
    }

    fun getGameState(gameState: GameState) {
        this.gameState = gameState
    }


    fun setTimeUntilGameStart(timeUntilGameStart: Int) {
    }

    // stage 1
    fun startLobby(player: Player) {
        setGameState(GameState.STARTING)
        worldConfig()
        broadcastTitle("Preparación", "El juego comenzara pronto")
        teleportPlayersToLobby(player)
        setWorldBorder(800, player.getLocation())
        preChargeWorldBorderChunks()
        broadcastServerMessage("Se ha establecido el muro a 800 bloques!")
        teamManager.createScoreboardObjective()
        teamManager.assignPlayersToTeams()
        broadcastServerMessage("Se han asignado los equipos!")
        for (team in teamManager.getTeams().values) {
            if (team?.getEntries()?.size ?: 0 > 1) broadcastServerMessage("Equipo " + team!!.getName() + ": " + team.getEntries())
        }
    }

    fun reassignTeams() {
        teamManager.resetScoreboardObjective()
        teamManager.createScoreboardObjective()
        teamManager.assignPlayersToTeams()
        broadcastServerMessage("Se han reasignado los equipos!")
        for (team in teamManager.getTeams().values) {
            if (team?.getEntries()?.size ?: 0 > 1) broadcastServerMessage("Equipo " + team!!.getName() + ": " + team.getEntries())
        }
    }

    // stage 2
    fun startGame() {
        startCountdown(10)
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, Runnable {
            broadcastTitle("El juego ha comenzado!", "Buena suerte!")
            setGameState(GameState.RUNNING)
            teleportTeamsToRandomLocations()
            disablePvP(60)
        }, (10 * 20).toLong())
        val startStage3InMinutes = 1
        val startStage4InMinutes = startStage3InMinutes + 1

        Bukkit.getScheduler()
            .scheduleSyncDelayedTask(plugin, Runnable { this.stage3() }, (startStage3InMinutes * 60 * 20).toLong())
        Bukkit.getScheduler().scheduleSyncDelayedTask(
            plugin,
            Runnable { this.startDeathMatch() },
            (startStage4InMinutes * 60 * 20).toLong()
        )
    }

    // stage 3
    fun stage3() {
        broadcastTitle("Cuidado!", "El muro ha empezado a cerrarse!")
        setWorldBorder(300, 5 * 60)
    }

    // stage 4
    fun startDeathMatch() {
        broadcastTitle("La pelea a muerte ha comenzado!", "Buena suerte!")
        broadcastServerMessage("El muro se va a reducir al minimo!")
        val worldBorderReductionInMinutes = 3
        val worldBorderSize = 50
        setWorldBorder(worldBorderSize, worldBorderReductionInMinutes * 60)
        gameStage = GameStage.STAGE4
    }

    fun endGame(player: Player?) {
        if (player != null) {
            val team: Team = teamManager.getTeamOfPlayer(player.getName())!!
            if (team != null && team.getEntries().size > 1) {
                Bukkit.broadcastMessage("El juego ha terminado! El equipo " + team.getName() + " ha ganado!")
            } else {
                Bukkit.broadcastMessage(
                    "El juego ha terminado! " + player.getName()
                            + " ha tenido que carrear al equipo" + team.getName() + " a la victoria!"
                )
            }
        }

        setGameState(GameState.ENDED)
    }

    fun getTeamManager(): com.cpbadarau.minecraft.TeamManager? {
        return teamManager
    }

    fun preChargeWorldBorderChunks() {
        val wb: WorldBorder = Bukkit.getWorld("world")!!.getWorldBorder()
        val size: Int = wb.getSize().toInt()
        val chunks = size / 16
        val radius = chunks / 2
        val center: Location = wb.getCenter()
        for (x in -radius until radius) {
            for (z in -radius until radius) {
                center.chunk.load()
            }
        }
    }

    fun givePlayersCompass(player1: Player, player2: Player) {
        val compass: ItemStack = ItemStack(Material.COMPASS)
        player1.getInventory().addItem(compass)
        player2.getInventory().addItem(compass)
        player1.setCompassTarget(player2.getLocation())
        player2.setCompassTarget(player1.getLocation())
    }

    fun checkDistanceBetweenPlayers(player1: Player, player2: Player) {
        // get the distance between the players and point the players to each other
        val distance = sqrt(player1.getLocation().distance(player2.getLocation()))
        givePlayersCompass(player1, player2)

        if (distance < 80) {
            Bukkit.broadcastMessage("Ya solo quedan 2!, la pelea a muerte con cuchillos entre " + player1.getName() + " y " + player2.getName())
        }
    }

    fun checkIfPlayersAreAlive(player: Player?) {
        if (gameType === GameType.SOLO) {
            if (Bukkit.getOnlinePlayers().size == 1) {
                endGame(Bukkit.getOnlinePlayers().stream().findFirst().get())
            }
        } else {
            val players: MutableList<Player>? = Bukkit.getOnlinePlayers().stream()!!
                .filter { p: Player -> p.getGameMode() == GameMode.SURVIVAL }.collect(Collectors.toList())
            val aliveTeams: ArrayList<Team> = ArrayList<Team>()
            if (players != null) {
                for (p in players) {
                    val t: Team = teamManager.getTeamOfPlayer(p.getName())!!
                    if (aliveTeams.contains(t)) {
                        continue
                    }
                    aliveTeams.add(t)
                }
            }
            if (aliveTeams.size == 1) {
                if (player != null) endGame(player)
                else endGame(players?.get(0))
            } else if (aliveTeams.size == 2) {
                checkDistanceBetweenPlayers(players!![0] , players!![1])
                broadcastServerMessage("Ya solo quedan 2 equipos!")
                broadcastServerMessage("Se os ha entregado una brújula para que os encontréis!")
            }
        }
    }

    fun broadcastTitle(title: String?, subtitle: String?) {
        for (player in Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, subtitle, 10, 70, 20)
        }
    }

    fun broadcastServerMessage(message: String) {
        Bukkit.broadcastMessage(message)
    }

    fun getGameState(): GameState {
        return gameState
    }

    fun disablePvP(timeUntilPvP: Int) {
        isPvPEnabled = false
        broadcastServerMessage("El pvp está desactivado y tenéis un periodo de gracia de unos cuantos segundos!")
        makePlayersInvincible(true)
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, Runnable {
            isPvPEnabled = true
            broadcastTitle("Suerte!", "Estas a tu suerte :P!")
            makePlayersInvincible(false)
        }, (timeUntilPvP * 20).toLong())
    }

    // world config
    fun worldConfig() {
        for (world in Bukkit.getWorlds()) {
            world.setDifficulty(Difficulty.HARD)
            world.setGameRule<Boolean>(GameRule.DO_DAYLIGHT_CYCLE, true)
            world.setGameRule<Boolean>(GameRule.DO_WEATHER_CYCLE, true)
            world.setGameRule<Boolean>(GameRule.DO_MOB_SPAWNING, true)
            world.setGameRule<Boolean>(GameRule.DO_IMMEDIATE_RESPAWN, true)
            world.setGameRule<Boolean>(GameRule.LOG_ADMIN_COMMANDS, false)
            world.setGameRule<Boolean>(GameRule.NATURAL_REGENERATION, false)
        }
    }

    fun makePlayersInvincible(invincible: Boolean) {
        for (player in Bukkit.getOnlinePlayers()) {
            player.setHealth(20.0)
            player.setSaturation(20f)
            player.setGameMode(GameMode.SURVIVAL)
            player.setInvulnerable(invincible)
        }
    }

    fun teleportPlayersToLobby(player: Player) {
        player.getWorld().setSpawnLocation(player.getLocation())
        for (p in Bukkit.getOnlinePlayers()) {
            val tpLocation: Location = player.getLocation().add(Math.random() * 10, 0.0, Math.random() * 10)
            p.teleport(tpLocation.world!!.getHighestBlockAt(tpLocation).location)
        }
    }

    fun setWorldBorder(size: Int, location: Location?) {
        Bukkit.getWorlds().forEach(Consumer<World> { world: World ->
            if (location != null) {
                world.getWorldBorder().setCenter(location)
            }
            world.getWorldBorder().setSize(size.toDouble())
            world.getWorldBorder().setWarningDistance(5)
            world.getWorldBorder().setWarningTime(3)
        })
    }

    fun setWorldBorder(size: Int, time: Int) {
        Bukkit.getWorlds().forEach(Consumer<World> { world: World ->
            world.getWorldBorder().setSize(size.toDouble(), time.toLong())
        })
    }

    fun teleportTeamsToRandomLocations() {
        val worldBorder: WorldBorder = Bukkit.getWorlds().get(0).getWorldBorder()

        // Calculate the maximum and minimum possible coordinates
        val minX: Double = worldBorder.getCenter().getX() - worldBorder.getSize() / 4
        val maxX: Double = worldBorder.getCenter().getX() + worldBorder.getSize() / 4
        val minZ: Double = worldBorder.getCenter().getZ() - worldBorder.getSize() / 4
        val maxZ: Double = worldBorder.getCenter().getZ() + worldBorder.getSize() / 4

        // Define the initial teleportation coordinates and the distance between teams
        var x = minX
        var z = minZ
        val distanceBetweenTeams = 150.0

        // teleport all teams to random locations
        for (team in teamManager.getTeams().values) {
            for (player in team!!.getEntries()!!) {
                val p: Player = Bukkit.getPlayer(player)!!
                // Teleport the player to the current coordinates
                p.teleport(
                    Location(
                        Bukkit.getWorlds().get(0),
                        x,
                        Bukkit.getWorlds().get(0).getHighestBlockYAt(x.toInt(), z.toInt()).toDouble(),
                        z
                    )
                )
            }

            // Increment the x coordinate for the next team
            x += distanceBetweenTeams

            // If the x coordinate is outside the world border, reset it to the minimum and increment the z coordinate
            if (x > maxX) {
                x = minX
                z += distanceBetweenTeams
            }

            // If the z coordinate is outside the world border, reset it to the minimum
            if (z > maxZ) {
                z = minZ
            }
        }
    }

    fun startCountdown(timeInSeconds: Int) {
        val scheduler: BukkitScheduler = Bukkit.getServer().getScheduler()
        val taskId: Int = scheduler.scheduleSyncRepeatingTask(plugin, object : Runnable {
            var countdown: Int = timeInSeconds

            override fun run() {
                if (countdown > 0) {
                    broadcastTitle(countdown.toString() + "", "")
                    countdown--
                }
            }
        }, 0L, 20L)

        scheduler.runTaskLater(plugin, Runnable { scheduler.cancelTask(taskId) }, (timeInSeconds + 1) * 20L)
    }
}
