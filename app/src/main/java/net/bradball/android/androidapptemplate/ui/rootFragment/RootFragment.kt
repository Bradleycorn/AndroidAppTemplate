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

        views.textView.text = when {
            viewModel.isAndroid11 -> "Ready to test FIAM Action Button on Android 11."
            else -> "Can't Reproduce FIAM Action Button defect."
        }

        views.instructions.text = when {
            viewModel.isAndroid11 -> "Send an In-App Message to this device and click on it's action button. " +
                    "Note that the browser does not open when the button is clicked." +
                    "\n\n Uncomment the <queries> node in the app manifest and try again and the button will work properly."
            else -> "You must install this app on a device running Android 11 to reproduce the defect."
        }
    }

}