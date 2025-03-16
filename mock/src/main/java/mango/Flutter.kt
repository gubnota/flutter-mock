package mango

import android.app.Activity
import android.content.Context
import android.util.Log

// --- Flutter Binary Messenger ---

interface FlutterBinaryMessenger {
    fun send(channel: String, message: Any?, completion: ((Any?) -> Unit)? = null)
}

class DebugBinaryMessenger : FlutterBinaryMessenger {
    override fun send(channel: String, message: Any?, completion: ((Any?) -> Unit)?) {
        Log.d("DebugBinaryMessenger", "Channel: $channel, Message: $message")
        completion?.invoke(null)
    }
}

// --- Flutter Method Channel ---

data class FlutterMethodCall(val method: String, val arguments: Any?)

typealias FlutterResult = (Any?) -> Unit

interface FlutterMethodCallHandler {
    fun onMethodCall(call: FlutterMethodCall, result: FlutterResult)
}

class FlutterMethodChannel(val name: String, val messenger: FlutterBinaryMessenger) {
    private var methodCallHandler: FlutterMethodCallHandler? = null

    fun setMethodCallHandler(handler: FlutterMethodCallHandler?) {
        methodCallHandler = handler
    }

    fun invokeMethod(method: String, arguments: Any?, result: FlutterResult? = null) {
        Log.d("FlutterMethodChannel", "Invoking method '$method' with arguments: $arguments on channel '$name'")
        val call = FlutterMethodCall(method, arguments)
        methodCallHandler?.onMethodCall(call, result ?: {}) ?: run {
            Log.d("FlutterMethodChannel", "No method call handler registered for channel $name")
            result?.invoke(null)
        }
    }

    fun sendMessage(message: Any?, result: FlutterResult? = null) {
        Log.d("FlutterMethodChannel", "Sending message on channel '$name': $message")
        messenger.send(name, message, result)
    }
}

// --- Flutter Event Channel ---

typealias FlutterEventSink = (Any?) -> Unit

interface FlutterStreamHandler {
    fun onListen(arguments: Any?, eventSink: FlutterEventSink)
    fun onCancel(arguments: Any?)
}

class FlutterEventChannel(val name: String, val messenger: FlutterBinaryMessenger) {
    private var streamHandler: FlutterStreamHandler? = null

    fun setStreamHandler(handler: FlutterStreamHandler?) {
        streamHandler = handler
    }

    fun receiveEvent(arguments: Any?) {
        Log.d("FlutterEventChannel", "Receiving event on channel '$name' with arguments: $arguments")
        streamHandler?.onListen(arguments) { event ->
            Log.d("FlutterEventChannel", "Event sink received event: $event")
        }
    }

    fun cancelEvent(arguments: Any?) {
        Log.d("FlutterEventChannel", "Canceling event on channel '$name' with arguments: $arguments")
        streamHandler?.onCancel(arguments)
    }
}

// --- Flutter Plugin Registrar & Plugin Interfaces ---

/**
 * Mock version of Flutter's FlutterPlugin interface.
 */
interface FlutterPlugin {
    fun onAttachedToEngine(binding: FlutterPluginBinding) {}  // Default no-op implementation.
    fun onDetachedFromEngine(binding: FlutterPluginBinding) {}  // Default no-op implementation.

    interface FlutterPluginBinding {
        val binaryMessenger: FlutterBinaryMessenger
        val applicationContext: Context
    }
}

// Concrete implementation for FlutterPluginBinding.
class DebugFlutterPluginBinding(
    override val binaryMessenger: FlutterBinaryMessenger = DebugBinaryMessenger(),
    override val applicationContext: Context
) : FlutterPlugin.FlutterPluginBinding

interface FlutterPluginRegistrar {
    fun messenger(): FlutterBinaryMessenger
    fun addMethodCallDelegate(delegate: FlutterMethodCallHandler, channel: FlutterMethodChannel)
}

class DebugPluginRegistrar(
    private val binaryMessenger: FlutterBinaryMessenger = DebugBinaryMessenger()
) : FlutterPluginRegistrar {

    private val delegates = mutableListOf<Pair<FlutterMethodCallHandler, FlutterMethodChannel>>()

    override fun messenger(): FlutterBinaryMessenger = binaryMessenger

    override fun addMethodCallDelegate(delegate: FlutterMethodCallHandler, channel: FlutterMethodChannel) {
        Log.d("DebugPluginRegistrar", "Registered delegate for channel: ${channel.name}")
        delegates.add(delegate to channel)
        channel.setMethodCallHandler(delegate)
    }
}

const val FlutterMethodNotImplemented = "FlutterMethodNotImplemented"

// --- Additional Plugin APIs: ActivityAware & ActivityPluginBinding ---

interface ActivityAware {
    fun onAttachedToActivity(binding: ActivityPluginBinding)
    fun onDetachedFromActivityForConfigChanges()
    fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding)
    fun onDetachedFromActivity()
}

interface ActivityPluginBinding {
    fun getActivity(): Activity?
    fun getBinaryMessenger(): FlutterBinaryMessenger
    // Additional properties (such as lifecycle) can be added if needed.
}

// --- Example Plugin Implementation ---
//
//import android.content.Context
//import android.util.Log
//import mango.FlutterMethodCall
//import mango.FlutterMethodCallHandler
//import mango.FlutterMethodChannel
//import mango.FlutterMethodNotImplemented
//import mango.FlutterPlugin
//import mango.FlutterPluginRegistrar
//import mango.FlutterResult
//
//class SamplePlugin : FlutterPlugin, FlutterMethodCallHandler {
//
//    private lateinit var applicationContext: Context
//
//    companion object {
//        @JvmStatic
//        fun registerWith(registrar: FlutterPluginRegistrar) {
//            val channel = FlutterMethodChannel("sample", registrar.messenger())
//            val instance = SamplePlugin()
//            registrar.addMethodCallDelegate(instance, channel)
//        }
//    }
//
//    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
//        val binaryMessenger = binding.binaryMessenger
//        applicationContext = binding.applicationContext
//        Log.d("SamplePlugin", "Attached to engine. Messenger: $binaryMessenger, Context: $applicationContext")
//    }
//
//    override fun onMethodCall(call: FlutterMethodCall, result: FlutterResult) {
//        Log.d("SamplePlugin", "Received method call: ${call.method} with arguments: ${call.arguments}")
//        if (call.method == "logout") {
//            // Implement your logout logic here.
//            result("Logged out successfully")
//        } else {
//            result(FlutterMethodNotImplemented)
//        }
//    }
//}