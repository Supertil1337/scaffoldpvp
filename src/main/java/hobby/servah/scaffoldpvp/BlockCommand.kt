package hobby.servah.scaffoldpvp

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class BlockCommand : CommandExecutor {
    //block [block_name]      Zum customizen des Scaffold Blocks

    //NEEEEEEEE, lass lieber menü mit ausgewählten blöcken machen
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        return false;
    }
}