import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.traveler.MyTrip
import com.example.traveler.Place
import com.example.traveler.R
import com.example.traveler.SearchPlaceFragment
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import org.json.JSONObject
import java.io.File


class MyTripDayAdapter(
    private val dayActivities: MutableList<Place>,
    private var selectedPosition: Int,
    private val onItemClicked: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val typeItem = 0
    private val typeAddButton = 1

    override fun getItemViewType(position: Int): Int {
        return if (position < dayActivities.size) typeItem else typeAddButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == typeItem) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mytrip, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.add_place_button, parent, false)
            AddPlaceViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == typeItem) {
            val itemHolder = holder as ViewHolder
            val activity = dayActivities[position].name
            itemHolder.textView.text = activity
            itemHolder.numberIndicator.text = (position + 1).toString()

            if (position == 0) {
                itemHolder.topConnectorLine.visibility = View.INVISIBLE
            } else {
                itemHolder.topConnectorLine.visibility = View.VISIBLE
            }

            if (position == dayActivities.size - 1) {
                itemHolder.bottomConnectorLine.visibility = View.INVISIBLE
            } else {
                itemHolder.bottomConnectorLine.visibility = View.VISIBLE
            }

            itemHolder.cardView.setOnClickListener {
                // 이전에 선택된 항목을 기록
                val previouslyExpanded = selectedPosition
                // 선택된 위치 업데이트
                selectedPosition = if (selectedPosition == position) -1 else position

                notifyItemChanged(previouslyExpanded)
                notifyItemChanged(selectedPosition)

                onItemClicked(position)
            }

            // 확장된 항목에 대한 뷰 높이 및 연결선 길이 조정
//            if (selectedPosition == position) {
//                itemHolder.cardLinearLayout.layoutParams.height = 300/* 확장된 높이 값 */
//                itemHolder.topConnectorLine.layoutParams.height = 300/* 확장된 선의 길이 */
//                itemHolder.bottomConnectorLine.layoutParams.height = 300/* 확장된 선의 길이 */
//            } else {
//                itemHolder.cardLinearLayout.layoutParams.height = 130/* 기본 높이 값 */
//                itemHolder.topConnectorLine.layoutParams.height = 130/* 기본 선의 길이 */
//                itemHolder.bottomConnectorLine.layoutParams.height = 130/* 기본 선의 길이 */
//            }
            itemHolder.cardLinearLayout.layoutParams.height = 130/* 기본 높이 값 */
            itemHolder.topConnectorLine.layoutParams.height = 130/* 기본 선의 길이 */
            itemHolder.bottomConnectorLine.layoutParams.height = 130/* 기본 선의 길이 */
            // 장소 변경 로직
            itemHolder.cardView.requestLayout() // 레이아웃 갱신
        } else {
            // '장소 추가' 버튼 로직
            val addButtonHolder = holder as AddPlaceViewHolder
            addButtonHolder.btnAddPlace.setOnClickListener {
                // 장소 추가 로직
                onItemClicked(dayActivities.size)
            }
        }
    }


    override fun getItemCount(): Int = dayActivities.size + 1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
        val cardView: CardView = view.findViewById(R.id.cardView)
        val cardLinearLayout: LinearLayout = view.findViewById(R.id.cardLinearLayout)
        val numberIndicator: TextView = view.findViewById(R.id.numberIndicator)
        val topConnectorLine: View = view.findViewById(R.id.topConnectorLine)
        val bottomConnectorLine: View = view.findViewById(R.id.bottomConnectorLine)
    }

    class AddPlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnAddPlace: Button = view.findViewById(R.id.btnAddPlace)
    }
}

interface OnPlaceUpdateListener {
    fun onPlaceUpdated()
}



// TripFragment
class MyTripDayFragment : Fragment() {
    private var dayActivities: MutableList<Place> = mutableListOf()
    private var selectedPosition: Int = 0
    private var onAddPlaceClicked: (() -> Unit)? = null
    private var selectedPlacePosition: Int = -1
    private var mytrip: MyTrip? = null
    private var selectedTabPosition: Int = 0

    var placeUpdateListener: OnPlaceUpdateListener? = null

    private fun updatePlaceInMyTrip(place: Place) {
        // 장소 업데이트 로직
        // ...

        // 부모 Fragment에게 알림
        placeUpdateListener?.onPlaceUpdated()
    }

    private fun openSearchPlaceFragment() {
        val searchPlaceFragment = SearchPlaceFragment().apply {
            onPlaceSelected = { place ->
                handlePlaceSelected(place)
            }
        }
        Log.d("openSearchPlaceFragment", "화면 전환")
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.InputContainer, searchPlaceFragment) // 적절한 컨테이너 ID 사용
            .addToBackStack(null)
            .commit()
    }

    private fun handlePlaceSelected(place: Place) {
        val fileName = "MyTrip.json"
        val internalStorageDir = requireActivity().applicationContext.getFilesDir()
        val file = File(internalStorageDir, fileName)
        val gson = Gson()

        try {
            val content = file.readText()
            Log.e("Load JSON", "저장되어 있던 my data 불러오기: $content")
            val myData = JSONObject(content)
            mytrip = gson.fromJson(myData.toString(), MyTrip::class.java)
            Log.d("Load JSON", myData.getString("places").toString())
        } catch (e: ApiException) {
        }

        if (selectedPlacePosition >= 0 && selectedPlacePosition < dayActivities.size) {
            // 선택된 위치에 장소 변경
            dayActivities[selectedPlacePosition] = place
        } else {
            // 장소 추가
            dayActivities.add(place)
        }
        // RecyclerView 업데이트

        placeUpdateListener?.onPlaceUpdated()
        Log.d("dayActivities", "$dayActivities")
        mytrip?.let { trip ->
            if (selectedPosition >= 0 && selectedPosition < trip.places.size) {
                trip.places[selectedTabPosition] = dayActivities // 선택된 날짜의 장소 목록 업데이트
            } else {
                // 오류 처리 또는 적절한 액션 수행
                Log.e("Error", "Selected position is out of bounds")
            }

            val updatedContent = gson.toJson(trip) // 전체 MyTrip 객체를 JSON으로 변환
            file.writeText(updatedContent) // 파일에 저장

            Log.d("updatedContent", updatedContent)
        }
        view?.findViewById<RecyclerView>(R.id.dayRecyclerView)?.adapter?.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 장소 추가 버튼 로직
        onAddPlaceClicked = {
            selectedPlacePosition = -1 // 추가 모드
            openSearchPlaceFragment()
        }
    }

    companion object {
        private const val ARG_DAY_ACTIVITIES = "day_activities"
        fun newInstance(dayActivities: MutableList<Place>, selectedTabPosition: Int, onAddPlaceClicked: () -> Unit): MyTripDayFragment =
            MyTripDayFragment().apply {
                this.onAddPlaceClicked = onAddPlaceClicked
                // ... 기존 코드 ...
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_DAY_ACTIVITIES, ArrayList(dayActivities))
                    putInt("selected_tab_position", selectedTabPosition)
                }
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dayActivities = it.getParcelableArrayList<Place>(ARG_DAY_ACTIVITIES) ?: mutableListOf()
            selectedTabPosition = it.getInt("selected_tab_position")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_input_day, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.dayRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = MyTripDayAdapter(dayActivities, selectedPosition) { position ->
            if (position == dayActivities.size) {
                Log.d("MyTripDayAdapter", "$position")
//                onAddPlaceClicked?.invoke()
                selectedPlacePosition = -1 // 추가 모드
                openSearchPlaceFragment()
            } else {
                // 기존 항목 클릭 로직
                Log.d("MyTripDayAdapter", "$position")
                selectedPlacePosition = position // 선택된 장소의 위치를 기록
                openSearchPlaceFragment()
            }
        }
        recyclerView.adapter = adapter

        return view
    }
}