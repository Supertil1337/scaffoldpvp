package hobby.servah.scaffoldpvp.phase

import hobby.servah.scaffoldpvp.Scaffoldpvp
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.scheduler.BukkitRunnable

class CountdownRunnable(private val plugin : Scaffoldpvp, private val world : World, private val utils: Utils, private val phaseManager: PhaseManager) : BukkitRunnable() {
    private var seconds = 5
    override fun run() {
        if(seconds <= 0){
            for (p in Bukkit.getOnlinePlayers()) p.showTitle(Title.title(Component.text("FIGHT").color(NamedTextColor.RED),
                Component.text("Eliminate your opponent!").color(NamedTextColor.BLUE)))
            phaseManager.changePhase(PvPPhase(plugin, world, utils, phaseManager), plugin)
            this.cancel()
            return
        }
        for(p in Bukkit.getOnlinePlayers()){
            p.showTitle(Title.title(Component.text("GET READY").color(NamedTextColor.RED),
                Component.text("$seconds seconds remain").color(NamedTextColor.BLUE)))
        }
        seconds--
    }

}