package com.timilehinaregbesola.remoteaccessroboarm.recording

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.timilehinaregbesola.remoteaccessroboarm.databinding.RecordingItemBinding
import com.timilehinaregbesola.remoteaccessroboarm.model.Recording

class RecordingListAdapter(
    val viewModel: AutomateActivityViewModel,
    val clickListener: RecordingListener
) : ListAdapter<Recording, RecordingListAdapter.ViewHolder>(RecordingDiffCallback()) {

//    private val context: Context = get()

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

    class ViewHolder private constructor(val binding: RecordingItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Recording,
            clickListener: RecordingListener,
            viewModel: AutomateActivityViewModel
        ) {
            binding.recording = item
            binding.clickListener = clickListener
            binding.txtRecordingTitle.text = item.title
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecordingItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class RecordingDiffCallback : DiffUtil.ItemCallback<Recording>() {
    override fun areItemsTheSame(oldItem: Recording, newItem: Recording): Boolean {
        return oldItem.recordingId == newItem.recordingId
    }

    override fun areContentsTheSame(oldItem: Recording, newItem: Recording): Boolean {
        return oldItem == newItem
    }
}

class RecordingListener(val clickListener: (recordingId: Long) -> Unit) {
    fun onclick(recording: Recording) = clickListener(recording.recordingId)
}
