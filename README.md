
# QR Reader

QR reader library with CameraX & Zxing for Android.

**Min API Level = 21**

![Preview](https://raw.githubusercontent.com/)
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
	implementation 'com.murgupluoglu.qrreader:QRReaderView:<latest-version>'
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
qrCodeReaderView.setListener { qrCode, status ->  
  qrTextView.text = qrCode  
}
```
#### Step 3
Start camera
```kotlin
qrCodeReaderView.startCamera(this@MainActivity)
```

#### Optional
Set configuration and enable torch
```kotlin
qrCodeReaderView.startCamera(  
    this@MainActivity,  
    QRCameraConfiguration(lensFacing = CameraX.LensFacing.BACK, readerMode = ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
)

qrCodeReaderView.enableTorch(!qrCodeReaderView.isTorchOn())
```

#### Use Inside Your CameraX Setup
If you have CameraX setup in your code. You can use only QRAnalyzer class.
```kotlin
val analysisConfig = ImageAnalysisConfig.Builder().apply {  
  ...
}.build()

val imageAnalyzer = ImageAnalysis(analysisConfig)

imageAnalyzer?.apply {  
  analyzer = QRAnalyzer().apply {  
    onFrameAnalyzed { qrCode, status, resultPoint, previewWidth, previewHeight, rotationDegrees ->  
      //read qrCode and status    
    }  
}}
```
