package ph.edu.thealpshotel_mojeca
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(navController: NavController) {

    val bookings = remember { BookingManager.getBookings() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Bookings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Gray
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // Header with booking count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "List of my bookings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            if (bookings.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "📋",
                            fontSize = 48.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "No bookings yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(bookings) { index, b ->
                        BookingItem(
                            index = index + 1,
                            fullName = "${b.firstName} ${b.lastName}",
                            hotelName = b.hotelName,
                            roomType = b.roomType,
                            checkIn = b.checkInDate,
                            checkOut = b.checkOutDate,
                            adults = b.adults,
                            children = b.children,
                            rooms = b.totalRooms,
                            paymentType = b.paymentMethod,
                            bookingType = b.bookingType,
                            price = b.totalPrice
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItem(
    index: Int,
    fullName: String,
    hotelName: String,
    roomType: String,
    checkIn: String,
    checkOut: String,
    adults: Int,
    children: Int,
    rooms: Int,
    bookingType: String,
    paymentType: String,
    price: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Header with booking number and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Booking number
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "#$index",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "BOOKING",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF666666),
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guest Information
            InfoRow(
                title = "GUEST",
                value = fullName,
                titleColor = Color(0xFF2196F3)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Hotel Information
            InfoRow(
                title = "HOTEL",
                value = hotelName,
                titleColor = Color(0xFF4CAF50)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Room Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    roomType,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF666666),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "$rooms room${if (rooms > 1) "s" else ""}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dates Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    InfoRowSmall(
                        title = "CHECK-IN",
                        value = checkIn,
                        titleColor = Color(0xFF4CAF50)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    InfoRowSmall(
                        title = "CHECK-OUT",
                        value = checkOut,
                        titleColor = Color(0xFFF44336)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guests and Booking Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRowSmall(
                    title = "GUESTS",
                    value = "$adults Adults, $children Children",
                    titleColor = Color(0xFFFF9800)
                )

                Spacer(modifier = Modifier.width(16.dp))

                InfoRowSmall(
                    title = "PURPOSE",
                    value = bookingType.replaceFirstChar { it.uppercase() },
                    titleColor = Color(0xFF9C27B0)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment and Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoRowSmall(
                    title = "PAYMENT",
                    value = paymentType.replaceFirstChar { it.uppercase() },
                    titleColor = Color(0xFF607D8B)
                )

                // Total Price
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "TOTAL AMOUNT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666)
                    )
                    Text(
                        "₱${String.format("%.0f", price)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    title: String,
    value: String,
    titleColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor,
            letterSpacing = 0.5.sp
        )
        Text(
            value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun InfoRowSmall(
    title: String,
    value: String,
    titleColor: Color
) {
    Column {
        Text(
            title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor,
            letterSpacing = 0.5.sp
        )
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

