package net.bradball.android.androidapptemplate.ui.rootFragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerFragment

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

    private var tabsToShow: List<String>? = null
    private var selectedTabPosition: Int? = null
    private var isReselected: Boolean = false

    private val tabListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            Log.d("MENU", "Tab selected: ${tab.text}")
            toggleMotionLayout()
            if (tab.position != selectedTabPosition && !isReselected) {
                toggleTabs(tab)
            }
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            toggleTabs(tab, true)

        }
        override fun onTabUnselected(tab: TabLayout.Tab?) {
            /* noop */ }
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

    private fun toggleTabs(tab: TabLayout.Tab? = null, reselected: Boolean = false) {
        isReselected = reselected
        val tabs = listOf("Win", "Place", "Show", "Exacta", "Trifecta", "Superfecta")
        val modifiers = listOf("Straight", "Key", "Box", "Key Box", "Wheel")

        if (tab == null) {
            tabsToShow = tabs

        } else {
            when (tab.text) {
                "Exacta",
                "Trifecta",
                "Superfecta" -> {
                    tabsToShow = modifiers
                    selectedTabPosition = tab.position

                }
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

        if (tabLayout.tabCount == modifiers.size) {
            tabLayout.setSelectedTabIndicator(R.drawable.rectangle_modifier_indicator)
            tabLayout.setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_CENTER)
        } else {
            tabLayout.setSelectedTabIndicator(R.drawable.line_tab_indicator)
            tabLayout.setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_BOTTOM)
        }


        if (tabLayout.tabCount == tabs.size && selectedTabPosition != null) {
            var tab = tabLayout.getTabAt(selectedTabPosition!!)
            if (tab != null) {
                tab.select()
                selectedTabPosition = null
            }
        }



    }

    private fun toggleMotionLayout(): Boolean {
        if (tabsToShow != null) {
            motionLayout.transitionToState(R.id.bet_type_selected)
        }

        return true
    }

}
