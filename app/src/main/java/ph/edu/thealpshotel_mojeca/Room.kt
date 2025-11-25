package ph.edu.thealpshotel_mojeca

data class Room(
    val room_id: Int,
    val room_type: String,
    val room_bed_type: String,
    val room_total_number_of_guests: Int,
    val room_features: List<String>,
    val room_price_for_one_night: Double
)