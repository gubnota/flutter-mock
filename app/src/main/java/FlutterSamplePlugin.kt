import android.content.Context
import android.util.Log
import mango.FlutterMethodCall
import mango.FlutterMethodCallHandler
import mango.FlutterMethodChannel
import mango.FlutterMethodNotImplemented
import mango.FlutterPlugin
import mango.FlutterPluginRegistrar
import mango.FlutterResult

class FlutterSamplePlugin : FlutterPlugin, FlutterMethodCallHandler {

    private lateinit var applicationContext: Context

    companion object {
        @JvmStatic
        fun registerWith(registrar: FlutterPluginRegistrar) {
            val channel = FlutterMethodChannel("sample", registrar.messenger())
            val instance = FlutterSamplePlugin()
            registrar.addMethodCallDelegate(instance, channel)
        }
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        val binaryMessenger = binding.binaryMessenger
        applicationContext = binding.applicationContext
        Log.d("SamplePlugin", "Attached to engine. Messenger: $binaryMessenger, Context: $applicationContext")
    }

    override fun onMethodCall(call: FlutterMethodCall, result: FlutterResult) {
        Log.d("SamplePlugin", "Received method call: ${call.method} with arguments: ${call.arguments}")
        if (call.method == "logout") {
            // Implement your logout logic here.
            result("Logged out successfully")
        } else {
            result(FlutterMethodNotImplemented)
        }
    }
}