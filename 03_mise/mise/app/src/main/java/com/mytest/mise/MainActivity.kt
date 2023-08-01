package com.mytest.mise

import android.Manifest
import android.app.Instrumentation.ActivityResult
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mytest.mise.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val PERMISSION_REQUEST_CODE = 100 //어떤 리퀘스트인지 확인을 위한 id개념
    private lateinit var getGPSPerssionLauncher : ActivityResult

    override fun onCreate(savedInstanceState: Bundle?) {
        //binding 객체 받기
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkAllPermissions()
    }

    private fun checkAllPermissions(){
        if(!isLocationServiceAvailable()){ //위치서비스가 꺼져있다면
            showDialogForLocationServiceSetting() //위치서비스 켜기 요청
        }else{
            isRuntimePermissionGranted() //위치관련 런타임 권한 설정되었는가
        }
    }

    //인터넷기반 위치나 gps기반 위치 둘중하나만 켜져있으면 true
    private fun isLocationServiceAvailable():Boolean{
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    private fun isRuntimePermissionGranted(){
        //권한이 있는지 여부체크
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        //둘중에 하나라도 권한이 부여안됐으면
        if(hasFineLocationPermission!=PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE) //this, 권한요청 리스트, 요청 코드
        }
    }

    //사용자가 권한요청에대한 설정후 실행될 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_CODE && permissions.size == REQUIRED_PERMISSIONS.size){ //내가보낸 요청코드가 맞고 요청 사이즈가 같다면
            var checkResult = true
            for(result in grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){ //권한이 승인이 아닌경우
                    checkResult=false
                    break
                }
            }
            if(checkResult){
                //위치 가져올 수 있음

            }else{
                //권한이 거부함을 알리고 앱종료
                Toast.makeText(this, "권한이 거부되어 앱이 종료됩니다.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun showDialogForLocationServiceSetting(){

    }


}