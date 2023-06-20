package hobby.servah.scaffoldpvp

import hobby.servah.scaffoldpvp.phase.Utils
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import java.util.UUID


class ClickListener(private val plugin: Scaffoldpvp, private val utils: Utils, private val world: World) : Listener {

    var task : BukkitTask? = Scaffold(utils).runTaskTimer(plugin, 1, 1)

    //wird von PvPPhase gechanged, wenn ein Spieler Schaden weil dann kurz Scaffold disabled wird
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