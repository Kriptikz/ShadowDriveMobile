package com.kriptikz.shadowdrivemobile.ui.screens.drive_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kriptikz.shadowdrivemobile.ui.screens.SDMScaffold
import com.kriptikz.shadowdrivemobile.ui.theme.ShadowDriveMobileTheme
import com.kriptikz.shadowdrivemobile.R

@Composable
fun DriveScreen(
    driveUiState: DriveUiState,
    modifier: Modifier = Modifier
) {
    SDMScaffold(title = "Drive") {
        when (driveUiState) {
            is DriveUiState.Loading -> LoadingScreen(modifier)
            is DriveUiState.Success -> PhotosGridScreen(photoUrls = driveUiState.fileNames, modifier = modifier)
            is DriveUiState.Error -> ErrorScreen(modifier)
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            modifier = Modifier.size(200.dp),
            painter = painterResource(id = R.drawable.loading_img),
            contentDescription = stringResource(id = R.string.loading)
        )
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(stringResource(id = R.string.loading_failed))
    }
}

@Preview(showBackground = true)
@Composable
fun DriveScreenPreview() {
    ShadowDriveMobileTheme {
        DriveScreen(DriveUiState.Success(listOf("Some State")))
    }
}

@Composable
fun ResultScreen(fileNames: List<String>, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
//      Text(text = "Found ${fileNames.size} files")
      Text(text = "${fileNames[0]}")
    }

}

@Composable
fun DrivePhotoCard(imageUrl: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = 8.dp,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "description",
            error = painterResource(id = R.drawable.ic_broken_image),
            placeholder = painterResource(id = R.drawable.loading_img),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun PhotosGridScreen(photoUrls: List<String>, modifier: Modifier = Modifier) {
    if (photoUrls.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(photoUrls.size) { index ->
                Column {
                    Text(
                        fontSize = 12.sp,
                        text = photoUrls[index].split("/")[4],
                        modifier = Modifier.height(16.dp).padding(start = 10.dp)
                    )
                    DrivePhotoCard(photoUrls[index])
                }
            }
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize()
        ) {
            Text(text = "No Files")
        }
    }
}
