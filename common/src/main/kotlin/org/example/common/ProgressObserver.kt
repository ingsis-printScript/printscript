package org.example.common

interface ProgressObserver {
    fun onStart() {}
    fun onAdvanceLine()
}
