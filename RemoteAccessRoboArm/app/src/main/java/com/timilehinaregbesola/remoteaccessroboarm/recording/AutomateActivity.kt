package com.timilehinaregbesola.remoteaccessroboarm.recording

import android.app.AlertDialog.Builder
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.timilehinaregbesola.remoteaccessroboarm.R
import com.timilehinaregbesola.remoteaccessroboarm.databinding.ActivityAutomateBinding
import com.timilehinaregbesola.remoteaccessroboarm.task.TaskActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class AutomateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAutomateBinding
    private val viewModel by viewModel<AutomateActivityViewModel>()
    var wifiModuleIp = ""
    private lateinit var adapter: RecordingListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAutomateBinding.inflate(layoutInflater)
        wifiModuleIp = intent.getStringExtra("ipAdress")!!
        viewModel.getAlarms()
        adapter = RecordingListAdapter(
            viewModel,
            RecordingListener { recordingId ->
                viewModel.onRecordingClicked(recordingId)
            }
        )
//        val itemDecoration = VerticalSpacingItemDecoration(15)
//        binding.alarmRecyclerView.addItemDecoration(itemDecoration)
        binding.recordingRecyclerView.adapter = adapter
        setupObservers()
        setContentView(binding.root)
    }

    private fun setupObservers() {
        viewModel.recordings.observe(
            this,
            {
                if (it.isNotEmpty()) {
                    binding.recordingEmpty.visibility = View.GONE
                    it.let { adapter.submitList(it) }
                } else {
                    binding.recordingEmpty.visibility = View.VISIBLE
                }
            }
        )

        viewModel.navigateToRecordingSettings.observe(
            this,
            { recording ->
                recording?.let {
                    val intent = Intent(this, TaskActivity::class.java)
                    intent.putExtra("recordingId", recording)
                    intent.putExtra("ip", wifiModuleIp)
                    startActivity(intent)
                    viewModel.onSettingsNavigated()
                }
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.automate_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_recording_menu -> {
                val alert = Builder(this@AutomateActivity)
                val edittext = EditText(baseContext)
                alert.setMessage("Enter a recording title")
                alert.setTitle("New Recording")

                alert.setView(edittext)

                alert.setPositiveButton("Ok") { dialog, whichButton ->
                    val recordingTitleValue = edittext.text.toString()
                    if (recordingTitleValue.isNotBlank()) {
                        viewModel.onAdd(recordingTitleValue)
                    }
                }

                alert.setNegativeButton("Cancel") { dialog, whichButton ->
                    // what ever you want to do with No option.
                }
                alert.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
