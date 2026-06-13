package com.widt.analyzer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.widt.model.WifiNetwork

class AnalyzerViewModel : ViewModel() {
    
    private val _wifiNetworks = MutableLiveData<List<WifiNetwork>>(emptyList())
    val wifiNetworks: LiveData<List<WifiNetwork>> = _wifiNetworks
    
    private val _isScanning = MutableLiveData(false)
    val isScanning: LiveData<Boolean> = _isScanning
    
    private val _selectedNetwork = MutableLiveData<WifiNetwork?>()
    val selectedNetwork: LiveData<WifiNetwork?> = _selectedNetwork
    
    fun updateNetworks(networks: List<WifiNetwork>) {
        _wifiNetworks.value = networks
        _isScanning.value = false
    }
    
    fun setScanning(scanning: Boolean) {
        _isScanning.value = scanning
    }
    
    fun selectNetwork(network: WifiNetwork) {
        _selectedNetwork.value = network
    }
    
    fun clearSelectedNetwork() {
        _selectedNetwork.value = null
    }
}