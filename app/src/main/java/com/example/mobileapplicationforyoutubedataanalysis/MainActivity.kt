package com.example.mobileapplicationforyoutubedataanalysis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.mobileapplicationforyoutubedataanalysis.model.ChannelModel
import com.example.mobileapplicationforyoutubedataanalysis.network.YouTubeService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//API KEY1: AIzaSyBcj3PVCQp4-MjEoBlDsJF1P9zNHjtA6k8
//API KEY2: AIzaSyDTyKbZ2ij67M4PwTo3aX1z2eu_ANmCReQ
//CHANNEL ID 1: UCTdmmBlsMlgouTiZ896hXWw
//CHANNEL ID 2: UCJVArFZ_Ug-b80hAyZ-Va7w

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var btnenter : Button
    lateinit var etAPI : EditText
    private lateinit var etID : EditText
    lateinit var resultJSON : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnenter = findViewById(R.id.btn_enter)
        etAPI = findViewById(R.id.API_KEY)
        etID = findViewById(R.id.youtube_id)
        btnenter.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_enter ->{
                val api = etAPI.text.toString()
                val id = etID.text.toString()
                getChannelData(api, id)
            }
        }
    }
    private fun getChannelData(apiKey:String, channelID: String){
        val retrofit = Retrofit.Builder().baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(YouTubeService::class.java)
        val part = "snippet,contentDetails,statistics"
        val call = service.getChannelData(part, channelID, apiKey)

        call.enqueue(object : Callback<ChannelModel> {
            override fun onResponse(call: Call<ChannelModel>, response: Response<ChannelModel>) {
                if (response.isSuccessful) {
                    val api = etAPI.text.toString()
                    val gson = Gson()
                    val jsonContent = gson.toJson(response.body())
                    val intent = Intent(this@MainActivity, SecondActivity::class.java)
                    intent.putExtra("API_KEY", api)
                    intent.putExtra("JSON_RESPONSE",jsonContent)
                    startActivity(intent)

                    println("Raw Json response: $jsonContent")
                    //resultJSON.text = jsonContent

                } else {
                    val errorMessage = getString(R.string.error_message, response.code())
                    resultJSON.text = errorMessage
                }
            }

            override fun onFailure(call: Call<ChannelModel>, t: Throwable) {
                val failureMessage = getString(R.string.failure_message, t.message)
                resultJSON.text = failureMessage
            }
        })
    }
}