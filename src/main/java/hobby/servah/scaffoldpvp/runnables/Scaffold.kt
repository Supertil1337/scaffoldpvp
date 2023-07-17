package hobby.servah.scaffoldpvp.runnables

import hobby.servah.scaffoldpvp.Scaffoldpvp
import hobby.servah.scaffoldpvp.Utils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class Scaffold(private val utils: Utils, private val plugin: Scaffoldpvp) : BukkitRunnable() {
    override fun run() {
        for(player in Bukkit.getOnlinePlayers()){
            if(utils.scaffold.keys.contains(player.uniqueId) && utils.scaffold.getValue(player.uniqueId)){
                val loc : Location = player.location.subtract(Vector(0, 1, 0))
                if(loc.block.type == Material.AIR){
                    loc.block.type = Material.DIAMOND_BLOCK
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