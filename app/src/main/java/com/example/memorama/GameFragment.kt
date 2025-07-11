package com.example.memorama

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memorama.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
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
        // this creates a horizontal layout Manager
        binding.recyclerview.layoutManager = GridLayoutManager(context, 4)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<Item>()

        for (i in 1..20) {
            data.add(Item(R.drawable.ic_card_face_down, "Item $i"))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = Adapter(data)

        // Setting the Adapter with the recyclerview
        binding.recyclerview.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gameSize = GameFragmentArgs.fromBundle(requireArguments()).size
        val gameTheme = GameFragmentArgs.fromBundle(requireArguments()).theme
        binding.tvTime.text = gameSize
        binding.tvMovements.text = gameTheme
        handler.post(timerRunnable)

    }

    private fun getThemeCards(theme: String): List<Int> {
        return when (theme.lowercase()) {
            "navegadores" -> listOf(R.drawable.ic_brave, R.drawable.ic_chrome, R.drawable.ic_duckduckgo, R.drawable.ic_edge, R.drawable.ic_firefox, R.drawable.ic_maxthon, R.drawable.ic_opera, R.drawable.ic_safari, R.drawable.ic_torch, R.drawable.ic_vivaldi)
            "carros" -> listOf(R.drawable.ic_bora, R.drawable.ic_cupra, R.drawable.ic_cybertruck, R.drawable.ic_gtr, R.drawable.ic_honda, R.drawable.ic_jetta, R.drawable.ic_mustang, R.drawable.ic_prius, R.drawable.ic_ram, R.drawable.ic_tsuru)
            "bebidas" -> listOf(R.drawable.ic_bacardi, R.drawable.ic_corona, R.drawable.ic_don_julio, R.drawable.ic_four, R.drawable.ic_jimador, R.drawable.ic_jose_cuervo, R.drawable.ic_kosako, R.drawable.ic_rancho, R.drawable.ic_smirnoff, R.drawable.ic_tecate)
            else -> listOf(R.drawable.ic_default_card)
        }
    }

    private fun getGridDimensions(size: String): Pair<Int, Int> {
        return when (size) {
            "4x4" -> 4 to 4
            "4x5" -> 4 to 5
            else -> 4 to 4
        }
    }

    override fun onDestroyView() {
        handler.removeCallbacks(timerRunnable)
        super.onDestroyView()
        _binding = null
    }

}