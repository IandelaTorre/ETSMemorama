package com.example.memorama

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.example.memorama.databinding.ActivityMainBinding
import com.example.memorama.ui.core.GameEndHandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.material.navigation.NavigationView
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private val signInRequestCode = 100
    var googleSignInAccount: GoogleSignInAccount? = null

    fun requestDriveSignIn() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        val client = GoogleSignIn.getClient(this, signInOptions)
        startActivityForResult(client.signInIntent, signInRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == signInRequestCode && resultCode == Activity.RESULT_OK) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener {
                    googleSignInAccount = it
                    backupDatabaseToDrive()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al iniciar sesión en Drive", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun backupDatabaseToDrive() {
        val dbPath = getDatabasePath("memoramagame.db")
        if (!dbPath.exists()) {
            Toast.makeText(this, "No hay base de datos para respaldar", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = GoogleAccountCredential.usingOAuth2(
            this, listOf(DriveScopes.DRIVE_FILE)
        )
        credential.selectedAccount = googleSignInAccount?.account

        val driveService = Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Memorama").build()

        Thread {
            try {
                val fileMetadata = com.google.api.services.drive.model.File()
                fileMetadata.name = "respaldo_memorama.db"

                val fileContent = FileContent("application/x-sqlite3", dbPath)

                driveService.files().create(fileMetadata, fileContent).execute()

                runOnUiThread {
                    Toast.makeText(this, "Respaldo subido a Drive", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Log.i("MainActivity", "Error al respaldar: ${e.message}")
                    Toast.makeText(this, "Error al respaldar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    fun restoreDatabaseFromDrive() {
        val credential = GoogleAccountCredential.usingOAuth2(
            this, listOf(DriveScopes.DRIVE_FILE)
        )
        credential.selectedAccount = googleSignInAccount?.account

        val driveService = Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Memorama").build()

        Thread {
            try {
                // Buscar el archivo por nombre
                val result = driveService.files().list()
                    .setQ("name = 'respaldo_memorama.db' and mimeType = 'application/x-sqlite3'")
                    .setSpaces("drive")
                    .execute()

                val file = result.files.firstOrNull()

                if (file == null) {
                    runOnUiThread {
                        Toast.makeText(this, "No se encontró el archivo en Drive", Toast.LENGTH_SHORT).show()
                    }
                    return@Thread
                }

                // Descargar el archivo
                val outputFile = getDatabasePath("memoramagame.db")
                driveService.files().get(file.id)
                    .executeMediaAndDownloadTo(outputFile.outputStream())

                runOnUiThread {
                    Toast.makeText(this, "Base de datos restaurada con éxito", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Log.i("MainActivity", "Error al restaurar: ${e.message}")
                    Toast.makeText(this, "Error al restaurar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // ✅ Restaurar sesión si ya estaba iniciada antes
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
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

        // Manejo de clics del menú lateral
        navView.setNavigationItemSelectedListener { menuItem ->
            val navController = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment)
                ?.findNavController()
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navController?.navigate(R.id.action_global_homeFragment)
                }
                R.id.nav_end -> {
                    val navHostFragment =
                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    val currentFragment = navHostFragment?.childFragmentManager?.fragments?.firstOrNull()

                    if (currentFragment is GameEndHandler) {
                        currentFragment.onGameEndEarly()
                    } else {
                        Toast.makeText(this, "Solo puedes terminar el juego desde la pantalla de juego", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.nav_solution -> {
                    val navHostFragment =
                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    val currentFragment = navHostFragment?.childFragmentManager?.fragments?.firstOrNull()

                    if (currentFragment is GameEndHandler) {
                        currentFragment.onRevealSolution()
                    } else {
                        Toast.makeText(this, "Solo puedes ver la solución desde la pantalla de juego", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.nav_score -> {
                    navController?.navigate(R.id.action_global_statsFragment)
                }
                R.id.nav_backup -> {
                    navController?.navigate(R.id.backupRestoreFragment)
                }
                R.id.nav_exit -> {
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}