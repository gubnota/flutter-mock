## Installation
Add module as a dependency from a local folder or via `build.gradle`.
In your project-level `build.gradle`:
```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' } // add this line
    }
}
```
Or inside your settings.gradle:
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
            maven { url 'https://jitpack.io' } // add this line
    }
}
```
or settings.gradle.kts:
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven (url = "https://jitpack.io" ) // add this line
    }
}
```
In your app's gradle (Module's App):
```groovy
dependencies {
    implementation 'com.github.gubnota:flutter-mock:v9'
}
```
```kotlin
dependencies {
    implementation("com.github.gubnota:flutter-mock:v9")
}
```

Now you can log messages from your plugins, put breakpoints in your code, profile your code, do whatever you do in a native environment.

This way you can debug your plugins in Android Studio without need to run Flutter which is way more faster.

- See also [FlutterMock](https://github.com/gubnota/FlutterMock) for iOS

## Usage
Create a test plugin class (it will have package <yourappname> at the top), call it `TestPlugin` and place below that line:

```kotlin
import android.content.Context
import android.util.Log
import mango.FlutterMethodCall
import mango.FlutterMethodCallHandler
import mango.FlutterMethodChannel
import mango.FlutterMethodNotImplemented
import mango.FlutterPlugin
import mango.FlutterPluginRegistrar
import mango.FlutterResult

class TestPlugin : FlutterPlugin, FlutterMethodCallHandler {

    private lateinit var applicationContext: Context

    companion object {
        @JvmStatic
        fun registerWith(registrar: FlutterPluginRegistrar) {
            val channel = FlutterMethodChannel("sample", registrar.messenger())
            val instance = TestPlugin()
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
```