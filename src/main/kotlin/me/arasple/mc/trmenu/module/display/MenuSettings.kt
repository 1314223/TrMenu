package me.arasple.mc.trmenu.module.display

import io.izzel.taboolib.internal.apache.lang3.ArrayUtils
import io.izzel.taboolib.util.lite.cooldown.Cooldown
import me.arasple.mc.trmenu.api.action.pack.Reactions
import me.arasple.mc.trmenu.module.internal.script.js.ScriptFunction
import me.arasple.mc.trmenu.util.bukkit.ItemMatcher
import me.arasple.mc.trmenu.util.collections.CycleList
import me.clip.placeholderapi.PlaceholderAPI

/**
 * @author Arasple
 * @date 2021/1/24 20:54
 */
class MenuSettings(
    val title: CycleList<String>,
    val titleUpdate: Int,
    private val enableArguments: Boolean = false,
    val defaultArguments: Array<String> = arrayOf(),
    val freeSlots: Set<Int> = setOf(),
    val defaultLayout: Int,
    expansions: Array<String>,
    val minClickDelay: Int,
    val hidePlayerInventory: Boolean,
    private val boundCommands: Array<Regex>,
    val boundItems: Array<ItemMatcher>,
    val openEvent: Reactions,
    val closeEvent: Reactions,
    val clickEvent: Reactions,
    val tasks: Map<Long, Reactions>,
    val internalFunctions: Set<ScriptFunction>
) {

    val clickDelay = Cooldown("CLICK_DELAY", minClickDelay).also { it.plugin = "TrMenu" }

    val dependExpasions: Array<String> = expansions
        get() {
            val registered = PlaceholderAPI.getRegisteredIdentifiers()
            return field.filter { ex -> registered.none { it.equals(ex, true) } }.toTypedArray()
        }

    /**
     * 匹配菜单绑定的命令
     *
     * @return 参数,
     *         -> 为空: 命令匹配该菜单，支持打开
     *         -> 不为空：命令匹配该菜单，支持打开，且携带传递参数
     *         -> Null： 命令与该菜单不匹配
     */
    fun matchCommand(menu: Menu, command: String): Array<String>? = command.split(" ").toTypedArray().let { it ->
        if (it.isNotEmpty()) {
            for (i in it.indices) {
                val read = read(it, i)
                val c = read[0]
                val args = ArrayUtils.remove(read, 0)
                if (boundCommands.any { it.matches(c) } && !(!menu.settings.enableArguments && args.isNotEmpty())) {
                    return@let args
                }
            }
        }
        return@let null
    }

    /**
     * 更好的兼容带参打开命令的同时支持菜单传递参数
     * 例如:
     * - 'is upgrade' 作为打开命令
     * - 'is upgrade 233' 将只会从 233 开始作为第一个参数
     */
    private fun read(cmds: Array<String>, index: Int): Array<String> {
        var commands = cmds
        val command = if (index == 0) commands[index]
        else {
            val cmd = StringBuilder()
            for (i in 0..index) cmd.append(commands[i]).append(" ")
            cmd.substring(0, cmd.length - 1)
        }
        for (i in 0..index) commands = ArrayUtils.remove(commands, 0)
        return ArrayUtils.insert(0, commands, command)
    }

}