package com.example.myradio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myradio.model.*
import com.example.myradio.ui.theme.MyRadioTheme

@Composable
fun Screen(
    stations: List<StationItem>,
    currentPlayingStation: StationItem?,
    currentSelectStation: StationItem?,
    lastUserEvent: UserNotification?,
    favAction: PlayerAction?,
    selectedStream: Stream?,

    onStreamClicked: (Stream) -> Unit,
    onStationClicked: (StationItem) -> Unit,
    onFavClicked: (PlayerAction) -> Unit

) {
    val scaffoldState = rememberScaffoldState()

    lastUserEvent?.let {
        LaunchedEffect(lastUserEvent) {
            scaffoldState.snackbarHostState.showSnackbar(lastUserEvent.message)
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            if (favAction != null) {
                PlayerActionButton(
                    action = favAction,
                    onAction = onFavClicked
                )
            }
        }
    // TODO Add player composable
    ) {
        Column {
            LazyColumn {
                items(stations) { station ->
                    StationRow(
                        station,
                        isSelected = currentSelectStation?.id == station.id,
                        selectedStream = selectedStream,
                        onStationClicked = onStationClicked,
                        onStreamClicked = onStreamClicked
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerActionButton(action: PlayerAction, onAction: (PlayerAction) -> Unit) {
    FloatingActionButton(
        onClick = {
            onAction(action)
        }
    ) {
        Icon(
            imageVector = action.imageVector,
            contentDescription = stringResource(id = action.contentDescription)
        )
    }
}

@Composable
fun StationRow(
    station: StationItem,
    isSelected: Boolean,
    selectedStream: Stream?,
    onStationClicked: (StationItem) -> Unit,
    onStreamClicked: (Stream) -> Unit
) {
    Column {
        Surface(
            elevation = if (isSelected) 4.dp else 0.dp
        ) {

            Row(
                modifier = Modifier
                    .clickable { onStationClicked(station) }
                    .padding(vertical = 4.dp),
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(station.station.icon)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_radio_no_image),
                    contentDescription = station.station.title,
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                        .size(64.dp)
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = station.station.title, style = MaterialTheme.typography.h6)
                    Text(
                        text = station.station.description,
                        style = MaterialTheme.typography.body2,
                        maxLines = if (isSelected) 4 else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        if (isSelected) {
            Row {
                station.station.streams.forEachIndexed { index, stream ->
                    StationSource("Src $index", stream == selectedStream) { onStreamClicked(stream) }
                }
            }
        }
    }
}

@Composable
fun StationSource(title: String, isSelected: Boolean, onClicked: () -> Unit) {
    val content: @Composable RowScope.() -> Unit = {
        Text(text = title)
    }
    if (isSelected) {
        OutlinedButton(
            onClick = onClicked,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
            content = content
        )
    } else {
        Button(
            onClick = onClicked,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
            content = content
        )
    }
}

val testStationItem = StationItem(
    Station(
        "Discover Trance",
        "Featuring the latest and greatest uplifting trance music available.",
        "talksport.webp",
        "https://www.discovertrance.com/",
        listOf(
            Stream(
                "audio/x-mpegurl",
                "http://uk01.discovertrance.com:9216/listen.pls?sid=1"
            ),
            Stream("audio/mpeg", "http://uk01.discovertrance.com:9216/stream/1/")
        )
    )
)

@Preview(showBackground = true, widthDp = 320)
@Composable
fun StationRowPreview() {
    MyRadioTheme {
        StationRow(
            station = testStationItem,
            isSelected = false,
            selectedStream = null,
            onStationClicked = {},
            onStreamClicked = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun StationSelectedRowPreview() {
    MyRadioTheme {
        StationRow(
            station = testStationItem,
            isSelected = true,
            selectedStream = testStationItem.station.streams[0],
            onStationClicked = {},
            onStreamClicked = {}
        )
    }
}
