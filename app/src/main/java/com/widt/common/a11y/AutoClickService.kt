package com.widt.common.a11y

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AutoClickService : AccessibilityService() {
    
    companion object {
        var isEnabled = false
    }
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        isEnabled = true
        
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
        setServiceInfo(info)
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        val packageName = event.packageName?.toString() ?: return
        
        // Only handle system dialogs and WiFi settings
        if (packageName != "com.android.systemui" && 
            packageName != "com.android.settings" &&
            !packageName.contains("wifi")) {
            return
        }
        
        val root = rootInActiveWindow ?: return
        
        // Look for connect/dialog buttons
        val buttonTexts = listOf(
            "Connect", "CONNECT", "Kết nối", "连接", "연결",
            "OK", "Accept", "Allow", "Allow always", "Đồng ý"
        )
        
        for (text in buttonTexts) {
            val nodes = root.findAccessibilityNodeInfosByText(text)
            if (nodes.isNotEmpty()) {
                var node = nodes[0]
                // Find clickable parent if needed
                while (node != null && !node.isClickable) {
                    node = node.parent
                }
                node?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                break
            }
        }
    }
    
    override fun onInterrupt() {
        isEnabled = false
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isEnabled = false
    }
}