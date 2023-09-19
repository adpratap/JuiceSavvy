package com.noreplypratap.juicesavvy.ui.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.noreplypratap.juicesavvy.databinding.FragmentAppListBinding
import com.noreplypratap.juicesavvy.ui.adapters.AppListAdapter
import com.noreplypratap.juicesavvy.viewmodels.AppListViewModel

class AppListFragment : Fragment() {

    private val viewModel: AppListViewModel by viewModels()

    private var _binding: FragmentAppListBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAppListBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataNow()
        loadAppList()

        binding.refreshList.setOnRefreshListener {
            loadAppList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        loadAppList()
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadAppList() {
        viewModel.apply {
            requireContext().getDataByApp()
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("NotifyDataSetChanged")
    private fun getDataNow() {
        viewModel.data.observe(viewLifecycleOwner) {
            it.let { listOfApps ->
                val appListAdapter = AppListAdapter(listOfApps)
                binding.rvList.apply {
                    adapter = appListAdapter
                }
                appListAdapter.notifyDataSetChanged()
                binding.refreshList.isRefreshing = false
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}