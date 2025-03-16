## Usage
Add module as a dependency from a local folder or via build.gradle.
In your project-level build.gradle:
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
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // important line 
    repositories {
            maven { url 'https://jitpack.io' } // add this line
    }
}
```

In your app's gradle (Module's App):
```groovy
dependencies {
    implementation 'com.github.gubnota:mango-flutter:1.0.1'
}
```
Now you can log messages from your plugins, put breakpoints in your code, profile your code, do whatever you do in a native environment.

This way you can debug your plugins in Android Studio without need to run Flutter which is way more faster.

- See also [FlutterMock](https://github.com/gubnota/FlutterMock)