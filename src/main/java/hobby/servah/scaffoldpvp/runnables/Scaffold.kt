package hobby.servah.scaffoldpvp.runnables

import hobby.servah.scaffoldpvp.Scaffoldpvp
import hobby.servah.scaffoldpvp.Utils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*
import kotlin.collections.HashMap

class Scaffold(private val utils: Utils, private val plugin: Scaffoldpvp, private val blockTypes: HashMap<UUID, Material>) : BukkitRunnable() {
    override fun run() {
        for(player in utils.scaffold.keys){
            val p = Bukkit.getPlayer(player) ?: return
            if(utils.scaffold.getValue(p.uniqueId)){
                val loc : Location = p.location.subtract(Vector(0, 1, 0))
                if(loc.block.type == Material.AIR){
                    loc.block.type = blockTypes[p.uniqueId]!!
                    //nur im FFA Gamemode
                    if(utils.world.name != "FFAScaffoldPvP") return
                    object : BukkitRunnable(){
                        override fun run() {
                            loc.block.type = Material.AIR
                        }

                    }.runTaskLater(plugin, 100L)

                }
            }

        }
    }
}