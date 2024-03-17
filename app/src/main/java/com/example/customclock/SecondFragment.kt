package com.example.customclock

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.customclock.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding: FragmentSecondBinding
        get() = _binding ?: throw RuntimeException("FragmentSecondBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clockView1.setTimeZone("Europe","Moscow")
        binding.clockView2.setTimeZone("America","New_York")
        binding.clockView3.setTimeZone("Asia","Tokyo")
        binding.clockView4.setTimeZone("Europe","Berlin")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}