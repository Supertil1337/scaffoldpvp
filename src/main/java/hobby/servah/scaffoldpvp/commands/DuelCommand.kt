package hobby.servah.scaffoldpvp.commands

import hobby.servah.scaffoldpvp.Scaffoldpvp
import hobby.servah.scaffoldpvp.Utils
import hobby.servah.scaffoldpvp.phase.PhaseManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap


class DuelCommand(val plugin: Scaffoldpvp) : CommandExecutor {

    //wenn ein spieler einem anderem spieler ein anfrage schickt, wird dieser dem anfragenden spieler zugewiesen
    //sonst null oder es gibt ihn nicht
    var duelRequests  = HashMap<UUID, UUID?>()
    companion object{
        var phaseManagers = HashMap<String, PhaseManager?>()
    }


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return false

        if(sender.world.name != "world"){
            sender.sendMessage(Component.text("Du kannst keine Anfrage schicken, während du in einem Duel bist!").color(NamedTextColor.RED))
            return false
        }

        if (args?.size != 1) {
            sender.sendMessage(Component.text("FREUNDCHEN, so geht das aber nicht!!!").color(NamedTextColor.RED))
            return false
        }
        val p: Player? = Bukkit.getPlayer(args[0])
        if (p == null) {
            sender.sendMessage(Component.text("den gibts nich, pass ma beim schreiben besser auf").color(NamedTextColor.RED))
            return false
        }

        if(duelRequests[p.uniqueId] == sender.uniqueId){
            p.sendMessage(Component.text(sender.name + " hat deine Anfrage angenommen!").color(NamedTextColor.BLUE))
            duelRequests[p.uniqueId] = null
            Utils.startMatch(sender, p, plugin)

            return false
        }


        duelRequests[sender.uniqueId] = p.uniqueId

        p.sendMessage(Component.text(sender.name + " hat dich herausgefordert!").color(NamedTextColor.BLUE))
        p.sendMessage(Component.text("Du kannst die Einladung mit /duel " + sender.name + " annehmen!").color(NamedTextColor.BLUE))
        sender.sendMessage(Component.text("Du hast " + p.name + " erfolgreich herausgefordert!").color(NamedTextColor.GREEN))

        return false
    }



}