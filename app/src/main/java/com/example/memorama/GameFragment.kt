package com.example.memorama

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memorama.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

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
    }

}