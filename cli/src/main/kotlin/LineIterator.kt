import java.nio.file.Files
import java.nio.file.Path

class LineIterator(filePath: Path) : Iterator<String> {
    private val reader = Files.newBufferedReader(filePath)
    private var nextLine: String? = null // necesaria pq readLine() avanza el cursor
    private var closed = false

    override fun hasNext(): Boolean {
        if (closed) return false
        if (nextLine != null) return true
        nextLine = reader.readLine()
        if (nextLine == null) {
            closed = true
            reader.close()
            return false
        }
        return true
    }

    override fun next(): String {
        if (!hasNext()) throw NoSuchElementException()
        val out = nextLine!!
        nextLine = null
        return out
    }
}
