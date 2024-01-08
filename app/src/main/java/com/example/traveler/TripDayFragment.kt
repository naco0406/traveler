import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.traveler.R

data class Trip (
    val id: Int,
    val city: String,
    val period: Int,
    val destinations: List<List<String>>,
    val restaurant: List<List<String>>,
    val lodging: List<String>,
    val selected: Int,
    val review: List<String>,
)


class TripDayAdapter(private val dayActivities: List<String>, private var selectedPosition: Int,
                     private val onItemClicked: (Int) -> Unit) :
    RecyclerView.Adapter<TripDayAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val activity = dayActivities[position]
        holder.textView.text = activity

        // 카드 클릭 리스너 추가
        holder.cardView.setOnClickListener {
            // 클릭된 항목으로 selectedPosition 업데이트
            notifyItemChanged(selectedPosition) // 이전 선택된 항목 업데이트
            selectedPosition = position
            notifyItemChanged(selectedPosition) // 새로 선택된 항목 업데이트
            onItemClicked(position) // 외부에서 처리할 추가적인 작업
        }

        // 선택된 항목에 대한 로직
        if (position == selectedPosition) {
            // 카드 포커스 및 인디케이터 색상 변경
            holder.cardView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).start()
            holder.currentIndicatorCircle.setBackgroundResource(R.drawable.circle)
        } else {
            // 기본 카드 및 인디케이터 설정
            holder.cardView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start()
            holder.currentIndicatorCircle.setBackgroundResource(R.drawable.circle)
        }
    }

    override fun getItemCount(): Int = dayActivities.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
        val cardView: CardView = view.findViewById(R.id.cardView)
        val currentIndicatorCircle: View = view.findViewById(R.id.currentIndicatorCircle)
    }
}


// TripFragment
class TripDayFragment : Fragment() {
    private var dayActivities: List<String> = listOf()
    private var selectedPosition: Int = -1

    companion object {
        private const val ARG_DAY_ACTIVITIES = "day_activities"

        fun newInstance(dayActivities: List<String>): TripDayFragment =
            TripDayFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(ARG_DAY_ACTIVITIES, ArrayList(dayActivities))
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dayActivities = it.getStringArrayList(ARG_DAY_ACTIVITIES).orEmpty()
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
            Toast.makeText(context, "Selected: ${dayActivities[position]}", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}


