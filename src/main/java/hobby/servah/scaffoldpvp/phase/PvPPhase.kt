package hobby.servah.scaffoldpvp.phase

import hobby.servah.scaffoldpvp.Scaffoldpvp
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.*


class PvPPhase(plugin: Scaffoldpvp?, private val world: World, private val utils: Utils, private val phaseManager: PhaseManager) : Phase(plugin) {

    private val canShoot = HashMap<UUID, Boolean>()

    init{
        type = "PvP"
        for(p in world.players){
            canShoot[p.uniqueId] = true
        }
        world.worldBorder.setSize(10.0, 120L)
        object : BukkitRunnable() {
            override fun run() {
                for(p in world.players) p.showTitle(Title.title(Component.text("Draw").color(NamedTextColor.YELLOW), Component.text("Time is up!").color(NamedTextColor.BLUE)))
                utils.endMatch(world.players, phaseManager, plugin!!)
            }

        }.runTaskLater(plugin!!, 6000L)
    }
    override fun disable() {
        //TODO("Not yet implemented")
    }

    override fun getNextPhase() {
        //TODO("Not yet implemented")
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
    fun leave(e: PlayerQuitEvent) {
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
    fun onDeath(e: PlayerDeathEvent){
        if(e.player.world != world) return
        val p = e.player
        e.isCancelled = true
        p.health = 20.0

        val list2 = world.players
        list2.remove(p)
        for(p2 in list2){
            p2.showTitle(Title.title(Component.text("You won!").color(NamedTextColor.GREEN), Component.text("Your opponent died!").color(NamedTextColor.BLUE)))
            p2.health = 20.0
        }

        p.showTitle(Title.title(Component.text("You died").color(NamedTextColor.RED), Component.text("You lost the fight!").color(NamedTextColor.BLUE)))

        val list = world.players
        list.add(p)
        utils.endMatch(list, phaseManager, plugin!!)
    }
    @EventHandler
    fun shoot(e: EntityShootBowEvent){
        if(e.entity.world != world) return
        val p = e.entity
        if(p !is Player) return
        if(canShoot[p.uniqueId] == false) {
            e.isCancelled = true
            return
        }
        canShoot[p.uniqueId] = false
        object : BukkitRunnable() {
            private var seconds = 3
            override fun run() {
                if(seconds <= 0){
                    canShoot[p.uniqueId] = true
                    p.level = 0
                    p.exp =  0F
                    this.cancel()
                    return
                }
                p.level = seconds
                p.exp = seconds / 3F
                seconds--
            }
        }.runTaskTimer(plugin!!, 0L, 20L)
    }

    @EventHandler
    fun damage(e: EntityDamageEvent) {
        if(e.entity.world != world) return
        val p = e.entity
        if(p !is Player) return
        if (e.cause == DamageCause.FALL){
            e.damage = e.damage / 2
            return
        }
        if(!phaseManager.clickListener?.allowed?.get(p.uniqueId)!!)
        //Bukkit.broadcast(Component.text("test1"))
        phaseManager.clickListener?.allowed?.set(p.uniqueId, false)
        val scaffold = utils.scaffold[p.uniqueId]
        utils.scaffold[p.uniqueId] = false
        object : BukkitRunnable(){
            override fun run() {
                //Bukkit.broadcast(Component.text("test2"))
                utils.scaffold[p.uniqueId] = scaffold!!
                phaseManager.clickListener?.allowed?.set(p.uniqueId, true)
            }

        }.runTaskLater(plugin!!, 20L)
    }
}