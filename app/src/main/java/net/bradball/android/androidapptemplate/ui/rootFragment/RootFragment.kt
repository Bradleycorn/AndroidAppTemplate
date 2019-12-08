package net.bradball.android.androidapptemplate.ui.rootFragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import dagger.android.support.DaggerFragment

import net.bradball.android.androidapptemplate.R
import net.bradball.android.androidapptemplate.di.ViewModelFactory
import net.bradball.android.androidapptemplate.ui.MainActivity
import javax.inject.Inject

class RootFragment : DaggerFragment() {

    @Inject
    protected lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by viewModels<RootViewModel> { viewModelFactory }

    private lateinit var locationButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_root, container, false)

        locationButton = view.findViewById<Button>(R.id.start_location).apply {
            setOnClickListener {
                val test = (requireActivity() as MainActivity).locationFlow().asLiveData().observe(viewLifecycleOwner, Observer {
                  Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                })
            }
        }

        return view
    }

}