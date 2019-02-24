package volkerandreasen.som.data

import volkerandreasen.som.Resources
import tomasvolker.numeriko.core.dsl.D
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.array2d.double.DoubleArray2D
import tomasvolker.numeriko.core.interfaces.factory.doubleArray2D
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.toDoubleArray1D
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun squareData() = List(1000) { Random.nextDoubleArray1D(2, 0.25, 0.75) }

fun discData() = List(1000) {
    val radius = /*sqrt(Random.nextDouble())*/Random.nextDouble(0.5)
    val angle = Random.nextDouble(2 * PI)
    D[radius * cos(angle), radius * sin(angle)]
}


fun fileData(name: String): List<DoubleArray1D> =
    File(Resources.url(name).substringAfter(':')).useLines { lines ->
        lines.drop(3).map {
            it.split(" ").map { it.toDouble() }.take(2).toDoubleArray1D()
        }.toList()
    }

fun loadMnist(name: String): List<Pair<DoubleArray2D, Int>> =
    File(Resources.url(name).substringAfter(':')).useLines { lines ->
        lines.drop(3)
            .filter { it.isNotBlank() }
            .map {
                val row = it.split(" ").filter { it.isNotBlank() }.map { it.toDouble() }
                val image = row.chunked(16)
                val label = row.last().toInt()
                doubleArray2D(16, 16) { x, y -> image[y][x] } to label
            }.toList()
    }