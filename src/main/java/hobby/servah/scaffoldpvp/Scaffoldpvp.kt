package hobby.servah.scaffoldpvp

import hobby.servah.scaffoldpvp.phase.Phase
import hobby.servah.scaffoldpvp.phase.PhaseManager
import hobby.servah.scaffoldpvp.phase.TabComplete
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

class Scaffoldpvp : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        //Scaffold().runTaskTimer(this, 10, 1)
        //Scaffold().cancel()
        logger.info("Started Scaffold")
        val pluginManager : PluginManager = Bukkit.getPluginManager()
        //pluginManager.registerEvents(ClickListener(this, ), this);
        //val phaseManager = PhaseManager(this)
        //getCommand("pvp")?.setExecutor(PvPCommand(this))
        //getCommand("joinqueue")?.setExecutor(JoinQueueCommand())
        getCommand("leave")?.setExecutor(LeaveCommand())
        getCommand("duel")?.setExecutor(DuelCommand(this))
        //TODO Tab complete einbauen oder brauch ich nicht?
        //getCommand("duel")?.tabCompleter = TabComplete()


        //troll
        pluginManager.registerEvents(Trololol(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
    fun getInstance(){

    }
}