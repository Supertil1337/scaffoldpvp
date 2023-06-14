package hobby.servah.scaffoldpvp.phase

import hobby.servah.scaffoldpvp.Scaffoldpvp
import org.bukkit.Bukkit

class Disabled(plugin: Scaffoldpvp?) : Phase(plugin) {
    override fun disable() {

    }

    override fun getNextPhase() {

    }
    init {
        for(p in Bukkit.getOnlinePlayers()){
            p.inventory.clear()
            p.sendMessage("Cleared inventory")
        }
    }
}