package com.example.memorama

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.memorama.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGoDetails.setOnClickListener {
            if (binding.etName.text.toString().isEmpty()) {
                Toast.makeText(context, R.string.enter_name, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val homeFragmentDirections =
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(binding.etName.text.toString())
            view.findNavController().navigate(homeFragmentDirections)
        }
    }

}