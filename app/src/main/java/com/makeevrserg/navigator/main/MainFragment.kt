package com.makeevrserg.navigator.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.makeevrserg.navigator.R
import com.makeevrserg.navigator.databinding.FragmentMainBinding
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private val TAG = "MainFragment"
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentMainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )

        val application = requireNotNull(this.activity).application

        val viewModelFactory = SchedulerViewModelFactory(application)
        val mainViewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(MainViewModel::class.java)

        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this



        setAutocompleteFragment()

        //Эвент, который срабатывает после неверного вввода или ошибки
        viewModel.errorHandlerMessage.observe(viewLifecycleOwner, {
            Toast.makeText(application, it.getContent() ?: return@observe, Toast.LENGTH_SHORT)
                .show()
        })

        viewModel.mapEvent.observe(viewLifecycleOwner, { isShowMap ->
            //https://developers.google.com/maps/documentation/urls/android-intents#kotlin
            val gmmIntentUri = Uri.parse("geo:0,0?q=${isShowMap.getContent() ?: return@observe}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            //Указываем интент GoogleMaps
            mapIntent.setPackage("com.google.android.apps.maps")
            //Необходимо убедиться что GoogleMaps установлен
            mapIntent.resolveActivity(application.packageManager)?.let {
                startActivity(mapIntent)
            }

        })

        return binding.root
    }

    private fun setAutocompleteFragment() {
        // Widge autocomplet'а для поиска места по введенному тексту
        val autocompleteFragment =
            (childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment)

        // Указываем, какие данные ннобходимо вернуть
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS
            )
        )
        // Указываем листенер после выбора места
        autocompleteFragment.setOnPlaceSelectedListener(viewModel.placeSelectionListener())
    }

}