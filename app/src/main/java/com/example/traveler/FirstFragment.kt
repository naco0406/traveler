package com.example.traveler

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class FirstFragment : Fragment() {

    private lateinit var inputEditText: EditText
    private lateinit var resultTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_first, container, false)

        inputEditText = view.findViewById(R.id.inputEditText)
        val searchButton: Button = view.findViewById(R.id.searchButton)
        resultTextView = view.findViewById(R.id.resultTextView)

        searchButton.setOnClickListener {
            val name = inputEditText.text.toString()
            searchName(name) { result ->
                activity?.runOnUiThread {
                    resultTextView.text = result
                }
            }
        }

        return view
    }
    private fun searchName(name: String, callback: (String) -> Unit) {
        val client = OkHttpClient()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            "{\"name\":\"$name\"}"
        )
        val serverIp = getString(R.string.server_ip)
        val url = "$serverIp/search"
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                // UI 스레드에서 UI 업데이트
                activity?.runOnUiThread {
                    // XML 뷰에 결과 표시
                    callback(responseData ?: "No response")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // 오류 처리
                activity?.runOnUiThread {
                    callback("Failed to connect: $e")
                }
            }
        })
    }

}