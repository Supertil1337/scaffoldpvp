package hobby.servah.scaffoldpvp

import hobby.servah.scaffoldpvp.commands.DuelCommand
import hobby.servah.scaffoldpvp.commands.LeaveCommand
import hobby.servah.scaffoldpvp.listeners.LobbyListener
import hobby.servah.scaffoldpvp.listeners.Trololol
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin

class Scaffoldpvp : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        logger.info("Started Scaffold")
        val pluginManager : PluginManager = Bukkit.getPluginManager()
        getCommand("leave")?.setExecutor(LeaveCommand())
        getCommand("duel")?.setExecutor(DuelCommand(this))
        //TODO Tab complete einbauen oder brauch ich nicht?
        //getCommand("duel")?.tabCompleter = TabComplete()

        pluginManager.registerEvents(LobbyListener(this), this)

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

    }

}