package ph.edu.thealpshotel_mojeca

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ph.edu.thealpshotel_mojeca.ui.theme.TheAlpsHotel_MojecaTheme

class HotelInfo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val hotelName = intent.getStringExtra("hotel_name") ?: "Unknown"
        val hotelRating = intent.getIntExtra("hotel_rating", 0)
        val hotelDistance = intent.getDoubleExtra("hotel_to_ski_distance", 0.0)
        val hotelImage = intent.getStringExtra("hotel_cover_image") ?: ""

        setContent {
            TheAlpsHotel_MojecaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HotelInfoContent(
                        hotelName = hotelName,
                        hotelRating = hotelRating,
                        hotelDistance = hotelDistance,
                        hotelImage = hotelImage,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun HotelInfoContent(
    hotelName: String,
    hotelRating: Int,
    hotelDistance: Double,
    hotelImage: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {

        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Left side: back button + title + logo
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "The Alps Hotel",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.france_national_flag),
                    contentDescription = "Logo",
                    modifier = Modifier.size(32.dp)
                )
            }

            // Right side: user icon
            IconButton(onClick = {
                val intent = Intent(context, User::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "User Profile",
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hotel details
        Text(text = hotelName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Rating: $hotelRating ⭐")
        Text(text = "Distance to ski lift: $hotelDistance km")
        Spacer(modifier = Modifier.height(16.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/$hotelImage")
                .crossfade(true)
                .build(),
            contentDescription = hotelName,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
    }
}
