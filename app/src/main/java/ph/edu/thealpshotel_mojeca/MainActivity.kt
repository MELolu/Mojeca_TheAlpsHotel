package ph.edu.thealpshotel_mojeca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson
import kotlin.math.ceil
import ph.edu.thealpshotel_mojeca.ui.theme.TheAlpsHotel_MojecaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheAlpsHotel_MojecaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val navController = rememberNavController()
                    var hotels by remember { mutableStateOf(emptyList<Hotel>()) }

                    // Load JSON from assets once
                    val context = LocalContext.current
                    LaunchedEffect(Unit) {
                        val json = context.assets.open("hotels.json").bufferedReader().use { it.readText() }
                        hotels = Gson().fromJson(json, Array<Hotel>::class.java).toList()
                    }

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            Homepage(navController = navController, hotels = hotels)
                        }
                        composable("profile") {
                            ProfileScreen(navController = navController)
                        }
                        composable("hotels_details_screen/{hotelId}") { backStackEntry ->
                            val hotelId = backStackEntry.arguments?.getString("hotelId")?.toInt()
                            val hotel = hotels.find { it.hotel_id == hotelId }
                            hotel?.let {
                                HotelDetailsScreen(navController, it)
                            }
                        }
                        composable("booking_confirm/{hotelId}/{roomId}") { backStackEntry ->
                            val hotelId = backStackEntry.arguments?.getString("hotelId")?.toInt()
                            val roomId = backStackEntry.arguments?.getString("roomId")?.toInt()
                            val hotel = hotels.find { it.hotel_id == hotelId }
                            // Load hotel details to get room information
                            val context = LocalContext.current
                            var details by remember { mutableStateOf<HotelDetails?>(null) }

                            LaunchedEffect(hotelId) {
                                try {
                                    val fileName = "hotels_details.$hotelId.json"
                                    val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
                                    details = Gson().fromJson(json, HotelDetails::class.java)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            hotel?.let {
                                BookingConfirmScreen(navController, it, roomId, details)
                            }
                        }

                        composable("my_bookings") {
                           MyBookingsScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}

// ======================
// Homepage
// ======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Homepage(navController: NavController, hotels: List<Hotel>) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredHotels = hotels.filter {
        it.hotel_name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "The Alps Hotel",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(R.drawable.france_national_flag),
                            contentDescription = "Flag Logo",
                            modifier = Modifier.width(40.dp)
                        )
                    }
                },
                actions = {
                    // Booking Icon
                    Icon(
                        painter = painterResource(R.drawable.bookmark),
                        contentDescription = "My Bookings",
                        modifier = Modifier
                            .size(49.dp)
                            .clickable { navController.navigate("my_bookings") }
                            .padding(end = 8.dp),
                        tint = Color.White
                    )

                    // Profile Icon
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "User Icon",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { navController.navigate("profile") },
                        tint = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Gray,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search...") },
                singleLine = true
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredHotels) { hotel ->
                    HotelCard(hotel) {
                        navController.navigate("hotels_details_screen/${hotel.hotel_id}")
                    }
                }
            }
        }
    }
}

// ======================
// Hotel Card with Image Padding
// ======================
@Composable
fun HotelCard(hotel: Hotel, onClick: () -> Unit) {
    val singleRating = hotel.hotel_rating.toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hotel image with padding
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .padding(4.dp) // Added padding around the image
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/${hotel.hotel_cover_image}")
                        .crossfade(true)
                        .build(),
                    contentDescription = hotel.hotel_name,
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = hotel.hotel_name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = hotel.hotel_rating.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    repeat(singleRating) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Stars",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFFC107)
                        )
                    }
                }
                Text(
                    text = "${hotel.hotel_to_ski_distance} km to ski left",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// ======================
// Profile Screen
// ======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "The Alps Hotel",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(R.drawable.france_national_flag),
                            contentDescription = "Flag Logo",
                            modifier = Modifier.width(40.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Gray,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Profile image with padding
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .padding(8.dp) // Added padding around profile image
            ) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(100.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Germel Mojeca ", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Developer", fontSize = 18.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "I am germel Mojeca Studying BSIT course at Comteq and business College ",
                fontSize = 16.sp,
                color = Color.DarkGray,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ======================
// Hotel Details Screen
// ======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(navController: NavController, hotel: Hotel) {
    val context = LocalContext.current
    var details by remember { mutableStateOf<HotelDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Guest Review", "Room Selection")

    LaunchedEffect(hotel.hotel_id) {
        try {
            val fileName = "hotels_details.${hotel.hotel_id}.json"
            val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
            details = Gson().fromJson(json, HotelDetails::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "The Alps Hotel",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(R.drawable.france_national_flag),
                            contentDescription = "Flag Logo",
                            modifier = Modifier.width(40.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "User Icon",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { navController.navigate("profile") },
                        tint = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Gray,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color.Black
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) Color.Black else Color.Gray
                                )
                            }
                        )
                    }
                }

                when (selectedTab) {
                    0 -> GuestReviewTab(details, hotel.hotel_name)
                    1 -> RoomSelectionTab(details, hotel.hotel_name, navController, hotel)
                }
            }
        }
    }
}

@Composable
fun GuestReviewTab(details: HotelDetails?, hotelName: String) {
    if (details == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No data available", color = Color.Black)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        item {
            Text(
                text = hotelName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = Color.Black
            )

            // Overall Ratings Section
            Text(
                "Overall Ratings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.Black
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    details.guest_reviews.ratings_categories.forEach { category ->
                        val entry = category.entries.first()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                entry.key,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    entry.value.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Rating",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFFFFC107)
                                )
                            }
                        }
                        if (category != details.guest_reviews.ratings_categories.last()) {
                            Divider(color = Color.LightGray, thickness = 1.dp)
                        }
                    }
                }
            }

            // Guest Reviews Section
            Text(
                "Reviews",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.Black
            )
        }

        // Horizontal Scroll for Reviews
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(details.guest_reviews.reviews_objects) { review ->
                    Card(
                        modifier = Modifier
                            .width(300.dp)
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header with username and country
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    review.username,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                                Text(
                                    review.country,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Review text
                            Text(
                                review.review_text,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = Color.DarkGray,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoomSelectionTab(details: HotelDetails?, hotelName: String, navController: NavController, hotel: Hotel) {
    if (details == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No data available", color = Color.Black)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        item {
            // Hotel name centered inside the tab
            Text(
                text = hotelName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = Color.Black
            )
        }

        items(details.rooms) { room ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate("booking_confirm/${hotel.hotel_id}/${room.room_id}")
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Room type
                    Text(
                        room.room_type,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Room details in two columns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = "Bed",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    room.room_bed_type,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = "Guests",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Guests: ${room.room_total_number_of_guests}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Price
                        Text(
                            "₱${String.format("%.0f", room.room_price_for_one_night)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF4CAF50)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Features
                    Text(
                        "Features:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = Color.Black
                    )

                    Column {
                        room.room_features.forEach { feature ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("•", color = Color(0xFF4CAF50))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    feature,
                                    fontSize = 14.sp,
                                    color = Color.DarkGray // Fixed color from White to DarkGray for visibility
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}