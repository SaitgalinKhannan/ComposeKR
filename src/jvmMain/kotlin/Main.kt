import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import com.sksamuel.scrimage.filter.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun app(trayState: TrayState, notification: Notification) {

    //------------------------------------------------------------------------------------------------------------------

    val lightColors = lightColors(
        primary = Color(33, 33, 33), //background1
        primaryVariant = Color(38, 50, 56), //background2
        secondary = Color(189, 189, 189), //button1
        secondaryVariant = Color(120, 144, 156), //button2
        surface = Color(2, 136, 209)

    )

    val darkColors = darkColors(
        primary = Color(33, 33, 33), //background1
        primaryVariant = Color(38, 50, 56), //background2
        secondary = Color(189, 189, 189), //button1
        secondaryVariant = Color(120, 144, 156), //button2
        surface = Color(2, 136, 209)
    )

    val colors = if (isSystemInDarkTheme()) darkColors else lightColors

    val typography = Typography(
        h1 = TextStyle(
            fontWeight = FontWeight.W100,
            fontSize = 96.sp
        ),
        button = TextStyle(
            fontWeight = FontWeight.W600,
            fontSize = 14.sp
        )
    )

    //------------------------------------------------------------------------------------------------------------------

    MaterialTheme(
        colors = colors,
        typography = typography
    ) {
        val photos = Photos()
        val imageProcessing = ImageProcessing()
        val deleter = Deleter()
        var tmp: Int
        val imageIndex = remember { mutableStateOf(0) }
        val images =
            remember { mutableStateOf(listOf("C:\\ComposeKR\\src\\jvmMain\\resources\\элизабетолсен___2022-09-13___00-18-11.png")) }
        val scope = rememberCoroutineScope { Dispatchers.Default }
        val isFiltersDialogOpen = remember { mutableStateOf(false) }
        val isDownloadDialogOpen = remember { mutableStateOf(false) }
        val isScaleDialogOpen = remember { mutableStateOf(false) }
        val isTrimDialogOpen = remember { mutableStateOf(false) }
        val isZoomDialogOpen = remember { mutableStateOf(false) }

        LaunchedEffect(images) {
            images.value = photos.imagesList()
        }

        //popup
        if (isFiltersDialogOpen.value) {
            Dialog(
                onCloseRequest = { isFiltersDialogOpen.value = false },
                state = rememberDialogState(position = WindowPosition(Alignment.Center)),
                title = "Filters"
            ) {
                lazyScrollable(colors, isFiltersDialogOpen, images, imageIndex)
            }
        }

        //image download popup
        if (isDownloadDialogOpen.value) {
            Dialog(
                onCloseRequest = { isDownloadDialogOpen.value = false },
                state = rememberDialogState(position = WindowPosition(Alignment.Center)),
                title = "Downloader"
            ) {
                imageDownloadPopup(colors, trayState, notification, images)
            }
        }

        if (isScaleDialogOpen.value) {
            Dialog(
                onCloseRequest = { isScaleDialogOpen.value = false },
                state = rememberDialogState(position = WindowPosition(Alignment.Center)),
                title = "Scale"
            ) {
                scalePopup(colors, isDownloadDialogOpen, imageIndex, images)
            }
        }

        if (isZoomDialogOpen.value) {
            Dialog(
                onCloseRequest = { isZoomDialogOpen.value = false },
                state = rememberDialogState(position = WindowPosition(Alignment.Center)),
                title = "Zoom"
            ) {
                zoomPopup(colors, isZoomDialogOpen, imageIndex, images)
            }
        }

        if (isTrimDialogOpen.value) {
            Dialog(
                onCloseRequest = { isTrimDialogOpen.value = false },
                state = rememberDialogState(
                    position = WindowPosition(Alignment.Center),
                    size = DpSize(400.dp, 400.dp)
                ),
                title = "Trim",
            ) {
                trimPopup(colors, isDownloadDialogOpen, imageIndex, images)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colors.primary, RectangleShape),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.05f)
                    .background(colors.primaryVariant),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(5.dp),
                    onClick = {
                        scope.launch {
                            isDownloadDialogOpen.value = true
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource("download.png"),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = colors.surface
                    )
                }
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.95f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = imageIndex.value,
                ) {
                    Image(
                        painter = photos.rememberImagePainter(File(images.value[imageIndex.value]), images),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(40.dp)
                        .padding(5.dp),
                    onClick = {
                        imageIndex.value = if (imageIndex.value == 0) images.value.lastIndex else imageIndex.value - 1
                    }
                ) {
                    Icon(
                        painter = painterResource("leftB.ico"),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = colors.surface
                    )
                }

                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp)
                        .padding(5.dp),
                    onClick = {
                        imageIndex.value = if (imageIndex.value == images.value.lastIndex) 0 else imageIndex.value + 1
                    },
                ) {
                    Icon(
                        painter = painterResource("rightB.ico"),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = colors.surface
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(colors.primaryVariant),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    modifier = Modifier
                        .size(25.dp)
                        .padding(5.dp),
                    onClick = {
                        if (images.value.size > 1) {
                            if (imageIndex.value == images.value.lastIndex) {
                                scope.launch {
                                    tmp = imageIndex.value
                                    imageIndex.value--
                                    images.value = deleter.deleteFile(images.value[tmp], images.value)
                                }
                            } else {
                                scope.launch {
                                    tmp = imageIndex.value
                                    images.value = deleter.deleteFile(images.value[tmp], images.value)

                                }
                            }
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource("trash.ico"),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = colors.surface
                    )
                }
                Spacer(modifier = Modifier.width(25.dp))

                IconButton(
                    modifier = Modifier
                        .size(25.dp)
                        .padding(5.dp),
                    onClick = {
                        scope.launch {
                            isTrimDialogOpen.value = true
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource("trim.png"),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = colors.surface
                    )
                }
                Spacer(modifier = Modifier.width(25.dp))

                IconButton(
                    modifier = Modifier
                        .size(25.dp)
                        .padding(5.dp),
                    onClick = {
                        scope.launch {
                            isZoomDialogOpen.value = true
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource("zoom.png"),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = colors.surface
                    )
                }
                Spacer(modifier = Modifier.width(25.dp))

                IconButton(
                    modifier = Modifier
                        .size(25.dp)
                        .padding(5.dp),
                    onClick = {
                        scope.launch {
                            imageProcessing.flipVertical(images.value[imageIndex.value])
                            images.value = photos.imagesList()
                            imageIndex.value++
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource("flipV.png"),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = colors.surface
                    )
                }
                Spacer(modifier = Modifier.width(25.dp))

                IconButton(
                    modifier = Modifier
                        .size(25.dp)
                        .padding(5.dp),
                    onClick = {
                        scope.launch {
                            imageProcessing.flipHorizontal(images.value[imageIndex.value])
                            images.value = photos.imagesList()
                            imageIndex.value++
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource("flipH.png"),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = colors.surface
                    )
                }
                Spacer(modifier = Modifier.width(25.dp))

                IconButton(
                    modifier = Modifier
                        .size(25.dp)
                        .padding(5.dp),
                    onClick = {
                        scope.launch {
                            imageProcessing.rotateL(images.value[imageIndex.value])
                            images.value = photos.imagesList()
                            imageIndex.value++
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource("rotateL.png"),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = colors.surface
                    )
                }
                Spacer(modifier = Modifier.width(25.dp))

                IconButton(
                    modifier = Modifier
                        .size(25.dp)
                        .padding(5.dp),
                    onClick = {
                        scope.launch {
                            imageProcessing.rotateR(images.value[imageIndex.value])
                            images.value = photos.imagesList()
                            imageIndex.value++
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource("rotateR.png"),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = colors.surface
                    )
                }
                Spacer(modifier = Modifier.width(25.dp))

                IconButton(
                    modifier = Modifier
                        .size(25.dp)
                        .padding(5.dp),
                    onClick = {
                        scope.launch {
                            isScaleDialogOpen.value = true
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource("scale.png"),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = colors.surface
                    )
                }
                Spacer(modifier = Modifier.width(25.dp))

                IconButton(
                    modifier = Modifier
                        .size(25.dp)
                        .padding(5.dp),
                    onClick = {
                        scope.launch {
                            isFiltersDialogOpen.value = true
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource("filter.png"),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = colors.surface
                    )
                }
                Spacer(modifier = Modifier.width(25.dp))

                IconButton(
                    modifier = Modifier
                        .size(25.dp)
                        .padding(5.dp),
                    onClick = {
                        scope.launch {
                            imageProcessing.backgroundGradient(images.value[imageIndex.value])
                            images.value = photos.imagesList()
                            imageIndex.value++
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource("backColor.png"),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = colors.surface
                    )
                }
            }
        }
    }
}

@Composable
fun lazyScrollable(
    colors: Colors,
    isDialogOpen: MutableState<Boolean>,
    images: MutableState<List<String>>,
    imageIndex: MutableState<Int>
) {
    val imageProcessing = ImageProcessing()
    val photos = Photos()
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.primaryVariant)
            .padding(10.dp)
    ) {

        val state = rememberLazyListState()

        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(end = 12.dp),
            state
        ) {
            item {
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(400.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = colors.secondaryVariant)
                        .padding(start = 10.dp)
                        .clickable {
                            scope.launch {
                                imageProcessing.smooth(images.value[imageIndex.value])
                                images.value = photos.imagesList()
                                imageIndex.value++
                                isDialogOpen.value = false
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = "Blur")
                }
                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(400.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = colors.secondaryVariant)
                        .padding(start = 10.dp)
                        .clickable {
                            scope.launch {
                                imageProcessing.filter(images.value[imageIndex.value], BorderFilter(10))
                                images.value = photos.imagesList()
                                imageIndex.value++
                                isDialogOpen.value = false
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = "Border")
                }
                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(400.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = colors.secondaryVariant)
                        .padding(start = 10.dp)
                        .clickable {
                            scope.launch {
                                imageProcessing.filter(images.value[imageIndex.value], ChromeFilter())
                                images.value = photos.imagesList()
                                imageIndex.value++
                                isDialogOpen.value = false
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = "Chrome")
                }
                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(400.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = colors.secondaryVariant)
                        .padding(start = 10.dp)
                        .clickable {
                            scope.launch {
                                imageProcessing.filter(images.value[imageIndex.value], ContourFilter())
                                images.value = photos.imagesList()
                                imageIndex.value++
                                isDialogOpen.value = false
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = "Contour")
                }
                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(400.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = colors.secondaryVariant)
                        .padding(start = 10.dp)
                        .clickable {
                            scope.launch {
                                imageProcessing.filter(images.value[imageIndex.value], ContrastFilter(5.0))
                                images.value = photos.imagesList()
                                imageIndex.value++
                                isDialogOpen.value = false
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = "Contrast")
                }
                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(400.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = colors.secondaryVariant)
                        .padding(start = 10.dp)
                        .clickable {
                            scope.launch {
                                imageProcessing.filter(images.value[imageIndex.value], CrystallizeFilter())
                                images.value = photos.imagesList()
                                imageIndex.value++
                                isDialogOpen.value = false
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = "Crystallize")
                }
                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(400.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = colors.secondaryVariant)
                        .padding(start = 10.dp)
                        .clickable {
                            scope.launch {
                                imageProcessing.filter(images.value[imageIndex.value], EdgeFilter())
                                images.value = photos.imagesList()
                                imageIndex.value++
                                isDialogOpen.value = false
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = "Edge")
                }
                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(400.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = colors.secondaryVariant)
                        .padding(start = 10.dp)
                        .clickable {
                            scope.launch {
                                imageProcessing.filter(images.value[imageIndex.value], DitherFilter())
                                images.value = photos.imagesList()
                                imageIndex.value++
                                isDialogOpen.value = false
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = "Dither")
                }
                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(400.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = colors.secondaryVariant)
                        .padding(start = 10.dp)
                        .clickable {
                            scope.launch {
                                imageProcessing.filter(images.value[imageIndex.value], GothamFilter())
                                images.value = photos.imagesList()
                                imageIndex.value++
                                isDialogOpen.value = false
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = "Gotham")
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }

        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}

@Composable
fun imageDownloadPopup(
    colors: Colors,
    trayState: TrayState,
    notification: Notification,
    images: MutableState<List<String>>
) {
    var text by remember { mutableStateOf("Witcher") }
    var text1 by remember { mutableStateOf("10") }
    var count by remember { mutableStateOf(10) }
    var buttonState by remember { mutableStateOf("Download") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.primaryVariant),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = text1,
            onValueChange = {
                text1 = it
                count = it.toInt()
            },
            label = { Text(text = "Count") }
        )

        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(text = "Search text") }
        )

        Spacer(modifier = Modifier.height(5.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    buttonState = "Downloading"
                    Downloader().downloadImages(text, count)
                    images.value = Photos().imagesList()
                    trayState.sendNotification(notification)
                    buttonState = "Downloaded"
                }
            }
        ) {
            Text(text = buttonState)
        }
    }
}

@Composable
fun scalePopup(
    colors: Colors,
    isScaleDialogOpen: MutableState<Boolean>,
    imageIndex: MutableState<Int>,
    images: MutableState<List<String>>
) {
    val scope = rememberCoroutineScope()
    var xText by remember { mutableStateOf("") }
    var yText by remember { mutableStateOf("") }
    var x by remember { mutableStateOf(1.0) }
    var y by remember { mutableStateOf(1.0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.primaryVariant),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = xText,
            onValueChange = { value ->
                if (value.toDouble() >= 1) {
                    x = value.filter { it.isDigit() }.toDouble()
                    xText = x.toString()
                }
            },
            label = { Text(text = "X") }
        )
        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = yText,
            onValueChange = { value ->
                if (value.toDouble() >= 1) {
                    y = value.filter { it.isDigit() }.toDouble()
                    yText = y.toString()
                }
            },
            label = { Text(text = "Y") }
        )
        Spacer(modifier = Modifier.height(5.dp))

        Button(
            onClick = {
                scope.launch {
                    ImageProcessing().downScale(images.value[imageIndex.value], x, y)
                    isScaleDialogOpen.value = false
                    images.value = Photos().imagesList()
                    imageIndex.value++
                }
            }
        ) {
            Text(text = "Downscale")
        }
    }
}

@Composable
fun zoomPopup(
    colors: Colors,
    isZoomDialogOpen: MutableState<Boolean>,
    imageIndex: MutableState<Int>,
    images: MutableState<List<String>>
) {
    val scope = rememberCoroutineScope()
    var zText by remember { mutableStateOf("") }
    var z by remember { mutableStateOf(1.0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.primaryVariant),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = zText,
            onValueChange = { value ->
                if (value.isNotEmpty() && value.toDouble() >= 1) {
                    z = value.filter { it.isDigit() }.toDouble()
                    zText = value
                }
            },
            label = { Text(text = "Z") }
        )
        Spacer(modifier = Modifier.height(5.dp))

        Button(
            onClick = {
                scope.launch {
                    ImageProcessing().zoom(images.value[imageIndex.value], 1.3)
                    isZoomDialogOpen.value = false
                    images.value = Photos().imagesList()
                    imageIndex.value++
                }
            }
        ) {
            Text(text = "Zoom")
        }
    }
}

@Composable
fun trimPopup(
    colors: Colors,
    isScaleDialogOpen: MutableState<Boolean>,
    imageIndex: MutableState<Int>,
    images: MutableState<List<String>>
) {
    val scope = rememberCoroutineScope()
    var leftText by remember { mutableStateOf("") }
    var topText by remember { mutableStateOf("") }
    var rightText by remember { mutableStateOf("") }
    var bottomText by remember { mutableStateOf("") }
    var left by remember { mutableStateOf(0) }
    var top by remember { mutableStateOf(0) }
    var right by remember { mutableStateOf(0) }
    var bottom by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.primaryVariant),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = leftText,
            onValueChange = { value ->
                if (value.isNotEmpty() && value.toInt() >= 0) {
                    left = value.filter { it.isDigit() }.toInt()
                    leftText = left.toString()
                }
            },
            label = { Text(text = "Left") }
        )
        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = topText,
            onValueChange = { value ->
                if (value.isNotEmpty() && value.toInt() >= 0) {
                    top = value.filter { it.isDigit() }.toInt()
                    topText = top.toString()
                }
            },
            label = { Text(text = "Top") }
        )
        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = rightText,
            onValueChange = { value ->
                if (value.isNotEmpty() && value.toInt() >= 0) {
                    right = value.filter { it.isDigit() }.toInt()
                    rightText = right.toString()
                }
            },
            label = { Text(text = "Right") }
        )
        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = bottomText,
            onValueChange = { value ->
                if (value.isNotEmpty() && value.toInt() >= 0) {
                    bottom = value.filter { it.isDigit() }.toInt()
                    bottomText = bottom.toString()
                }
            },
            label = { Text(text = "Bottom") }
        )
        Spacer(modifier = Modifier.height(5.dp))

        Button(
            onClick = {
                scope.launch {
                    ImageProcessing().trim(images.value[imageIndex.value], left, top, right, bottom)
                    isScaleDialogOpen.value = false
                    images.value = Photos().imagesList()
                    imageIndex.value++
                }
            }
        ) {
            Text(text = "Trim")
        }
    }
}

fun main() = application {
    val icon = painterResource("Kotlin.ico")
    var isOpen by remember { mutableStateOf(true) }
    val trayState = rememberTrayState()
    val notification = rememberNotification("Notification", "Downloaded!")

    if (isOpen) {
        Tray(
            state = trayState,
            icon = icon,
            menu = {
                Item(
                    "Send notification",
                    onClick = {
                        trayState.sendNotification(notification)
                    }
                )

                Item(
                    "Exit",
                    onClick = {
                        isOpen = false
                    }
                )
            }
        )

        Window(
            onCloseRequest = { isOpen = false },
            //onCloseRequest = ::exitApplication,
            icon = icon,
            title = "Compose for Desktop",
            state = rememberWindowState(),
        ) {
            app(trayState, notification)
        }
    }
}