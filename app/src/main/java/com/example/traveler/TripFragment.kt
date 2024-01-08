// TripFragment.kt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.traveler.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TripAdapter(private val trip: Trip) : RecyclerView.Adapter<TripAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // destinations, restaurant, lodging 항목에 따라 뷰 바인딩 구현
        val allItems = trip.destinations.flatten() + trip.restaurant.flatten() + trip.lodging

        // 현재 위치에 맞는 데이터를 뷰에 바인딩
        holder.textView.text = allItems[position]

        val context = holder.itemView.context

    }

    private val totalItemCount: Int by lazy {
        trip.destinations.flatten().size + trip.restaurant.flatten().size + trip.lodging.size
    }

    override fun getItemCount(): Int = totalItemCount

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 뷰 홀더 구성 요소 초기화
        val textView: TextView = view.findViewById(R.id.textView)
    }
}
class TripFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabs: TabLayout
    private lateinit var trip: Trip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 데이터 로드 로직
        trip = Trip(
            id = 659,
            city = "서울",
            period = 2,
            destinations = listOf(
                listOf("경복궁", "인사동", "북촌 한옥마을", "남산타워"),
                listOf("동대문 디자인 플라자", "홍대 거리", "이태원", "청계천")
            ),
            restaurant = listOf(
                listOf("경복궁 근처 한식당", "인사동 전통찻집", "삼청동 맛집"),
                listOf("동대문 근처 식당", "홍대 인근 카페", "이태원 글로벌 레스토랑")
            ),
            lodging = listOf("명동 근처 호텔"),
            selected = 5,
            review = listOf("재미없어요", "맛없어요", "시끄러워요")
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trip, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        tabs = view.findViewById(R.id.tabs)

        // ViewPager2와 TabLayout 설정
        viewPager.adapter = TripViewPagerAdapter(requireActivity(), trip)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = "Day ${position + 1}"
        }.attach()

        return view
    }

    // ViewPager2 어댑터
    private inner class TripViewPagerAdapter(fa: FragmentActivity, private val trip: Trip) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = trip.destinations.size

        override fun createFragment(position: Int): Fragment {
            // 각 포지션에 맞는 TripDayFragment 생성
            val dayActivities = trip.destinations[position] + trip.restaurant[position]
            return TripDayFragment.newInstance(dayActivities)
        }
    }
}
