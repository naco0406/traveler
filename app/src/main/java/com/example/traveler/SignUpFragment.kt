package com.example.traveler

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import com.example.traveler.Person
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class SignUpFragment : DialogFragment() {
    private val TAG = this.javaClass.simpleName
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signup_phoneNum = view.findViewById<EditText>(R.id.signup_phoneNum)
        val signup_password = view.findViewById<EditText>(R.id.signup_password)
        val okButton = view.findViewById<Button>(R.id.dialogButtonOK)
        val cancelButton = view.findViewById<Button>(R.id.dialogButtonCancel)


        // 확인 버튼 클릭 리스너
        okButton.setOnClickListener {
            val enteredPhoneNum = signup_phoneNum.text.toString()
            val enteredPassword = signup_password.text.toString()
            val myUser = Person("Unknown",enteredPhoneNum,"HI",enteredPassword)
            val jsonString = toJson(myUser)
            Log.e(TAG,jsonString)
            sendUserDataToServer(jsonString) { result ->
                Log.e(TAG, "Result: $result")
            }

            // TODO: 입력된 텍스트에 대한 처리 추가
            dismiss() // 다이얼로그 닫기
        }

        // 취소 버튼 클릭 리스너
        cancelButton.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(false) // 다이얼로그 외부 터치로 닫히지 않도록 설정
        }
    }

    private fun sendUserDataToServer(jsonData: String, callback:(String) -> Unit) {
        val client = OkHttpClient()

        // 서버 URL
        val serverIp = getString(R.string.server_ip)
        val url = "$serverIp/addPerson"

        // JSON 데이터를 RequestBody로 변환
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonData)

        // HTTP POST 요청 생성
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // 요청 실행
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                activity?.runOnUiThread {
                    if (responseData != null) {
                        callback(responseData)
                    }
                }
            }

            override fun onFailure(call:Call, e: IOException) {
                activity?.runOnUiThread {
                    callback("Failed to connect: $e")
                }
            }
        })
    }

}