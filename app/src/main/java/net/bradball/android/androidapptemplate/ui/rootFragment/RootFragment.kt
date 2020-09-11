package net.bradball.android.androidapptemplate.ui.rootFragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bradball.android.androidapptemplate.databinding.FragmentRootBinding
import net.bradball.android.androidapptemplate.extras.ViewBoundFragment

@AndroidEntryPoint
class RootFragment : ViewBoundFragment<FragmentRootBinding>() {

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentRootBinding {
        return FragmentRootBinding.inflate(inflater, container, false)
    }

    private val viewModel: RootViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}
