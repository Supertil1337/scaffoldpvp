package hobby.servah.scaffoldpvp

import hobby.servah.scaffoldpvp.phase.Utils
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask


class ClickListener(private val plugin: Scaffoldpvp, private val utils: Utils, private val world: World) : Listener {

    var task : BukkitTask? = Scaffold(utils).runTaskTimer(plugin, 1, 1)

    @EventHandler
    fun onClick(e: PlayerInteractEvent){
        if(e.player.world != world) return
        if(e.action.isRightClick){
            if(e.item != ItemStack(Material.STICK)) return
            utils.scaffold[e.player.uniqueId] = !(utils.scaffold[e.player.uniqueId])!!
            e.isCancelled = true
        }

    }

}