import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class Deleter {
    suspend fun deleteFile(path: String, lst: List<String>): List<String> {
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val file = File(path)
            val newLst = lst.toMutableList()
            file.delete()
            newLst.remove(path)
            newLst.toList()
        }
    }
}