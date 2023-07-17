package hobby.servah.scaffoldpvp.commands

import hobby.servah.scaffoldpvp.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LeaveCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(sender !is Player) return false
        val world: World = sender.world

        if(DuelCommand.phaseManagers[world.name]?.currentPhase?.type == "End"){
            sender.sendMessage(Component.text("Bitte benutze das Barrier Item in deiner Hotbar!").color(NamedTextColor.BLUE))
            return false
        }
        if(sender.world.name == "FFAScaffoldPvP"){
            Utils.leave(sender)
            return true
        }
        val list = world.players
        list.remove(sender)
        for(p in list){
            p.showTitle(
                Title.title(
                    Component.text("You won").color(NamedTextColor.GREEN),
                    Component.text("Your opponent gave up!").color(NamedTextColor.BLUE)
                )
            )
        }
        for (p in world.players) Utils.playerLeaveDuelWorld(world, p)
        return false;
    }
}