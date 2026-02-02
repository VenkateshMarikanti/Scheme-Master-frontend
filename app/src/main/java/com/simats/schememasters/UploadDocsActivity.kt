package com.simats.schememasters

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.simats.schememasters.models.RegisterResponse
import com.simats.schememasters.network.RetrofitClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class UploadDocsActivity : AppCompatActivity() {

    private var currentSchemeId: Int = 1
    private var currentUploadingDoc: String = ""
    private lateinit var layoutDocumentsContainer: LinearLayout
    private var requiredDocs: Array<String> = arrayOf()
    private val uploadedDocsMap = mutableMapOf<String, Uri?>()

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) openCamera() else Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let { handleFileSelection(it) }
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            val uri = saveBitmapToFile(imageBitmap)
            handleFileSelection(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_docs)

        currentSchemeId = intent.getIntExtra("SCHEME_ID", 1)
        // Use a default list if the extra is not provided, which prevents the crash.
        requiredDocs = intent.getStringArrayExtra("REQUIRED_DOCS") ?: arrayOf("Aadhaar Card", "Income Certificate")

        layoutDocumentsContainer = findViewById(R.id.layoutDocumentsContainer)
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        setupDynamicDocCards()

        findViewById<MaterialButton>(R.id.btnSubmit).setOnClickListener {
            val missingDocs = requiredDocs.filter { uploadedDocsMap[it] == null }
            
            val intent = Intent(this, ValidationResultsActivity::class.java)
            intent.putExtra("SCHEME_ID", currentSchemeId)
            intent.putExtra("REQUIRED_DOCS", requiredDocs)
            intent.putExtra("MISSING_DOCS", missingDocs.toTypedArray())
            startActivity(intent)
            
            // Upload actual files in background
            uploadedDocsMap.forEach { (type, uri) ->
                uri?.let { uploadDocumentToServer(it, type) }
            }
        }
    }

    private fun setupDynamicDocCards() {
        layoutDocumentsContainer.removeAllViews() // Clear previous views

        requiredDocs.forEach { docName ->
            val cardView = LayoutInflater.from(this).inflate(R.layout.item_upload_doc_card, layoutDocumentsContainer, false)
            
            val tvName = cardView.findViewById<TextView>(R.id.tvDocName)
            val tvStatus = cardView.findViewById<TextView>(R.id.tvStatus)
            val btnUpload = cardView.findViewById<LinearLayout>(R.id.btnUpload)
            val cardFile = cardView.findViewById<MaterialCardView>(R.id.cardFile)
            val tvFileName = cardView.findViewById<TextView>(R.id.tvFileName)
            val btnClear = cardView.findViewById<ImageView>(R.id.btnClear)

            tvName.text = docName
            uploadedDocsMap[docName] = null

            btnUpload.setOnClickListener {
                currentUploadingDoc = docName
                showImagePickerOptions()
            }

            btnClear.setOnClickListener {
                uploadedDocsMap[docName] = null
                tvStatus.text = "MISSING"
                tvStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
                btnUpload.visibility = View.VISIBLE
                cardFile.visibility = View.GONE
            }

            cardView.tag = docName
            layoutDocumentsContainer.addView(cardView)
        }
    }

    private fun handleFileSelection(uri: Uri) {
        val fileName = getFileName(uri).lowercase()
        
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".pdf")) {
            
            uploadedDocsMap[currentUploadingDoc] = uri

            val cardView = layoutDocumentsContainer.findViewWithTag<View>(currentUploadingDoc)
            cardView?.let {
                val tvStatus = it.findViewById<TextView>(R.id.tvStatus)
                val btnUpload = it.findViewById<LinearLayout>(R.id.btnUpload)
                val cardFile = it.findViewById<MaterialCardView>(R.id.cardFile)
                val tvFileName = it.findViewById<TextView>(R.id.tvFileName)

                tvFileName.text = getFileName(uri)
                tvStatus.text = "UPLOADED"
                tvStatus.setTextColor(resources.getColor(R.color.teal_700, null))
                btnUpload.visibility = View.GONE
                cardFile.visibility = View.VISIBLE
            }
        } else {
            Toast.makeText(this, "Invalid Format! Please upload JPG, PNG or PDF only.", Toast.LENGTH_LONG).show()
        }
    }

    private fun showImagePickerOptions() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Upload $currentUploadingDoc")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> checkCameraPermission()
                options[item] == "Choose from Gallery" -> {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "*/*"
                    val mimeTypes = arrayOf("image/jpeg", "image/png", "application/pdf")
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                    galleryLauncher.launch(intent)
                }
                options[item] == "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(takePictureIntent)
        }
    }

    private fun uploadDocumentToServer(uri: Uri, docType: String) {
        val file = getFileFromUri(uri) ?: return
        val requestFile = RequestBody.create(MediaType.parse(contentResolver.getType(uri) ?: "image/*"), file)
        val body = MultipartBody.Part.createFormData("document", file.name, requestFile)
        val userId = RequestBody.create(MultipartBody.FORM, "1") 
        val type = RequestBody.create(MultipartBody.FORM, docType)

        RetrofitClient.instance.uploadDocument(userId, type, body).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) Toast.makeText(this@UploadDocsActivity, "$docType Uploaded", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {}
        })
    }

    private fun getFileFromUri(uri: Uri): File? {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val file = File(cacheDir, getFileName(uri))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return file
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            } finally { cursor?.close() }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) result = result?.substring(cut + 1)
        }
        return result ?: "document.jpg"
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri {
        val file = File(cacheDir, "captured_${System.currentTimeMillis()}.jpg")
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
        return Uri.fromFile(file)
    }
}
