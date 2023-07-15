package hobby.servah.scaffoldpvp

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class Trololol : Listener {
    @EventHandler
    fun trollllll(e: PlayerJoinEvent){
        if(e.player.name == "Bittnin"){
            e.player.showDemoScreen()
            e.player.showElderGuardian()
            e.player.showWinScreen()
        }
    }
}