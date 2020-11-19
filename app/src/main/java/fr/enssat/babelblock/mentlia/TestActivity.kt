package fr.enssat.babelblock.mentlia

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import fr.enssat.babelblock.mentlia.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    private lateinit var viewModel: TestViewModel
    private lateinit var viewModelFactory: TestViewModelFactory

    private val REQUEST_CODE_RECORD_AUDIO_PERMISSION = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelFactory = TestViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TestViewModel::class.java)
        viewModel.getViewState().observe(this, { viewState ->
            render(viewState)
        })

        binding.speechRecognizerTask.setTaskBlock(viewModel.speechRecognizerBlock)
        binding.translateTask.setTaskBlock(viewModel.translateBlock)
        binding.ttsTask.setTaskBlock(viewModel.ttsBlock)
        binding.runBtn.setOnClickListener {
            viewModel.run()
        }
    }

    private fun render(viewState: TestViewModel.ViewState?) {
        //Timber.e(viewState.toString())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_RECORD_AUDIO_PERMISSION) {
            viewModel.permissionToRecordAudio = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }

        if (viewModel.permissionToRecordAudio) {
            //binding.micBtn.performClick()
        }
    }
}