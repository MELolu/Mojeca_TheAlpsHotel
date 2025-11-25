package ph.edu.thealpshotel_mojeca

data class GuestReview(
    val ratings_categories: List<Map<String, Double>>,
    val reviews_objects: List<ReviewObject>
)