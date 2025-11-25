package ph.edu.thealpshotel_mojeca

data class Booking(
    val id: Int,
    val hotelName: String,
    val roomType: String,
    val firstName: String,
    val lastName: String,
    val checkInDate: String,
    val checkOutDate: String,
    val adults: Int,
    val children: Int,
    val totalRooms: Int,
    val totalPrice: Double,
    val paymentMethod: String,
    val bookingType: String,
    val status: String = "Confirmed"
)