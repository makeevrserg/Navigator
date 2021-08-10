package com.makeevrserg.navigator.events

class EventHandler (private val content: String,private val type:EventType) {
    enum class EventType{
        ERROR,ADDRESS
    }
    //При создании класса эвент считаетсся включенным.
    var isEnabled = true
        private set

    fun getContent(): String? {
        if (!isEnabled)
            return null
        isEnabled = false
        return content
    }
}