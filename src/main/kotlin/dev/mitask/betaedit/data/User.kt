package dev.mitask.betaedit.data

import net.minecraft.util.math.Vec3i

class User {
    private val history: MutableList<HistoryEdit> = mutableListOf()
    var pos1: Vec3i? = null
    var pos2: Vec3i? = null

    fun addHistory(historyEdit: HistoryEdit) {
        if(history.size > 5) history.removeAt(0)

        history.add(historyEdit)
    }
}
