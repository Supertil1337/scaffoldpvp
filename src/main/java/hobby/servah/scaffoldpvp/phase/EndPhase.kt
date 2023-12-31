package hobby.servah.scaffoldpvp.phase

import hobby.servah.scaffoldpvp.commands.DuelCommand
import hobby.servah.scaffoldpvp.Scaffoldpvp
import hobby.servah.scaffoldpvp.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.Material
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
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class EndPhase(private val world: World, private val utils: Utils, plugin: Scaffoldpvp?) : Phase(plugin) {
    //wird auf true gesetzt sobald einer nochmal spielen will und wenn es schon true ist wird halt neue runde gestartet
    //private var playAgain: Boolean = false
    private var playAgain = HashMap<UUID, Boolean>()
    init {
        type = "End"
        for(task in DuelCommand.phaseManagers[world.name]?.tasks!!) {
            if(Bukkit.getServer().scheduler.isCurrentlyRunning(task.taskId) || Bukkit.getServer().scheduler.isQueued(task.taskId)){
                task.cancel()
            }
        }
        for(p in world.players){
            playAgain[p.uniqueId] = false
        }
    }
    override fun disable() {

    }

    override fun getNextPhase() {

    }
    @EventHandler
    fun onClick(e: PlayerInteractEvent){
        if(e.player.world != world) return
        if(e.action.isRightClick){
            val list = world.players
            list.remove(e.player)
            if(e.item?.type == Material.BARRIER){
                //leave
                for(p in list) p.sendMessage(Component.text(e.player.name + " möchte nicht nochmal spielen!").color(NamedTextColor.DARK_GRAY))
                for(p in world.players) Utils.playerLeaveDuelWorld(world, p, plugin!!)
            }
            else if(e.item?.type  == Material.DIAMOND_SWORD){
                if(playAgain[list[0].uniqueId] == true){
                    //nochmal spielen

                    Utils.startMatch(world.players[0], world.players[1], plugin!!)
                    if(world.playerCount != 0) return
                    Bukkit.unloadWorld(world, false)
                    FileUtils.deleteDirectory(File(world.name))
                    DuelCommand.phaseManagers[world.name]?.currentPhase = null
                    DuelCommand.phaseManagers[world.name]?.clickListener?.task?.cancel()
                    DuelCommand.phaseManagers[world.name]?.clickListener?.task = null
                    DuelCommand.phaseManagers[world.name]?.clickListener = null
                    DuelCommand.phaseManagers[world.name] = null
                }

                else if(playAgain[e.player.uniqueId] == false){
                    //Anderem Spieler mitteilen, dass du nochmal spielen möchtest
                    playAgain[e.player.uniqueId] = true
                    for(p in list) p.sendMessage(Component.text(e.player.name + " möchte nochmal spielen!").color(NamedTextColor.BLUE))
                    e.player.sendMessage(Component.text("Dein Gegner muss deine Anfrage annehmen, damit ihr nochmal spielen könnt!").color(NamedTextColor.BLUE))
                }

            }
        }
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
        if(e.block.blockData.material == Material.DIAMOND_BLOCK) return
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
        e.isCancelled = true
    }

    @EventHandler
    fun onItemClick(e: InventoryClickEvent) {
        if(e.whoClicked.world != world) return
        if(e.cursor === null) return
        e.isCancelled = true
    }
    @EventHandler
    fun onDamage(e: EntityDamageEvent){
        val p = e.entity
        if(p !is Player) return
        if(p.world != world) return
        e.isCancelled = true
    }
    @EventHandler
    fun onLeave(e: PlayerQuitEvent){
        val p = e.player
        if(p.world != world) return
        val list = world.players
        for (p1 in list) {
            p.sendMessage(Component.text(e.player.name + " möchte nicht nochmal spielen!").color(NamedTextColor.DARK_GRAY))
            Utils.playerLeaveDuelWorld(world, p1, plugin!!)
        }
    }
}