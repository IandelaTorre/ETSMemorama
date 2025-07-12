package com.example.memorama

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.memorama.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    // Puedes obtener esto desde SharedPreferences o alguna fuente dinámica
    private val userName = "Ian Pérez"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navView = findViewById(R.id.nav_view)

        // Asignar la Toolbar como ActionBar
        setSupportActionBar(toolbar)

        // Establecer el título dinámico
        supportActionBar?.title = "Bienvenido"

        // Toggle del Drawer
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val headerView = binding.navView.getHeaderView(0)
        val headerTextView = headerView.findViewById<TextView>(R.id.header_username)
        headerTextView.text = userName

        // Manejo de clics del menú lateral
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_end -> {
                    Toast.makeText(this, "Terminar juego", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_solution -> {
                    Toast.makeText(this, "Ver solucion", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_score -> {
                    Toast.makeText(this, "Ver puntuación", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_backup -> {
                    Toast.makeText(this, "Respaldo y restauración", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_exit -> {
                    Toast.makeText(this, "Salir", Toast.LENGTH_SHORT).show()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}