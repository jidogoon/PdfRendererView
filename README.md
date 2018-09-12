# PdfRendererView
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

`PdfRendererView` is a PDF renderer view using Android PDFRenderer API (added in API 21)
(https://developer.android.com/reference/android/graphics/pdf/PdfRenderer.html)

`PdfRendererView` can be used to lightweighten app that using PDF View library.

Getting Started
================

Getting started is easy. Just add the library as a dependency in your projects build.gradle file. Be sure you've listed jCenter as a repository:

```Gradle
repositories {
    jcenter()
}
        
dependencies{
    implementation 'com.jidogoon:PdfRendererView:1.0.5'
}
```

Using The Module
================

Using the module is not much different from using any other view. Simply define the view in your XML layout:

```xml
<com.jidogoon.pdfrendererview.PdfRendererView
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

```kotlin
pdfView.initWithFile(File("/sdcard/downloads/PDFFile.pdf"))
```
or
```
pdfView.initWithFile(File("/sdcard/downloads/PDFFile.pdf"), Quality.NORMAL)
```

```kotlin
pdfView.initWithPath("/sdcard/downloads/PDFFile.pdf", Quality.FAST)
```

```kotlin
pdfView.initWithUrl("https://www.cs.toronto.edu/~hinton/absps/fastnc.pdf", Quality.ENHANCED)
```

```kotlin
pdfView.statusListener = object: PdfRendererView.StatusCallBack {
    override fun onDownloadStart() {
        loading.visibility = View.VISIBLE
    }
    override fun onDownloadSuccess() {
        // downloading progress is not supported yet. will be added soon.
        loading.visibility = View.GONE
    }
    override fun onError(error: Throwable) {
        loading.visibility = View.GONE
    }
}
```
You're done!
