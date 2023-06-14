package hobby.servah.scaffoldpvp.phase

import hobby.servah.scaffoldpvp.ClickListener
import hobby.servah.scaffoldpvp.Scaffoldpvp
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager

abstract class Phase(val plugin: Scaffoldpvp?) : Listener {
    abstract fun disable()
    abstract fun getNextPhase()
}

class PhaseManager(val plugin: Scaffoldpvp, private val duelWorld: World){

    private val utils: Utils = Utils(duelWorld)
    var currentPhase: Phase? = StartingPhase(plugin, duelWorld, utils, this)
    var clickListener : ClickListener? = ClickListener(plugin, utils, duelWorld)
    private val pluginManager: PluginManager = Bukkit.getPluginManager()
    val world: World

    init {
        changePhase(currentPhase!!, plugin)
        world = duelWorld
        pluginManager.registerEvents(clickListener!!, plugin)
    }

    /*companion object{
        private var currentPhase: Phase = Disabled(null)    //so lange random shit ausprobiert. das geringste übel ist jetzt plugin nicht verwenden zu können und stattdessem Scaffoldpvp()
        private val pluginManager: PluginManager = Bukkit.getPluginManager()
        fun changePhase(newPhase: Phase, plugin: Scaffoldpvp) {
            currentPhase.disable()
            HandlerList.unregisterAll(currentPhase)

            currentPhase = newPhase
            pluginManager.registerEvents(currentPhase, plugin)
            //logging
            //Bukkit.broadcast(Component.text(HandlerList.getRegisteredListeners(plugin).toString()))
            //test
            //Bukkit.broadcast(Component.text(plugin.name))
        }
    }

     */

    fun changePhase(newPhase: Phase, plugin: Scaffoldpvp) {
        currentPhase?.disable()
        HandlerList.unregisterAll(currentPhase!!)

        currentPhase = newPhase
        pluginManager.registerEvents(currentPhase!!, plugin)


        //logging
        //Bukkit.broadcast(Component.text(HandlerList.getRegisteredListeners(plugin).toString()))
        //test
        //Bukkit.broadcast(Component.text(plugin.name))
    }
}