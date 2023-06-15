package hobby.servah.scaffoldpvp

import hobby.servah.scaffoldpvp.phase.PhaseManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.WorldCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
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
            //ab in die runde (von JoinQueueCommand gecopiet)
            try {
                val sourceDirectory = File("ScaffoldPvP")
                val destinationDirectory = File("Duel")
                FileUtils.copyDirectory(sourceDirectory, destinationDirectory)
            }
            catch (e: IOException){
                e.printStackTrace();
            }


            val newWorld = Bukkit.createWorld(WorldCreator("Duel"))
            if(newWorld == null){
                sender.sendMessage("joo dat is null");
                return false
            }
            //val spawnLocation: Location = newWorld.spawnLocation;
            //sender.teleport(spawnLocation)
            val players = arrayOf(p, sender)

            //PhaseManager übernimmt
            phaseManagers[newWorld.name] = PhaseManager(plugin, newWorld, players)


            return false
        }


        duelRequests[sender.uniqueId] = p.uniqueId

        p.sendMessage(Component.text(sender.name + " hat dich herausgefordert!").color(NamedTextColor.BLUE))
        p.sendMessage(Component.text("Du kannst die Einladung mit /duel " + sender.name + " annehmen!").color(NamedTextColor.BLUE))
        sender.sendMessage(Component.text("Du hast " + p.name + " erfolgreich herausgefordert!").color(NamedTextColor.GREEN))

        return false
    }



}