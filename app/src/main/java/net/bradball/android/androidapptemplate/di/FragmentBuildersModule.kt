package net.bradball.android.androidapptemplate.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.bradball.android.androidapptemplate.ui.rootFragment.RootFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    /**
     * List Fragments here.
     *
     * If you want to create submodules for a group of fragments,
     * can can provide a module to the annotation on the parent
     * fragment, like this:
     *
     * @ContributesAndroidInjector(modules = [MySubModule::class])
     */

    @ContributesAndroidInjector
    abstract fun contributeRootFragment(): RootFragment

}
