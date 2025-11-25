package ph.edu.thealpshotel_mojeca

object BookingManager {
    private val bookings = mutableListOf<Booking>()
    private var nextId = 1

    fun addBooking(booking: Booking) {
        bookings.add(booking.copy(id = nextId++))
    }

    fun getBookings(): List<Booking> {
        return bookings.toList()
    }

    fun clearBookings() {
        bookings.clear()
        nextId = 1
    }
}