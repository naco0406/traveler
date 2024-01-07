package com.example.traveler

import android.app.AlertDialog
import android.media.Image
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
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
import java.io.IOException
import com.example.traveler.Person
import org.json.JSONObject
import java.io.File

class ThirdFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
//    private var name: String = ""
//    private var phoneNum: String = ""
//    private val viewModel: SignViewModel by activityViewModels()
//    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }
//    private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//
//        try {
//            val account = task.getResult(ApiException::class.java)
//
//            // 이름, 이메일 등이 필요하다면 아래와 같이 account를 통해 각 메소드를 불러올 수 있다.
//            val userName = account?.displayName
//            val userEmail = account?.email
//            val userPhotoUrl = account?.photoUrl
//
//            Log.d("google", "userName: $userName")
//            Log.d("google", "userEmail: $userEmail")
//            Log.d("google", "userPhotoUrl: $userPhotoUrl")
//
////            moveSignUpActivity()
//
//        } catch (e: ApiException) {
//            Log.e(ThirdFragment::class.java.simpleName, e.stackTraceToString())
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_third, container, false)
        val naverLoginButton = rootView.findViewById<NidOAuthLoginButton>(R.id.naverLoginButton)
//        addListener(rootView)
        val signupButton = rootView.findViewById<Button>(R.id.signupButton)
        val loginButton = rootView.findViewById<Button>(R.id.loginButton)
        val loginPhone = rootView.findViewById<EditText>(R.id.login_phoneNum)
        val loginPassword = rootView.findViewById<EditText>(R.id.login_password)

        loginButton.setOnClickListener {
            val phone = loginPhone.text.toString()
            val password = loginPassword.text.toString()
            Log.e(TAG, "entered phoneNum: $phone, entered password: $password")
            //server에 해당 회원 정보가 있는지 확인하도록 요청
            searchMember(phone, password) { result ->

                Log.e(TAG, "결과 데이터 : $result")

                val status = JSONObject(result).getString("status")
                Log.e(TAG, status)

                if (status == "success") {
                    Log.e(TAG, "로그인 성공")
                    //json file을 내부 저장소에 저장
                    val fileName = "Mypage.json"
                    val internalStorageDir = context?.getFilesDir()
                    val file = File(internalStorageDir, fileName)
                    file.writeText(result)

                    val fileContents = file.readText()
                    val myData = JSONObject(fileContents).getJSONObject("UserData")
                    val password = myData.getString("password")
                    val phone = myData.getString("phone")
                    //val nickname = myData.getString("nickname")
                    Log.e(TAG, "로그인한 유저 정보 - 전화번호 : $phone")

                }
                else if (status == "wrong_password") {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("로그인 실패")
                    builder.setMessage("비밀번호가 일치하지 않습니다.")
                    builder.setPositiveButton("확인") { dialog, which ->
                        dialog.dismiss()
                    }

                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.show()
                }
                else {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("로그인 실패")
                    builder.setMessage("일치하는 회원 정보가 없습니다.")
                    builder.setPositiveButton("확인") { dialog, which ->
                        dialog.dismiss()
                    }

                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.show()
                }

            }




        }


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
                            searchNaverMember(name, phoneNum) { result ->

                                Log.e(TAG, "결과 데이터 : $result")

                                val status = JSONObject(result).getString("status")
                                Log.e(TAG, status)

                                if (status == "success") {
                                    //json file을 내부 저장소에 저장
                                    val fileName = "Mypage.json"
                                    val internalStorageDir = context?.getFilesDir()
                                    val file = File(internalStorageDir, fileName)
                                    file.writeText(result)

                                    val fileContents = file.readText()
                                    val myData = JSONObject(fileContents).getJSONObject("UserData")
                                    val userName = myData.getString("name")
                                    val phone = myData.getString("phone")
                                    val nickname = myData.getString("nickname")
                                    Log.e(TAG, "네이버 로그인한 유저 정보 - 전화번호 : $phone")

                                }
                                else {
                                    val builder = AlertDialog.Builder(context)
                                    builder.setTitle("로그인 실패")
                                    builder.setMessage("일치하는 회원 정보가 없습니다.")
                                    builder.setPositiveButton("확인") { dialog, which ->
                                        dialog.dismiss()
                                    }

                                    val alertDialog: AlertDialog = builder.create()
                                    alertDialog.show()
                                    Log.e(TAG, "로그인에 실패하였습니다.")
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

        signupButton.setOnClickListener {
            // ...
            val dialogFragment = SignUpFragment()
            dialogFragment.show(childFragmentManager, "SignUpFragment")
        }





        return rootView
    }

    private fun searchNaverMember(name: String, phoneNum: String, callback: (String) -> Unit) {
        val client = OkHttpClient()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            "{\"name\":\"$name\", \"phoneNum\":\"$phoneNum\"}")

        val request = Request.Builder()
            .url("http://43.200.170.97:5000/searchNaverUser")
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

    private fun searchMember(phoneNum: String, password: String, callback: (String) -> Unit) {
        val client = OkHttpClient()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            "{\"password\":\"$password\", \"phoneNum\":\"$phoneNum\"}")

        val request = Request.Builder()
            .url("http://43.200.170.97:5000/searchUser")
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

    fun switchToMypage(rootview:View) {
        val myPageFragment = MyPageFragment()

        val mainActivity = activity as? MainActivity
        val adapter = mainActivity?.getViewPagerAdapter()
        adapter?.addFragment
    }



//    private fun addListener(view: View) {
//        val clGoogleLogin = view.findViewById<ImageButton>(R.id.clGoogleLogin)
//        clGoogleLogin.setOnClickListener {
//            requestGoogleLogin()
//        }
//    }
//
//    private fun requestGoogleLogin() {
//        googleSignInClient.signOut()
//        val signInIntent = googleSignInClient.signInIntent
//        googleAuthLauncher.launch(signInIntent)
//    }
//
//    private fun getGoogleClient(): GoogleSignInClient {
//        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
////            .requestScopes(Scope("https://www.googleapis.com/auth/pubsub"))
//            .requestServerAuthCode(getString(R.string.google_login_client_id)) // string 파일에 저장해둔 client id 를 이용해 server authcode를 요청한다.
//            .requestEmail() // 이메일도 요청할 수 있다.
//            .build()
//
//        return GoogleSignIn.getClient(requireActivity(), googleSignInOption)
//    }


}