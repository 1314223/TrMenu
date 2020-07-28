package me.arasple.mc.trmenu.modules.action.impl.item

import io.izzel.taboolib.cronus.CronusUtils
import io.izzel.taboolib.util.item.Items
import me.arasple.mc.trmenu.modules.action.base.Action
import me.arasple.mc.trmenu.utils.Tasks
import me.arasple.mc.trmenu.utils.Utils
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/3/30 19:50
 */
class ActionGiveItem : Action("(give|add)(-)?item(s)?") {

    override fun onExecute(player: Player) {
        val item = getContent(player)
        Tasks.run(true) {
            if (Utils.isJson(item)) {
                CronusUtils.addItem(player, Items.fromJson(item))
            }
        }
    }

}