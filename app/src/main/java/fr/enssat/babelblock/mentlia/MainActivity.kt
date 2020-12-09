package fr.enssat.babelblock.mentlia

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fr.enssat.babelblock.mentlia.databinding.ActivityMainBinding
import org.imaginativeworld.oopsnointernet.NoInternetDialog
import org.json.JSONArray


class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_RECORD_AUDIO_PERMISSION = 10
        const val LAST_TASKBLOCK_CHAIN_PREF_NAME = "last_taskblock_chain"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var viewModelFactory: MainViewModelFactory
    private var loadingDialog: AlertDialog? = null
    private var noInternetDialog: NoInternetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        viewModelFactory = MainViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        val preferences = getPreferences(Context.MODE_PRIVATE)
        if (preferences.contains(LAST_TASKBLOCK_CHAIN_PREF_NAME)) {
            val jsonArray = JSONArray(preferences.getString(LAST_TASKBLOCK_CHAIN_PREF_NAME, null))
            viewModel.taskBlockChain.fromJSON(jsonArray, applicationContext)
        }

        binding.addBtn?.setOnClickListener {
            val fragment = AddTaskBlockFragment.newInstance(true)
            fragment.show(supportFragmentManager, "fragment_add_task")
        }

        viewModel.viewState.observe(this, { refresh() })
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.run -> {
                    runTaskBlockChain()
                    true
                }
                R.id.reset -> {
                    MaterialAlertDialogBuilder(this)
                        .setMessage(R.string.confirm_delete_all_blocks)
                        .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int ->
                            getPreferences(Context.MODE_PRIVATE).edit().remove(
                                LAST_TASKBLOCK_CHAIN_PREF_NAME
                            ).apply()
                            viewModel.taskBlockChain.removeAll()
                        }
                        .setNegativeButton(R.string.no, null)
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            binding.activityMainDrawerLayout.openDrawer(GravityCompat.START)
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        noInternetDialog = NoInternetDialog.Builder(this).apply {
            cancelable = false
            noInternetConnectionTitle = getString(R.string.no_internet_connection_title)
            noInternetConnectionMessage = getString(R.string.no_internet_connection_message)
            pleaseTurnOnText = getString(R.string.please_turn_on_text)
            wifiOnButtonText = getString(R.string.wifi_on_button_text)
            mobileDataOnButtonText = getString(R.string.mobile_data_on_button_text)
            onAirplaneModeTitle = getString(R.string.on_airplane_mode_title)
            onAirplaneModeMessage = getString(R.string.on_airplane_mode_message)
            pleaseTurnOffText = getString(R.string.please_turn_off_text)
            airplaneModeOffButtonText = getString(R.string.airplane_mode_off_button_text)
        }.build()
    }

    override fun onPause() {
        super.onPause()
        noInternetDialog?.destroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_RECORD_AUDIO_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runTaskBlockChain()
            } else {
                MaterialAlertDialogBuilder(this)
                    .setMessage(R.string.record_audio_persmission_not_allowed)
                    .setPositiveButton(android.R.string.ok, null)
                    .setNeutralButton(getString(R.string.open_settings)) { _: DialogInterface, _: Int ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .show()
            }
        }
    }

    private fun runTaskBlockChain() {
        val permissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {

            if (viewModel.canBeRun()) {
                val preferences = getPreferences(Context.MODE_PRIVATE)
                preferences.edit().putString(
                    LAST_TASKBLOCK_CHAIN_PREF_NAME,
                    viewModel.taskBlockChain.toJSON().toString()
                ).apply()
                viewModel.run()
            } else {
                MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.invalid_taskblock_chain))
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_CODE_RECORD_AUDIO_PERMISSION
            )
        }
    }

    private fun refresh() {
        val viewState = viewModel.viewState.value!!
        if (viewState.preparingExecution) {
            this.createLoadingDialogIfNecessary()
            loadingDialog!!.setContentView(
                viewState.taskBlock!!.getPrepareExecutionView(
                    layoutInflater, resources
                )
            )

        } else if (viewState.executing) {
            this.createLoadingDialogIfNecessary()
            loadingDialog!!.setContentView(
                viewState.taskBlock!!.getExecuteView(
                    layoutInflater, resources
                )
            )

        } else {
            if (loadingDialog != null) {
                loadingDialog!!.dismiss()
                loadingDialog = null
            }
        }

        if (viewState.error != null) {
            MaterialAlertDialogBuilder(this)
                .setMessage(viewState.error.userErrorMessage)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }

    private fun createLoadingDialogIfNecessary() {
        if (loadingDialog == null) {
            loadingDialog = MaterialAlertDialogBuilder(this)
                .setCancelable(false)
                .show()
        }
    }
}