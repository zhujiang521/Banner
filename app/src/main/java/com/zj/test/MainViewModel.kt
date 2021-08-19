package com.zj.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _xState = MutableLiveData(0f)
    val xState: LiveData<Float> = _xState

    fun onxStateChanged(position: Float) {
        _xState.value = position
    }

    private val _yState = MutableLiveData(0f)
    val yState: LiveData<Float> = _yState

    fun onyStateChanged(position: Float) {
        _yState.value = position
    }

}