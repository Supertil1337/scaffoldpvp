package hobby.servah.scaffoldpvp

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.util.StringUtil


class TabComplete : TabCompleter {
    private val COMMANDS : Iterable<String> = arrayOf("minecraft", "spigot", "bukkit", "google").asIterable()


    //create a static array of values
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String> {
        //create new array
        //create new array
        val completions: MutableList<String> = ArrayList()
        //copy matches of first argument from list (ex: if first arg is 'm' will return just 'minecraft')
        //copy matches of first argument from list (ex: if first arg is 'm' will return just 'minecraft')
        StringUtil.copyPartialMatches<MutableList<String>>(args!![0], COMMANDS, completions)
        //sort the list
        //Collections.sort(completions);
        //Edit: sorting the list it's not required anymore
        //sort the list
        //Collections.sort(completions);
        //Edit: sorting the list it's not required anymore
        return completions
    }

}