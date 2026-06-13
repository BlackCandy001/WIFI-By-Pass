package com.widt.tester.engine

import kotlinx.coroutines.flow.Flow

/**
 * Interface for all connection testing strategies
 */
interface ConnectionEngine {
    val name: String
    val isAvailable: Boolean
    val requiresSetup: Boolean
    val speedRating: Int  // 1-5, 5 = fastest
    val description: String
    
    suspend fun testPassword(ssid: String, password: String, timeoutMs: Long = 3000): TestResult
}

sealed class TestResult {
    object Connected : TestResult()
    object WrongPassword : TestResult()
    object Timeout : TestResult()
    data class Error(val message: String) : TestResult()
}

data class EngineStatus(
    val name: String,
    val isAvailable: Boolean,
    val requiresSetup: Boolean,
    val speedRating: Int,
    val description: String
)