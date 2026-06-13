package com.widt.tester

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.widt.model.WifiNetwork
import com.widt.tester.engine.EngineSelector
import com.widt.tester.engine.TestResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TesterViewModel : ViewModel() {
    
    private lateinit var engineSelector: EngineSelector
    private lateinit var dictionaryLoader: DictionaryLoader
    
    private var testJob: Job? = null
    
    private val _selectedNetwork = MutableLiveData<WifiNetwork?>()
    val selectedNetwork: LiveData<WifiNetwork?> = _selectedNetwork
    
    private val _dictionary = MutableLiveData<List<String>>(emptyList())
    val dictionary: LiveData<List<String>> = _dictionary
    
    private val _isTesting = MutableLiveData(false)
    val isTesting: LiveData<Boolean> = _isTesting
    
    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> = _progress
    
    private val _total = MutableLiveData(0)
    val total: LiveData<Int> = _total
    
    private val _currentPassword = MutableLiveData("")
    val currentPassword: LiveData<String> = _currentPassword
    
    private val _result = MutableLiveData<String?>()
    val result: LiveData<String?> = _result
    
    private val _engineStatus = MutableLiveData<String>("")
    val engineStatus: LiveData<String> = _engineStatus
    
    fun init(context: android.content.Context) {
        engineSelector = EngineSelector(context)
        dictionaryLoader = DictionaryLoader(context)
        loadDefaultDictionary()
    }
    
    fun selectNetwork(network: WifiNetwork) {
        _selectedNetwork.value = network
        _result.value = null
        _progress.value = 0
    }
    
    fun loadDefaultDictionary() {
        val dict = dictionaryLoader.loadDefaultDictionary()
        _dictionary.value = dict
        _total.value = dict.size
    }
    
    fun loadCustomDictionary(uri: android.net.Uri): Boolean {
        val dict = dictionaryLoader.loadFromUri(uri)
        return if (dict.isNotEmpty()) {
            _dictionary.value = dict
            _total.value = dict.size
            true
        } else {
            false
        }
    }

    fun loadFromRaw(fileName: String) {
        val dict = dictionaryLoader.loadFromRaw(fileName)
        if (dict.isNotEmpty()) {
            _dictionary.value = dict
            _total.value = dict.size
        }
    }
    
    fun startTest() {
        val network = _selectedNetwork.value ?: return
        val dict = _dictionary.value ?: return
        
        testJob?.cancel()
        
        testJob = viewModelScope.launch {
            _isTesting.value = true
            _result.value = null
            _progress.value = 0
            
            val bestEngine = engineSelector.getBestEngine()
            _engineStatus.value = bestEngine?.name ?: "No engine available"
            
            for ((index, password) in dict.withIndex()) {
                if (_isTesting.value != true) break
                
                _currentPassword.value = password
                _progress.value = index + 1
                
                val result = engineSelector.testWithBestEngine(network.ssid, password, 3000)
                
                when (result) {
                    is TestResult.Connected -> {
                        _result.value = password
                        _isTesting.value = false
                        break
                    }
                    is TestResult.Timeout -> {
                        // Continue to next password
                    }
                    is TestResult.Error -> {
                        _engineStatus.value = "Error: ${result.message}"
                        _isTesting.value = false
                        break
                    }
                    else -> {}
                }
                
                // Small delay between attempts
                delay(500)
            }
            
            _isTesting.value = false
            if (_result.value == null) {
                _result.value = null  // Not found
            }
        }
    }
    
    fun stopTest() {
        testJob?.cancel()
        _isTesting.value = false
    }
    
    fun getEngineStatus(): String {
        val best = engineSelector.getBestEngine()
        val all = engineSelector.getAllEngines()
        return """
            Best: ${best?.name ?: "None"}
            Available: ${all.joinToString { it.name }}
        """.trimIndent()
    }
    
    override fun onCleared() {
        super.onCleared()
        testJob?.cancel()
    }
}