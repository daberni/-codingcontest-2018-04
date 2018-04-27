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

    val level = "lvl3"

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

    val shapes = asteroids
            .filter { it.hasData }
            .groupBy { it.shape }.values
            .map { it.sortedBy { it.timestamp } }

    val intervalled = shapes.flatMap { group ->
        val pairs = group.mapIndexed { index1, occurance1 ->
            group.mapIndexedNotNull { index2, occurance2 ->
                if (index2 > index1) occurance1 to occurance2
                else null
            }
        }.flatten().map { it to (it.second.timestamp - it.first.timestamp) }

        val intervals = pairs.groupBy({ it.second }, { it.first })
        val filteredIntervals = intervals.filter { interval ->
            intervals.none { interval.key != it.key && it.key % interval.key == 0 || interval.value.containsAll(it.value) }
        }
        intervals.flatMap { (interval, pairs) ->
            val offsets = pairs.groupBy { it.first.timestamp % interval }
            /*
            val sequences = offsets.values.map {
                it.flatMap { it.toList() }
            }
            */
            val sequences = pairs.fold(mutableListOf(mutableListOf<Occurance>())) { mutableList, pair ->
                if (mutableList.last().isEmpty()) {
                    mutableList.last().add(pair.first)
                    mutableList.last().add(pair.second)
                } else if (mutableList.last().last() == pair.first) {
                    mutableList.last().add(pair.second)
                } else {
                    mutableList.add(mutableListOf(pair.first, pair.second))
                }
                mutableList
            }
            sequences.map { interval to it as List<Occurance> }
        }
    }
            .filter { it.second.count() >= 4 }
            .filter { it.second.first().timestamp <= (start + it.first) }
            .filter { it.second.last().timestamp >= (end - it.first) }
            .sortedBy { it.second.first().timestamp }

    return intervalled.map { "${it.second.first().timestamp} ${it.second.last().timestamp} ${it.second.count()}" }
}
