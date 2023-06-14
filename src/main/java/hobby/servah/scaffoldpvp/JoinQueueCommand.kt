package hobby.servah.scaffoldpvp

import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException


class JoinQueueCommand : CommandExecutor{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        sender.sendMessage(File("ScaffoldPvP").path)
        if(sender !is Player) return false
        try {
            val sourceDirectory = File("ScaffoldPvP")
            val destinationDirectory = File("Queue") //destination path
            FileUtils.copyDirectory(sourceDirectory, destinationDirectory)
        }
        catch (e: IOException){
            e.printStackTrace();
        }

        /*val worldCreator = WorldCreator("myNewWorld");
        worldCreator.createWorld();
        val newWorld: World? = Bukkit.getWorld("myNewWorld");
        assert(newWorld != null)

         */
        //val newWorld = Bukkit.getWorld("Queue")
        val newWorld = Bukkit.createWorld(WorldCreator("Queue"))
        if(newWorld == null){
            sender.sendMessage("joo dat is null");
            return false
        }
        val spawnLocation: Location = newWorld.spawnLocation;
        sender.teleport(spawnLocation);
        return false
    }

}