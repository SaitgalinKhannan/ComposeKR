import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class Photos {
    private val emptyPainter = ColorPainter(Color.Transparent)
    private val file = File("C:/CPhotos")
    private val formats = listOf("png", "jpeg", "jpg")
    private val images = mutableListOf<String>()

    suspend fun imagesList(): List<String> {
        var tmp: String
        val deferred = CoroutineScope(Dispatchers.IO).async {
            val imagesPath = file.listFiles()
            if (imagesPath != null) {
                for (i in imagesPath) {
                    tmp = i.absolutePath
                    if (formats.contains(tmp.substringAfterLast('.')))
                        images.add(tmp.replace('\\', '/'))
                        //images.add(tmp)
                }
            }
            if (images.isEmpty()) {
                images.add("C:\\ComposeKR\\src\\jvmMain\\resources\\Kotlin.png")
            }
            return@async images
        }
        return deferred.await()
    }

    @Composable
    fun rememberImagePainter(file: File, images: MutableState<List<String>>): Painter {
        val painter by produceState<Painter>(emptyPainter, file) {
            value = withContext(Dispatchers.IO) {
                try {
                    loadImageBitmap(file, images)
                } catch (e: IOException) {
                    emptyPainter
                }
            }
        }
        return painter
    }

    private suspend fun loadImageBitmap(file: File, images: MutableState<List<String>>): Painter {
        val fileInputStream = withContext(Dispatchers.IO) {
            FileInputStream(file)
        }
        val bytes = fileInputStream.buffered().readBytes()
        withContext(Dispatchers.IO) {
            fileInputStream.close()
        }
        return try {
            BitmapPainter(Image.makeFromEncoded(bytes).toComposeImageBitmap())
        } catch (e: Exception) {
            file.delete()
            images.value = imagesList()
            println(e.message)
            BitmapPainter(Image.makeFromEncoded(withContext(Dispatchers.IO) {
                FileInputStream("C:\\ComposeKR\\src\\jvmMain\\resources\\элизабетолсен___2022-09-13___00-18-11.png").buffered()
            }.readBytes()).toComposeImageBitmap())
        }
    }
}