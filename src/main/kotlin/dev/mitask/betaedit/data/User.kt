package dev.mitask.betaedit.data

import net.minecraft.util.math.Vec3i

class User {
    private val history: MutableList<HistoryEdit> = mutableListOf()
    private val undoHistory: MutableList<HistoryEdit> = mutableListOf()
    var pos1: Vec3i? = null
    var pos2: Vec3i? = null

    fun addHistory(historyEdit: HistoryEdit) {
        if(history.size >= 5) history.removeAt(0)

        history.add(historyEdit)
    }

    fun popUndoHistory(): HistoryEdit? {
        if(undoHistory.isNotEmpty()) {
            val edit = undoHistory.removeAt(undoHistory.size - 1)
            addHistory(edit)
            return edit
        }
        return null
    }

    fun popHistory(): HistoryEdit? {
        if(history.isNotEmpty()) {
            val edit = history.removeAt(history.size - 1)

            if(undoHistory.size >= 5) undoHistory.removeAt(0)
            undoHistory.add(edit)

            return edit
        }
        return null
    }
}
