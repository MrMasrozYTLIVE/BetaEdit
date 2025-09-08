package dev.mitask.betaedit

import dev.mitask.betaedit.commands.*
import dev.mitask.betaedit.data.User
import dev.mitask.betaedit.util.Cuboid
import net.glasslauncher.glassbrigadier.api.event.CommandRegisterEvent
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.util.Identifier
import net.modificationstation.stationapi.api.util.Namespace

class BetaEdit {
    companion object {
        val tasks: MutableList<Cuboid> = mutableListOf()
        val users: MutableMap<String, User> = mutableMapOf()
        val namespace = Namespace.resolve()
        val wandIdentifier: Identifier = Identifier.of(namespace, "wand")
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
