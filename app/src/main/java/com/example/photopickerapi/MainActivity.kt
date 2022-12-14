package com.example.photopickerapi

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ext.SdkExtensions.getExtensionVersion
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                //By default, the system grants your app access to media files until the device is restarted or until your app stops.
                //If your app performs long-running work, such as uploading a large file in the background, you might need this access to be persisted for a longer period of time.
                //To do so, call the takePersistableUriPermission() method:
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, flag)
                Log.d("mLog", "Selected URI: $uri")
            } else {
                Log.d("mLog", "No media selected")
            }
        }

        // Registers a photo picker activity launcher in multi-select mode.
        // In this example, the app allows the user to select up to 5 media files.
        val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (uris.isNotEmpty()) {
                uris.forEach {
                    Log.d("mLog", "Selected URI: $it")
                }
                Log.d("mLog", "Number of items selected: ${uris.size}")
            } else {
                Log.d("mLog", "No media selected")
            }
        }

        val photoPickerButton: Button = findViewById(R.id.photoPickerButton)
        photoPickerButton.setOnClickListener {
            handlePhotoPickerLaunch(pickMedia)
//            handleMultiplePhotoPickerLaunch(pickMultipleMedia)
        }
// Include only one of the following calls to launch(), depending on the types
// of media that you want to allow the user to choose from.

// Launch the photo picker and allow the user to choose images and videos.
//        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))

// Launch the photo picker and allow the user to choose only images.
//        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

// Launch the photo picker and allow the user to choose only videos.
//        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))

// Launch the photo picker and allow the user to choose only images/videos of a
// specific MIME type, such as GIFs.
//        val mimeType = "image/gif"
//        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType(mimeType)))

    }

    private fun isPhotoPickerAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("mLog", "TRUE")
            true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d("mLog", ">= 10")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getExtensionVersion(Build.VERSION_CODES.R) >= 2
            } else {
                TODO("VERSION.SDK_INT < TIRAMISU")
            }
        } else {
            false
        }
    }

    private fun handlePhotoPickerLaunch(pickMedia: ActivityResultLauncher<PickVisualMediaRequest>) {
        if (isPhotoPickerAvailable()) {
            Log.d("mLog", "Available")
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            // To launch the system photo picker, invoke an intent that includes the
            // ACTION_PICK_IMAGES action. Consider adding support for the
            // EXTRA_PICK_IMAGES_MAX intent extra.
        } else {
            Log.d("mLog", "Not Available")
            requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            // Consider implementing fallback functionality so that users can still
            // select images and videos.
        }
    }

    private fun handleMultiplePhotoPickerLaunch(pickMultipleMedia: ActivityResultLauncher<PickVisualMediaRequest>) {
        if (isPhotoPickerAvailable()) {
            Log.d("mLog", "Available")

            // For this example, launch the photo picker and allow the user to choose images
            // and videos. If you want the user to select a specific type of media file,
            // use the overloaded versions of launch(), as shown in the section about how
            // to select a single media item.
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        } else {
            Log.d("mLog", "Not Available")
            // Consider implementing fallback functionality so that users can still
            // select images and videos.
        }
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
//            pickContentLegacyDocumentTree.launch()
            pickContentLegacyMediaStore.launch()
            Log.d("mLog", "Granted")
        } else {
            Log.d("mLog", "Not Granted")
        }
    }


    private val pickContentLegacyDocumentTree = registerForActivityResult(PickContentLegacyDocumentTree()) {
        Log.d("mLog", "Uri $it")
    }

    private val pickContentLegacyMediaStore = registerForActivityResult(PickContentLegacyMediaStore()) {
        Log.d("mLog", "Uri $it")
    }

}