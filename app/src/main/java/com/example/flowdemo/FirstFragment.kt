package com.example.flowdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.flowdemo.databinding.FragmentFirstBinding


class FirstFragment<T: ViewDataBinding> : Fragment() {

    private lateinit var fragmentViewModel: FirstFragmentViewModel
    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        //data binding
        fragmentViewModel = ViewModelProviders.of(this).get(FirstFragmentViewModel::class.java)

        return FragmentFirstBinding.inflate(inflater, container, false).apply {
            binding = this
            viewModel = fragmentViewModel
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initWatcher()
    }

    private fun initWatcher() {
        fragmentViewModel.firstErrorFlow.observe(viewLifecycleOwner, Observer {
            binding.firstLayout.error = it
        })
    }

}