package ph.edu.thealpshotel_mojeca

data class HotelDetails(
    val hotel_id: Int,
    val hotel_name: String,
    val guest_reviews: GuestReview,
    val rooms: List<Room>
)