package cheysoff.weather

class City(private val name : String, private val latitude: Int, private val longitude: Int) {
    fun GetName() : String {
        return name
    }
    fun GetLatitude() : Int {
        return latitude
    }
    fun GetLongitude() : Int {
        return longitude
    }
}