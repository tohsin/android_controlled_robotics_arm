package com.timilehinaregbesola.remoteaccessroboarm.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.timilehinaregbesola.remoteaccessroboarm.databinding.TaskItemBinding
import com.timilehinaregbesola.remoteaccessroboarm.fromCommandCode
import com.timilehinaregbesola.remoteaccessroboarm.model.Task

class TaskListAdapter(
    val viewModel: TaskViewModel,
    val clickListener: TaskListener
) : ListAdapter<Task, TaskListAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener, viewModel)
    }

    fun deleteTask(position: Int) {
        val recording = getItem(position)
        viewModel.onDelete(recording)
    }

    class ViewHolder private constructor(val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Task,
            clickListener: TaskListener,
            viewModel: TaskViewModel
        ) {
            binding.task = item
            binding.clickListener = clickListener
            binding.txtActuator.text = item.actuator.fromCommandCode()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TaskItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}

class TaskListener(val clickListener: (taskId: String) -> Unit) {
    fun onclick(task: Task) = clickListener(task.id)
}
