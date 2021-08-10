package com.makeevrserg.navigator.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.makeevrserg.navigator.events.EventHandler
import java.lang.IllegalArgumentException

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val TAG = "MainViewModel"


    private var _address: String? = null

    //Когда true observer видит это и запускает интент с картой.
    private val _mapEvent = MutableLiveData<EventHandler>()
    public val mapEvent: LiveData<EventHandler>
        get() = _mapEvent


    private val _errorHandlerMessage = MutableLiveData<EventHandler>()
    public val errorHandlerMessage: LiveData<EventHandler>
        get() = _errorHandlerMessage

    private val _disablePlaces = MutableLiveData<Boolean>(true)
    public val disablePlaces: LiveData<Boolean>
        get() = _disablePlaces

    //Инициализация GoogleMapsAPI для
    //Если ключ не указан, то фрагемент с поиском не будет указан
    private fun initGooglePlaces(application: Application) {
        try {
            Places.initialize(application, "")
            Places.createClient(application)
            _disablePlaces.value = false
        } catch (e: IllegalArgumentException) {
            _disablePlaces.value = true
            _errorHandlerMessage.value = EventHandler(
                "Ключ не подходит. Поиск по улицам недоступен.",
                EventHandler.EventType.ERROR
            )
        }
    }

    init {
        initGooglePlaces(application)
    }

    public fun startShowMap() {
        if (_address == null || _address!!.isEmpty()) {
            _errorHandlerMessage.value =
                EventHandler("Вы не выбрали место", EventHandler.EventType.ERROR)
            return
        }
        _mapEvent.value = EventHandler(_address!!, EventHandler.EventType.ADDRESS)
    }

    //Демонстрация работы без ключа
    public fun testShowMap() {
        _address = "ул. Социалистическая, 74, Ростов-на-Дону, Ростовская обл.,"
        startShowMap()
    }

    public fun placeSelectionListener() = object : PlaceSelectionListener {
        override fun onPlaceSelected(place: Place) {
            _address = place.name
        }

        override fun onError(status: Status) {
            //Если пользователь сам отменил или прервал ввод, то это не будет считаться за ошибку
            if (!status.isCanceled || !status.isInterrupted)
                _errorHandlerMessage.value =
                    EventHandler("Ошибка: ${status.status}", EventHandler.EventType.ERROR)
        }

    }
}