package ph.edu.thealpshotel_mojeca

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.ceil
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmScreen(navController: NavController, hotel: Hotel, roomId: Int?, details: HotelDetails?) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var checkInDate by remember { mutableStateOf("") }
    var checkOutDate by remember { mutableStateOf("") }
    var adults by remember { mutableStateOf("1") }
    var children by remember { mutableStateOf("0") }
    var travelPurpose by remember { mutableStateOf("sightseeing") }
    var paymentMethod by remember { mutableStateOf("cash") }

    // Dialog states
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val room = details?.rooms?.find { it.room_id == roomId }
    val totalGuests = remember(adults, children) {
        val adultsCount = adults.toIntOrNull() ?: 1
        val childrenCount = children.toIntOrNull() ?: 0
        adultsCount + childrenCount
    }
    val calculatedRooms = remember(totalGuests, room) {
        if (room != null && room.room_total_number_of_guests > 0) {
            ceil(totalGuests.toDouble() / room.room_total_number_of_guests).toInt()
        } else 1
    }

    // Simple price calculation
    val totalPrice = remember(calculatedRooms, room, travelPurpose) {
        var price = (room?.room_price_for_one_night ?: 0.0) * calculatedRooms
        if (travelPurpose == "business") {
            price += 150 // Additional fee for business
        }
        price
    }

    // Date validation and formatting functions
    fun isValidDateFormat(date: String): Boolean {
        return try {
            // Check common date patterns with regex
            val patterns = listOf(
                "\\d{1,2}/\\d{1,2}/\\d{2,4}", // MM/dd/yy or MM/dd/yyyy
                "\\d{1,2}-\\d{1,2}-\\d{2,4}", // MM-dd-yy or MM-dd-yyyy
                "[A-Za-z]{3} \\d{1,2}, \\d{4}", // MMM dd, yyyy
                "[A-Za-z]{3}, [A-Za-z]{3} \\d{1,2}, \\d{4}" // EEE, MMM dd, yyyy
            )

            patterns.any { pattern -> date.matches(Regex(pattern)) }
        } catch (e: Exception) {
            false
        }
    }

    fun formatDate(input: String): String {
        return try {
            val formats = listOf(
                "MM/dd/yy", "MM/dd/yyyy", "MM-dd-yy", "MM-dd-yyyy",
                "MMM dd, yyyy", "MMM dd yyyy", "EEE, MMM dd, yyyy"
            )

            for (format in formats) {
                try {
                    val sdf = SimpleDateFormat(format, Locale.US)
                    sdf.isLenient = false

                    // For 2-digit years, interpret them as 2000-2099
                    if (format == "MM/dd/yy" || format == "MM-dd-yy") {
                        sdf.set2DigitYearStart(Calendar.getInstance().apply {
                            set(2000, 0, 1)
                        }.time)
                    }

                    val parsedDate = sdf.parse(input)
                    if (parsedDate != null) {
                        // Convert to "EEE, MMM dd, yyyy" format
                        val outputFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US)
                        return outputFormat.format(parsedDate)
                    }
                } catch (e: Exception) {
                    // Continue to next format
                }
            }
            input // Return original if no format matches
        } catch (e: Exception) {
            input
        }
    }

    fun areDatesValid(checkIn: String, checkOut: String): Boolean {
        return try {
            // Try to parse both dates
            val checkInFormatted = if (isValidDateFormat(checkIn)) formatDate(checkIn) else checkIn
            val checkOutFormatted = if (isValidDateFormat(checkOut)) formatDate(checkOut) else checkOut

            val inputFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US)
            val checkInDate = inputFormat.parse(checkInFormatted)
            val checkOutDate = inputFormat.parse(checkOutFormatted)

            if (checkInDate != null && checkOutDate != null) {
                // Check if check-out is after check-in
                checkOutDate.after(checkInDate)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    // SIMPLIFIED Validation function
    fun validateBooking(): Boolean {
        return when {
            firstName.isEmpty() -> {
                errorMessage = "Please enter your first name"
                false
            }
            lastName.isEmpty() -> {
                errorMessage = "Please enter your last name"
                false
            }
            checkInDate.isEmpty() -> {
                errorMessage = "Please enter check-in date"
                false
            }
            checkOutDate.isEmpty() -> {
                errorMessage = "Please enter check-out date"
                false
            }
            !isValidDateFormat(checkInDate) -> {
                // Show what the user entered for debugging
                errorMessage = "Invalid check-in date format: '$checkInDate'. Use: 09/10/24, 09-10-2024, Sep 10, 2024, or Tue, Sep 10, 2024"
                false
            }
            !isValidDateFormat(checkOutDate) -> {
                errorMessage = "Invalid check-out date format: '$checkOutDate'. Use: 09/10/24, 09-10-2024, Sep 10, 2024, or Tue, Sep 10, 2024"
                false
            }
            !areDatesValid(checkInDate, checkOutDate) -> {
                errorMessage = "Check-out date must be after check-in date"
                false
            }
            adults.toIntOrNull() == null || adults.toInt() <= 0 -> {
                errorMessage = "Please enter valid number of adults"
                false
            }
            children.toIntOrNull() == null || children.toInt() < 0 -> {
                errorMessage = "Please enter valid number of children"
                false
            }
            else -> true
        }
    }

    // Function to handle date input with better auto-formatting
    fun handleDateInput(newValue: String, isCheckIn: Boolean = true) {
        if (isCheckIn) {
            checkInDate = newValue
            // Auto-format
            if (isValidDateFormat(newValue)) {
                checkInDate = formatDate(newValue)
            }
        } else {
            checkOutDate = newValue
            if (isValidDateFormat(newValue)) {
                checkOutDate = formatDate(newValue)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Booking Confirm",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Gray
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    // Hotel and Room Info
                    Text(
                        "You are going to reserve:",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )

                    Text(
                        hotel.hotel_name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )

                    // Room type
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            room?.room_type ?: "Room",
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bed type and guest capacity
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                "Bed: ${room?.room_bed_type ?: "N/A"}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                "Guests: ${room?.room_total_number_of_guests ?: 0}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Personal Information
                item {
                    Text("First Name", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp), color = Color.Black)
                    SimpleWhiteTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = "Enter first name"
                    )

                    Text("Last Name", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp), color = Color.Black)
                    SimpleWhiteTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = "Enter last name"
                    )
                }

                // Dates
                item {
                    Text("Check-in date", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp), color = Color.Black)
                    Text(
                        "Accepted formats: 09/10/24, 09-10-2024, Sep 10, 2024, Tue, Sep 10, 2024",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    SimpleWhiteTextField(
                        value = checkInDate,
                        onValueChange = { handleDateInput(it, true) },
                        placeholder = "e.g., 09/10/24, 09-10-2024, Sep 10, 2024, or Tue, Sep 10, 2024"
                    )

                    Text("Check-out date", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp), color = Color.Black)
                    Text(
                        "Accepted formats: 09/10/24, 09-10-2024, Sep 10, 2024, Tue, Sep 10, 2024",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    SimpleWhiteTextField(
                        value = checkOutDate,
                        onValueChange = { handleDateInput(it, false) },
                        placeholder = "e.g., 09/15/24, 09-15-2024, Sep 15, 2024, or Tue, Sep 15, 2024"
                    )
                }

                // Room type section
                item {
                    Text("Room Type:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp), color = Color.Black)
                    Text(
                        room?.room_type ?: "Room",
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Occupants - Two columns for Adults and Children
                item {
                    Text("Occupants", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp), color = Color.Black)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Adults Column
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Adults", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp), color = Color.Black)
                            SimpleWhiteTextField(
                                value = adults,
                                onValueChange = { newValue ->
                                    // Allow only numbers
                                    if (newValue.all { it.isDigit() }) {
                                        adults = newValue
                                    }
                                },
                                placeholder = "0",
                                keyboardType = KeyboardType.Number
                            )
                        }

                        // Children Column
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Children", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp), color = Color.Black)
                            SimpleWhiteTextField(
                                value = children,
                                onValueChange = { newValue ->
                                    // Allow only numbers
                                    if (newValue.all { it.isDigit() }) {
                                        children = newValue
                                    }
                                },
                                placeholder = "0",
                                keyboardType = KeyboardType.Number
                            )
                        }
                    }

                    Text(
                        "Total Rooms: $calculatedRooms",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.Black
                    )
                }

                // Travel Purpose
                item {
                    Text("Travel for business?", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp), color = Color.Black)
                    Column {
                        RadioButtonItem(
                            selected = travelPurpose == "sightseeing",
                            onClick = { travelPurpose = "sightseeing" },
                            text = "For sightseeing"
                        )
                        RadioButtonItem(
                            selected = travelPurpose == "business",
                            onClick = { travelPurpose = "business" },
                            text = "+₱150 for business with a meeting room"
                        )
                    }
                }

                // Payment Method
                item {
                    Text("Which way to pay?", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp), color = Color.Black)
                    Column {
                        RadioButtonItem(
                            selected = paymentMethod == "cash",
                            onClick = { paymentMethod = "cash" },
                            text = "Cash"
                        )
                        RadioButtonItem(
                            selected = paymentMethod == "credit",
                            onClick = { paymentMethod = "credit" },
                            text = "Credit card"
                        )
                        RadioButtonItem(
                            selected = paymentMethod == "epay",
                            onClick = { paymentMethod = "epay" },
                            text = "E-pay"
                        )
                    }
                }

                // Price and Book Button
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Total: ₱${String.format("%.0f", totalPrice)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Button(
                            onClick = {
                                if (validateBooking()) {
                                    showConfirmationDialog = true
                                } else {
                                    showErrorDialog = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text(
                                "Book Now",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            // Error Dialog - SHOWS SPECIFIC ERROR
            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Error")
                        }
                    },
                    text = {
                        Text(errorMessage)
                    },
                    confirmButton = {
                        Button(
                            onClick = { showErrorDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("OK")
                        }
                    }
                )
            }

            // Confirmation Dialog
            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialog = false },
                    title = {
                        Text("Confirm Booking")
                    },
                    text = {
                        Column {
                            Text("Are you going to book this room?")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Save booking information
                                val booking = Booking(
                                    id = 0,
                                    hotelName = hotel.hotel_name,
                                    roomType = room?.room_type ?: "Room",
                                    firstName = firstName,
                                    lastName = lastName,
                                    checkInDate = formatDate(checkInDate),
                                    checkOutDate = formatDate(checkOutDate),
                                    adults = adults.toInt(),
                                    children = children.toInt(),
                                    totalRooms = calculatedRooms,
                                    totalPrice = totalPrice,
                                    paymentMethod = paymentMethod,
                                    bookingType = travelPurpose
                                )

                                BookingManager.addBooking(booking)

                                showConfirmationDialog = false
                                navController.navigate("my_bookings")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showConfirmationDialog = false }
                        ) {
                            Text("No", color = Color.Gray)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleWhiteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray
            )
        },

        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
fun RadioButtonItem(selected: Boolean, onClick: () -> Unit, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.Black)
    }
}