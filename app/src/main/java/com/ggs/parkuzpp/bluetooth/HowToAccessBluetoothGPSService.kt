package com.ggs.parkuzpp.bluetooth
//
//import com.ggs.parkuzpp.location.BluetoothTriggeredGPSService
//
//class BluetoothService : Service() {
//
//  //
//
//    private var locationService: BluetoothTriggeredGPSService? = null
//    private var isBound = false
//
//    private val locationConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
//            locationService = (binder as BluetoothTriggeredGPSService.LocationBinder).getService()
//            isBound = true
//            locationService?.requestLocation(object : BluetoothTriggeredGPSService.LocationCallback {
//                override fun onLocationReceived(location: Location) {
//                    isBound = false
//                }
//                override fun onLocationFailed() {
//                    isBound = false
//                }
//            })
//        }
//        override fun onServiceDisconnected(name: ComponentName) {
//            isBound = false
//        }
//    }
//
//    fun onBluetoothDisconnected() {
//        bindService(
//            Intent(this, BluetoothTriggeredGPSService::class.java),
//            locationConnection,
//            Context.BIND_AUTO_CREATE
//        )
//    }
//
//}