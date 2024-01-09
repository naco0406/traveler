import android.graphics.Color
import android.os.Bundle
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
import com.example.traveler.Place
import com.example.traveler.R


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
            if (selectedPosition == position) {
                itemHolder.cardLinearLayout.layoutParams.height = 300/* 확장된 높이 값 */
                itemHolder.topConnectorLine.layoutParams.height = 300/* 확장된 선의 길이 */
                itemHolder.bottomConnectorLine.layoutParams.height = 300/* 확장된 선의 길이 */
            } else {
                itemHolder.cardLinearLayout.layoutParams.height = 130/* 기본 높이 값 */
                itemHolder.topConnectorLine.layoutParams.height = 130/* 기본 선의 길이 */
                itemHolder.bottomConnectorLine.layoutParams.height = 130/* 기본 선의 길이 */
            }
            itemHolder.cardView.requestLayout() // 레이아웃 갱신
        } else {
            // '장소 추가' 버튼 로직
            val addButtonHolder = holder as AddPlaceViewHolder
            addButtonHolder.btnAddPlace.setOnClickListener {
                // 장소 추가 로직
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


// TripFragment
class MyTripDayFragment : Fragment() {
    private var dayActivities: MutableList<Place> = mutableListOf()
    private var selectedPosition: Int = -1

    companion object {
        private const val ARG_DAY_ACTIVITIES = "day_activities"

        fun newInstance(dayActivities: MutableList<Place>): MyTripDayFragment =
            MyTripDayFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_DAY_ACTIVITIES, ArrayList(dayActivities))
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dayActivities = it.getParcelableArrayList<Place>(ARG_DAY_ACTIVITIES) ?: mutableListOf()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_input_day, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.dayRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MyTripDayAdapter(dayActivities, selectedPosition) { position ->
            // 클릭된 항목 처리 로직
        }

        return view
    }
}
