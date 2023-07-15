package hobby.servah.scaffoldpvp.phase

import hobby.servah.scaffoldpvp.Scaffoldpvp
import hobby.servah.scaffoldpvp.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.InventoryHolder
import org.bukkit.scheduler.BukkitRunnable


class StartingPhase(plugin : Scaffoldpvp?, private val world : World, private val utils: Utils, private val phaseManager: PhaseManager, private val players: Array<Player>) : Phase(plugin){

    init {
        type = "Starting"
        utils.firstSpawn = true
        for (p in players) utils.setupPlayer(plugin!!, p)
        val wb = world.worldBorder
        wb.center = world.spawnLocation
        wb.size = 100.0
        for(x in -50..50){
            for(z in -50..50){
                world.setBlockData(Location(world, x.toDouble(), -20.0, z.toDouble()), Material.BARRIER.createBlockData())
            }
        }
        tasks = tasks.plus(object: BukkitRunnable() {
            private var seconds = 5
            override fun run() {
                if(seconds <= 0){
                    for (p in world.players) p.showTitle(
                        Title.title(Component.text("FIGHT").color(NamedTextColor.RED),
                            Component.text("Eliminate your opponent!").color(NamedTextColor.BLUE)))
                    phaseManager.changePhase(PvPPhase(plugin, world, utils, phaseManager), plugin!!)
                    this.cancel()

                    return
                }
                for(p in world.players){
                    p.showTitle(
                        Title.title(Component.text("GET READY").color(NamedTextColor.RED),
                            Component.text("$seconds seconds remain").color(NamedTextColor.BLUE)))
                }
                seconds--
            }
        }.runTaskTimer(plugin!!, 0L, 20L))
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
        e.item.remove()
        e.isCancelled = true
    }

    @EventHandler
    fun onBlockBreak(e : BlockBreakEvent) {
        if(e.player.world != world) return
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
        e.isCancelled = true
    }

    @EventHandler
    fun onItemDrop(e: PlayerDropItemEvent) {
        if(e.player.world != world) return
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
        e.isCancelled = true
    }
    @EventHandler
    fun leave(e: PlayerQuitEvent){
        if(e.player.world != world) return
        val list = world.players
        for (p in list) {
            p.showTitle(
                Title.title(
                    Component.text("You won!").color(NamedTextColor.GREEN),
                    Component.text("Your opponent gave up!").color(NamedTextColor.BLUE)
                )
            )
            Utils.playerLeaveDuelWorld(world, p)
        }
    }
    @EventHandler
    fun shoot(e: EntityShootBowEvent){
        val p = e.entity
        if(p !is Player) return
        e.isCancelled = true
        p.sendMessage(Component.text("Du kannst erst schießen, wenn das Spiel begonnen hat!").color(NamedTextColor.RED))
    }
}