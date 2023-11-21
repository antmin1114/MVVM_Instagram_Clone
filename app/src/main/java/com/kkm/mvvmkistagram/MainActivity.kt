package com.kkm.mvvmkistagram

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.kkm.mvvmkistagram.databinding.ActivityMainBinding
import com.kkm.mvvmkistagram.fragment.AlarmFragment
import com.kkm.mvvmkistagram.fragment.DetailViewFragment
import com.kkm.mvvmkistagram.fragment.GridFragment
import com.kkm.mvvmkistagram.fragment.UserFragment
import android.Manifest

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_CODE)

        var f = DetailViewFragment()
        supportFragmentManager.beginTransaction().replace(R.id.main_content, f).commit()

        binding.bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.action_home -> {
                    var f = DetailViewFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, f).commit()
                    true
                }
                R.id.action_search -> {
                    var f = GridFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, f).commit()
                    true
                }
                R.id.action_add_photo -> {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        var i = Intent(this, AddPhotoActivity::class.java)
                        startActivity(i)
                    } else {
                        Toast.makeText(this, "권한 허용이 안됨", Toast.LENGTH_LONG).show()
                    }
                    true
                }

                R.id.action_favorite_alarm -> {
                    var f = AlarmFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, f).commit()
                    true
                }
                R.id.action_account -> {
                    var f = UserFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, f).commit()
                    true
                }
            }
            false

        }
    }

/*    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) exitProcess(0)
                }
            }
        }
    }*/

}