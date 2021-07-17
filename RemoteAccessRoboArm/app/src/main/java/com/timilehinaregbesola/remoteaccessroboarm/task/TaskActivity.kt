package com.timilehinaregbesola.remoteaccessroboarm.task

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.timilehinaregbesola.remoteaccessroboarm.R
import com.timilehinaregbesola.remoteaccessroboarm.databinding.ActivityTaskBinding
import com.timilehinaregbesola.remoteaccessroboarm.databinding.NewTaskBottomSheetBinding
import com.timilehinaregbesola.remoteaccessroboarm.fromCommandCode
import com.timilehinaregbesola.remoteaccessroboarm.model.*
import com.timilehinaregbesola.remoteaccessroboarm.toCommandCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.Socket

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    private val viewModel by viewModel<TaskViewModel>()
    private var wifiModuleIp = ""
    private lateinit var adapter: TaskListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recordingId = intent.getLongExtra("recordingId", 0)
        wifiModuleIp = intent.getStringExtra("ip")!!
        viewModel.getRecording(recordingId)
        viewModel.getTasks()
        adapter = TaskListAdapter(
            viewModel,
            TaskListener { taskId ->
                viewModel.onTaskClicked(taskId)
            }
        )
        binding.taskRecyclerView.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.taskRecyclerView)
        setupObservers()
        binding.playFab.setOnClickListener {
            val tasks = viewModel.tasks.value
            var repeat = binding.editTextRepeat.editText?.text.toString()
            if (repeat.isNullOrBlank()) repeat = "1"
            val cmr = CommandAndRepeat(commandNo = tasks!!.size, repeatNo = repeat.toInt())
            val commands = mutableListOf<Command>()
            tasks.forEach {
                commands.add(Command(commandId = it.actuator, angle = it.angle))
            }
            val socketCommand = SocketCommand(commandRepeat = cmr, commands = commands)
            launchSocket(socketCommand)
        }
    }

    private fun launchSocket(socketCommand: SocketCommand) {
        val iPandPort = "192.168.137.239:21567"
        val temp = iPandPort.split(":").toTypedArray()
        val wifiModulePort = Integer.valueOf(temp[1])
        lifecycleScope.launch(context = Dispatchers.IO) {
            try {
                val command = socketCommandToString(socketCommand)
                Log.d("LaunchSocket", "command: $command")
                val inetAddress: InetAddress = InetAddress.getByName(wifiModuleIp)
                val socket = Socket(inetAddress, wifiModulePort)
                Log.d("socket", "socket: $socket")
                val dataOutputStream = DataOutputStream(socket.getOutputStream())
                dataOutputStream.write(command.toByteArray())
                dataOutputStream.close()
                socket.close()
            } catch (e: Exception) {
                Log.d("FreeModeActivity", e.toString())
            }
        }
    }

    private fun setupObservers() {
        viewModel.tasks.observe(
            this,
            {
                if (it.isNotEmpty()) {
                    binding.tasksEmpty.visibility = View.GONE
                    it.let { adapter.submitList(ArrayList(it)) }
                } else {
                    binding.tasksEmpty.visibility = View.VISIBLE
                }
            }
        )

        viewModel.taskClicked.observe(
            this,
            {
                showBottomSheet(fromAdd = false, taskId = it)
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.task_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_task_menu -> {
                showBottomSheet(fromAdd = true)
                true
            }
            R.id.done_task_menu -> {
                viewModel.onUpdateRecording(viewModel.currentRecording)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showBottomSheet(fromAdd: Boolean, taskId: String? = null) {
        val bt = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val actuators = arrayListOf<String?>("Left", "Right", "Base", "Gripper", "Timer")
        val grips = arrayListOf<String?>("Grip", "Ungrip")
        val sheetBinding = NewTaskBottomSheetBinding.inflate(layoutInflater)

        if (!fromAdd) {
            val task = viewModel.getTask(taskId)
            Log.d("showBra", task!!.actuator.toString())
            val index = actuators.indexOf(task!!.actuator.fromCommandCode())
            if (index != -1) {
                actuators.removeAt(index)
                actuators.add(0, task.actuator.fromCommandCode())
                sheetBinding.editText.editText?.setText(task.angle.toString())
            } else {
                actuators.removeAt(3)
                actuators.add(0, "Gripper")
                val ind = grips.indexOf(task.actuator.fromCommandCode())
                grips.removeAt(ind)
                grips.add(0, task.actuator.fromCommandCode())
            }
        }

        sheetBinding.btnOk.setOnClickListener {
            var actuator = sheetBinding.actuatorSpinner.selectedItem.toString()
            if (actuator != "Gripper") {
                val angle = sheetBinding.editText.editText?.text.toString()
                if (fromAdd) {
                    val task = Task(actuator = actuator.toCommandCode(), angle = angle.toInt())
                    viewModel.onAdd(task)
                } else {
                    val task = viewModel.getTask(taskId)
                    task!!.actuator = actuator.toCommandCode()
                    task.angle = angle.toInt()
                    viewModel.onUpdate(task)
                }
            } else {
                if (fromAdd) {
                    actuator = sheetBinding.gripperSpinner.selectedItem.toString()
                    val task = Task(actuator = actuator.toCommandCode(), angle = 0)
                    viewModel.onAdd(task)
                } else {
                    val task = viewModel.getTask(taskId)
                    task!!.actuator = actuator.toCommandCode()
                    task.angle = 0
                    viewModel.onUpdate(task)
                }
            }
            bt.dismiss()
        }
        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            actuators as List<Any?>
        )
        val grad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            grips as List<Any?>
        )
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        grad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sheetBinding.actuatorSpinner.adapter = ad
        sheetBinding.gripperSpinner.adapter = grad
        sheetBinding.actuatorSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val text = p0?.getItemAtPosition(p2)
                    if (text == "Gripper") {
                        sheetBinding.editText.isEnabled = false
                        sheetBinding.editText.visibility = View.INVISIBLE
                        sheetBinding.gripperSpinner.visibility = View.VISIBLE
                    } else {
                        sheetBinding.gripperSpinner.visibility = View.GONE
                        sheetBinding.editText.visibility = View.VISIBLE
                        sheetBinding.editText.isEnabled = true
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

        bt.setContentView(sheetBinding.root)
        bt.show()
    }
}
