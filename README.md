
# QR Reader

**Min API Level = 21**

![Preview](https://github.com/murgupluoglu/qrreader-android/blob/master/github/sample.gif)
## Setup

### Gradle

Add the JitPack repository to your project level `build.gradle`:

```groovy
allprojects {
 repositories {
    google()
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```

Add this to your app `build.gradle`:

```groovy
dependencies {
	implementation 'com.github.murgupluoglu:qrreader-android:2.0.0'
}
```

## Usage

This library using Camera permission. Please grant permission before start. 

```xml
<uses-permission android:name="android.permission.CAMERA"/>
```

#### Step 1

Add QRReaderView to your XML like any other view.

```xml
<androidx.constraintlayout.widget.ConstraintLayout 
  android:layout_width="match_parent"  
  android:layout_height="match_parent"  
  android:orientation="vertical">  
  
 <com.murgupluoglu.qrreader.QRReaderView  
  android:id="@+id/qrCodeReaderView"  
  android:layout_width="0dp"  
  android:layout_height="0dp" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

#### Step 2
Add listener for returned qr code.
```kotlin
qrCodeReaderView.setListener(object : QRReaderListener{  
    override fun onRead(barcode: FirebaseVisionBarcode, barcodes: List<FirebaseVisionBarcode>) {  
        
  }  
  
    override fun onError(exception: Exception) {  
        
    }  
})
```
#### Step 3
Start camera
```kotlin
qrCodeReaderView.startCamera(this@MainActivity)
```

#### Optional
Set configuration and enable torch
```kotlin
val firebaseOptions =  
        FirebaseVisionBarcodeDetectorOptions.Builder().setBarcodeFormats(  
                FirebaseVisionBarcode.FORMAT_QR_CODE,  
                FirebaseVisionBarcode.FORMAT_AZTEC)  
                .build()  
val config = QRCameraConfiguration(lensFacing = CameraSelector.LENS_FACING_FRONT, options = firebaseOptions)

qrCodeReaderView.startCamera(this@MainActivity, config)

qrCodeReaderView.enableTorch(true)
```

#### Use Inside Your CameraX Setup
If you have CameraX setup in your code. You can use only QRAnalyzer class.
```kotlin
imageAnalyzer.setAnalyzer(mainExecutor, QRAnalyzer(rotation, config.options).apply {  
  onFrameAnalyzed { qrStatus, barcode, barcodes, exeption ->  
  
})
```