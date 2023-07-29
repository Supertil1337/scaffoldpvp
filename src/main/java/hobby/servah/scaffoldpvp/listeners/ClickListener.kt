package hobby.servah.scaffoldpvp.listeners

import hobby.servah.scaffoldpvp.runnables.Scaffold
import hobby.servah.scaffoldpvp.Scaffoldpvp
import hobby.servah.scaffoldpvp.Utils
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitTask
import java.util.UUID


class ClickListener(plugin: Scaffoldpvp, private val utils: Utils, private val world: World) : Listener {

    var blockTypes = HashMap<UUID, Material>()
    var task : BukkitTask? = Scaffold(utils, plugin, blockTypes).runTaskTimer(plugin, 1, 1)

    //wird von PvPPhase gechanged, wenn ein Spieler Schaden nimmt, weil dann kurz Scaffold disabled wird
    var allowed = HashMap<UUID, Boolean>()
    init {
        for(p in world.players){
            allowed[p.uniqueId] = true
        }
    }

    @EventHandler
    fun onClick(e: PlayerInteractEvent){
        if(e.player.world != world) return
        if(e.action.isRightClick && allowed[e.player.uniqueId] == true){
            if(e.item?.type != Material.STICK) return
            utils.scaffold[e.player.uniqueId] = !(utils.scaffold[e.player.uniqueId])!!
            e.isCancelled = true
        }

    }

}