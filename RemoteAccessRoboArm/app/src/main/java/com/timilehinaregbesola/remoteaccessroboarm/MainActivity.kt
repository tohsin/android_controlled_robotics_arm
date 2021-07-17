package com.timilehinaregbesola.remoteaccessroboarm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.timilehinaregbesola.remoteaccessroboarm.databinding.ActivityMainBinding
import com.timilehinaregbesola.remoteaccessroboarm.recording.AutomateActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnFreeMode.setOnClickListener {
            val intent = Intent(this, FreeModeActivity::class.java)
            intent.putExtra("ipAdress", binding.edtIpAddress.text.toString())
            startActivity(intent)
        }
        binding.btnAutomate.setOnClickListener {
            val intent = Intent(this, AutomateActivity::class.java)
            intent.putExtra("ipAdress", binding.edtIpAddress.text.toString())
            startActivity(intent)
        }
    }
}
