package com.kriptikz.shadowdrivemobile.ui.screens.home_screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kriptikz.shadowdrivemobile.R
import com.kriptikz.shadowdrivemobile.ui.screens.SDMScaffold
import com.kriptikz.shadowdrivemobile.ui.theme.ShadowDriveMobileTheme


@Composable
fun HomeScreen(
    homeUiState: HomeUiState,
    navController: NavController,
    onNavigateToDrive: (drivePublicKey: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val usedStorage = homeUiState.usedStorage
    val totalStorage = homeUiState.totalStorage
    val drives = homeUiState.drives
    val recentItems = homeUiState.recentItems

    SDMScaffold(title = "Home") {
        val scrollableState = ScrollState(0)
        Column(
            modifier = Modifier
                .scrollable(
                    scrollableState,
                    Orientation.Horizontal,
                    enabled = true,
                )
        ) {
            Box(
                modifier = Modifier.background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RectangleShape)
                        .fillMaxWidth()
                        .fillMaxHeight(0.24f)
                        .background(Color.Black)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    AvailableStorage(usedStorage, totalStorage)
                    Text(
                        text = "Drives",
                        style = TextStyle(
                            color = Color.LightGray,
                            fontSize = 16.sp
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                    HorizontalScrollingDrives(
                        drives = drives,
                        onClick = onNavigateToDrive
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Recent Files",
                        style = TextStyle(
                            color = Color.LightGray,
                            fontSize = 16.sp
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                    VerticalScrollingRecentFileItems(recentItems = recentItems)
                }

            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ShadowDriveMobileTheme {
        HomeScreen(HomeUiState(
            usedStorage = 14.5,
            totalStorage = 40.0,
            drives = listOf("Photos", "Videos", "Pictures", "Temporary", "AnotherOne", "Ok"),
            recentItems = listOf(
                RecentItem(
                    name = "file.txt",
                    day = "today",
                    size = "10.0MB",
                ),
                RecentItem(
                    name = "file.txt",
                    day = "today",
                    size = "10.0MB",
                ),
                RecentItem(
                    name = "file.txt",
                    day = "today",
                    size = "10.0MB",
                ),
                RecentItem(
                    name = "file.txt",
                    day = "today",
                    size = "10.0MB",
                ),
                RecentItem(
                    name = "file.txt",
                    day = "today",
                    size = "10.0MB",
                ),
                RecentItem(
                    name = "omarsName.txt",
                    day = "today",
                    size = "10.0GB",
                ),
                RecentItem(
                    name = "picture.png",
                    day = "yesterday",
                    size = "420.0MB",
                ),
                RecentItem(
                    name = "picture.jpg",
                    day = "yesterday",
                    size = "420.0GB",
                ),
            )
        ),
        navController = rememberNavController(),
        onNavigateToDrive = {}
        )
    }
}

@Composable
fun DriveClickable(text: String, onClick: (drivePublicKey: String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White)
            .padding(8.dp)
            .clickable {
                println("CLICKED: ${text}")
                onClick(text)
            }
    ) {
        Icon(painter = painterResource(
            id = R.drawable.ic_folder_drives),
            contentDescription = null,
            tint = Color.LightGray,
        )
        Text(
            text = text,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@Preview
@Composable
fun MyDrivesPreview() {
    DriveClickable(
        "Photos",
    ) {}
}

@Composable
fun HorizontalScrollingDrives(drives: List<String>, onClick: (drivePublicKey: String) -> Unit, modifier: Modifier = Modifier) {
    LazyRow(
        content = {
            items(drives) { drive ->
                DriveClickable(
                    text = drive,
                    onClick = onClick
                )

            }
        },
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(widthDp = 200)
@Composable
fun HorizontalScrollingDrivesPreview() {
    val drives = listOf("Photos", "Videos", "Pictures", "Temporary", "AnotherOne", "Ok")
    HorizontalScrollingDrives(drives, onClick = {})
}

@Composable
fun AvailableStorage(usedStorage: Double, totalStorage: Double, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(top = 60.dp)
            .clip(RoundedCornerShape(18.dp))
            .fillMaxWidth(0.9f)
            .height(150.dp)
            .background(Color.DarkGray)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(0.6f)
        ) {
            Text(
                text = "Available Cloud Storage",
                style = TextStyle(Color.LightGray),
                modifier = Modifier.height(30.dp)
            )
            Text(
                text = "${usedStorage}GB of ${totalStorage}GB used",
                style = TextStyle(Color.White),
                modifier = Modifier.height(30.dp)
            )
        }
        Box(
            contentAlignment = Alignment.Center,
        ){
            CircularProgressBar(percentage = (usedStorage / totalStorage).toFloat(), number = 100, startPercentage = 0.0f)
        }
    }
}

@Preview(widthDp = 500)
@Composable
fun AvailableStoragePreview() {
    AvailableStorage(14.5, 40.0, modifier = Modifier.background(Color.White))
}

@Composable
fun CircularProgressBar(
    startPercentage: Float,
    percentage: Float,
    number: Int,
    fontSize: TextUnit = 18.sp,
    radius: Dp = 50.dp,
    color: Color = Color.Blue,
    strokeWidth: Dp = 6.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
    ) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val curPercentage = animateFloatAsState(
        targetValue = if(animationPlayed) percentage else startPercentage,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2f)
    ) {
        Box(
            modifier = Modifier
                .size(radius * 2f)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        Canvas(modifier = Modifier.size(radius * 2f)) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * curPercentage.value,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = (curPercentage.value * number).toInt().toString() + '%',
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(widthDp = 500)
@Composable
fun AvailableStorageProgressCirclePreview() {
    CircularProgressBar(percentage = 0.8f, number = 100, startPercentage = 0.8f)
}

@Composable
fun RecentFileItem(recentItem: RecentItem, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(color = Color.Gray)
        )
        Column(
            modifier = Modifier
                .padding(8.dp)
                .padding(start = 14.dp)
        ) {
            Text(
                text = recentItem.name,
                fontWeight = FontWeight.Bold,
                style = TextStyle(color = Color.Black),
            )
            Text(
                text = recentItem.day,
                fontWeight = FontWeight.Light,
                style = TextStyle(color = Color.LightGray),
            )
        }
        Text(
            text = recentItem.size,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(widthDp = 500)
@Composable
fun RecentFileItemPreview() {
    val recentItem: RecentItem = RecentItem(
        name = "firstItem.doc",
        day = "today",
        size = "0.2GB"
    )
    RecentFileItem(recentItem = recentItem, modifier = Modifier.background(Color.White))
}

data class RecentItem(
    val name: String,
    val day: String,
    val size: String,
)

@Composable
fun VerticalScrollingRecentFileItems(recentItems: List<RecentItem>, modifier: Modifier = Modifier) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 30.dp),
        content = {
            items(recentItems) { recentItem ->
                RecentFileItem(recentItem)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    )
}

@Preview(widthDp = 500)
@Composable
fun VerticalScrollingRecentFileItemPreview() {
    val recentItems = listOf(
        RecentItem(
            name = "file.txt",
            day = "today",
            size = "10.0MB",
        ),
        RecentItem(
            name = "file.txt",
            day = "today",
            size = "10.0MB",
        ),
        RecentItem(
            name = "file.txt",
            day = "today",
            size = "10.0MB",
        ),
        RecentItem(
            name = "file.txt",
            day = "today",
            size = "10.0MB",
        ),
        RecentItem(
            name = "file.txt",
            day = "today",
            size = "10.0MB",
        ),
    )
    VerticalScrollingRecentFileItems(recentItems = recentItems, modifier = Modifier.background(Color.White))
}
