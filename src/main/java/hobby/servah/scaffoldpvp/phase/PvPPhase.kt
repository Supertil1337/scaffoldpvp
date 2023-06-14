package hobby.servah.scaffoldpvp.phase

import hobby.servah.scaffoldpvp.Scaffoldpvp
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class PvPPhase(plugin: Scaffoldpvp?, private val world: World, private val utils: Utils, private val phaseManager: PhaseManager) : Phase(plugin) {

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
    fun onDeath(e: PlayerDeathEvent){
        if(e.player.world != world) return
        e.drops.clear()
        val list = world.players
        list.remove(e.player)
        for(p in list) p.showTitle(Title.title(Component.text("You won!").color(NamedTextColor.GREEN), Component.text("Your opponent died!").color(NamedTextColor.BLUE)))
    }
    @EventHandler
    fun onRespawn(e: PlayerRespawnEvent){
        if(e.player.world != world) return
        e.player.showTitle(Title.title(Component.text("You died").color(NamedTextColor.RED), Component.text("You lost the fight!").color(NamedTextColor.BLUE)))


        //TODO eigentliches play again und leave features noch adden
        object : BukkitRunnable() {
            override fun run() {
                val list = world.players
                list.add(e.player)
                for(p in list){
                    p.inventory.clear()
                    val leave = ItemStack(Material.BARRIER)
                    val leaveMeta = leave.itemMeta
                    leaveMeta.isUnbreakable = true //einfach so
                    leaveMeta.displayName(Component.text("Leave").color(NamedTextColor.RED))
                    leave.itemMeta = leaveMeta
                    val playAgain = ItemStack(Material.DIAMOND_SWORD)
                    val playAgainMeta = playAgain.itemMeta
                    playAgainMeta.isUnbreakable = true
                    playAgainMeta.displayName(Component.text("Play again").color(NamedTextColor.BLUE))
                    playAgain.itemMeta = playAgainMeta
                    p.inventory.setItem(8, leave)
                    p.inventory.setItem(0, playAgain)

                    Utils.playerLeaveDuelWorld(world, p)
                }
            }
        }.runTaskLater(plugin!!, 1)


    }
    @EventHandler
    fun leave(e: PlayerQuitEvent){
        val list = world.players
        for(p in list){
            p.showTitle(Title.title(Component.text("You won!").color(NamedTextColor.GREEN), Component.text("Your opponent died!").color(NamedTextColor.BLUE)))
            Utils.playerLeaveDuelWorld(world, p)
        }

    }
}