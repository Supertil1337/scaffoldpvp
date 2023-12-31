package hobby.servah.scaffoldpvp.listeners

import hobby.servah.scaffoldpvp.Scaffoldpvp
import hobby.servah.scaffoldpvp.Utils
import hobby.servah.scaffoldpvp.commands.DuelCommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.UUID
import javax.inject.Named
import javax.naming.Name

class LobbyListener(val plugin: Scaffoldpvp) : Listener {
    var queuedPlayer: UUID? = null
    @EventHandler
    fun onHit(e: PlayerInteractEntityEvent){
        if(e.player.world.name != Scaffoldpvp.duelLobbyName) return
        val p = e.rightClicked
        if(p !is Player) return
        if(e.player.itemInHand.type != Material.NETHERITE_HOE) return
        DuelCommand.requestDuel(e.player, p, plugin)
    }
    @EventHandler
    fun onHit2(e: EntityDamageByEntityEvent){
        val p = e.entity
        if(p !is Player) return
        if(p.world.name != Scaffoldpvp.duelLobbyName) return
        val p2 = e.damager
        if(p2 !is Player) return
        if(p2.itemInHand.type != Material.NETHERITE_HOE) return
        DuelCommand.requestDuel(p2, p, plugin)
    }
    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        if(e.player.world.name != Scaffoldpvp.duelLobbyName) return
        val p = e.player
        Utils.lobbySetup(p, plugin)
    }
    @EventHandler
    fun onDamage(e: EntityDamageEvent){
        if(e.entity.world.name != Scaffoldpvp.duelLobbyName) return
        e.isCancelled = true
    }
    @EventHandler
    fun onInteract(e: PlayerInteractEvent){
        if(e.player.world.name != Scaffoldpvp.duelLobbyName) return
        val p = e.player
        if(!e.action.isRightClick) return
        if(e.hand == EquipmentSlot.OFF_HAND) return

        //FFA
        if(p.itemInHand.type == Material.TRIDENT){
            plugin.ffa?.join(p)
            return
        }
        //Block Customization
        if(p.itemInHand.type == Material.DIAMOND_BLOCK){
            val inv: Inventory = Bukkit.createInventory(null, 18, Component.text("Block Customization"))
            for(block in plugin.blocks.values){
                inv.addItem(ItemStack(block))
            }
            p.openInventory(inv)
        }
        //Queue
        if(p.itemInHand.type != Material.BAMBOO) return

        if(queuedPlayer == p.uniqueId){
            queuedPlayer = null
            p.sendMessage(Component.text("Du wurdest aus der Warteschlange entfernt!").color(NamedTextColor.BLUE))
            return
        }
        if(DuelCommand.duelRequests[p.uniqueId] != null){
            p.sendMessage(Component.text("Deine Duellanfrage an ${Bukkit.getPlayer(DuelCommand.duelRequests[p.uniqueId]!!)?.name} wurde entfernt!").color(NamedTextColor.BLUE))
            DuelCommand.duelRequests[p.uniqueId] = null
        }
        if(queuedPlayer == null){
            queuedPlayer = p.uniqueId
            p.sendMessage(Component.text("Du wurdest zur Warteschlange hinzugefügt!").color(NamedTextColor.GREEN))
            return
        }
        val p2 = Bukkit.getPlayer(queuedPlayer!!)
        if(p2 == null){
            queuedPlayer = p.uniqueId
            p.sendMessage(Component.text("Du wurdest zur Warteschlange hinzugefügt!").color(NamedTextColor.GREEN))
            return
        }
        queuedPlayer = null
        Utils.startMatch(p, p2, plugin)

    }

    @EventHandler
    fun onItemDrop(e: PlayerDropItemEvent) {
        if(e.player.world.name != Scaffoldpvp.duelLobbyName) return
        e.isCancelled = true
    }

    @EventHandler
    fun onItemMoveBetweenInvs(e: InventoryMoveItemEvent) {
        val holder: InventoryHolder? = e.initiator.holder
        if(holder !is Player) return
        if(holder.world.name != Scaffoldpvp.duelLobbyName) return
        e.isCancelled = true

    }

    @EventHandler
    fun onItemClick(e: InventoryClickEvent) {
        if(e.whoClicked.world.name != Scaffoldpvp.duelLobbyName) return
        if(e.cursor === null) return
        if(e.view.title() == Component.text("Block Customization") && e.currentItem != null){
            e.whoClicked.persistentDataContainer.set(NamespacedKey(plugin, "Block"), PersistentDataType.INTEGER, e.slot)
            e.inventory.close()
        }
        e.isCancelled = true
    }
    @EventHandler
    fun onPickUp(e : PlayerAttemptPickupItemEvent) {
        if(e.player.world.name != Scaffoldpvp.duelLobbyName) return
        e.item.remove()
        e.isCancelled = true
    }
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent){
        if(e.player.world.name != Scaffoldpvp.duelLobbyName) return
        e.isCancelled = true
    }
}