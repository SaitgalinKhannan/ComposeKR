import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class Downloader {
    private val client = HttpClient(CIO)
    private val parsingYaImages = ParsingYaImages()

    suspend fun downloadImages(searchText: String, count: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val lst = parsingYaImages.yaImages(searchText)

            if (count < lst.size) {
                for (i in 0 until count) {
                    download(lst[i])
                }
            } else {
                for (i in lst) {
                    download(i)
                }
            }
        }.join()
    }

    private suspend fun download(uri: String) {
        val url = Url(uri)
        val theDir = File("C:/CPhotos/")
        if (!theDir.exists()) {
            theDir.mkdirs()
        }
        val file = File("${theDir.path}/${url.pathSegments.last()}")
        try {
            client.get(url).bodyAsChannel().copyAndClose(file.writeChannel())
        } catch (e: Exception) {
            println(e.message)
        }
    }
}