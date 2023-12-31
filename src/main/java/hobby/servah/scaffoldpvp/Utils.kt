package hobby.servah.scaffoldpvp

import hobby.servah.scaffoldpvp.commands.DuelCommand
import hobby.servah.scaffoldpvp.commands.DuelCommand.Companion.phaseManagers
import hobby.servah.scaffoldpvp.phase.EndPhase
import hobby.servah.scaffoldpvp.phase.PhaseManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.apache.commons.io.FileUtils
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import java.io.File
import java.io.IOException
import java.util.*
import javax.naming.Name
import kotlin.collections.ArrayList


class Utils(val world: World) {


    val spawn1: Location = world.spawnLocation
    val spawn2: Location = world.spawnLocation.add(Vector(10, 0, 0))

    var firstSpawn: Boolean = true

    var scaffold = HashMap<UUID, Boolean>()

    fun setupPlayer(p: Player) {
        scaffold[p.uniqueId] = false
        if(firstSpawn){
            spawn1.let { p.teleport(it) }
            firstSpawn = false
        }
        else{
            spawn2.let { p.teleport(it) }
            firstSpawn = true
        }
        setupPlayer2(p)
    }
    fun setupPlayer2(p: Player){
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
    fun endMatch(players: MutableList<Player>, phaseManager: PhaseManager, plugin: Scaffoldpvp){
        for(p in players){
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

            phaseManager.changePhase(EndPhase(world, this, plugin), plugin)
        }
    }
    companion object{
        fun playerLeaveDuelWorld(world: World, p: Player, plugin: Scaffoldpvp){
            if(!world.name.startsWith("Duel")){
                p.sendMessage(Component.text("You're currently not in a duel!").color(NamedTextColor.RED))
                return
            }
            leave(p, plugin)

            if(world.playerCount != 0) return
            Bukkit.unloadWorld(world, false)
            FileUtils.deleteDirectory(File(world.name))

            for(task in phaseManagers[world.name]?.currentPhase?.tasks!!){
                if(Bukkit.getServer().scheduler.isCurrentlyRunning(task.taskId) || Bukkit.getServer().scheduler.isQueued(task.taskId)){
                   task.cancel()
                }
            }
            for(task in phaseManagers[world.name]?.tasks!!) {
                if(Bukkit.getServer().scheduler.isCurrentlyRunning(task.taskId) || Bukkit.getServer().scheduler.isQueued(task.taskId)){
                    task.cancel()
                }
            }

            phaseManagers[world.name]?.currentPhase = null
            phaseManagers[world.name]?.clickListener?.task?.cancel()
            phaseManagers[world.name]?.clickListener?.task = null
            phaseManagers[world.name]?.clickListener = null
            phaseManagers[world.name] = null
        }
        fun startMatch(p1: Player, p2: Player, plugin: Scaffoldpvp){
            //copy world
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
            //create/register world
            val newWorld = Bukkit.createWorld(WorldCreator("Duel$counter"))
            if(newWorld == null){
                p1.sendMessage("joo dat is null");
                return
            }
            val players = arrayOf(p1, p2)

            //PhaseManager übernimmt
            phaseManagers[newWorld.name] = PhaseManager(plugin, newWorld, players)
        }
        fun lobbySetup(p: Player, plugin: Scaffoldpvp){
            if(!p.persistentDataContainer.has(NamespacedKey(plugin, "Block")))
                p.persistentDataContainer.set(NamespacedKey(plugin, "Block"), PersistentDataType.INTEGER, 0)


            p.health = 20.0
            p.activePotionEffects.clear()
            p.inventory.clear()
            p.gameMode = GameMode.ADVENTURE
            p.level = 0
            p.exp = 0F
            val hoe = ItemStack(Material.NETHERITE_HOE)
            val meta = hoe.itemMeta
            meta.isUnbreakable = true
            meta.displayName(Component.text("Request Duel").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD))
            meta.lore(mutableListOf(Component.text("Hit a player to request a duel with them!").color(NamedTextColor.GREEN)))
            hoe.itemMeta = meta
            p.inventory.addItem(hoe)

            val bamboo = ItemStack(Material.BAMBOO)
            val meta2 = bamboo.itemMeta
            meta2.isUnbreakable = true
            meta2.displayName(Component.text("Join Queue").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
            meta2.lore(mutableListOf(Component.text("Right click to join the queue!").color(NamedTextColor.YELLOW)))
            bamboo.itemMeta = meta2
            p.inventory.addItem(bamboo)

            val trident = ItemStack(Material.TRIDENT)
            val meta3 = trident.itemMeta
            meta3.isUnbreakable = true
            meta3.displayName(Component.text("Join FFA").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD))
            meta3.lore(mutableListOf(Component.text("Right click to join the FFA(Free for all) Gamemode!").color(NamedTextColor.AQUA)))
            trident.itemMeta = meta3
            p.inventory.addItem(trident)

            val diaBlock = ItemStack(Material.DIAMOND_BLOCK)
            val meta4 = diaBlock.itemMeta
            meta4.isUnbreakable = true
            meta4.displayName(Component.text("Choose a block").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
            meta4.lore(mutableListOf(Component.text("Right click to choose a block for the scaffolding!").color(NamedTextColor.DARK_GREEN)))
            diaBlock.itemMeta = meta4
            p.inventory.addItem(diaBlock)
        }
        fun leave(p: Player, plugin: Scaffoldpvp){
            p.inventory.clear()
            p.foodLevel = 20
            p.health = 20.0
            p.gameMode = GameMode.ADVENTURE
            val loc: Location = Bukkit.getWorld(Scaffoldpvp.duelLobbyName)!!.spawnLocation
            p.teleport(loc)
            lobbySetup(p, plugin)
        }
    }

}