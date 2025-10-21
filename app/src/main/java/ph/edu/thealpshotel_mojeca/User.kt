package ph.edu.thealpshotel_mojeca

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import ph.edu.thealpshotel_mojeca.ui.theme.TheAlpsHotel_MojecaTheme

// Main User Activity
class User : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge mode for immersive UI
        enableEdgeToEdge()
        setContent {
            TheAlpsHotel_MojecaTheme {
                // Scaffold provides basic layout structure
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Call the Profile composable to display user info
                    Profile(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Profile(modifier: Modifier = Modifier) {

    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Left side: back button + hotel title + logo
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Back button to navigate to MainActivity
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

                // Hotel title
                Text(
                    text = "The Alps Hotel",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Hotel logo (flag image)
                Image(
                    painter = painterResource(id = R.drawable.france_national_flag),
                    contentDescription = "Logo",
                    modifier = Modifier.size(32.dp)
                )
            }

            // Right side: user icon (optional navigation)
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

        Spacer(modifier = Modifier.height(24.dp))

        // Profile content section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            // User avatar/profile picture
            Image(
                painter = painterResource(id = R.drawable.leonardo_da_vinci),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // User name
            Text(
                text = "Germel Mojeca",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // User role or description
            Text(
                text = "Student",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(16.dp))


            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Germel Mojeca is a student and currently studying in Comteq College",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    TheAlpsHotel_MojecaTheme {
        Profile()
    }
}
