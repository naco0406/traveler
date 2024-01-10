package com.example.traveler

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class FilteringFragment : DialogFragment() {

    private lateinit var peopleNumText: TextView
    private lateinit var periodText: TextView
    private var currentNumPeople = 0
    private var currentPeriod = 0


    override fun onStart() {
        super.onStart()

        val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filtering, container, false)
        peopleNumText = view.findViewById<TextView>(R.id.peopleNumText)
        periodText = view.findViewById<TextView>(R.id.periodText)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val peopleNum_plusButton = view.findViewById<Button>(R.id.peopleNum_plusButton)
        val peopleNum_minusButton = view.findViewById<Button>(R.id.peopleNum_minusButton)
        val period_plusButton = view.findViewById<Button>(R.id.period_plusButton)
        val period_minusButton = view.findViewById<Button>(R.id.period_minusButton)

        // + 버튼 클릭 시 호출되는 함수 설정
        peopleNum_plusButton.setOnClickListener {
            currentNumPeople++
            peopleNumText.text = currentNumPeople.toString()
        }

        peopleNum_minusButton.setOnClickListener {
            currentNumPeople--
            peopleNumText.text = currentNumPeople.toString()
        }

        period_plusButton.setOnClickListener {
            currentPeriod++
            periodText.text = currentPeriod.toString()
        }

        period_minusButton.setOnClickListener {
            currentPeriod--
            periodText.text = currentPeriod.toString()
        }

        val closeButton: Button = view.findViewById(R.id.backButton)
        val filteringSearchButton = view.findViewById<Button>(R.id.filteringSearchButton)
        closeButton.setOnClickListener {
            dismiss()
        }

        filteringSearchButton.setOnClickListener {

            val resultBundle = Bundle().apply {
                putInt("num_people", currentNumPeople)
                putInt("period", currentPeriod)
            }

            parentFragmentManager.setFragmentResult("dialog_result", resultBundle)
            // 다이얼로그를 닫는 코드
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 다이얼로그의 스타일 등을 설정하고 반환
        return super.onCreateDialog(savedInstanceState)
    }



}

