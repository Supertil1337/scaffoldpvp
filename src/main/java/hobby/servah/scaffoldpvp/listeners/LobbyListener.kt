package hobby.servah.scaffoldpvp.listeners

import hobby.servah.scaffoldpvp.Scaffoldpvp
import hobby.servah.scaffoldpvp.commands.DuelCommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

class LobbyListener(val plugin: Scaffoldpvp) : Listener {
    @EventHandler
    fun onHit(e: PlayerInteractEntityEvent){
        if(e.player.world.name != "world") return
        val p = e.rightClicked
        if(p !is Player) return
        if(e.player.itemInHand.type != Material.NETHERITE_HOE) return
        DuelCommand.requestDuel(e.player, p, plugin)
    }
    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        if(e.player.world.name != "world") return
        val p = e.player
        p.health = 20.0
        p.activePotionEffects.clear()
        p.inventory.clear()
        p.gameMode = GameMode.ADVENTURE
        p.level = 0
        p.exp = 0F
        val hoe = ItemStack(Material.NETHERITE_HOE)
        val meta = hoe.itemMeta
        meta.isUnbreakable = true
        meta.displayName(Component.text("Request Duel").color(NamedTextColor.BLUE))
        meta.lore(mutableListOf(Component.text("Hit a player to request a duel with them!").color(NamedTextColor.GREEN)))
        hoe.itemMeta = meta
        p.inventory.addItem(hoe)
    }
    @EventHandler
    fun onDamage(e: EntityDamageEvent){
        if(e.entity.world.name != "world") return
        e.isCancelled = true
    }
}