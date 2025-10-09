package ph.edu.thealpshotel_mojeca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Person
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson
import ph.edu.thealpshotel_mojeca.ui.theme.TheAlpsHotel_MojecaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheAlpsHotel_MojecaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Homepage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Homepage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var hotels by remember { mutableStateOf(emptyList<Hotel>()) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredHotels = hotels.filter {
        it.hotel_name.contains(searchQuery, ignoreCase = true)
    }

    // Load json data
    LaunchedEffect(Unit) {
        val json = context.assets.open("hotels.json")
            .bufferedReader()
            .use { it.readText() }
        val gson = Gson()
        val hotelArray = gson.fromJson(json,
            Array<Hotel>::class.java)
        hotels = hotelArray.toList()
    }

    //Container
    Column(
        modifier = Modifier
    ){
        //Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "The Alp's Hotels",
            )
            // Left side: title Logo
            Image(
                painter = painterResource(id = R.drawable.france_national_flag),
                contentDescription = "Logo",
                modifier = Modifier.width(30.dp)

            )
            // Right side: user icon
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "User Icon",
                modifier = Modifier.width(40.dp)
            )
        }
        // Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = {Text("Search.....")},
            singleLine = true,
        )
        // Hotel List
        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredHotels){ hotel ->
                HotelCard(hotel)
            }
        }
    }
}

@Composable
fun HotelCard(hotel: Hotel) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hotel Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${hotel.hotel_cover_image}")
                    .crossfade(true)
                    .build(),
                contentDescription = hotel.hotel_name,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                modifier = Modifier.size(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            // Hotel Info
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 14.dp)
            ) {
                Text(
                    text = hotel.hotel_name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hotel.hotel_rating.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    //Star
                    // TODO: Replace with real star rating
                    repeat(hotel.hotel_rating.toInt()) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star Icon",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFFD600)
                        )
                    }
                }
                Text(
                    text = "${hotel.hotel_to_ski_distance} km to ski lift",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    TheAlpsHotel_MojecaTheme {
        Homepage()
    }
}
