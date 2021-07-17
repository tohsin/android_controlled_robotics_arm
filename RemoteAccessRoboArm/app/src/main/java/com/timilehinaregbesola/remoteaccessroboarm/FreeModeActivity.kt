package com.timilehinaregbesola.remoteaccessroboarm

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.goodiebag.protractorview.ProtractorView
import com.goodiebag.protractorview.ProtractorView.OnProtractorViewChangeListener
import com.timilehinaregbesola.remoteaccessroboarm.databinding.ActivityFreeModeBinding
import com.timilehinaregbesola.remoteaccessroboarm.model.Command
import com.timilehinaregbesola.remoteaccessroboarm.model.CommandAndRepeat
import com.timilehinaregbesola.remoteaccessroboarm.model.SocketCommand
import com.timilehinaregbesola.remoteaccessroboarm.model.socketCommandToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.Socket

/**[No of command;No of repeat]:[servo;angle]:[...
 Servo commands:
 Base - 1
 Left - 2
 Right - 3
 Grip - 4
 Ungrip -5
 Clock - 6

 Example commands in free mode:
 1;1:4;0
 1;1:5;0 **/

class FreeModeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFreeModeBinding
    private var isGripped = false

    private val iPandPort = "192.168.137.239:21567"
    var myAppSocket: Socket? = null
    var wifiModuleIp = ""
    var wifiModulePort = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFreeModeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        wifiModuleIp = intent.getStringExtra("ipAdress")!!
        Log.d("TEST", "IP:$wifiModuleIp")
        setProtractorViewChangeListeners()
        setUpdateButtonClickListeners()

        binding.baseSlider.addOnChangeListener { _, value, _ ->
            // Responds to when slider's value is changed
            binding.txtBaseAngle.text = value.toString()
        }
        binding.btnGrab.setOnClickListener {
            isGripped = !isGripped
            val cmr = CommandAndRepeat(commandNo = 1, repeatNo = 1)
            if (isGripped) {
                binding.btnGrab.text = "Ungrab"
                binding.btnGrab.setBackgroundColor(Color.GRAY)
                val command = Command(commandId = 4, angle = 0)
                val socketCommand = SocketCommand(commandRepeat = cmr, commands = listOf(command))
                launchSocket(socketCommand = socketCommand)
            } else {
                binding.btnGrab.text = "Grab"
                binding.btnGrab.setBackgroundColor(Color.BLUE)
                // 1;1:5;0
                val command = Command(commandId = 5, angle = 0)
                val socketCommand = SocketCommand(commandRepeat = cmr, commands = listOf(command))
                launchSocket(socketCommand = socketCommand)
            }
        }
    }

    private fun setProtractorViewChangeListeners() {
        binding.leftProtractor.onProtractorViewChangeListener =
            object : OnProtractorViewChangeListener {
                override fun onProgressChanged(pv: ProtractorView, progress: Int, b: Boolean) {
                    // protractorView's getters can be accessed using pv instance.
                    binding.txtLeftAngle.text = progress.toString()
                }

                override fun onStartTrackingTouch(pv: ProtractorView) {}
                override fun onStopTrackingTouch(pv: ProtractorView) {}
            }

        binding.rightProtractor.onProtractorViewChangeListener = object : OnProtractorViewChangeListener {
            override fun onProgressChanged(pv: ProtractorView, progress: Int, b: Boolean) {
                // protractorView's getters can be accessed using pv instance.
                binding.txtRightAngle.text = progress.toString()
            }

            override fun onStartTrackingTouch(pv: ProtractorView) {}
            override fun onStopTrackingTouch(pv: ProtractorView) {}
        }
    }

    private fun setUpdateButtonClickListeners() {
        val cmr = CommandAndRepeat(commandNo = 1, repeatNo = 1)
        binding.leftBtnUpdate.setOnClickListener {
            val angle = binding.txtLeftAngle.text.toString()
            val command = Command(commandId = 2, angle = angle.toInt())
            val socketCommand = SocketCommand(commandRepeat = cmr, commands = listOf(command))
            launchSocket(socketCommand = socketCommand)
        }
        binding.rightBtnUpdate.setOnClickListener {
            val angle = binding.txtRightAngle.text.toString()
            val command = Command(commandId = 3, angle = angle.toInt())
            val socketCommand = SocketCommand(commandRepeat = cmr, commands = listOf(command))
            launchSocket(socketCommand = socketCommand)
        }
        binding.baseBtnUpdate.setOnClickListener {
            val angle = binding.txtBaseAngle.text.toString()
            val command = Command(commandId = 1, angle = angle.toFloat().toInt())
            val socketCommand = SocketCommand(commandRepeat = cmr, commands = listOf(command))
            launchSocket(socketCommand = socketCommand)
        }
    }

    private fun launchSocket(socketCommand: SocketCommand) {
        getIPandPort()
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

    fun getIPandPort() {
        Log.d("MYTEST", "IP String: $iPandPort")
        val temp = iPandPort.split(":").toTypedArray()
        wifiModulePort = Integer.valueOf(temp[1])
        Log.d("MY TEST", "IP:$wifiModuleIp")
        Log.d("MY TEST", "PORT:$wifiModulePort")
    }
}
