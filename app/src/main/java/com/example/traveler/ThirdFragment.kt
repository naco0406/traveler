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

class ThirdFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var name: String = ""
    private var email: String = ""
    private var birthDay: String = ""
    private var birthYear: String = ""
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
                            val email = result.profile?.email.toString()
                            val gender = result.profile?.gender.toString()
                            val birthDay  = result.profile?.birthday.toString()
                            val phoneNum = result.profile?.mobile.toString()
                            val birthYear = result.profile?.birthYear.toString()

                            Log.e(TAG, "네이버 로그인한 유저 정보 - 이름 : $name")
                            Log.e(TAG, "네이버 로그인한 유저 정보 - 이메일 : $email")
                            Log.e(TAG, "네이버 로그인한 유저 정보 - 성별 : $gender")
                            Log.e(TAG, "네이버 로그인한 유저 정보 - 생일 : $birthDay")
                            Log.e(TAG, "네이버 로그인한 유저 정보 - 전화번호 : $phoneNum")
                            Log.e(TAG, "네이버 로그인한 유저 정보 - 태어난 연도 : $birthYear")
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

}