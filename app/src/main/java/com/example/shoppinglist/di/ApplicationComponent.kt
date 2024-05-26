package com.example.shoppinglist.di

import android.app.Application
import com.example.shoppinglist.presentation.MainActivity
import com.example.shoppinglist.presentation.ShopItemFragment
import com.example.shoppinglist.presentation.ShopListApp
import dagger.BindsInstance
import dagger.Component


@ApplicationScope
@Component(
    modules = [DataModule::class, ViewModelModule::class]
)
interface ApplicationComponent {

    fun inject(application: ShopListApp)

    fun inject(fragment: ShopItemFragment)

    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory{
        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }

}