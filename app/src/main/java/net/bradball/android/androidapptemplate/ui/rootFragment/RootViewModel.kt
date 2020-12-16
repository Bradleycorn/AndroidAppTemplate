package net.bradball.android.androidapptemplate.ui.rootFragment

import android.os.Build
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel

class RootViewModel @ViewModelInject constructor() : ViewModel() {

    val isAndroid11: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}
