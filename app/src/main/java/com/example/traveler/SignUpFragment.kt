package com.example.traveler

import android.app.AlertDialog
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
import androidx.fragment.app.FragmentTransaction
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import com.example.traveler.Person
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.oauth.view.NidOAuthLoginButton
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException

class SignUpFragment : Fragment() {
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
        val naverSignupButton = view.findViewById<NidOAuthLoginButton>(R.id.naverSignup)



        // 확인 버튼 클릭 리스너
        okButton.setOnClickListener {
            val enteredPhoneNum = signup_phoneNum.text.toString()
            val enteredPassword = signup_password.text.toString()
            val myUser = Person("Unknown",enteredPhoneNum,"HI",enteredPassword)
            val jsonString = toJson(myUser)
            Log.e(TAG,jsonString)


            //server에 해당 회원 정보가 있는지 확인하도록 요청
            searchMember(enteredPhoneNum, enteredPassword) { result ->

                Log.e(TAG, "결과 데이터 : $result")

                val status = JSONObject(result).getString("status")
                Log.e(TAG, status)

                if (status == "success") {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("회원가입 실패")
                    builder.setMessage("이미 존재하는 정보입니다.")
                    builder.setPositiveButton("확인") { dialog, which ->
                        dialog.dismiss()
                    }

                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.show()
                }
                else {
                    //server에 해당 회원 정보를 추가
                    sendUserDataToServer(jsonString) { addresult ->
                        Log.e(TAG, "회원 가입 성공: $addresult")
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("회원가입 성공")
                        builder.setMessage("회원가입을 축하합니다.")
                        builder.setPositiveButton("확인") { dialog, which ->
                            dialog.dismiss()
                        }

                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.show()

                    }
                }
            }


            // TODO: 입력된 텍스트에 대한 처리 추가
             // 다이얼로그 닫기
        }

        // 취소 버튼 클릭 리스너
        cancelButton.setOnClickListener {
            parentFragmentManager.beginTransaction().apply{
                replace(R.id.fragment3_container, LoginFragment())
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                addToBackStack(null)
                commit()
            }
        }

        naverSignupButton.setOnClickListener {
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
                            val myUser = Person(name, phoneNum, "Unknown", "")
                            val jsonString = toJson(myUser)


                            //server에 해당 회원 정보를 추가
                            searchNaverMember(name, phoneNum) { result ->

                                Log.e(TAG, "결과 데이터 : $result")

                                val status = JSONObject(result).getString("status")
                                Log.e(TAG, status)

                                if (status == "success") {
                                    val builder = AlertDialog.Builder(context)
                                    builder.setTitle("회원가입 실패")
                                    builder.setMessage("이미 존재하는 정보입니다.")
                                    builder.setPositiveButton("확인") { dialog, which ->
                                        dialog.dismiss()
                                    }

                                    val alertDialog: AlertDialog = builder.create()
                                    alertDialog.show()
                                }
                                else {
                                    //server에 해당 회원 정보를 추가
                                    sendUserDataToServer(jsonString) { addresult ->
                                        Log.e(TAG, "회원 가입 성공: $addresult")
                                        val builder = AlertDialog.Builder(context)
                                        builder.setTitle("회원가입 성공")
                                        builder.setMessage("회원가입을 축하합니다.")
                                        builder.setPositiveButton("확인") { dialog, which ->
                                            dialog.dismiss()
                                        }

                                        val alertDialog: AlertDialog = builder.create()
                                        alertDialog.show()
                                    }
                                }
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

    private fun searchMember(phoneNum: String, password: String, callback: (String) -> Unit) {
        val client = OkHttpClient()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            "{\"password\":\"$password\", \"phoneNum\":\"$phoneNum\"}")
        val serverIp = getString(R.string.server_ip)
        val url = "$serverIp/searchUser"
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
                    if (responseData != null) {
                        callback(responseData)
                    }
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

    private fun searchNaverMember(name: String, phoneNum: String, callback: (String) -> Unit) {
        val client = OkHttpClient()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            "{\"name\":\"$name\", \"phoneNum\":\"$phoneNum\"}")

        val serverIp = getString(R.string.server_ip)
        val url = "$serverIp/searchNaverUser"
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
                    if (responseData != null) {
                        callback(responseData)
                    }
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