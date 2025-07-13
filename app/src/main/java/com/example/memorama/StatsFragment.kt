package com.example.memorama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.memorama.databinding.FragmentStatsBinding
import com.example.memorama.db.GameStatsDatabaseHelper

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showAllStats()
    }

    private fun showAllStats() {
        val dbHelper = GameStatsDatabaseHelper(requireContext())
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${GameStatsDatabaseHelper.TABLE_NAME}", null)

        val builder = StringBuilder()

        if (cursor.moveToFirst()) {
            builder.appendLine("ID | UUID | Nombre | Tema | Tamaño | Tiempo | Intentos | Terminó | Ganó")
            builder.appendLine("--------------------------------------------------------------------------")

            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val uuid = cursor.getString(cursor.getColumnIndexOrThrow("uuid"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val tema = cursor.getString(cursor.getColumnIndexOrThrow("theme"))
                val tamano = cursor.getString(cursor.getColumnIndexOrThrow("size_map"))
                val tiempo = cursor.getString(cursor.getColumnIndexOrThrow("time"))
                val intentos = cursor.getString(cursor.getColumnIndexOrThrow("movements"))
                val termino = cursor.getInt(cursor.getColumnIndexOrThrow("end_game")) == 1
                val gano = cursor.getInt(cursor.getColumnIndexOrThrow("win_game")) == 1

                builder.appendLine("$id | ${uuid.take(5)}... | $nombre | $tema | $tamano | $tiempo | $intentos | ${termino.toYesNo()} | ${gano.toYesNo()}")
            } while (cursor.moveToNext())
        } else {
            builder.appendLine("No hay registros de partidas aún.")
        }

        cursor.close()
        db.close()

        binding.tvStats.text = builder.toString()
    }

    private fun Boolean.toYesNo(): String = if (this) "Sí" else "No"
}
