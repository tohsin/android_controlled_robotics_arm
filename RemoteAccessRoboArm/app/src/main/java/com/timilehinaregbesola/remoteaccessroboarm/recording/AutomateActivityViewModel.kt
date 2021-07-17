package com.timilehinaregbesola.remoteaccessroboarm.recording

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timilehinaregbesola.remoteaccessroboarm.RoboRepository
import com.timilehinaregbesola.remoteaccessroboarm.model.Recording
import kotlinx.coroutines.launch

class AutomateActivityViewModel(private val repository: RoboRepository) : ViewModel() {
//    var addClicked = MutableLiveData<Boolean?>()
    val recordings = MutableLiveData<List<Recording>>()

    private val _navigateToRecordingSettings = MutableLiveData<Long?>()
    val navigateToRecordingSettings
        get() = _navigateToRecordingSettings

    fun onUpdate(recording: Recording) {
        viewModelScope.launch {
            repository.updateRecording(recording)
            getAlarms()
        }
    }

    fun getAlarms() {
        viewModelScope.launch {
            val recordingList = repository.getRecordings()
            recordings.postValue(recordingList)
        }
    }

    // Called when add menu is pressed
    fun onAdd(title: String) {
        val new = Recording(title = title)
        viewModelScope.launch {
            val id = repository.addRecording(new)
//            addClicked.value = true
            _navigateToRecordingSettings.value = id
//            getAlarms()
        }
    }

    fun onDelete(recording: Recording) {
        viewModelScope.launch {
            repository.deleteRecording(recording)
            getAlarms()
        }
    }

    fun onClear() {
        viewModelScope.launch {
            repository.clear()
            getAlarms()
        }
    }

    fun onRecordingClicked(id: Long) {
//        addClicked.value = false
        _navigateToRecordingSettings.value = id
    }

    fun onSettingsNavigated() {
        _navigateToRecordingSettings.value = null
    }
}
