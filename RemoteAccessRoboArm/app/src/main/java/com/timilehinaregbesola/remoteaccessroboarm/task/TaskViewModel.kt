package com.timilehinaregbesola.remoteaccessroboarm.task

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timilehinaregbesola.remoteaccessroboarm.RoboRepository
import com.timilehinaregbesola.remoteaccessroboarm.model.Recording
import com.timilehinaregbesola.remoteaccessroboarm.model.Task
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TaskViewModel(private val repository: RoboRepository) : ViewModel() {
    lateinit var taskList: MutableList<Task>
    lateinit var currentRecording: Recording
    val tasks = MutableLiveData<List<Task>>()

    private val _taskClicked = MutableLiveData<String?>()
    val taskClicked
        get() = _taskClicked

    fun getTasks() {
        tasks.value = taskList
    }

    fun onUpdate(task: Task) {
        for (tt in taskList) {
            if (tt.id == task.id) {
                taskList[taskList.indexOf(tt)] = task
                break
            }
        }
        tasks.value = emptyList()
        tasks.value = taskList
    }

    fun onTaskClicked(taskId: String) {
        _taskClicked.value = taskId
    }

    // Called when add menu is pressed
    fun onAdd(task: Task) {
        taskList.add(task)
        tasks.value = taskList
    }

    fun onUpdateRecording(recording: Recording) {
        viewModelScope.launch {
            repository.updateRecording(recording)
        }
    }

    fun onDelete(task: Task) {
        viewModelScope.launch {
            taskList.remove(task)
            tasks.value = taskList
        }
    }

    fun onClear() {
        viewModelScope.launch {
            taskList.clear()
            tasks.value = taskList
        }
    }

    fun onAlarmSettingsNavigated() {
        _taskClicked.value = null
    }

    fun getRecording(recordingId: Long) {
        runBlocking {
            currentRecording = repository.findRecording(recordingId)!!
        }
        taskList = currentRecording.tasks as MutableList<Task>
        tasks.value = taskList
//        if (taskList.isNotEmpty()) currentRecording.tasks = taskList
    }

    fun getTask(taskId: String?): Task? {
        return if (taskId != null) {
            var taskk: Task? = Task(angle = 0, actuator = 0)
            for (task in taskList) {
                if (task.id == taskId) {
                    taskk = task
                    break
                }
            }
            taskk
        } else {
            null
        }
    }
}
