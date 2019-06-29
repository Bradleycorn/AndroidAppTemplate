package net.bradball.android.androidapptemplate.di


import android.app.Application
import android.content.Context

import dagger.Module
import dagger.Provides
import net.bradball.android.androidapptemplate.TemplateApplication

@Module(includes = [ViewModelBuildersModule::class])
open class AppModule {

    @Provides
    open fun provideApplication(application: TemplateApplication): Application = application

    @Provides
    open fun provideContext(application: TemplateApplication): Context = application.applicationContext

}