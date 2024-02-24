package com.cpbadarau.minecraft

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Team
import java.util.*
import java.util.function.Consumer

class TeamManager {
    fun getTeams(): Map<String, Team?> {
        return teams
    }

    private val teams: MutableMap<String, Team?> = HashMap()
    private val teamNames =
        arrayOf("Kazajistán", "Uzbekistán", "Kirguistán", "Pakistán", "Tayikistán", "Turkmenistán", "Perú")

    private var areTeamsAssigned = false

    init {
        resetScoreboardObjective()
    }

    fun createTeam(name: String) {
        Bukkit.getLogger().info("Creating team $name")
        if (teams.containsKey(name)) {
            deleteTeam(name)
        }
        val team = Objects.requireNonNull(Bukkit.getScoreboardManager())!!.mainScoreboard.registerNewTeam(name)
        teams[name] = team
    }

    fun addPlayerToTeam(playerName: String?, teamName: String) {
        val team = teams[teamName]
        team?.addEntry(playerName!!)
    }

    fun removePlayerFromTeam(playerName: String?) {
        // find the team the player is in and remove them
        val team = getTeamOfPlayer(playerName)
        team?.removeEntry(playerName!!)
    }

    fun deleteTeam(teamName: String) {
        val team = teams[teamName]
        if (team != null) {
            team.unregister()
            teams.remove(teamName)
        }
    }

    fun getTeam(teamName: String): Team? {
        return teams[teamName]
    }

    fun getTeamOfPlayer(playerName: String?): Team? {
        for (team in teams.values) {
            if (team!!.hasEntry(playerName!!)) {
                return team
            }
        }
        return null
    }

    fun createScoreboardObjective() {
        resetScoreboardObjective()
        Arrays.stream(teamNames).forEach { name: String -> this.createTeam(name) }
        val scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard
        val teamsObjective = scoreboard.registerNewObjective("teams", "dummy", "Teams")
        // add all teams to the scoreboard
        for (team in teams.values) {
            teamsObjective.getScore(team!!.name).score = team.entries.size
            team.prefix = team.name + " - "
        }
        teamsObjective.displaySlot = DisplaySlot.SIDEBAR
        // set the scoreboard for all players
        for (player in Bukkit.getOnlinePlayers()) {
            player.scoreboard = scoreboard
        }
    }

    fun resetScoreboardObjective() {
        // remove current team list from the server
        teams.clear()
        Bukkit.getScoreboardManager()!!.mainScoreboard.teams.forEach(Consumer { obj: Team -> obj.unregister() })
        val scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard
        scoreboard.objectives.forEach(Consumer { obj: Objective -> obj.unregister() })
        for (player in Bukkit.getOnlinePlayers()) {
            player.scoreboard = scoreboard
        }
        Bukkit.getScoreboardManager()!!.mainScoreboard.clearSlot(DisplaySlot.SIDEBAR)
        Bukkit.getScoreboardManager()!!.mainScoreboard.clearSlot(DisplaySlot.BELOW_NAME)
        Bukkit.getScoreboardManager()!!.mainScoreboard.clearSlot(DisplaySlot.PLAYER_LIST)
        areTeamsAssigned = false
    }

    // update the scoreboard with the current team sizes
    fun updateScoreboardObjective() {
        val teamsObjective = Bukkit.getScoreboardManager()!!.mainScoreboard.getObjective("teams")
        for (team in teams.values) {
            teamsObjective!!.getScore(team!!.name).score = team.entries.size
        }
    }

    fun assignPlayersToTeams() {
        // get all online players and shuffle them
        val players: List<Player?> = ArrayList(Bukkit.getOnlinePlayers())
        Collections.shuffle(players)
        // also shuffle the team list
        val teamList: List<Team?> = ArrayList(teams.values)
        Collections.shuffle(teamList)
        // assign each player to a team
        var teamIndex = 0
        for (player in players) {
            val team = teamList[teamIndex]
            team!!.addEntry(player!!.name)
            teamIndex++
            if (teamIndex >= teams.size) {
                teamIndex = 0
            }
        }
        updateScoreboardObjective()
        areTeamsAssigned = true
    }

    fun resetTeams() {
        // remove all players from the teams
        for (team in teams.values) {
            team!!.entries.clear()
        }
        areTeamsAssigned = false
        // remove all teams from the server
        for (team in teams.values) {
            team!!.unregister()
        }
    }

    fun areTeamsAssigned(): Boolean {
        return areTeamsAssigned
    }

    fun displayAllTeamsInChat() {
        Bukkit.broadcastMessage("Teams:")
        for (team in teams.values) {
            if (!team!!.entries.isEmpty()) Bukkit.broadcastMessage(team.name + ": " + team.entries)
        }
    }

    fun arePlayersInTheSameTeam(name1: String?, name2: String?): Boolean {
        val team1 = getTeamOfPlayer(name1)
        val team2 = getTeamOfPlayer(name2)
        return team1 != null && team2 != null && team1 == team2
    }

    fun getPlayerOfTeam(team: Team): Player? {
        Bukkit.getLogger().info("all teams $teams")
        for (t in teams.values) {
            if (t == team) {
                Bukkit.getLogger().info("team $t")
                for (player in t.entries) {
                    Bukkit.getLogger().info("player $player")
                    return Bukkit.getPlayer(player)
                }
            }
        }
        return null
    }
}
