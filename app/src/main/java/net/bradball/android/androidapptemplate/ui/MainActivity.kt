package net.bradball.android.androidapptemplate.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import net.bradball.android.androidapptemplate.R

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.app_bar)
        val navController = findNavController(R.id.nav_host_fragment)
        setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }
}
