package com.bigneardranch.to_do.listeners

import android.view.View
import com.bigneardranch.to_do.data.database.TaskModel

interface TaskGestureListener {
    fun onClick(taskId: Int)
    fun onLongClick(v: View, taskId: Int)
    fun onSolvedCheckClicked(task: TaskModel, isChecked: Boolean)
    fun onInProgressCheckClicked(task: TaskModel, isChecked: Boolean)
}