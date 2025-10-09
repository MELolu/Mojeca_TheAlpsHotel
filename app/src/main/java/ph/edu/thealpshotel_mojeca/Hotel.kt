package ph.edu.thealpshotel_mojeca

import android.graphics.Bitmap

data class Hotel(
    val hotel_id: Int,
    val hotel_name: String,
    val hotel_rating: Double,
    val hotel_to_ski_distance: Double,
    val hotel_cover_image: String,
)
