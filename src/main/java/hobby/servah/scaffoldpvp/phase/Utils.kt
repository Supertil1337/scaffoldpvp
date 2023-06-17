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
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class Utils(private val world: World) {


    val spawn1: Location = world.spawnLocation
    val spawn2: Location = world.spawnLocation.add(Vector(10, 0, 0))

    var firstSpawn: Boolean = true

    var scaffold = HashMap<UUID, Boolean>()

    fun setupPlayer(plugin: Scaffoldpvp, p: Player) {

        scaffold[p.uniqueId] = false




        if(firstSpawn){
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
        val bow = ItemStack(Material.BOW)
        val meta3 = bow.itemMeta
        meta3.addEnchant(Enchantment.ARROW_DAMAGE, 1, false)
        meta3.isUnbreakable = true
        bow.itemMeta = meta3
        p.inventory.addItem(bow)
        p.inventory.setItem(8, ItemStack(Material.ARROW, 5))
        p.inventory.armorContents = arrayOf(
            ItemStack(Material.DIAMOND_BOOTS), ItemStack(Material.DIAMOND_LEGGINGS),
            ItemStack(Material.DIAMOND_CHESTPLATE), ItemStack(Material.DIAMOND_HELMET)
        )
        p.inventory.setItemInOffHand(ItemStack(Material.STICK))
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
            p.gameMode = GameMode.ADVENTURE
            val loc: Location = Bukkit.getWorld("World")!!.spawnLocation
            p.teleport(loc)

            if(world.playerCount != 0) return
            Bukkit.unloadWorld(world, false)
            FileUtils.deleteDirectory(File(world.name))
            DuelCommand.phaseManagers[world.name]?.currentPhase = null
            DuelCommand.phaseManagers[world.name]?.clickListener?.task?.cancel()
            DuelCommand.phaseManagers[world.name]?.clickListener?.task = null
            DuelCommand.phaseManagers[world.name]?.clickListener = null
            DuelCommand.phaseManagers[world.name] = null
        }
        fun startMatch(p1: Player, p2: Player, plugin: Scaffoldpvp){
            var counter = 0
            try {
                val sourceDirectory = File("ScaffoldPvP")
                fun loop(){
                    val destinationDirectory = File("Duel$counter")
                    if(destinationDirectory.exists()){
                        counter++
                        loop()
                    }
                    else{
                        FileUtils.copyDirectory(sourceDirectory, destinationDirectory)
                    }
                }
                loop()

            }
            catch (e: IOException){
                e.printStackTrace()
            }


            val newWorld = Bukkit.createWorld(WorldCreator("Duel$counter"))
            if(newWorld == null){
                p1.sendMessage("joo dat is null");
                return
            }
            //val spawnLocation: Location = newWorld.spawnLocation;
            //sender.teleport(spawnLocation)
            val players = arrayOf(p1, p2)

            //PhaseManager übernimmt
            DuelCommand.phaseManagers[newWorld.name] = PhaseManager(plugin, newWorld, players)
        }
    }

}