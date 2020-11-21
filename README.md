
# QR Reader

**Min API Level = 21**

![Preview](https://github.com/murgupluoglu/qrreader-android/blob/master/github/sample.gif)
## Setup

You must setup Firebase for use this library in your application. 
[Firebase Android Setup](https://firebase.google.com/docs/android/setup)

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

[![](https://jitpack.io/v/murgupluoglu/qrreader-android.svg)](https://jitpack.io/#murgupluoglu/qrreader-android)

```groovy
dependencies {
	implementation 'com.github.murgupluoglu:qrreader-android:lastVersion'
}
```

## Usage

This library using Camera permission. Please grant permission before start. 

```xml
<uses-permission android:name="android.permission.CAMERA"/>
```

#### Step 1

Add QRReaderFragment to your XML like any other fragment.

```xml
<androidx.constraintlayout.widget.ConstraintLayout
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  android:orientation="vertical">

    <fragment
        android:id="@+id/qrCodeReaderFragment"
        android:layout_width="0dp"
        android:name="com.murgupluoglu.qrreader.QRReaderFragment"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### Step 2
Define your fragment for access later
```kotlin
val qrCodeReader : QRReaderFragment by lazy {
    qrCodeReaderFragment as QRReaderFragment
}
```

#### Step 3
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
val qrOptions: BarcodeScannerOptions = BarcodeScannerOptions.Builder()
    .setBarcodeFormats(
        Barcode.FORMAT_QR_CODE,
        Barcode.FORMAT_AZTEC)
    .build()
val config = QRCameraConfiguration(lensFacing = CameraSelector.LENS_FACING_FRONT, options = qrOptions)

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
