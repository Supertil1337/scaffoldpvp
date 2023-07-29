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
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.Exception

class Scaffoldpvp : JavaPlugin() {
    var ffaworld: World? = null
    var ffa: FFA? = null
    companion object{
        const val duelLobbyName = "world"
    }

    //All the possible blocks mapped to a number
    val blocks = mapOf(0 to Material.DIAMOND_BLOCK, 1 to Material.STONE_BRICKS, 2 to Material.CHISELED_STONE_BRICKS, 3 to Material.PURPUR_BLOCK,
        4 to Material.PRISMARINE, 5 to Material.DARK_PRISMARINE, 6 to Material.GILDED_BLACKSTONE, 7 to Material.RED_NETHER_BRICKS, 8 to Material.BRICKS,
        9 to Material.BONE_BLOCK, 10 to Material.BLUE_ICE, 11 to Material.END_STONE_BRICKS, 12 to Material.IRON_BLOCK, 13 to Material.COAL_BLOCK,
        14 to Material.QUARTZ_BLOCK)
    override fun onEnable() {

        // Plugin startup logic
        logger.info("Started Scaffold")

        //Register Commands
        val pluginManager : PluginManager = Bukkit.getPluginManager()
        getCommand("leave")?.setExecutor(LeaveCommand(this))
        getCommand("duel")?.setExecutor(DuelCommand(this))
        //Register events
        pluginManager.registerEvents(LobbyListener(this), this)

        //Create FFA World
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

        //Closing all duel worlds
        logger.info("Teleporting all players to the lobby and removing the duel worlds")
        for(w in Bukkit.getWorlds()){
            if(w.name.startsWith("Duel")) {
                for (p in w.players) Utils.playerLeaveDuelWorld(w, p, this)
            }
        }
        //Closing FFA World
        if(ffaworld != null){
            for(p in ffaworld!!.players){
                val loc: Location = Bukkit.getWorld(duelLobbyName)!!.spawnLocation
                p.teleport(loc)
                Utils.lobbySetup(p, this)
            }
            Bukkit.unloadWorld(ffaworld!!, false)
            FileUtils.deleteDirectory(File(ffaworld!!.name))
            logger.info("Deleted FFA World!")
        }
        else logger.warning("FFA World konnte nicht gefunden werden!")

    }

}