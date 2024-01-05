package com.example.traveler

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.errorCode
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.oauth.view.NidOAuthLoginButton
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class ThirdFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var name: String = ""
    private var phoneNum: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_third, container, false)
        val naverLoginButton = rootView.findViewById<NidOAuthLoginButton>(R.id.naverLoginButton)

        naverLoginButton.setOnClickListener {
            // 네이버 로그인 버튼이 클릭되었을 때의 동작
            val oAuthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 네이버 로그인 API 호출 성공 시 유저 정보를 가져온다
                    NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(result: NidProfileResponse) {
                            val name = result.profile?.name.toString()
                            val phoneNum = result.profile?.mobile.toString()

                            Log.e(TAG, "네이버 로그인한 유저 정보 - 이름 : $name")
                            Log.e(TAG, "네이버 로그인한 유저 정보 - 전화번호 : $phoneNum")

                            //server에 해당 회원 정보가 있는지 확인하도록 요청
                            searchMember(name, phoneNum) { result ->
                                Log.e(TAG,"로그인에 성공하였습니다.")
                            }
                        }

                        override fun onError(errorCode: Int, message: String) {
                            // API 호출 중 에러 발생 시 동작을 정의
                            Log.e(TAG, "네이버 로그인 API 에러 - 코드: $errorCode, 메시지: $message")
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            // HTTP 상태 코드가 실패로 반환됐을 때의 동작을 정의
                            Log.e(TAG, "네이버 로그인 API 실패 - HTTP 상태 코드: $httpStatus, 메시지: $message")
                        }
                    })
                }

                override fun onError(errorCode: Int, message: String) {
                    // 네이버 로그인 중 에러 발생 시 동작을 정의
                    Log.e(TAG, "네이버 로그인 중 에러 - 코드: $errorCode, 메시지: $message")
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    // 네이버 로그인 중 HTTP 상태 코드가 실패로 반환됐을 때의 동작을 정의
                    Log.e(TAG, "네이버 로그인 중 실패 - HTTP 상태 코드: $httpStatus, 메시지: $message")
                }
            }

            NaverIdLoginSDK.initialize(
                requireContext(),
                getString(R.string.naver_client_id),
                getString(R.string.naver_client_secret),
                "Traveler"
            )
            NaverIdLoginSDK.authenticate(requireContext(), oAuthLoginCallback)
        }

        return rootView
    }

    private fun searchMember(name: String, phoneNum: String, callback: (String) -> Unit) {
        val client = OkHttpClient()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            "{\"name\":\"$name\", \"phoneNum\":\"$phoneNum\"}")

        val request = Request.Builder()
            .url("http://43.200.170.97:5000/search")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseDataStream = response.body?.byteStream()
                val UserData = responseDataStream?.bufferedReader()?.use {it.readText()}
                activity?.runOnUiThread {
                    // XML 뷰에 결과 표시
                    callback(UserData ?: "No response")
                }
            }
        })

    }

}