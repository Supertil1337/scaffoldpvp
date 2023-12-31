package hobby.servah.scaffoldpvp.listeners

import hobby.servah.scaffoldpvp.Scaffoldpvp
import hobby.servah.scaffoldpvp.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.RenderType
import org.bukkit.scoreboard.Score
import org.bukkit.scoreboard.Scoreboard
import java.util.*
import javax.naming.Name
import kotlin.math.floor
import kotlin.math.roundToInt

class FFA(val world: World, val plugin: Scaffoldpvp) : Listener {
    //Contains if the players can shoot or if they can't because they shot less than 3 seconds ago
    private val canShoot = HashMap<UUID, Boolean>()
    val utils: Utils = Utils(world)
    val clickListener: ClickListener = ClickListener(plugin, utils, world)

    init {
        Bukkit.getPluginManager().registerEvents(clickListener, plugin)
        //Setup world boundaries
        world.worldBorder.size = 200.0
        for(x in -100..100){
            for(z in -100..100){
                world.setBlockData(Location(world, x.toDouble(), -10.0, z.toDouble()), Material.BARRIER.createBlockData())
            }
        }

    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        if(e.player.world != world) return
        val p = e.player
        join(p)
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
        e.isDropItems = false
        if(e.block.blockData.material != Material.GRASS_BLOCK && e.block.blockData.material != Material.DIRT) return
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
        val p = e.player
        p.scoreboard = Bukkit.getScoreboardManager().newScoreboard
        Utils.leave(p, plugin)
    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent){
        if(e.player.world != world) return
        val p = e.player
        e.isCancelled = true

        //Stats saven
        val data: PersistentDataContainer = e.player.persistentDataContainer
        data.set(NamespacedKey(plugin, "Deaths"), PersistentDataType.INTEGER,
            data.get(NamespacedKey(plugin, "Deaths"), PersistentDataType.INTEGER)?.plus(1)!!
        )

        //Scufftes Scoreboard Zeug
        val killer = e.entity.killer
        if(killer != null){
            val data2: PersistentDataContainer = killer.persistentDataContainer
            val oldKills = data2.get(NamespacedKey(plugin, "Kills"), PersistentDataType.INTEGER)
            data2.set(NamespacedKey(plugin, "Kills"), PersistentDataType.INTEGER,
                data2.get(NamespacedKey(plugin, "Kills"), PersistentDataType.INTEGER)?.plus(1)!!
            )
            killer.scoreboard.resetScores("Kills: $oldKills")
            val deaths = data2.get(NamespacedKey(plugin, "Deaths"), PersistentDataType.INTEGER)
            killer.scoreboard.resetScores("K/D: ${oldKills!! / deaths!!}")
            val scoreboard = killer.scoreboard
            val kills: Score? = scoreboard.getObjective("Stats")?.getScore("Kills: ${oldKills.plus(1)}")
            kills?.score = 3
            killer.scoreboard.resetScores("K/D: ${oldKills.toFloat().div(deaths.toFloat()).times(100).roundToInt().toFloat() / 100}")
            val kd = scoreboard.getObjective("Stats")?.getScore("K/D: ${oldKills.toFloat().plus(1).div(deaths.toFloat()).times(100).roundToInt().toFloat() / 100}")
            kd?.score = 1
            killer.scoreboard = scoreboard

            killer.health = 20.0
        }
        for(p1 in world.players){
            p1.sendMessage(Component.text("${p.name} died!").color(NamedTextColor.RED))
        }
        p.scoreboard = Bukkit.getScoreboardManager().newScoreboard
        Utils.leave(p, plugin)
    }
    //Shoot bow or prevent it when on cooldown
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
        }.runTaskTimer(plugin, 0L, 20L)
    }
    //reduce fall damage and temporarily disable scaffold
    @EventHandler
    fun damage(e: EntityDamageEvent) {
        if(e.entity.world != world) return
        val p = e.entity
        if(p !is Player) return
        if (e.cause == EntityDamageEvent.DamageCause.FALL){
            if(e.entity.fallDistance <= 6){
                e.isCancelled = true
                return
            }
            e.damage = e.damage / 2
            return
        }
        if(clickListener.allowed[p.uniqueId]!!)
            clickListener.allowed[p.uniqueId] = false
        val scaffold = utils.scaffold[p.uniqueId]
        utils.scaffold[p.uniqueId] = false
        object : BukkitRunnable(){
            override fun run() {
                utils.scaffold[p.uniqueId] = scaffold!!
                clickListener.allowed[p.uniqueId] = true
            }

        }.runTaskLater(plugin, 20L)
    }
    fun join(p: Player){
        //setup data
        canShoot[p.uniqueId] = true
        utils.scaffold[p.uniqueId] = false
        clickListener.allowed[p.uniqueId] = true
        if(!p.persistentDataContainer.has(NamespacedKey(plugin, "Kills"), PersistentDataType.INTEGER))
            p.persistentDataContainer.set(NamespacedKey(plugin, "Kills"), PersistentDataType.INTEGER, 0)
        if(!p.persistentDataContainer.has(NamespacedKey(plugin, "Deaths"), PersistentDataType.INTEGER))
            p.persistentDataContainer.set(NamespacedKey(plugin, "Deaths"), PersistentDataType.INTEGER, 0)
        clickListener.blockTypes[p.uniqueId] = plugin.blocks[p.persistentDataContainer.get(NamespacedKey(plugin, "Block"), PersistentDataType.INTEGER)]!!

        p.inventory.heldItemSlot = 0

        //setup scoreboard
        val scoreboard: Scoreboard = Bukkit.getScoreboardManager().newScoreboard

        val objective: Objective = scoreboard.registerNewObjective("Stats", Criteria.DUMMY, "Your FFA Statistics", RenderType.INTEGER)
        objective.displaySlot = DisplaySlot.SIDEBAR
        val kills = p.persistentDataContainer.get(NamespacedKey(plugin, "Kills"), PersistentDataType.INTEGER)
        val killScore: Score = objective.getScore("Kills: $kills")
        val deaths = p.persistentDataContainer.get(NamespacedKey(plugin, "Deaths"), PersistentDataType.INTEGER)
        val deathScore: Score = objective.getScore("Deaths: $deaths")
        val kdScore = objective.getScore("K/D: ${(kills!!.toFloat().div(deaths?.toFloat()!!)).times(100).roundToInt().toFloat() / 100}")

        killScore.score = 3
        deathScore.score = 2
        kdScore.score = 1

        p.scoreboard = scoreboard

        //Spawn Location
        val min = -25
        val max = 25
        val x = floor(Math.random() *(max - min + 1) + min)
        val z = floor(Math.random() *(max - min + 1) + min)
        p.teleport(Location(world, world.spawnLocation.x + x, world.spawnLocation.y, world.spawnLocation.z  + z))

        utils.setupPlayer2(p)
    }
}