package com.example.memorama

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memorama.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001
    }


    private lateinit var adapter: Adapter
    private lateinit var cards: List<Item>
    private var selectedIndex1: Int? = null
    private var selectedIndex2: Int? = null
    private var movements = 0
    private var secondsElapsed = 0

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            secondsElapsed++
            binding.tvTime.text = "Tiempo: ${secondsElapsed}s"
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestNotificationPermission()
        createNotificationChannel()

        val gameSize = GameFragmentArgs.fromBundle(requireArguments()).size
        val gameTheme = GameFragmentArgs.fromBundle(requireArguments()).theme

        // Determinar tamaño del tablero
        val (columns, rows) = when (gameSize) {
            "4x4" -> 4 to 4
            "4x5" -> 4 to 5
            else -> 4 to 4
        }

        val totalCards = columns * rows
        val themeImages = getThemeCards(gameTheme).shuffled()
        val neededPairs = totalCards / 2

        val selectedImages = themeImages.take(neededPairs)
        cards = (selectedImages + selectedImages)
            .mapIndexed { index, resId -> Item(resId, "card_$index") }
            .shuffled()

        adapter = Adapter(cards) { position ->
            val card = cards[position]

            if (card.isFlipped || card.isMatched || selectedIndex2 != null) return@Adapter

            card.isFlipped = true
            adapter.notifyItemChanged(position)

            if (selectedIndex1 == null) {
                selectedIndex1 = position
            } else {
                selectedIndex2 = position
                movements++
                updateMovements()

                val firstCard = cards[selectedIndex1!!]
                val secondCard = cards[selectedIndex2!!]

                if (firstCard.imageResId == secondCard.imageResId) {
                    firstCard.isMatched = true
                    secondCard.isMatched = true
                    selectedIndex1 = null
                    selectedIndex2 = null
                    if (cards.all { it.isMatched }) {
                        showWinDialog()
                    }
                } else {
                    handler.postDelayed({
                        firstCard.isFlipped = false
                        secondCard.isFlipped = false
                        adapter.notifyItemChanged(selectedIndex1!!)
                        adapter.notifyItemChanged(selectedIndex2!!)
                        selectedIndex1 = null
                        selectedIndex2 = null
                    }, 1000)
                }
            }
        }

        binding.recyclerview.layoutManager = GridLayoutManager(requireContext(), columns)
        binding.recyclerview.adapter = adapter

        updateMovements()
        handler.post(timerRunnable)

        // Detener temporizador al salir
        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                handler.removeCallbacks(timerRunnable)
            }
        })
    }

    private fun updateMovements() {
        binding.tvMovements.text = "Movimientos: $movements"
    }

    private fun getThemeCards(theme: String): List<Int> {
        return when (theme.lowercase()) {
            "navegadores" -> listOf(R.drawable.ic_brave, R.drawable.ic_chrome, R.drawable.ic_duckduckgo, R.drawable.ic_edge, R.drawable.ic_firefox, R.drawable.ic_maxthon, R.drawable.ic_opera, R.drawable.ic_safari, R.drawable.ic_torch, R.drawable.ic_vivaldi)
            "carros" -> listOf(R.drawable.ic_bora, R.drawable.ic_cupra, R.drawable.ic_cybertruck, R.drawable.ic_gtr, R.drawable.ic_honda, R.drawable.ic_jetta, R.drawable.ic_mustang, R.drawable.ic_prius, R.drawable.ic_ram, R.drawable.ic_tsuru)
            "bebidas" -> listOf(R.drawable.ic_bacardi, R.drawable.ic_corona, R.drawable.ic_don_julio, R.drawable.ic_four, R.drawable.ic_jimador, R.drawable.ic_jose_cuervo, R.drawable.ic_kosako, R.drawable.ic_rancho, R.drawable.ic_smirnoff, R.drawable.ic_tecate)
            else -> listOf(R.drawable.ic_default_card)
        }
    }

    private fun showWinDialog() {
        val title = (requireActivity() as AppCompatActivity).supportActionBar?.title?.toString()
        val userName = title?.substringAfter("Hola, ") ?: "Jugador"

        val message = "¡¡Felicidades $userName!!\nTerminaste el juego en un tiempo de $secondsElapsed segundos y $movements movimientos."

        sendWinNotification(userName)

        handler.removeCallbacks(timerRunnable)

        AlertDialog.Builder(requireContext())
            .setTitle("Juego finalizado")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Volver") { dialog, _ ->
                dialog.dismiss()
                findNavController().popBackStack()
            }
            .show()
    }

    private fun sendWinNotification(userName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted = ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionGranted) {
                Toast.makeText(
                    requireContext(),
                    "Permiso de notificaciones denegado, no se mostrará la notificación",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        val notificationManager = NotificationManagerCompat.from(requireContext())

        val builder = NotificationCompat.Builder(requireContext(), "MEMORAMA_CHANNEL")
            .setSmallIcon(R.drawable.ic_trophy)
            .setContentTitle("¡¡Felicidades $userName!!")
            .setContentText("Ganaste esta partida")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1, builder.build())
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "GameWinChannel"
            val descriptionText = "Canal para notificaciones de victoria"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("MEMORAMA_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permiso concedido (puedes mostrar un Toast o registrar algo si quieres)
            } else {
                Toast.makeText(requireContext(), "No se podrán mostrar notificaciones", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(timerRunnable)
        _binding = null
    }
}
