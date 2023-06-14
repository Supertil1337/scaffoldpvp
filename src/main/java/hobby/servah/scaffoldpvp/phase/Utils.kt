package hobby.servah.scaffoldpvp.phase

import hobby.servah.scaffoldpvp.DuelCommand
import hobby.servah.scaffoldpvp.Scaffoldpvp
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.apache.commons.io.FileUtils
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.util.Vector
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class Utils(private val world: World) {
    /*companion object{
        var spawn1: Location? = null
        var spawn2: Location? = null

        var firstSpawn: Boolean = true

        var scaffold = HashMap<UUID, Boolean>()

        fun setupPlayer(plugin: Scaffoldpvp, p: Player) {

            scaffold[p.uniqueId] = false

            //logging
            //Bukkit.broadcast(spawn1.toString(), spawn2.toString())
            if(spawn1 == null || spawn2 == null){
                Bukkit.broadcast(Component.text("Es wurden noch keine Spawns festgelegt").color(NamedTextColor.RED))
                PhaseManager.changePhase(Disabled(plugin), plugin)
                return
            }
            if(firstSpawn){
                //logging
                //p.sendMessage("§aDu wurdest zu deinem spawn teleportiert")
                spawn1?.let { p.teleport(it) }
                //p.teleport(spawn1!!)
                firstSpawn = false
            }
            else{
                spawn2?.let { p.teleport(it) }
                firstSpawn = true
            }
            p.foodLevel = 20
            p.health = 20.0

            //give the player all the necessary items
            p.inventory.clear()
            p.inventory.addItem(ItemStack(Material.DIAMOND_SWORD))
            val pickaxe: ItemStack = ItemStack(Material.DIAMOND_PICKAXE)
            var meta: ItemMeta = pickaxe.itemMeta
            meta.addEnchant(Enchantment.DIG_SPEED, 50, true)
            pickaxe.itemMeta = meta
            p.inventory.addItem(pickaxe)
            p.inventory.armorContents = arrayOf(
                ItemStack(Material.DIAMOND_BOOTS), ItemStack(Material.DIAMOND_LEGGINGS),
                ItemStack(Material.DIAMOND_CHESTPLATE), ItemStack(Material.DIAMOND_HELMET)
            )
            p.inventory.setItemInOffHand(ItemStack(Material.STICK))
            Bukkit.broadcast(Component.text("Equipping the players").color(NamedTextColor.BLUE))
        }
    }

     */



    /*fun getSpawn1(): Location? {
        return spawn1
    }
    fun getSpawn2(): Location? {
        return spawn2
    }
    fun setSpawn1(loc: Location?){
        spawn1 = loc
    }
    fun setSpawn2(loc: Location?){
        spawn2 = loc
    }

     */

    //konnte man mit pvp command setzten, jetzt aber fest
    //var spawn1: Location? = null
    //var spawn2: Location? = null

    val spawn1: Location = world.spawnLocation
    val spawn2: Location = world.spawnLocation.add(Vector(10, 0, 0))

    var firstSpawn: Boolean = true

    var scaffold = HashMap<UUID, Boolean>()

    fun setupPlayer(plugin: Scaffoldpvp, p: Player) {

        scaffold[p.uniqueId] = false

        //logging
        //Bukkit.broadcast(spawn1.toString(), spawn2.toString())


        //TODO einfach feste spawns festlegen und die spieler dahin teleportieren
        /*if(spawn1 == null || spawn2 == null){
            Bukkit.broadcast(Component.text("Es wurden noch keine Spawns festgelegt").color(NamedTextColor.RED))
            PhaseManager.changePhase(Disabled(plugin), plugin)
            return
        }

         */
        if(firstSpawn){
            //logging
            //p.sendMessage("§aDu wurdest zu deinem spawn teleportiert")
            spawn1.let { p.teleport(it) }
            //p.teleport(spawn1!!)
            firstSpawn = false
        }
        else{
            spawn2.let { p.teleport(it) }
            firstSpawn = true
        }



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
        p.inventory.armorContents = arrayOf(
            ItemStack(Material.DIAMOND_BOOTS), ItemStack(Material.DIAMOND_LEGGINGS),
            ItemStack(Material.DIAMOND_CHESTPLATE), ItemStack(Material.DIAMOND_HELMET)
        )
        p.inventory.setItemInOffHand(ItemStack(Material.STICK))
        //Bukkit.broadcast(Component.text("Equipping the players").color(NamedTextColor.BLUE))
    }
    companion object{
        fun playerLeaveDuelWorld(world: World, p: Player){
            if(!world.name.startsWith("Duel")){
                p.sendMessage(Component.text("You're currently not in a duel!").color(NamedTextColor.RED))
                return
            }
            p.inventory.clear()
            p.foodLevel = 20
            p.health = 20.0
            val loc: Location = Bukkit.getWorld("World")!!.spawnLocation
            p.teleport(loc)
            //logging
            //Bukkit.broadcast(Component.text("moin"))

            if(world.playerCount != 0) return
            Bukkit.unloadWorld(world, false)
            FileUtils.deleteDirectory(File(world.name))
            DuelCommand.phaseManagers[world.name]?.currentPhase = null
            DuelCommand.phaseManagers[world.name]?.clickListener?.task?.cancel()
            DuelCommand.phaseManagers[world.name]?.clickListener?.task = null
            DuelCommand.phaseManagers[world.name]?.clickListener = null
            DuelCommand.phaseManagers[world.name] = null

            //logging
            //Bukkit.broadcast(Component.text("moinmointest"))
        }
    }

}