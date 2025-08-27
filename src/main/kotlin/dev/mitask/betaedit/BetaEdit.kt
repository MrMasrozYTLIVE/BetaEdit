package dev.mitask.betaedit

import dev.mitask.betaedit.commands.*
import dev.mitask.betaedit.util.Cuboid
import net.glasslauncher.glassbrigadier.api.event.CommandRegisterEvent
import net.mine_diver.unsafeevents.listener.EventListener

class BetaEdit {
    companion object {
        val tasks: MutableList<Cuboid> = mutableListOf()
    }

    @EventListener
    fun registerCommands(event: CommandRegisterEvent) {
        event.register(FillCommand())
    }
}
