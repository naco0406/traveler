//
//
//class MapFragment : Fragment(), OnMapReadyCallback {
//
//    private lateinit var naverMap: NaverMap
//    private lateinit var locationSource: FusedLocationSource
//    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_map, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
////        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as com.naver.maps.map.MapFragment?
////            ?: com.naver.maps.map.MapFragment.newInstance().also {
////                childFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
////            }
////        mapFragment.getMapAsync(this)
////
////        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
//    }
//
//    override fun onMapReady(naverMap: NaverMap) {
//        this.naverMap = naverMap
//
//        // 지도 설정
//        naverMap.apply {
//            maxZoom = 18.0
//            minZoom = 10.0
//            setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true)
//            setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
//            setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, true)
//            uiSettings.isLocationButtonEnabled = true
//            locationTrackingMode = LocationTrackingMode.Follow
//            locationSource = this@MapFragment.locationSource
//        }
//
//        // 초기 위치 설정
//        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.4979921, 127.028046)).animate(
//            CameraAnimation.Easing)
//        naverMap.moveCamera(cameraUpdate)
//    }
//}
