package hobby.servah.scaffoldpvp

import hobby.servah.scaffoldpvp.commands.DuelCommand
import hobby.servah.scaffoldpvp.commands.LeaveCommand
import hobby.servah.scaffoldpvp.listeners.FFA
import hobby.servah.scaffoldpvp.listeners.LobbyListener
import hobby.servah.scaffoldpvp.listeners.Trololol
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.Exception

class Scaffoldpvp : JavaPlugin() {
    var ffaworld: World? = null
    public var ffa: FFA? = null
    override fun onEnable() {
        // Plugin startup logic
        logger.info("Started Scaffold")
        val pluginManager : PluginManager = Bukkit.getPluginManager()
        getCommand("leave")?.setExecutor(LeaveCommand())
        getCommand("duel")?.setExecutor(DuelCommand(this))
        //TODO Tab complete einbauen oder brauch ich nicht?
        //getCommand("duel")?.tabCompleter = TabComplete()

        pluginManager.registerEvents(LobbyListener(this), this)
        try {
            FileUtils.copyDirectory(File("ScaffoldPvP"), File("FFAScaffoldPvP"))
        }
        catch (e: Exception){
            logger.warning("FFA World konnte nicht erstellt werden!")
        }
        val exists = File("FFAScaffoldPvP").exists()
        if(exists) {
            ffaworld = Bukkit.createWorld(WorldCreator("FFAScaffoldPvP"))
            if (ffaworld != null) {
                ffa = FFA(ffaworld!!, this)
                pluginManager.registerEvents(ffa!!, this)
            }
            else logger.warning("FFA World konnte nicht geladen werden!")
        }
        else logger.warning("FFA World konnte nicht gefunden werden!")

        //troll
        pluginManager.registerEvents(Trololol(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info("Teleporting all players to the lobby and removing the duel worlds")
        for(w in Bukkit.getWorlds()){
            if(w.name.startsWith("Duel")) {
                for (p in w.players) Utils.playerLeaveDuelWorld(w, p)
            }
        }
        if(ffaworld != null){
            for(p in ffaworld!!.players){
                p.inventory.clear()
                p.foodLevel = 20
                p.health = 20.0
                p.gameMode = GameMode.ADVENTURE
                val loc: Location = Bukkit.getWorld("world")!!.spawnLocation
                p.teleport(loc)
                Utils.lobbySetup(p)
            }
            Bukkit.unloadWorld(ffaworld!!, false)
            FileUtils.deleteDirectory(File(ffaworld!!.name))
            logger.info("Deleted FFA World!")
        }
        else logger.warning("FFA World konnte nicht gefunden werden!")

    }

}