package net.bradball.android.androidapptemplate.extras

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class ViewBoundFragment<T: ViewBinding>: Fragment() {

    protected abstract fun inflate(inflater: LayoutInflater, container: ViewGroup?): T

    private var viewBinding: T? = null
    val views: T
        get() = viewBinding ?: throw IllegalAccessException("Cannot access view binding. view has not been created yet, or has already been destroyed")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = inflate(inflater, container)

        return views.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

}