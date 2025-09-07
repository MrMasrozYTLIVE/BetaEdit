package dev.mitask.betaedit

import dev.mitask.betaedit.commands.*
import dev.mitask.betaedit.data.User
import dev.mitask.betaedit.util.Cuboid
import net.glasslauncher.glassbrigadier.api.event.CommandRegisterEvent
import net.mine_diver.unsafeevents.listener.EventListener

class BetaEdit {
    companion object {
        val tasks: MutableList<Cuboid> = mutableListOf()
        val users: MutableMap<String, User> = mutableMapOf()
    }

    @EventListener
    @Suppress("unused")
    fun registerCommands(event: CommandRegisterEvent) {
        event.register(FillCommand())

        event.register(WandCommand())
        event.register(SetCommand())
        event.register(UndoCommand())
        event.register(RedoCommand())
        event.register(Pos1Command())
        event.register(Pos2Command())
    }
}
