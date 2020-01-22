package com.application.weatherforecast.presentation.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.application.weatherforecast.BuildConfig
import com.application.weatherforecast.R
import com.application.weatherforecast.presentation.ui.fragments.CurrentCityForecastFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment? ?: return

        val navController = host.navController

        val drawerLayout: DrawerLayout? = drawerLayout
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.home_dest),
            drawerLayout
        )

        setupActionBar(navController, appBarConfiguration)
        setupNavigationMenu(navController)

    }

    private fun setupNavigationMenu(navController: NavController) {
        val sideNavView = navView
        sideNavView?.setupWithNavController(navController)
    }

    private fun setupActionBar(
        navController: NavController,
        appBarConfig: AppBarConfiguration
    ) {
        setupActionBarWithNavController(navController, appBarConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.navHostFragment))
                || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.navHostFragment).navigateUp(appBarConfiguration)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment)
        val childFragments = navHostFragment?.childFragmentManager?.fragments
        childFragments?.forEach { fragment ->
            if (fragment is CurrentCityForecastFragment) {
                fragment.activityResult(requestCode, resultCode)
            }
        }
    }

    fun showSnackbar(mainTextStringId: Int, actionStringId: Int) {
        val snack = Snackbar.make(
            findViewById(android.R.id.content),
            getString(mainTextStringId),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(actionStringId)) {
                // Build intent that displays the App settings screen.
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID, null
                )
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        val text =
            snack.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        text.setTextColor(ContextCompat.getColor(this, R.color.white))
        snack.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment)
        val childFragments = navHostFragment?.childFragmentManager?.fragments
        childFragments?.forEach { fragment ->
            if (fragment is CurrentCityForecastFragment) {
                fragment.requestPermissionsResult(requestCode, grantResults)
            }
        }
    }
}
