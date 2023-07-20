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

class FFA(val world: World, val plugin: Scaffoldpvp) : Listener {
    private val canShoot = HashMap<UUID, Boolean>()
    val utils: Utils = Utils(world)
    val clickListener: ClickListener = ClickListener(plugin, utils, world)

    init {
        Bukkit.getPluginManager().registerEvents(clickListener, plugin)
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
        val p = e.player
        p.scoreboard = Bukkit.getScoreboardManager().newScoreboard
        Utils.leave(p)
    }

    //brauch ich das überhaupt noch?, glaub schon lol
    @EventHandler
    fun onDeath(e: PlayerDeathEvent){
        if(e.player.world != world) return
        val p = e.player
        e.isCancelled = true
        //vielleicht noch einbauen
        /*p.showTitle(
            Title.title(
                Component.text("You died").color(NamedTextColor.RED), Component.text("You lost the fight!").color(
                    NamedTextColor.BLUE)
            )
        )

         */
        val data: PersistentDataContainer = e.player.persistentDataContainer
        data.set(NamespacedKey(plugin, "Deaths"), PersistentDataType.INTEGER,
            data.get(NamespacedKey(plugin, "Deaths"), PersistentDataType.INTEGER)?.plus(1)!!
        )
        val killer = e.entity.killer
        if(killer != null){
            val data2: PersistentDataContainer = killer.persistentDataContainer
            val oldKills = data2.get(NamespacedKey(plugin, "Kills"), PersistentDataType.INTEGER)
            data2.set(NamespacedKey(plugin, "Kills"), PersistentDataType.INTEGER,
                data2.get(NamespacedKey(plugin, "Kills"), PersistentDataType.INTEGER)?.plus(1)!!
            )
            killer.scoreboard.resetScores("Kills: $oldKills")
            val scoreboard = killer.scoreboard
            val score: Score? = scoreboard.getObjective("Stats")?.getScore("Kills: ${oldKills?.plus(1)}")
            score?.score = 2
            killer.scoreboard = scoreboard
        }

        //Bukkit.broadcast(Component.text("${p.name} died!").color(NamedTextColor.RED))
        for(p1 in world.players){
            p1.sendMessage(Component.text("${p.name} died!").color(NamedTextColor.RED))
        }
        p.scoreboard = Bukkit.getScoreboardManager().newScoreboard
        Utils.leave(p)
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
        }.runTaskTimer(plugin, 0L, 20L)
    }

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
        canShoot[p.uniqueId] = true
        utils.scaffold[p.uniqueId] = false
        clickListener.allowed[p.uniqueId] = true
        if(!p.persistentDataContainer.has(NamespacedKey(plugin, "Kills"), PersistentDataType.INTEGER))
            p.persistentDataContainer.set(NamespacedKey(plugin, "Kills"), PersistentDataType.INTEGER, 0)
        if(!p.persistentDataContainer.has(NamespacedKey(plugin, "Deaths"), PersistentDataType.INTEGER))
            p.persistentDataContainer.set(NamespacedKey(plugin, "Deaths"), PersistentDataType.INTEGER, 0)

        val scoreboard: Scoreboard = Bukkit.getScoreboardManager().newScoreboard

        val objective: Objective = scoreboard.registerNewObjective("Stats", Criteria.DUMMY, "Your FFA Statistics", RenderType.INTEGER)
        objective.displaySlot = DisplaySlot.SIDEBAR
        val kills = p.persistentDataContainer.get(NamespacedKey(plugin, "Kills"), PersistentDataType.INTEGER)
        val killScore: Score = objective.getScore("Kills: $kills")
        val deaths = p.persistentDataContainer.get(NamespacedKey(plugin, "Deaths"), PersistentDataType.INTEGER)
        val deathScore: Score = objective.getScore("Deaths: $deaths")

        killScore.score = 2
        deathScore.score = 1

        p.scoreboard = scoreboard


        val min = -25
        val max = 25
        val x = floor(Math.random() *(max - min + 1) + min)
        val z = floor(Math.random() *(max - min + 1) + min)
        p.teleport(Location(world, world.spawnLocation.x + x, world.spawnLocation.y, world.spawnLocation.z  + z))

        //von utils gecopied
        p.foodLevel = 20
        p.health = 20.0
        p.gameMode = GameMode.SURVIVAL
        //give the player all the necessary items
        p.inventory.clear()
        val sword = ItemStack(Material.DIAMOND_SWORD)
        val meta2 = sword.itemMeta
        meta2.isUnbreakable = true
        sword.itemMeta = meta2
        p.inventory.addItem(sword)
        val pickaxe = ItemStack(Material.DIAMOND_PICKAXE)
        val meta: ItemMeta = pickaxe.itemMeta
        meta.addEnchant(Enchantment.DIG_SPEED, 50, true)
        meta.isUnbreakable = true
        pickaxe.itemMeta = meta
        p.inventory.addItem(pickaxe)
        val bow = ItemStack(Material.BOW)
        val meta3 = bow.itemMeta
        meta3.addEnchant(Enchantment.ARROW_DAMAGE, 1, false)
        meta3.addEnchant(Enchantment.ARROW_INFINITE, 1, false)
        meta3.isUnbreakable = true
        bow.itemMeta = meta3
        p.inventory.addItem(bow)
        p.inventory.setItem(9, ItemStack(Material.ARROW, 1))
        p.inventory.armorContents = arrayOf(
            ItemStack(Material.DIAMOND_BOOTS), ItemStack(Material.DIAMOND_LEGGINGS),
            ItemStack(Material.DIAMOND_CHESTPLATE), ItemStack(Material.DIAMOND_HELMET)
        )
        val switch = ItemStack(Material.STICK)
        val meta4 = switch.itemMeta
        meta4.displayName(Component.text("Switch").color(NamedTextColor.DARK_GREEN))
        val loreList = ArrayList<TextComponent>()
        loreList.add(Component.text("Right to click to toggle Scaffold").color(NamedTextColor.DARK_PURPLE))
        meta4.lore(loreList)
        switch.itemMeta = meta4
        p.inventory.setItemInOffHand(switch)
    }
}