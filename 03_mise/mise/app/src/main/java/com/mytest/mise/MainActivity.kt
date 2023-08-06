package com.mytest.mise

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.mytest.mise.databinding.ActivityMainBinding
import com.mytest.mise.network.dto.AirQualityRes
import com.mytest.mise.network.retrofit.connection.RetrofitConnection
import com.mytest.mise.network.service.AirQualityApiCrt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.IllegalArgumentException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val PERMISSION_REQUEST_CODE = 100 //어떤 리퀘스트인지 확인을 위한 id개념
    private lateinit var getGPSPermissionLauncher : ActivityResultLauncher<Intent>
    private val KEY = "5c292cd6-d8eb-41ba-88b7-7c06047425e0"

    override fun onCreate(savedInstanceState: Bundle?) {
        //binding 객체 받기
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkAllPermissions()
        Log.d("미세","업데이트 실행")
        updateUI()
        setUpdateBtn()

    }

    private fun updateUI(){
        val locationProvider = LocationProvider(this)
        val longitude = locationProvider.getLongitude()
        val latitude = locationProvider.getLatitude()
        if(longitude!=0.0 && latitude!=0.0){
            val address = getCurrentAddress(latitude,longitude) ?: return
            binding.tvLocationTitle.text = address.thoroughfare
            binding.tvLocationSubtitle.text = "${address.countryName} ${address.adminArea}"
            Thread{
                getAirQualityData(latitude,longitude)
            }.start()

        }else{
            Toast.makeText(this, "위경도를 받아올 수 없습니다.",Toast.LENGTH_LONG).show()
        }

    }

    private fun getAirQualityData(latitude:Double, longitude: Double){

        val airQualityApiCrt = RetrofitConnection.getInstance().create(AirQualityApiCrt::class.java)
        lifecycleScope.launch {

            val res = airQualityApiCrt.getAirQuality(latitude,longitude,KEY)

            if(res.isSuccessful) {
                val body = res.body()!!
                withContext(Dispatchers.Main) {
                    // UI 업데이트 작업
                    updateAirUI(body)
                }
                Log.d("미세",body!!.data.city.toString())
            }
            else
                Log.d("미세","실패")
        }

        /*
        val airQualityApi = RetrofitConnection.getInstance().create(AirQualityApi::class.java)

        airQualityApi.getAirQuality(latitude, longitude, KEY)
            .enqueue(
                object : Callback<AirQualityRes> {
                    //reponse온 경우
                    override fun onResponse(
                        call: Call<AirQualityRes>,
                        response: Response<AirQualityRes>
                    ) {
                        //성공적 response
                        if(response.isSuccessful){
                            Toast.makeText(this@MainActivity, "api 요청 완료",Toast.LENGTH_LONG).show()
                            response.body()?.let{
                                updateAirUI(it)
                                Log.d("미세",it.data.city.toString())
                            }
                        }
                        else{
                            Toast.makeText(this@MainActivity, "api 요청 실패",Toast.LENGTH_LONG).show()
                        }
                    }

                    //response 실패
                    override fun onFailure(call: Call<AirQualityRes>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "api 요청 실패2",Toast.LENGTH_LONG).show()
                    }
                }

            )*/
    }

    suspend fun updateAirUI(airQualityRes: AirQualityRes){
        Log.d("미세","업데이트")
        val pollutionData = airQualityRes.data.current.pollution
        binding.tvCount.text = pollutionData.aqius.toString()

        val dateTime = ZonedDateTime.parse(pollutionData.ts).withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        binding.tvCheckTime.text = dateTime.format(dateFormatter)

        when(pollutionData.aqius){
            in 0..50->{
                binding.tvTitle.text="좋음"
                binding.imgBg.setBackgroundColor(ContextCompat.getColor(this, R.color.good))
            }
            in 51..150->{
                binding.tvTitle.text="보통"
                binding.imgBg.setBackgroundColor(ContextCompat.getColor(this, R.color.soso))
            }
            in 151..200->{
                binding.tvTitle.text="나쁨"
                binding.imgBg.setBackgroundColor(ContextCompat.getColor(this, R.color.bad))
            }
            else->{
                binding.tvTitle.text="매우나쁨"
                binding.imgBg.setBackgroundColor(ContextCompat.getColor(this, R.color.badbad))
            }

        }
    }

    private fun setUpdateBtn(){
        binding.btnRefresh.setOnClickListener{
            Toast.makeText(this@MainActivity, "새로고침 완료",Toast.LENGTH_LONG).show()
            updateUI()
        }
    }

    private fun getCurrentAddress(latitude : Double, longitude : Double) : Address?{
        val geoCoder = Geocoder(this, Locale.KOREA)
        var addresses : List<Address>? = null
        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 3)
        }catch (ioException : IOException){
            Toast.makeText(this, "지오코더 사용 불가",Toast.LENGTH_LONG).show()
        }catch (illegalArgumentException : IllegalArgumentException){
            Toast.makeText(this, "잘못된 위경도",Toast.LENGTH_LONG).show()
        }

        if(addresses.isNullOrEmpty()){
            Toast.makeText(this, "주소가 발견되지 않았습니다.",Toast.LENGTH_LONG).show()
            return null
        }
        return addresses[0]
    }

    private fun checkAllPermissions(){
        if(!isLocationServicesAvailable()){ //위치서비스가 꺼져있다면
            showDialogForLocationServiceSetting() //위치서비스 켜기 요청
        }else{
            isRuntimePermissionGranted() //위치관련 런타임 권한 설정되었는가
        }
    }

    //인터넷기반 위치나 gps기반 위치 둘중하나만 켜져있으면 true
    private fun isLocationServicesAvailable():Boolean{
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

    //권한이 있다면 위치 on시킴
    private fun showDialogForLocationServiceSetting() {
        //위치 서비스 활성화 창 띄운 이후 실행할 롤백함수 설정
        getGPSPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (isLocationServicesAvailable()) {//위치 서비스 활성화면
                    isRuntimePermissionGranted() //권한 여부 확인
                } else { //위치 서비스가 허용되지 않았다면 앱을 종료합니다.
                    Toast.makeText(this@MainActivity, "위치 서비스를 사용할 수 없습니다.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("위치 서비스가 꺼져있습니다. 설정해야 앱을 사용할 수 있습니다.")
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            getGPSPermissionLauncher.launch(callGPSSettingIntent)
        })
        builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
            Toast.makeText(this@MainActivity, "기기에서 위치서비스(GPS) 설정 후 사용해주세요.", Toast.LENGTH_SHORT).show()
            finish()
        })
        builder.create().show()
    }


}