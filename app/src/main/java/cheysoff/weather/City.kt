package cheysoff.weather

class City(private val name : String, private val latitude: Double, private val longitude: Double) {
    fun GetName() : String {
        return name
    }
    fun GetLatitude() : Double {
        return latitude
    }
    fun GetLongitude() : Double {
        return longitude
    }
}