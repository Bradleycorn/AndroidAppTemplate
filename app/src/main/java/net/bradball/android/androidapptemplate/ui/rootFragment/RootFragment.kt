package net.bradball.android.androidapptemplate.ui.rootFragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_root.*

import net.bradball.android.androidapptemplate.R
import net.bradball.android.androidapptemplate.di.ViewModelFactory
import javax.inject.Inject

class RootFragment : DaggerFragment() {

    @Inject
    protected lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by viewModels<RootViewModel> { viewModelFactory }

    private lateinit var motionLayout: MotionLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var betType: TextView

    private val tabListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            Log.d("MENU", "Tab selected: ${tab.text}")
            toggleMotionLayout()
            toggleTabs(tab)
        }

        override fun onTabReselected(tab: TabLayout.Tab?) { /* noop */ }
        override fun onTabUnselected(tab: TabLayout.Tab?) { /* noop */ }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_root, container, false)

        motionLayout = view.findViewById(R.id.my_motion_layout)
        tabLayout = view.findViewById(R.id.bet_tyoe_list)
        betType = view.findViewById(R.id.bet_type)

        view.findViewById<ImageView>(R.id.back_arrow).apply {
            setOnClickListener {
                motionLayout.transitionToState(R.id.no_bet_type_selected)
                toggleTabs()
            }
        }

        setupTabs()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toggle_state -> toggleMotionLayout()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupTabs() {
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout.addOnTabSelectedListener(tabListener)

        toggleTabs()

    }

    private fun toggleTabs(tab: TabLayout.Tab? = null) {

        val tabs = listOf("Win", "Place", "Show", "Exacta", "Trifecta", "Superfecta")
        val modifiers = listOf("Straight", "Key", "Box", "Key Box", "Wheel")

        var tabsToShow: List<String>? = null

        if (tab == null) {
            tabsToShow = tabs
        } else {
            when (tab.text) {
                "Exacta",
                "Trifecta",
                "Superfecta" -> tabsToShow = modifiers
                else -> tabsToShow = null
            }
        }


        tabs.find {
            it == tab?.text
        }?.let {
            betType.text = tab?.text
        }



        tabsToShow?.let {
            tabLayout.removeAllTabs()
            it.forEach { tabName ->
                val tab = tabLayout.newTab().apply {
                    text = tabName
                }
                tabLayout.addTab(tab)
            }
        }
    }

    private fun toggleMotionLayout(): Boolean {

        motionLayout.transitionToState(R.id.bet_type_selected)

        return true
    }

}
