import java.io.File
import java.util.*

data class Occurance(val timestamp: Int, val image: List<List<Int>>) {

    val hasData get() = image.fold(false) { value, data ->
        value || data.fold(value) { value, data ->
            value || data > 0
        }
    }

}

fun main(args: Array<String>) {

    val level = "lvl1"

    File("input").listFiles({ dir, filename -> filename.startsWith(level) }).sortedBy { it.name }.forEach {
        val result = processFile(it)
        val joined = result.joinToString("\n")

        println("--- OUTPUT ---")
        println(joined)

        File("output/" + it.name).apply {
            delete()
            createNewFile()
            writeText(joined)
        }

        println()
    }
}

fun processFile(file: File): List<String> {
    println("processing ${file.name}...")

    val scanner = Scanner(file.readText())
    val start = scanner.nextInt()
    val end = scanner.nextInt()

    val count = scanner.nextInt()
    val asteroids = (0 until count).map {
        val timestamp = scanner.nextInt()
        val rowcount = scanner.nextInt()
        val colcount = scanner.nextInt()

        val image = (0 until rowcount).map {
            (0 until colcount).map {
                scanner.nextInt()
            }
        }

        Occurance(timestamp, image)
    }

    val result = asteroids.filter { it.hasData }.sortedBy { it.timestamp }

    return result.map { it.timestamp.toString() }
}
