import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class ParsingYaImages {
    private val client = HttpClient(CIO)

    suspend fun yaImages(searchText: String): List<String> {
        val text = searchText.replace(" ", "%20")
        val str = client.get("https://yandex.ru/images/search?text=$text&ncrnd=${(1000..9999).random()}").bodyAsText()
        var substr: String
        var tmp: String

        substr = if (text.contains("%20")) {
            str.substringAfter("<h1 class=\"a11y-hidden\">Результаты поиска</h1>")
                .substringBeforeLast("</div></div></div><script nonce=")
        } else {
            str.substringAfter("<h1 class=\"a11y-hidden\">Результаты поиска</h1>")
                .substringBeforeLast("<div class=\"incut incut_position_line incut_visibility_hidden i-bem\"")
        }

        val lst: MutableList<String> = mutableListOf()
        var i = 0

        while (substr.isNotEmpty() && i != 30) {
            tmp = substr.substringAfter("\"origin\":{").substringBefore("}").substringAfter("\"url\":\"")
                .substringBefore("\"")

            if (!lst.contains(tmp) && (tmp.contains(".jpg") || tmp.contains(".png") || tmp.contains(".jpeg"))) {
                lst.add(tmp)
            }

            substr = substr.substringAfter("</a></div></div>")
            i++
        }

        return lst
    }
}
