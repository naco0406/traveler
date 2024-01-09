import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.traveler.Place
import com.example.traveler.R


class TripDayAdapter(private val dayActivities: List<Place>, private var selectedPosition: Int,
                     private val onItemClicked: (Int) -> Unit) :
    RecyclerView.Adapter<TripDayAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemHolder = holder as ViewHolder
        val activity = dayActivities[position].name
        holder.textView.text = activity

        // 카드 클릭 리스너 추가
        holder.cardView.setOnClickListener {
            // 클릭된 항목으로 selectedPosition 업데이트
            notifyItemChanged(selectedPosition) // 이전 선택된 항목 업데이트
            selectedPosition = position
            notifyItemChanged(selectedPosition) // 새로 선택된 항목 업데이트
            onItemClicked(position) // 외부에서 처리할 추가적인 작업
        }

        // Inside onBindViewHolder method of TripDayAdapter
        val number = position + 1 // Assuming the first position is 1
        holder.numberIndicator.text = number.toString()

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

    }

    override fun getItemCount(): Int = dayActivities.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
        val cardView: CardView = view.findViewById(R.id.cardView)
        val currentIndicatorCircle: View = view.findViewById(R.id.currentIndicatorCircle)
        val cardLinearLayout: LinearLayout = view.findViewById(R.id.cardLinearLayout)
        val numberIndicator: TextView = view.findViewById(R.id.numberIndicator)
        val topConnectorLine: View = view.findViewById(R.id.topConnectorLine)
        val bottomConnectorLine: View = view.findViewById(R.id.bottomConnectorLine)
    }
}


// TripFragment
class TripDayFragment : Fragment() {
    private var dayActivities: List<Place> = listOf()
    private var selectedPosition: Int = -1

    companion object {
        private const val ARG_DAY_ACTIVITIES = "day_activities"

        fun newInstance(dayActivities: List<Place>): TripDayFragment =
            TripDayFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_DAY_ACTIVITIES, ArrayList(dayActivities))
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dayActivities = it.getParcelableArrayList<Place>(ARG_DAY_ACTIVITIES) ?: listOf()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trip_day, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.dayRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = TripDayAdapter(dayActivities, selectedPosition) { position ->
            // 클릭된 항목 처리 로직
            Toast.makeText(context, "Selected: ${dayActivities[position].name}", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}


