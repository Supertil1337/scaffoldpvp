package hobby.servah.scaffoldpvp


//NOT USED


/*class PvPCommand(val plugin: Scaffoldpvp) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(Bukkit.getOnlinePlayers().size > 2){
            sender.sendMessage("Im Moment können nicht mehr als zwei Spieler kämpfen" + NamedTextColor.DARK_GRAY)
            return false
        }
        if(args?.get(0).equals("start")){
            PhaseManager.changePhase(LobbyPhase(plugin), plugin)
            sender.sendMessage(Component.text("PvP Started!").color(NamedTextColor.GREEN))
        }
        else if(args?.get(0).equals("stop")){
            PhaseManager.changePhase(Disabled(plugin), plugin)
            sender.sendMessage("PvP Stopped!" + NamedTextColor.GREEN)
        }
        else if(args?.get(0).equals("spawn")){
            if(sender is Player){
                val loc: Location? = sender.player?.location
                val arg = args?.get(1)
                if(arg.equals("1")){
                    //test
                    //sender.sendMessage(loc.toString())

                    Utils.spawn1 = loc
                    //logging
                    //sender.sendMessage(Utils.spawn1.toString())
                }
                else if(arg.equals("2")){
                    Utils.spawn2 = loc
                }
                else{
                    sender.sendMessage(Component.text("Kollege was hast du denn da geschrieben?? nur 1 und 2"
                            + NamedTextColor.DARK_PURPLE))
                }
            }
            else{
                sender.sendMessage("Nur Spieler können diesen Command ausführen!" + NamedTextColor.GOLD)
                return false
            }

        }
        else sender.sendMessage(Component.text("Tf bist du aufn kopf geflogen?? so geht das nich" + NamedTextColor.RED))
        return false
    }

}

 */