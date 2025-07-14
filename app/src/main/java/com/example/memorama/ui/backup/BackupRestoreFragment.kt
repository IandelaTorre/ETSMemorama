package com.example.memorama.ui.backup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.memorama.MainActivity
import com.example.memorama.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes

class BackupRestoreFragment : Fragment(R.layout.fragment_backup_restore) {

    private val signInRequestCode = 101
    private var actionAfterSignIn: (() -> Unit)? = null

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        val btnBackup = view.findViewById<Button>(R.id.btnBackup)
        val btnRestore = view.findViewById<Button>(R.id.btnRestore)

        btnBackup.setOnClickListener {
            actionAfterSignIn = { (requireActivity() as MainActivity).backupDatabaseToDrive() }
            checkAndSignIn()

        }

        btnRestore.setOnClickListener {
            actionAfterSignIn = { (requireActivity() as MainActivity).restoreDatabaseFromDrive() }
            checkAndSignIn()
        }
    }

    private fun checkAndSignIn() {
        val activity = requireActivity() as MainActivity
        val existingAccount = GoogleSignIn.getLastSignedInAccount(activity)

        if (existingAccount != null && GoogleSignIn.hasPermissions(
                existingAccount,
                Scope(DriveScopes.DRIVE_FILE)
            )
        ) {
            actionAfterSignIn?.invoke()
        } else {
            val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                .build()

            val client = GoogleSignIn.getClient(requireActivity(), signInOptions)
            startActivityForResult(client.signInIntent, signInRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == signInRequestCode && resultCode == Activity.RESULT_OK) {
            GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener {
                (requireActivity() as MainActivity).googleSignInAccount = it
                actionAfterSignIn?.invoke()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
