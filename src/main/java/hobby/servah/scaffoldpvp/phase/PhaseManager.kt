package hobby.servah.scaffoldpvp.phase

import hobby.servah.scaffoldpvp.listeners.ClickListener
import hobby.servah.scaffoldpvp.Scaffoldpvp
import hobby.servah.scaffoldpvp.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.PluginManager
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

abstract class Phase(val plugin: Scaffoldpvp?) : Listener {
    var type: String = ""
    var tasks: List<BukkitTask> = ArrayList()
    abstract fun disable()
    abstract fun getNextPhase()
}

class PhaseManager(val plugin: Scaffoldpvp, duelWorld: World, players: Array<Player>){

    private val utils: Utils = Utils(duelWorld)
    var currentPhase: Phase? = StartingPhase(plugin, duelWorld, utils, this, players)
    var clickListener : ClickListener? = ClickListener(plugin, utils, duelWorld)
    private val pluginManager: PluginManager = Bukkit.getPluginManager()
    val world: World
    var tasks: List<BukkitTask> = ArrayList()

    init {
        //initialize everything
        changePhase(currentPhase!!, plugin)
        world = duelWorld
        pluginManager.registerEvents(clickListener!!, plugin)


        tasks = tasks.plus(object : BukkitRunnable(){
            override fun run() {
                for(p in world.players){
                    p.sendMessage(Component.text("Das Spiel endet in 60 Sekunden!").color(NamedTextColor.RED))
                }
            }
        }.runTaskLater(plugin, 60 * 2 * 20))

        tasks = tasks.plus(object : BukkitRunnable(){
            var counter = 10
            override fun run() {
                if(counter <= 0){
                    for(p in world.players){
                        p.showTitle(
                            Title.title(
                                Component.text("Draw").color(NamedTextColor.YELLOW),
                                Component.text("You ran out of time!").color(NamedTextColor.DARK_PURPLE)
                            )
                        )
                    }
                    //End Match
                    utils.endMatch(world.players, this@PhaseManager, plugin)
                    this.cancel()
                    return
                }
                for(p in world.players){
                    p.sendMessage(Component.text("Das Spiel endet in $counter Sekunden!").color(NamedTextColor.RED))
                }
                counter--
            }

        }.runTaskTimer(plugin, 170 * 20, 20))

        //persistent data "block" zuweisen nach parse durch blocks map
        for(p in players) clickListener!!.blockTypes[p.uniqueId] = plugin.blocks[p.persistentDataContainer.get(NamespacedKey(plugin, "Block"), PersistentDataType.INTEGER)]!!
    }



    fun changePhase(newPhase: Phase, plugin: Scaffoldpvp) {
        currentPhase?.disable()
        HandlerList.unregisterAll(currentPhase!!)

        currentPhase = newPhase
        pluginManager.registerEvents(currentPhase!!, plugin)
    }
}