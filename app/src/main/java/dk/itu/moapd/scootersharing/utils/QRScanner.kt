package dk.itu.moapd.scootersharing.utils

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

typealias BarcodeListener = (barcode: String) -> Unit


class QRScanner(private val barcodeListener: BarcodeListener) : ImageAnalysis.Analyzer {
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Pass image to the scanner and have it do its thing
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Task completed successfully
                    for (barcode in barcodes) {
                        barcodeListener(barcode.rawValue ?: "")
                    }
                    imageProxy.close()
                    mediaImage.close()
                }
                .addOnFailureListener {
                    // You should really do something about Exceptions
                }
                .addOnCompleteListener {
                    // It's important to close the imageProxy
                    imageProxy.close()
                    mediaImage.close()
                }
        }
    }
}