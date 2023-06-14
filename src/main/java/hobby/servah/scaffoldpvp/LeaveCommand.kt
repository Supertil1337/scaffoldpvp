package hobby.servah.scaffoldpvp

import hobby.servah.scaffoldpvp.phase.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

class LeaveCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(sender !is Player) return false
        val world: World = sender.world
        Utils.playerLeaveDuelWorld(world, sender)
        return false;
    }
}