import java.io.File
import java.util.*

data class Position(val x: Int, val y: Int, val intensity: Int) {

    val present = intensity > 0
}

data class Occurance(val timestamp: Int, val image: List<Position>) {

    val shape = image.filter { keepCol(it.x) && keepRow(it.y) }.map { it.present }

    val hasData = image.any { it.present }

    fun keepRow(y: Int) = image.any { it.y == y && it.present }

    fun keepCol(x: Int) = image.any { it.x == x && it.present }
}

fun main(args: Array<String>) {

    val level = "lvl2"

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

        val image = (0 until rowcount).flatMap { y ->
            (0 until colcount).map { x ->
                Position(x, y, scanner.nextInt())
            }
        }

        Occurance(timestamp, image)
    }

    val result = asteroids
            .filter { it.hasData }
            .groupBy { it.shape }.values
            .map { it.sortedBy { it.timestamp } }
            .sortedBy { it.first().timestamp }

    return result.map { "${it.first().timestamp} ${it.last().timestamp} ${it.count()}" }
}
