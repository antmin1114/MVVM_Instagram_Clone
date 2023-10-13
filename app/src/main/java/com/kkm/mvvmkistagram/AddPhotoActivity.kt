package com.kkm.mvvmkistagram

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kkm.mvvmkistagram.databinding.ActivityAddPhotoBinding
import com.kkm.mvvmkistagram.model.ContentModel
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var photoUri: Uri? = null
    var photoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            photoUri = it.data?.data
            binding.uploadImageview.setImageURI(photoUri)
        }
    }
    var storage = FirebaseStorage.getInstance()
    var auth = FirebaseAuth.getInstance()
    var firestore = FirebaseFirestore.getInstance()

    lateinit var binding: ActivityAddPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_photo)

        binding.uploadImageview.setOnClickListener {
            selectContent()
        }
        binding.addphotoUploadBtn.setOnClickListener {
            contentUpload()
        }
//        var i = Intent(Intent.ACTION_GET_CONTENT)
//        i.type = "image/*"

        selectContent()

    }

    fun selectContent() {

        val intent: Intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }

        val chooserIntent = Intent.createChooser(intent, "앨범을 고르시오")
        photoResult.launch(chooserIntent)

    }

    fun contentUpload() {
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + ".png"
        // images/IMAGE_123123213.png
        var storagePath = storage.reference.child("images").child(imageFileName)

        storagePath.putFile(photoUri!!).continueWithTask {  // 구글 서버 과부하 방지를 위해서 특정 이미지만 받아올 수 있게하는 작업
            return@continueWithTask storagePath.downloadUrl
        }.addOnCompleteListener { downloadUri ->

            var contentModel = ContentModel()
            contentModel.imageUrl = downloadUri.result.toString()
            contentModel.explain = binding.addphotoEditEdittext.text.toString()
            contentModel.uid = auth.uid
            contentModel.userId = auth.currentUser?.email
            contentModel.timestamp = System.currentTimeMillis()


            firestore.collection("images").document().set(contentModel).addOnSuccessListener {
                Toast.makeText(this, "업로드 성공: ${downloadUri.result}", Toast.LENGTH_LONG).show()
                finish()
            }

        }

    }
}