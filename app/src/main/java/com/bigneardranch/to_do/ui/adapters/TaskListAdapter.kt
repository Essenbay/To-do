package com.bigneardranch.to_do.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigneardranch.to_do.data.database.TaskModel
import com.bigneardranch.to_do.databinding.TaskListItemBinding
import com.bigneardranch.to_do.listeners.TaskGestureListener

class TaskListAdapter(
    private val taskList: List<TaskModel>,
    private val taskGestureListener: TaskGestureListener
) : RecyclerView.Adapter<TaskListAdapter.TaskHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        val binding =
            TaskListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskHolder(binding)
    }


    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        holder.bind(
            taskList[position],
            taskGestureListener
        )
    }

    override fun getItemCount(): Int = taskList.size


    class TaskHolder(private val binding: TaskListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            task: TaskModel,
            taskGestureListener: TaskGestureListener
        ) {
            binding.task = task
            binding.root.setOnClickListener {
                taskGestureListener.onClick(task.id)
            }
            binding.checkboxTaskSolved.setOnCheckedChangeListener { _, isChecked ->
                taskGestureListener.onSolvedCheckClicked(task, isChecked)
            }
            binding.checkboxTaskInProgress.setOnCheckedChangeListener { _, isChecked ->
                taskGestureListener.onInProgressCheckClicked(task, isChecked)
            }
            binding.root.setOnLongClickListener {
                taskGestureListener.onLongClick(it, task.id)
                true
            }
        }
    }
}