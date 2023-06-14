package hobby.servah.scaffoldpvp.phase

import hobby.servah.scaffoldpvp.Scaffoldpvp
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.scheduler.BukkitRunnable


class StartingPhase(plugin : Scaffoldpvp?, private val world : World, private val utils: Utils, private val phaseManager: PhaseManager) : Phase(plugin){
    //private var task: BukkitTask
    init {
        utils.firstSpawn = true
        for (p in Bukkit.getOnlinePlayers()) utils.setupPlayer(plugin!!, p)
        //REMOVE BECAUSE wer will das
        //Bukkit.broadcast(Component.text("Initializing Starting Phase!").color(NamedTextColor.BLUE))
        //task = Bukkit.getScheduler().runTaskTimer(plugin, CountdownRunnable(plugin), 0, 20)
        //funktioniert nicht so richtig, möglicherweise nur bei respawn???
        //task = CountdownRunnable(plugin!!, world, utils, phaseManager).runTaskTimer(plugin, 0, 20)
        //nur test
        /*val task2: BukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable(){
            Bukkit.broadcast(Component.text("moin"))
        }, 0L, 20L)

         */
        /*val task3: BukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable() {
            var seconds = 5
            Bukkit.broadcast(Component.text("running"))
            if(seconds <= 0){
                for (p in Bukkit.getOnlinePlayers()) p.showTitle(
                    Title.title(Component.text("FIGHT", NamedTextColor.RED),
                    Component.text("Eliminate your opponent!", NamedTextColor.BLUE)))
                phaseManager.changePhase(PvPPhase(plugin, world, utils, phaseManager), plugin)
                //this.cancel()


                return
            }
            for(p in Bukkit.getOnlinePlayers()){
                p.showTitle(
                    Title.title(Component.text("GET READY", NamedTextColor.RED),
                    Component.text("$seconds seconds remain", NamedTextColor.BLUE)))
            }
            seconds--
        }, 0L, 20L)

         */
        object : BukkitRunnable() {
            private var seconds = 5
            override fun run() {
                //logging
                //Bukkit.broadcast(Component.text("running"))
                if(seconds <= 0){
                    for (p in Bukkit.getOnlinePlayers()) p.showTitle(
                        Title.title(Component.text("FIGHT").color(NamedTextColor.RED),
                            Component.text("Eliminate your opponent!").color(NamedTextColor.BLUE)))
                    phaseManager.changePhase(PvPPhase(plugin, world, utils, phaseManager), plugin!!)
                    this.cancel()

                    return
                }
                for(p in Bukkit.getOnlinePlayers()){
                    p.showTitle(
                        Title.title(Component.text("GET READY").color(NamedTextColor.RED),
                            Component.text("$seconds seconds remain").color(NamedTextColor.BLUE)))
                }
                seconds--
            }
        }.runTaskTimer(plugin!!, 0L, 20L)


        //Bukkit.broadcast(Component.text(plugin.name))
        //Bukkit.broadcast(Component.text("running2"))
    }

    override fun disable() {
        //fckn unnötige funktionen lul                            sind jetzt auch auskommentiert lol
        /*if(!task.isCancelled){
            task.cancel()
        }

         */
    }

    override fun getNextPhase() {
        //glaub nicht dass ich das brauche... mal schauen
    }


    //vielleicht woandershin verschieben, wird ja auch noch in PVP Phase benutzt(war für setupPlayer()), ist erledigt


    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        if(e.player.world != world) return
        utils.setupPlayer(plugin!!, e.player)
    }

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        if(e.entity.world != world) return
        e.isCancelled = true
    }

    @EventHandler
    fun onPickUp(e : PlayerAttemptPickupItemEvent) {
        if(e.player.world != world) return
        //if(e.player.hasPermission("pvp.doshit")) return
        e.item.remove()
        e.isCancelled = true
    }

    @EventHandler
    fun onBlockBreak(e : BlockBreakEvent) {
        if(e.player.world != world) return
        //if(e.player.hasPermission("pvp.doshit")) return
        e.isCancelled = true
    }

    @EventHandler
    fun onHunger(e: FoodLevelChangeEvent) {
        if(e.entity.world != world) return
        e.isCancelled = true
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if(e.player.world != world) return
        //if(e.player.hasPermission("pvp.doshit")) return
        e.isCancelled = true
    }

    @EventHandler
    fun onItemDrop(e: PlayerDropItemEvent) {
        if(e.player.world != world) return
        //if(e.player.hasPermission("pvp.doshit")) return
        e.isCancelled = true
    }

    @EventHandler
    fun onItemMoveBetweenInvs(e: InventoryMoveItemEvent) {
        val holder: InventoryHolder? = e.initiator.holder
        if(holder !is Player) return                    //TODO checken ob das ganze so wirklich funktioniert
        if(holder.world != world) return
        e.isCancelled = true

    }

    @EventHandler
    fun onItemClick(e: InventoryClickEvent) {
        if(e.whoClicked.world != world) return
        if(e.cursor === null) return
        e.isCancelled = true
    }



    @EventHandler
    fun onMove(e: PlayerMoveEvent){
        if(e.player.world != world) return
        //if(e.player.hasPermission("pvp.doshit")) return
        e.isCancelled = true
    }
}