package com.cpbadarau.minecraft

import com.cpbadarau.minecraft.commands.*
import com.cpbadarau.minecraft.listeners.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.java.JavaPlugin

class CDLF_UHC : JavaPlugin() {
    private lateinit var gameManager: com.cpbadarau.minecraft.GameManager
    private lateinit var teamManager: com.cpbadarau.minecraft.TeamManager
    private lateinit var playerManager: com.cpbadarau.minecraft.PlayerManager

    override fun onEnable() {
        this.teamManager = com.cpbadarau.minecraft.TeamManager()
        this.gameManager = com.cpbadarau.minecraft.GameManager(this, this.teamManager)
        this.playerManager = com.cpbadarau.minecraft.PlayerManager(this.gameManager, this.teamManager)

        assignCommands()
        registerListeners()
        createRecipes()
    }

    private fun assignCommands() {
        registerCommand(StartGameCommand(gameManager), "start_game")
        registerCommand(AssignTeamsCommand(teamManager), "assign_teams")
        registerCommand(CreateScoreboardObjectiveTestCommand(teamManager), "create_scoreboard_objective")
        registerCommand(ResetScoreboardObjectiveTestCommand(teamManager), "reset_scoreboard_objective")
        registerCommand(InitiateLobbyCommand(gameManager), "initialize_lobby")
        registerCommand(ChangeWorldBorderCommand(gameManager), "change_world_border")
        registerCommand(ReassignTeamsCommand(gameManager!!), "reassign_teams")
    }

    private fun registerListeners() {
        registerListener(PlayerJoinListener(playerManager))
        registerListener(PlayerDeathListener(gameManager))
        registerListener(EntityDamageEventListener())
        registerListener(PlayerInteractEventListener())
        registerListener(EntityDamageByEntityEventListener())
        registerListener(InventoryClickEventListener())
    }

    private fun registerCommand(commandExecutor: CommandExecutor, commandName: String) {
        getCommand(commandName)!!.setExecutor(commandExecutor)
    }

    private fun registerListener(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    override fun onDisable() {
        teamManager.resetTeams()
        teamManager.resetScoreboardObjective()
    }

    private fun createRecipes() {
        createNotchAppleRecipe()
        createTotem()
    }

    private fun createNotchAppleRecipe() {
        val notchApple: ItemStack = ItemStack(Material.ENCHANTED_GOLDEN_APPLE)

        val key: NamespacedKey = NamespacedKey(this, "notch_apple")
        val recipe: ShapelessRecipe = ShapelessRecipe(key, notchApple)

        recipe.addIngredient(8, Material.GOLD_BLOCK)
        recipe.addIngredient(1, Material.PLAYER_HEAD)

        Bukkit.addRecipe(recipe)
    }

    private fun createTotem() {
        val totem: ItemStack = ItemStack(Material.TOTEM_OF_UNDYING)

        val key: NamespacedKey = NamespacedKey(this, "custom_totem")
        val recipe: ShapelessRecipe = ShapelessRecipe(key, totem)

        recipe.addIngredient(3, Material.PLAYER_HEAD)
        recipe.addIngredient(6, Material.GOLD_BLOCK)

        Bukkit.addRecipe(recipe)
    }
}