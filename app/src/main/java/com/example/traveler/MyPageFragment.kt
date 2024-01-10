package com.example.traveler

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException


class MyPageFragment : Fragment() {
    private val TAG = this.javaClass.simpleName


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_page, container, false)
        val parentlayout = view.findViewById<LinearLayout>(R.id.myPage)
        val myname = view.findViewById<TextView>(R.id.myName)
        val myphone = view.findViewById<TextView>(R.id.myPhoneNum)
        val mynickname = view.findViewById<TextView>(R.id.myNickName)
        val edit_myname = view.findViewById<EditText>(R.id.edit_myName)
        val edit_myphone = view.findViewById<EditText>(R.id.edit_myPhoneNum)
        val edit_mynickname = view.findViewById<EditText>(R.id.edit_myNickName)
        val button_myname = view.findViewById<ImageButton>(R.id.button_myname)
        val button_myphone = view.findViewById<ImageButton>(R.id.button_myphoneNum)
        val button_mynickname = view.findViewById<ImageButton>(R.id.button_mynickname)
        val button_logout = view.findViewById<Button>(R.id.button_logout)
        val nameText = view.findViewById<TextView>(R.id.nameText)


        val fileName = "Mypage.json"
        val internalStorageDir = requireActivity().applicationContext.getFilesDir()
        val file = File(internalStorageDir, fileName)

        try {
            val content = file.readText()
            Log.e(TAG, "저장되어 있던 my data 불러오기: $content")
            val myData = JSONObject(content)
            val userName = myData.getString("name")
            nameText.text = userName + "님"
            myname.text = myData.getString("name")
            myphone.text = myData.getString("phone")
            mynickname.text = myData.getString("nickname")
            editInfo(myname, edit_myname, button_myname, "name")
            editInfo(myphone, edit_myphone, button_myphone, "phone")
            editInfo(mynickname, edit_mynickname, button_mynickname, "nickname")

            //logout 버튼 누르면 다시 login fragment로 이동
            button_logout.setOnClickListener {
                val content = file.readText()
                val myData = JSONObject(content)
                Log.e(TAG, myData.toString())
                val user = Person(myData.getString("name"), myData.getString("phone"), myData.getString("nickname"), "")
                updateMember(user)
                parentFragmentManager.beginTransaction().apply{
                    replace(R.id.fragment3_container, LoginFragment())
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    addToBackStack(null)
                    commit()
                    }

                NidOAuthLogin().callDeleteTokenApi(object : OAuthLoginCallback {
                    override fun onSuccess() {
                        //서버에서 토큰 삭제에 성공한 상태입니다.
                        Log.e(TAG, "success in logout")

                    }
                    override fun onFailure(httpStatus: Int, message: String) {
                        // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                        // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                        Log.d(TAG, "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                        Log.d(TAG, "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
                    }
                    override fun onError(errorCode: Int, message: String) {
                        // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                        // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                        onFailure(errorCode, message)
                    }
                })


            }

            println(content)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return view

    }

    fun editInfo(
        target_text: TextView,
        target_edit: EditText,
        editingButton: ImageButton,
        target_key: String
    ) {
        target_text.setOnLongClickListener {
            target_edit.visibility = View.VISIBLE
            editingButton.visibility = View.VISIBLE
            target_edit.setText(target_text.text)
            target_text.visibility = View.GONE

            target_edit.requestFocus()
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(target_edit, InputMethodManager.SHOW_IMPLICIT)

            true

        }


        editingButton.setOnClickListener {
            target_text.visibility = View.VISIBLE
            target_text.text = target_edit.text.toString()
            target_edit.setText("")
            target_edit.visibility = View.GONE
            editingButton.visibility = View.GONE


            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editingButton.windowToken, 0)

            //내부 저장소 파일 업데이트
            val fileName = "Mypage.json"
            val internalStorageDir = requireActivity().applicationContext.getFilesDir()
            val file = File(internalStorageDir, fileName)
            val content = file.readText()

            val myData = JSONObject(content)
            myData.put(target_key, target_text.text.toString())
            Log.e(TAG,target_text.text.toString())
            Log.e(TAG, myData.toString())
            file.writeText(myData.toString())

            //데이터 베이스 업데이트
            val user = Person(myData.getString("name"), myData.getString("phone"), myData.getString("nickname"), "")
            updateMember(user)

            true

        }


    }

    private fun updateMember(user:Person) {
        Log.d("updateMember", "user: $user")
        val client = OkHttpClient()
        val jsonString = toJson(user).toString()
        Log.d("updateMember", "jsonString: $jsonString")
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonString)
        Log.d("updateMember", "requestBody: $requestBody")
        val serverIp = getString(R.string.server_ip)
        val url = "$serverIp/updateUser"
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.e(TAG, responseBody ?: "")
                } else {
                    // 서버 응답이 실패한 경우 처리
                    Log.e(TAG, "Server responded with error: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Network request failed: ${e.message}")
                // 오류 처리를 원하는대로 수행
            }
        })

    }
}
