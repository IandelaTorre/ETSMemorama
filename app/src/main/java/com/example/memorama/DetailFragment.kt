package com.example.memorama

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.memorama.databinding.FragmentDetailBinding
import androidx.navigation.findNavController

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = DetailFragmentArgs.fromBundle(requireArguments()).username

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Hola, $username"

        binding.btnGoGame.setOnClickListener {
            val detailFragmentDirections = DetailFragmentDirections.actionDetailFragmentToGameFragment(binding.spSize.selectedItem.toString(), binding.spThemes.selectedItem.toString())
            view.findNavController().navigate(detailFragmentDirections)
        }

        ArrayAdapter.createFromResource(view.context, R.array.array_theme, android.R.layout.simple_spinner_item).also {
            adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spThemes.adapter = adapter
        }

        ArrayAdapter.createFromResource(view.context, R.array.array_size, android.R.layout.simple_spinner_item).also {
                adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spSize.adapter = adapter
        }
    }

}