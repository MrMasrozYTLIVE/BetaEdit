package dev.mitask.betaedit.event

import dev.mitask.betaedit.BetaEdit
import dev.mitask.betaedit.data.User
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.server.event.network.PlayerLoginEvent

@Suppress("unused")
class PlayerListener {
    @EventListener
    fun onLogin(event: PlayerLoginEvent) {
        BetaEdit.users[event.player.name] = User()
    }
}