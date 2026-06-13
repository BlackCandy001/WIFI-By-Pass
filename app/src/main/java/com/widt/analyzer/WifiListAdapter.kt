package com.widt.analyzer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.widt.R
import com.widt.model.WifiNetwork

class WifiListAdapter(
    private val onItemClick: (WifiNetwork) -> Unit
) : RecyclerView.Adapter<WifiListAdapter.ViewHolder>() {
    
    private var networks: List<WifiNetwork> = emptyList()
    
    fun submitList(newNetworks: List<WifiNetwork>) {
        networks = newNetworks
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wifi_network, parent, false)
        return ViewHolder(view, onItemClick)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(networks[position])
    }
    
    override fun getItemCount(): Int = networks.size
    
    class ViewHolder(
        itemView: View,
        private val onItemClick: (WifiNetwork) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val tvSsid: TextView = itemView.findViewById(R.id.tv_ssid)
        private val tvBssid: TextView = itemView.findViewById(R.id.tv_bssid)
        private val tvSignal: TextView = itemView.findViewById(R.id.tv_signal)
        private val tvEncryption: TextView = itemView.findViewById(R.id.tv_encryption)
        private val tvChannel: TextView = itemView.findViewById(R.id.tv_channel)
        private val tvVendor: TextView = itemView.findViewById(R.id.tv_vendor)
        
        fun bind(network: WifiNetwork) {
            tvSsid.text = network.ssid
            tvBssid.text = network.bssid
            
            val signalPercent = com.widt.utils.WifiUtils.getSignalStrengthPercentage(network.level)
            tvSignal.text = "${network.level} dBm ($signalPercent%)"
            
            tvEncryption.text = network.encryptionType
            tvChannel.text = "Ch ${network.channel}"
            
            tvVendor.text = network.vendor ?: "Unknown"
            tvVendor.visibility = if (network.vendor != null) View.VISIBLE else View.GONE
            
            itemView.setOnClickListener { onItemClick(network) }
        }
    }
}